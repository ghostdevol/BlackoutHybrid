document.addEventListener("DOMContentLoaded", () => {

  const content = document.getElementById("content");
  const links = document.querySelectorAll(".nav a");

  async function loadPage(page) {
    try {
      console.log("Loading:", page);
      const response = await fetch(page);
      if (!response.ok) throw new Error("Page not found");

      const html = await response.text();
      content.innerHTML = html;

    } catch (err) {
      content.innerHTML = "<h2>Error loading page.</h2>";
      console.error(err);
    }
  }

  // Load default page
  loadPage("home.html");

  // Add click listeners
  links.forEach(link => {
    link.addEventListener("click", e => {
      e.preventDefault();
      const page = link.getAttribute("data-page");
      loadPage(page);
    });
  });

});
