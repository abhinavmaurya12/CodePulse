const CACHE_NAME = "codepulse-cache-v5";

// Works from any folder (e.g. /CodePulse/ on GitHub Pages or the site root):
// the app root is taken from the service worker's own scope.
const APP_ROOT = new URL(self.registration.scope).pathname;

const APP_FILES = [
  APP_ROOT,
  APP_ROOT + "index.html",
  APP_ROOT + "manifest.json",
  APP_ROOT + "icon.svg",
  APP_ROOT + "icon-192.png",
  APP_ROOT + "icon-512.png",
  APP_ROOT + "icon-maskable-512.png"
];

self.addEventListener("install", (event) => {
  event.waitUntil(
    caches.open(CACHE_NAME)
      .then((cache) => cache.addAll(APP_FILES))
      .then(() => self.skipWaiting())
  );
});

self.addEventListener("activate", (event) => {
  event.waitUntil(
    caches.keys()
      .then((keys) =>
        Promise.all(
          keys
            .filter((key) => key.startsWith("codepulse-cache-") && key !== CACHE_NAME)
            .map((key) => caches.delete(key))
        )
      )
      .then(() => self.clients.claim())
  );
});

self.addEventListener("fetch", (event) => {
  const request = event.request;
  const url = new URL(request.url);

  // Only handle same-origin GETs. Judge0 API calls (other origin / POST) go straight to the network.
  if (request.method !== "GET" || url.origin !== self.location.origin) {
    return;
  }

  event.respondWith(
    caches.match(request).then((cachedResponse) => {
      if (cachedResponse) {
        return cachedResponse;
      }

      return fetch(request).then((response) => {
        if (response.ok && url.pathname.startsWith(APP_ROOT)) {
          const copy = response.clone();
          caches.open(CACHE_NAME).then((cache) => cache.put(request, copy));
        }

        return response;
      }).catch(() => {
        if (request.mode === "navigate") {
          return caches.match(APP_ROOT + "index.html");
        }

        return new Response("You are offline and this resource is not cached.", {
          status: 503,
          statusText: "Offline"
        });
      });
    })
  );
});
