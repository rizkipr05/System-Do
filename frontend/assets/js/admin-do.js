requireRole("ADMIN", "login-admin.html");
setAdminGreeting();

const el = (id) => document.getElementById(id);
let orders = [];

function renderOrders() {
  const tbody = el("orderTable");
  tbody.innerHTML = "";
  if (!orders.length) {
    tbody.innerHTML = `<tr><td colspan="7" class="empty-state">Belum ada DO.</td></tr>`;
    return;
  }
  orders.forEach((o) => {
    const row = document.createElement("tr");
    row.innerHTML = `
      <td>#${o.id}</td>
      <td>${o.doNumber || "-"}</td>
      <td>${o.customerName}</td>
      <td>${o.status}</td>
      <td>${o.addressLine || "-"}</td>
      <td>${o.items.map((i) => `${i.productName} (${i.quantity})`).join(", ")}</td>
      <td class="text-end">
        <select class="form-select form-select-sm d-inline w-auto me-2" data-action="status" data-id="${o.id}">
          <option value="DRAFT" ${o.status === "DRAFT" ? "selected" : ""}>DRAFT</option>
          <option value="APPROVED" ${o.status === "APPROVED" ? "selected" : ""}>APPROVED</option>
          <option value="PACKING" ${o.status === "PACKING" ? "selected" : ""}>PACKING</option>
          <option value="READY_TO_SHIP" ${o.status === "READY_TO_SHIP" ? "selected" : ""}>READY_TO_SHIP</option>
          <option value="IN_TRANSIT" ${o.status === "IN_TRANSIT" ? "selected" : ""}>IN_TRANSIT</option>
          <option value="DELIVERED" ${o.status === "DELIVERED" ? "selected" : ""}>DELIVERED</option>
          <option value="FAILED" ${o.status === "FAILED" ? "selected" : ""}>FAILED</option>
        </select>
        <button class="btn btn-sm btn-outline-primary" data-action="apply" data-id="${o.id}">Update</button>
      </td>
    `;
    tbody.appendChild(row);
  });
}

async function loadOrders() {
  orders = await getJson("/admin/orders");
  renderOrders();
}

el("orderTable").addEventListener("click", async (e) => {
  const btn = e.target.closest("button");
  if (!btn) return;
  const id = btn.dataset.id;
  const rowSelect = btn.parentElement.querySelector("select");
  const status = rowSelect.value;
  await postJson(`/admin/orders/${id}/status`, { status });
  showAlert("orderAlert", "Status diperbarui");
  await loadOrders();
});

(async function init() {
  try {
    await loadOrders();
  } catch (err) {
    console.error(err);
    const errBox = document.getElementById("doError");
    if (errBox) {
      errBox.textContent = err?.message || "Gagal memuat data.";
      errBox.classList.remove("d-none");
    }
  }
})();
