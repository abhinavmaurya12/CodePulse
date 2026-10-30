# ⚡ CodePulse — Modern Code Editor

### Write. Run. Preview. Create.

**CodePulse** is a modern, VS Code-inspired coding environment designed to provide developers with a clean, powerful, and familiar workspace. It brings together code editing, file management, source control, Java compilation, live previews, and developer tools in one interface.

🌐 **Live Demo:** [Launch CodePulse](https://abhinavmaurya12.github.io/CodePulse/)

📦 **Repository:** [GitHub - CodePulse](https://github.com/abhinavmaurya12/CodePulse)

---

## ✨ Features

- 💻 **Modern Code Editor** — VS Code-inspired interface with syntax highlighting and line numbers.
- 📁 **File Explorer** — Create, open, and manage files and folders.
- 🔍 **Search & Replace** — Quickly find and replace text in your code.
- ▶️ **Run & Compile** — Compile and execute Java programs using the local Java backend.
- 🌐 **Live Preview** — Preview HTML projects directly inside the editor.
- 🗂️ **Multiple Tabs** — Work with multiple files simultaneously.
- 🌓 **Dark & Light Themes** — Switch between different editor appearances.
- 🔀 **Source Control** — Git-inspired source control workspace.
- 🧩 **Extensions Panel** — Explore extension-related functionality.
- 🧪 **Testing Panel** — Dedicated workspace for testing tools.
- 🗄️ **Database Panel** — SQL query workspace and database-related tools.
- 👤 **Developer Account** — Personalize your workspace with profile details and a photo.
- ⌨️ **Keyboard Shortcuts** — Speed up your development workflow.
- 📱 **Responsive Interface** — Desktop and mobile-friendly layout.
- 📲 **PWA Support** — Install CodePulse as an application on supported devices.

## 🚀 Live Preview

Experience CodePulse directly in your browser:

**[⚡ Launch CodePulse](https://abhinavmaurya12.github.io/CodePulse/)**

## 🛠️ Tech Stack

| Technology | Purpose |
|---|---|
| HTML5 | Application structure |
| CSS3 | Styling and responsive layout |
| JavaScript | Editor functionality and interactions |
| Java | Local compilation and execution backend |
| SQL | Database-related functionality |
| PWA | Installable web application |
| Service Worker | Offline and application caching support |
| Git & GitHub | Version control and hosting |

## 📂 Project Structure

```text
CodePulse/
│
├── CodeServer.java
├── cp.sql
├── index.html
│
├── st_desktop.css
├── st_mobile.css
│
├── manifest.json
├── sw.js
│
├── icon.svg
├── icon-192.png
├── icon-512.png
├── icon-maskable-512.png
│
└── README.md
```

## ⌨️ Keyboard Shortcuts

| Shortcut | Action |
|---|---|
| `Ctrl + Shift + P` | Show All Commands |
| `Ctrl + P` | Go to File |
| `Alt + N` | New File |
| `F5` | Run Code |
| `Ctrl + F` | Find |
| `Ctrl + H` | Replace |
| `Ctrl + G` | Go to Line |
| `Ctrl + Shift + O` | Go to Symbol |
| `Alt + Shift + Z` | Zen Mode |
| `Ctrl + B` | Toggle Sidebar |
| `Ctrl + J` | Toggle Panel |

## ⚙️ Run Locally

### 1. Clone the repository

```bash
git clone https://github.com/abhinavmaurya12/CodePulse.git
```

### 2. Navigate to the project folder

```bash
cd CodePulse
```

### 3. Open the application

Open `index.html` in your browser or use a local development server.

### 4. Enable the Java backend (optional)

**Prerequisites:**
- Java Development Kit (JDK 8 or newer)
- Java compiler (`javac`)
- Java runtime (`java`)

Compile the backend:

```bash
javac CodeServer.java
```

Start the local server:

```bash
java CodeServer
```

Open:

```text
http://localhost:8080
```

**Note:** The current Java server implementation expects `CodeCanvas.html` in the same directory. Make sure the frontend filename and backend configuration match before starting the Java server.

## 🎯 Project Goals

- Recreate a modern IDE experience inspired by Visual Studio Code.
- Build an interactive developer workspace.
- Explore frontend development using HTML, CSS, and JavaScript.
- Integrate Java compilation and execution through a local backend.
- Develop a responsive and installable web application.

## 🔮 Future Enhancements

- [ ] Advanced syntax highlighting for multiple languages.
- [ ] Integrated terminal with command execution.
- [ ] AI-powered coding assistant.
- [ ] Code auto-completion and IntelliSense.
- [ ] Advanced Git and GitHub integration.
- [ ] Extension marketplace integration.
- [ ] Multiple programming language support.
- [ ] Cloud-based project storage.
- [ ] Collaborative coding and real-time editing.
- [ ] Improved offline support.

## 👨‍💻 Author

**Abhinav Maurya**

- 🐙 GitHub: [@abhinavmaurya12](https://github.com/abhinavmaurya12)
- 💼 LinkedIn: [Connect with me](https://www.linkedin.com/in/abhinav-maurya-25162b338/)
- ⚡ Project: [CodePulse](https://abhinavmaurya12.github.io/CodePulse/)

## ⚠️ Disclaimer

CodePulse is an independent educational project inspired by Visual Studio Code. It is not affiliated with, endorsed by, or sponsored by Microsoft.

## ⭐ Support

If you like CodePulse, consider giving this repository a ⭐ on GitHub. Your feedback and suggestions are always appreciated!

---

**Made with ❤️ by Abhinav Maurya**

*CodePulse — Feel the pulse of coding.*
