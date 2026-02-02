requireRole("QUALITY_CONTROL", "login-quality-control.html");
setAdminGreeting();

const el = (id) => document.getElementById(id);
let orders = [];
let drivers = [];

function renderOrders() {
  const tbody = el("shipTable");
  tbody.innerHTML = "";
  if (!orders.length) {
    tbody.innerHTML = `<tr><td colspan="7" class="empty-state">Belum ada order.</td></tr>`;
    return;
  }
  orders.forEach((o) => {
    const row = document.createElement("tr");
    row.innerHTML = `
      <td>#${o.id}</td>
      <td>${o.doNumber || "-"}</td>
      <td>${o.customerName}</td>
      <td>${o.status}</td>
      <td>${o.driverName || "-"}</td>
      <td>${o.addressLine || "-"}</td>
      <td class="text-end">
        <select class="form-select form-select-sm d-inline w-auto me-2" data-role="driver" data-id="${o.id}">
          ${drivers.map((d) => `<option value="${d.id}" ${o.driverName === d.name ? "selected" : ""}>${d.name}</option>`).join("")}
        </select>
        <button class="btn btn-sm btn-outline-primary me-1" data-action="assign" data-id="${o.id}">Assign</button>
        <button class="btn btn-sm btn-outline-warning" data-action="reschedule" data-id="${o.id}">Reschedule</button>
      </td>
    `;
    tbody.appendChild(row);
  });
}

async function loadOrders() {
  orders = await getJson("/admin/orders");
  renderOrders();
}

async function loadDrivers() {
  drivers = await getJson("/admin/drivers");
}

el("shipTable").addEventListener("click", async (e) => {
  const btn = e.target.closest("button");
  if (!btn) return;
  const id = btn.dataset.id;
  const action = btn.dataset.action;
  if (action === "assign") {
    const select = btn.parentElement.querySelector("select[data-role='driver']");
    const driverId = Number(select.value);
    await postJson(`/admin/orders/${id}/assign-driver`, { driverId });
    showAlert("shipAlert", "Driver di-assign");
  } else if (action === "reschedule") {
    await postJson(`/admin/orders/${id}/reschedule`, {});
    showAlert("shipAlert", "Order di-reschedule");
  }
  await loadOrders();
});

(async function init() {
  try {
    await loadDrivers();
    await loadOrders();
  } catch (err) {
    console.error(err);
  }
})();
