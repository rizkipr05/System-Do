requireRole("CUSTOMER", "login-customer.html");
setGreeting();

const el = (id) => document.getElementById(id);
let orders = [];

function renderTrackingSteps(order) {
  const steps = el("trackingSteps");
  steps.innerHTML = "";
  const currentIndex = statusSteps.indexOf(order.status);
  statusSteps.forEach((s, idx) => {
    const li = document.createElement("li");
    const active = idx <= currentIndex && currentIndex >= 0 ? "status-ok" : "status-warn";
    li.innerHTML = `<span class="status-pill ${active}">${statusLabels[s] || s}</span>`;
    steps.appendChild(li);
  });
  el("trackingStatusChip").textContent = `Status: ${statusLabels[order.status] || order.status}`;
}

function renderTracking() {
  const select = el("trackingOrderSelect");
  select.innerHTML = orders.map((o) => `<option value="${o.id}">#${o.id} - ${statusLabels[o.status] || o.status}</option>`).join("");
  if (!orders.length) {
    el("trackingEmpty").classList.remove("d-none");
    el("trackingSteps").innerHTML = "";
    el("trackingStatusChip").textContent = "Status: -";
    return;
  }
  el("trackingEmpty").classList.add("d-none");
  renderTrackingSteps(orders[0]);
}

async function loadOrders() {
  orders = await getJson("/orders");
  renderTracking();
}

el("trackingOrderSelect").addEventListener("change", (e) => {
  const id = e.target.value;
  const order = orders.find((o) => `${o.id}` === id);
  if (order) renderTrackingSteps(order);
});

loadOrders().catch(console.error);
