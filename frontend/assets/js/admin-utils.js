function setAdminGreeting() {
  const u = JSON.parse(localStorage.getItem("user") || "{}");
  const name = u.name || "Admin";
  const greeting = document.getElementById("greeting");
  const badge = document.getElementById("adminBadge");
  if (greeting) greeting.textContent = name;
  if (badge) badge.textContent = u.email ? `${u.email}` : "Admin";
}

function showAlert(id, message) {
  const box = document.getElementById(id);
  if (!box) return;
  box.textContent = message;
  box.classList.remove("d-none");
  setTimeout(() => box.classList.add("d-none"), 2500);
}
