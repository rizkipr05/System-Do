requireRole("DRIVER", "login-driver.html");
setDriverGreeting();

const el = (id) => document.getElementById(id);
let orders = [];

function renderOrders() {
  const tbody = el("driverTable");
  tbody.innerHTML = "";
  if (!orders.length) {
    tbody.innerHTML = `<tr><td colspan="6" class="text-center text-muted">Belum ada pengiriman hari ini.</td></tr>`;
    return;
  }
  orders.forEach((o) => {
    const items = (o.items || []).map((i) => `${i.productName} (${i.quantity})`).join(", ");
    const row = document.createElement("tr");
    row.innerHTML = `
      <td>${o.doNumber || o.id}</td>
      <td>${o.customerName || "-"}</td>
      <td>${o.addressLine || "-"}</td>
      <td>${o.addressCity || "-"}</td>
      <td>${o.status || "-"}</td>
      <td>${items || "-"}</td>
    `;
    tbody.appendChild(row);
  });
}

function renderStats() {
  const total = orders.length;
  const inTransit = orders.filter((o) => o.status === "IN_TRANSIT").length;
  const delivered = orders.filter((o) => o.status === "DELIVERED" || o.status === "CONFIRMED").length;
  const failed = orders.filter((o) => o.status === "FAILED").length;
  el("statTotal").textContent = total;
  el("statTransit").textContent = inTransit;
  el("statDelivered").textContent = delivered;
  el("statFailed").textContent = failed;
}

async function loadOrders() {
  orders = await getJson("/driver/orders");
  renderStats();
  renderOrders();
}

loadOrders().catch(console.error);
