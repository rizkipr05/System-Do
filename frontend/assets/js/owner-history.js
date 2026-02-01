requireRole("CUSTOMER", "login-owner.html");
setGreeting();

const el = (id) => document.getElementById(id);
let orders = [];

function renderHistory() {
  const tbody = el("historyTable");
  tbody.innerHTML = "";
  if (!orders.length) {
    tbody.innerHTML = `<tr><td colspan="5" class="empty-state">Belum ada order.</td></tr>`;
    return;
  }
  orders.forEach((o) => {
    const items = o.items.map((i) => `${i.productName} (${i.quantity} ${i.unit || ""})`).join(", ");
    const row = document.createElement("tr");
    row.innerHTML = `
      <td>#${o.id}</td>
      <td>${formatDate(o.createdAt)}</td>
      <td>${statusLabels[o.status] || o.status}</td>
      <td>${o.address?.addressLine || "-"}</td>
      <td>${items}</td>
    `;
    tbody.appendChild(row);
  });
}

async function loadOrders() {
  orders = await getJson("/orders");
  renderHistory();
}

loadOrders().catch(console.error);
