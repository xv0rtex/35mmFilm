const CACHE_NAME = "filmpin-cache-v2";
const ASSETS = ["/css/style.css", "/favicon.png", "/manifest.webmanifest"];
self.addEventListener("install", (event) => {
  event.waitUntil(caches.open(CACHE_NAME).then((cache) => cache.addAll(ASSETS)));
  self.skipWaiting();
});
self.addEventListener("activate", (event) => {
  event.waitUntil(
    caches
      .keys()
      .then((keys) =>
        Promise.all(keys.map((k) => (k !== CACHE_NAME ? caches.delete(k) : null)))
      )
      .then(() => self.clients.claim())
  );
});
self.addEventListener("fetch", (event) => {
  const req = event.request;
  const url = new URL(req.url);
  if (req.method !== "GET") {
    return;
  }
  if (req.mode === "navigate" || (req.headers.get("accept") || "").includes("text/html")) {
    event.respondWith(
      fetch(req)
        .then((res) => {
          const clone = res.clone();
          caches.open(CACHE_NAME).then((cache) => cache.put(req, clone));
          return res;
        })
        .catch(() => caches.match(req))
    );
    return;
  }
  if (url.pathname.startsWith("/images/uploads/")) {
    event.respondWith(fetch(req).catch(() => caches.match(req)));
    return;
  }
  const isStatic =
    url.pathname.startsWith("/css/") ||
    url.pathname.startsWith("/js/") ||
    url.pathname.startsWith("/images/") ||
    url.pathname === "/favicon.png" ||
    url.pathname === "/manifest.webmanifest";
  if (isStatic) {
    event.respondWith(
      caches.match(req).then((cached) => {
        if (cached) return cached;
        return fetch(req).then((res) => {
          const clone = res.clone();
          caches.open(CACHE_NAME).then((cache) => cache.put(req, clone));
          return res;
        });
      })
    );
    return;
  }
  event.respondWith(fetch(req).catch(() => caches.match(req)));
});
