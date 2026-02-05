const statusLabels = {
  DRAFT: "Draft (diproses admin)",
  APPROVED: "Approved",
  PACKING: "Packing Gudang",
  READY_TO_SHIP: "Siap Kirim",
  IN_TRANSIT: "Dalam Perjalanan",
  DELIVERED: "Terkirim",
  FAILED: "Gagal Kirim",
  CONFIRMED: "Konfirmasi Penerimaan"
};

const statusSteps = [
  "DRAFT",
  "APPROVED",
  "PACKING",
  "READY_TO_SHIP",
  "IN_TRANSIT",
  "DELIVERED",
  "CONFIRMED"
];

function setGreeting() {
  const u = JSON.parse(localStorage.getItem("user") || "{}");
  const name = u.name || "Customer";
  const greeting = document.getElementById("greeting");
  const badge = document.getElementById("customerBadge");
  if (greeting) greeting.textContent = name;
  if (badge) badge.textContent = u.email ? `${u.email}` : "Customer";
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

function formatDate(value) {
  if (!value) return "-";
  const d = new Date(value);
  return d.toLocaleString("id-ID");
}
