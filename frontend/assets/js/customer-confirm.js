requireRole("CUSTOMER", "login-customer.html");
setGreeting();

const el = (id) => document.getElementById(id);
let orders = [];

function renderConfirmOptions() {
  const delivered = orders.filter((o) => o.status === "DELIVERED");
  el("confirmOrderSelect").innerHTML = delivered.map((o) => {
    const label = o.doNumber ? o.doNumber : `#${o.id}`;
    return `<option value="${o.id}">${label}</option>`;
  }).join("");
  if (!delivered.length) {
    el("confirmOrderSelect").innerHTML = `<option value="">Belum ada order terkirim</option>`;
  }
}

async function loadOrders() {
  orders = await getJson("/orders");
  renderConfirmOptions();
}

const canvas = el("signatureCanvas");
const ctx = canvas.getContext("2d");
let drawing = false;

function resizeCanvas() {
  const rect = canvas.getBoundingClientRect();
  canvas.width = rect.width;
  canvas.height = rect.height;
  ctx.lineWidth = 2;
  ctx.lineCap = "round";
  ctx.strokeStyle = "#2f2a40";
}

resizeCanvas();
window.addEventListener("resize", resizeCanvas);

canvas.addEventListener("mousedown", (e) => {
  drawing = true;
  ctx.beginPath();
  ctx.moveTo(e.offsetX, e.offsetY);
});
canvas.addEventListener("mousemove", (e) => {
  if (!drawing) return;
  ctx.lineTo(e.offsetX, e.offsetY);
  ctx.stroke();
});
canvas.addEventListener("mouseup", () => { drawing = false; });
canvas.addEventListener("mouseleave", () => { drawing = false; });

el("clearSignatureBtn").addEventListener("click", (e) => {
  e.preventDefault();
  ctx.clearRect(0, 0, canvas.width, canvas.height);
});

async function fileToBase64(file) {
  if (!file) return null;
  return new Promise((resolve, reject) => {
    const reader = new FileReader();
    reader.onload = () => resolve(reader.result);
    reader.onerror = reject;
    reader.readAsDataURL(file);
  });
}

el("submitConfirmBtn").addEventListener("click", async (e) => {
  e.preventDefault();
  const orderId = el("confirmOrderSelect").value;
  if (!orderId) return;
  const receiverName = el("confirmReceiver").value.trim();
  if (!receiverName) {
    showAlert("confirmAlert", "Nama penerima wajib diisi", "danger");
    return;
  }
  const proofFile = el("confirmProof").files[0];
  const proofData = await fileToBase64(proofFile);
  const signatureData = canvas.toDataURL("image/png");
  const payload = {
    receiverName,
    note: el("confirmNote").value.trim(),
    proofImageData: proofData,
    signatureData
  };
  await postJson(`/orders/${orderId}/confirm`, payload);
  showAlert("confirmAlert", "Konfirmasi terkirim");
  el("confirmNote").value = "";
  el("confirmReceiver").value = "";
  el("confirmProof").value = "";
  ctx.clearRect(0, 0, canvas.width, canvas.height);
  await loadOrders();
});

loadOrders().catch(console.error);
