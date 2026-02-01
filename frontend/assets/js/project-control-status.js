requireRole("DRIVER", "login-project-control.html");
setDriverGreeting();

const el = (id) => document.getElementById(id);
let orders = [];

function renderOrders() {
  const select = el("statusOrderSelect");
  select.innerHTML = orders.map((o) => `<option value="${o.id}">${o.doNumber || o.id} - ${o.customerName}</option>`).join("");
  if (orders.length) renderOrderDetail(orders[0]);
}

function renderOrderDetail(order) {
  if (!order) return;
  el("statusAddress").textContent = `${order.addressLine || "-"}, ${order.addressCity || "-"}`;
  el("statusItems").textContent = order.items.map((i) => `${i.productName} (${i.quantity})`).join(", ");
}

async function loadOrders() {
  orders = await getJson("/driver/orders");
  renderOrders();
}

el("statusOrderSelect").addEventListener("change", (e) => {
  const order = orders.find((o) => `${o.id}` === e.target.value);
  renderOrderDetail(order);
});

el("statusForm").addEventListener("submit", async (e) => {
  e.preventDefault();
  const orderId = el("statusOrderSelect").value;
  const payload = {
    status: el("statusSelect").value,
    note: el("statusNote").value.trim()
  };
  await postJson(`/driver/orders/${orderId}/status`, payload);
  showAlert("statusAlert", "Status diperbarui");
  el("statusNote").value = "";
  await loadOrders();
});

loadOrders().catch(console.error);
