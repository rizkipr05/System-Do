const statusLabels = {
  DRAFT: "Draft",
  SIGNED: "TTD Driver",
  APPROVED: "ACC Customer"
};

const statusSteps = ["DRAFT", "SIGNED", "APPROVED"];

function setGreeting() {
  const u = JSON.parse(localStorage.getItem("user") || "{}");
  const name = u.name || "Customer";
  const greeting = document.getElementById("greeting");
  const badge = document.getElementById("customerBadge");
  if (greeting) greeting.textContent = name;
  if (badge) badge.textContent = u.email ? `${u.email}` : "Customer";
}

function showAlert(id, message) {
  const box = document.getElementById(id);
  if (!box) return;
  box.textContent = message;
  box.classList.remove("d-none");
  setTimeout(() => box.classList.add("d-none"), 2500);
}

function formatDate(value) {
  if (!value) return "-";
  const d = new Date(value);
  return d.toLocaleString("id-ID");
}
