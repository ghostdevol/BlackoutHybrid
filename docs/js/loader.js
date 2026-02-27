document.addEventListener("DOMContentLoaded", () => {

  const content = document.getElementById("content");
  const sidebarLinks = document.querySelectorAll(".nav a");

  /**
   * Load HTML page into #content
   * @param {string} page - The HTML filename to load
   */
  async function loadPage(page) {
    try {
      console.log("Loading:", page);

      const response = await fetch(page);
      if (!response.ok) throw new Error("Page not found");

      const html = await response.text();
      content.innerHTML = html;

      // Initialize internal tabs
      initTabs();

      // Dynamically load viewer.js if page is viewer.html
      if (page === "viewer.html") {
        const existing = document.getElementById("viewer-script");
        if(!existing){
          const script = document.createElement("script");
          script.src = "viewer.js";
          script.id = "viewer-script"; // prevent duplicate loading
          document.body.appendChild(script);
        }
      }

    } catch (err) {
      content.innerHTML = "<h2>Error loading page.</h2>";
      console.error(err);
    }
  }

  /**
   * Initialize tabs inside the loaded page
   */
  function initTabs() {
    const tabButtons = content.querySelectorAll(".tab-btn");
    const tabContents = content.querySelectorAll(".tab-content");

    if(tabButtons.length === 0) return; // no tabs in this page

    // Hide all tab contents
    tabContents.forEach(c => c.style.display = "none");

    // Show default tab
    if(tabContents.length > 0) tabContents[0].style.display = "block";

    // Attach click events to each tab button
    tabButtons.forEach(btn => {
      btn.addEventListener("click", () => {
        tabContents.forEach(c => c.style.display = "none");
        const target = content.querySelector(`#${btn.dataset.tab}`);
        if(target) target.style.display = "block";
      });
    });
  }

  // Attach sidebar link events
  sidebarLinks.forEach(link => {
    link.addEventListener("click", e => {
      e.preventDefault();
      const page = link.dataset.page;
      loadPage(page);
    });
  });

  // Load default page on startup
  loadPage("home.html");

});
