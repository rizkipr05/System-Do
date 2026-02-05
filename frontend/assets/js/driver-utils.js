function setDriverGreeting() {
  const u = JSON.parse(localStorage.getItem("user") || "{}");
  const name = u.name || "Driver";
  const greeting = document.getElementById("greeting");
  const badge = document.getElementById("driverBadge");
  if (greeting) greeting.textContent = name;
  if (badge) badge.textContent = u.email ? `${u.email}` : "Driver";
}

function showAlert(id, message, type = "success") {
  const box = document.getElementById(id);
  if (!box) return;
  box.textContent = message;
  box.classList.remove("alert-success", "alert-danger", "alert-warning", "alert-info");
  box.classList.add(`alert-${type}`);
  box.classList.remove("d-none");
  setTimeout(() => box.classList.add("d-none"), 2500);
}
