requireRole("PROJECT_CONTROL", "login-project-control.html");
setDriverGreeting();

const el = (id) => document.getElementById(id);
let orders = [];

function renderOrders() {
  const tbody = el("assignedTable");
  tbody.innerHTML = "";
  if (!orders.length) {
    tbody.innerHTML = `<tr><td colspan="6" class="empty-state">Belum ada DO ditugaskan.</td></tr>`;
    return;
  }
  orders.forEach((o) => {
    const items = o.items.map((i) => `${i.productName} (${i.quantity})`).join(", ");
    const row = document.createElement("tr");
    row.innerHTML = `
      <td>${o.doNumber || o.id}</td>
      <td>${o.customerName}</td>
      <td>${o.addressLine || "-"}</td>
      <td>${o.addressCity || "-"}</td>
      <td>${o.status}</td>
      <td>${items}</td>
    `;
    tbody.appendChild(row);
  });
}

async function loadOrders() {
  orders = await getJson("/driver/orders");
  renderOrders();
}

loadOrders().catch(console.error);
