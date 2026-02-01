requireRole("DRIVER", "login-project-control.html");
setDriverGreeting();

const el = (id) => document.getElementById(id);
let orders = [];

function renderOrders() {
  const select = el("proofOrderSelect");
  select.innerHTML = orders.map((o) => `<option value="${o.id}">${o.doNumber || o.id} - ${o.customerName}</option>`).join("");
}

async function loadOrders() {
  orders = await getJson("/driver/orders");
  renderOrders();
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

el("proofForm").addEventListener("submit", async (e) => {
  e.preventDefault();
  const orderId = el("proofOrderSelect").value;
  const proofFile = el("proofPhoto").files[0];
  const proofData = await fileToBase64(proofFile);
  const signatureData = canvas.toDataURL("image/png");
  const payload = {
    status: "DELIVERED",
    note: el("proofNote").value.trim(),
    proofImageData: proofData,
    signatureData
  };
  await postJson(`/driver/orders/${orderId}/status`, payload);
  showAlert("proofAlert", "Bukti serah terima terkirim");
  el("proofNote").value = "";
  el("proofPhoto").value = "";
  ctx.clearRect(0, 0, canvas.width, canvas.height);
  await loadOrders();
});

loadOrders().catch(console.error);
