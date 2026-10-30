import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import java.io.*;
import java.net.InetSocketAddress;
import java.net.URLDecoder;
import java.nio.charset.Charset;
import java.nio.file.*;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.regex.*;

/*
 * CodeServer - the "compile" and "run" engine behind CodeCanvas.html
 * Same idea as Editor2015: write the file, call javac, then java -cp, and return the text.
 *
 *   javac CodeServer.java
 *   java CodeServer
 *   then open http://localhost:8080
 *
 * Keep CodeCanvas.html in the same folder. Needs a JDK (not just a JRE). Java 8 or newer.
 */
public class CodeServer
{
    static final int PORT = 8080;
    static final long RUN_SECONDS = 10;
    static final Charset UTF8 = Charset.forName("UTF-8");

    static class Result
    {
        String out = "";
        int code;
        boolean timedOut;
    }

    public static void main(String[] args) throws Exception
    {
        // Listens on this computer only, so nobody else on the network can run code on it.
        HttpServer server = HttpServer.create(new InetSocketAddress("127.0.0.1", PORT), 0);
        server.createContext("/", ex -> route(ex));
        server.setExecutor(Executors.newCachedThreadPool());
        server.start();
        System.out.println("CodeCanvas is running at http://localhost:" + PORT);
        System.out.println("javac: " + tool("javac"));
        System.out.println("java : " + tool("java"));
    }

    static void route(HttpExchange ex) throws IOException
    {
        try
        {
            String host = ex.getRequestHeaders().getFirst("Host");
            if (host == null || !(host.equals("localhost:" + PORT) || host.equals("127.0.0.1:" + PORT)))
            {
                send(ex, 403, "text/plain", "Forbidden");
                return;
            }
            String path = ex.getRequestURI().getPath();
            if (ex.getRequestMethod().equals("GET"))
            {
                if (path.equals("/ping"))
                {
                    send(ex, 200, "text/plain", "pong");
                    return;
                }
                Path page = Paths.get("CodeCanvas.html");
                if (Files.exists(page))
                    send(ex, 200, "text/html; charset=utf-8", new String(Files.readAllBytes(page), UTF8));
                else
                    send(ex, 404, "text/plain", "Put CodeCanvas.html in the same folder as CodeServer.java");
                return;
            }
            // Only pages served by this server may submit code (blocks other websites).
            String origin = ex.getRequestHeaders().getFirst("Origin");
            if (origin == null || !(origin.equals("http://localhost:" + PORT) || origin.equals("http://127.0.0.1:" + PORT)))
            {
                send(ex, 403, "text/plain", "Forbidden");
                return;
            }
            Map<String, String> q = query(ex.getRequestURI().getRawQuery());
            String code = new String(readAll(ex.getRequestBody()), UTF8);
            String input = q.containsKey("input") && !q.get("input").isEmpty() ? q.get("input") + "\n" : "";
            String file = q.containsKey("file") ? q.get("file") : "Main.java";
            if (path.equals("/compile"))
                send(ex, 200, "text/plain; charset=utf-8", build(file, code, input, false));
            else if (path.equals("/run"))
                send(ex, 200, "text/plain; charset=utf-8", build(file, code, input, true));
            else
                send(ex, 404, "text/plain", "Not found");
        }
        catch (Exception e)
        {
            send(ex, 500, "text/plain; charset=utf-8", "FAIL\n" + e);
        }
    }

    static String build(String file, String code, String input, boolean run) throws Exception
    {
        Matcher m = Pattern.compile("public\\s+(?:final\\s+|abstract\\s+)*(?:class|interface|enum)\\s+(\\w+)").matcher(code);
        String cls = m.find() ? m.group(1) : file.replaceAll("\\.java$", "");
        if (!cls.matches("\\w+"))
            return "FAIL\nInvalid class name: " + cls;

        Path dir = Files.createTempDirectory("codecanvas");
        try
        {
            Path src = dir.resolve(cls + ".java");
            Files.write(src, code.getBytes(UTF8));
            Result c = exec(dir, "", 30, tool("javac"), "-encoding", "UTF-8", "-d", dir.toString(), src.toString());
            if (c.timedOut)
                return "FAIL\njavac took too long and was stopped.";
            if (c.code != 0)
                return "FAIL\n" + c.out;
            if (!run)
                return "OK\n" + c.out;
            Result r = exec(dir, input, RUN_SECONDS, tool("java"), "-cp", dir.toString(), cls);
            String tail = r.timedOut
                ? "\n[Stopped: the program ran longer than " + RUN_SECONDS + " seconds]"
                : "\n[Process finished with exit code " + r.code + "]";
            return "OK\n" + r.out + tail;
        }
        finally
        {
            delete(dir.toFile());
        }
    }

    static Result exec(Path dir, String input, long seconds, String... cmd) throws Exception
    {
        Process p = new ProcessBuilder(cmd).directory(dir.toFile()).redirectErrorStream(true).start();
        OutputStream os = p.getOutputStream();
        try
        {
            os.write(input.getBytes(UTF8));
            os.close();
        }
        catch (IOException ignored) {}

        final StringBuilder sb = new StringBuilder();
        final InputStream is = p.getInputStream();
        Thread reader = new Thread(() ->
        {
            try
            {
                byte[] buf = new byte[4096];
                int n;
                while ((n = is.read(buf)) > 0)
                {
                    synchronized (sb)
                    {
                        if (sb.length() < 200000)
                            sb.append(new String(buf, 0, n, UTF8));
                    }
                }
            }
            catch (IOException ignored) {}
        });
        reader.start();

        boolean done = p.waitFor(seconds, TimeUnit.SECONDS);
        if (!done)
            p.destroyForcibly();
        reader.join(2000);

        Result r = new Result();
        r.timedOut = !done;
        r.code = done ? p.exitValue() : -1;
        synchronized (sb) { r.out = sb.toString(); }
        return r;
    }

    // Finds javac / java next to the JDK that is running this server.
    static String tool(String name)
    {
        boolean win = System.getProperty("os.name").toLowerCase().contains("win");
        String exe = win ? name + ".exe" : name;
        File home = new File(System.getProperty("java.home"));
        File[] candidates = { new File(home, "bin/" + exe), new File(home.getParentFile() == null ? home : home.getParentFile(), "bin/" + exe) };
        for (File f : candidates)
            if (f.exists())
                return f.getPath();
        return name;
    }

    static Map<String, String> query(String raw) throws UnsupportedEncodingException
    {
        Map<String, String> m = new HashMap<String, String>();
        if (raw == null)
            return m;
        for (String kv : raw.split("&"))
        {
            int i = kv.indexOf('=');
            if (i > 0)
                m.put(URLDecoder.decode(kv.substring(0, i), "UTF-8"), URLDecoder.decode(kv.substring(i + 1), "UTF-8"));
        }
        return m;
    }

    static byte[] readAll(InputStream in) throws IOException
    {
        ByteArrayOutputStream bo = new ByteArrayOutputStream();
        byte[] buf = new byte[4096];
        int n;
        while ((n = in.read(buf)) > 0)
            bo.write(buf, 0, n);
        return bo.toByteArray();
    }

    static void send(HttpExchange ex, int status, String type, String body) throws IOException
    {
        byte[] b = body.getBytes(UTF8);
        ex.getResponseHeaders().set("Content-Type", type);
        ex.sendResponseHeaders(status, b.length);
        OutputStream os = ex.getResponseBody();
        os.write(b);
        os.close();
    }

    static void delete(File f)
    {
        File[] kids = f.listFiles();
        if (kids != null)
            for (File k : kids)
                delete(k);
        f.delete();
    }
}
