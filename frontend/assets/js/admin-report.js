requireRole("ADMIN", "login-admin.html");
setAdminGreeting();

const el = (id) => document.getElementById(id);
let orders = [];

function renderList(targetId, items) {
  const ul = el(targetId);
  ul.innerHTML = "";
  if (!items.length) {
    ul.innerHTML = `<li class="empty-state">Belum ada data.</li>`;
    return;
  }
  items.forEach((i) => {
    const li = document.createElement("li");
    li.textContent = `${i.label} - ${i.value}`;
    ul.appendChild(li);
  });
}

async function loadReport() {
  const r = await getJson("/admin/reports/summary");
  el("reportTotalCustomers").textContent = r.totalCustomers ?? 0;
  el("reportOrdersToday").textContent = r.ordersToday ?? 0;
  el("reportOrdersMonth").textContent = r.ordersThisMonth ?? 0;
  el("reportActiveShipments").textContent = r.activeShipments ?? 0;

  const statusList = Object.entries(r.statusCounts || {}).map(([k, v]) => ({ label: k, value: v }));
  renderList("reportStatusList", statusList);
  renderList("reportTopProducts", r.topProducts || []);
  renderList("reportTopCustomers", r.topCustomers || []);
}

function renderDoSheet(order) {
  if (!order) return;
  el("doNumber").textContent = order.doNumber || "-";
  el("doDate").textContent = order.createdAt ? new Date(order.createdAt).toLocaleDateString("id-ID") : "-";
  el("doCustomer").textContent = order.customerName || "-";
  el("doAddress").textContent = order.addressLine || "-";
  el("doStatus").textContent = order.status || "-";
  el("doNote").textContent = order.note || "-";

  const tbody = el("doItems");
  tbody.innerHTML = "";
  if (!order.items || !order.items.length) {
    tbody.innerHTML = `<tr><td colspan="4" class="empty-state">Tidak ada item.</td></tr>`;
    return;
  }
  order.items.forEach((i, idx) => {
    const row = document.createElement("tr");
    row.innerHTML = `
      <td>${idx + 1}</td>
      <td>${i.productName}</td>
      <td>${i.quantity}</td>
      <td>-</td>
    `;
    tbody.appendChild(row);
  });
}

async function loadOrders() {
  orders = await getJson("/admin/orders");
  const select = el("reportOrderSelect");
  select.innerHTML = orders.map((o) => `<option value="${o.id}">${o.doNumber || o.id} - ${o.customerName}</option>`).join("");
  if (orders.length) renderDoSheet(orders[0]);
}

el("reportOrderSelect").addEventListener("change", (e) => {
  const order = orders.find((o) => `${o.id}` === e.target.value);
  renderDoSheet(order);
});

el("printDoBtn").addEventListener("click", () => {
  window.print();
});

Promise.all([loadReport(), loadOrders()]).catch(console.error);
