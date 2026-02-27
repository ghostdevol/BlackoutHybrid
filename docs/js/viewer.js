document.addEventListener("DOMContentLoaded", () => {
    const params = new URLSearchParams(window.location.search);
    const page = params.get("page");
    const content = document.getElementById("content");

    if (!page) {
        content.innerHTML = "<h2>Welcome to GhostTech Web</h2>";
        return;
    }

    fetch("pages/" + page)
        .then(res => res.text())
        .then(html => {
            content.innerHTML = html;
        })
        .catch(() => {
            content.innerHTML = "<h2>Page not found.</h2>";
        });
});
