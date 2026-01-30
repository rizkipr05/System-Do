requireRole("ADMIN", "login-admin.html");
setAdminGreeting();

const el = (id) => document.getElementById(id);
let customers = [];
let addresses = [];
let products = [];
let orders = [];
let cart = [];

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

function renderCart() {
  const tbody = el("cartTable");
  tbody.innerHTML = "";
  if (!cart.length) {
    tbody.innerHTML = `<tr><td colspan="4" class="empty-state">Belum ada item.</td></tr>`;
    return;
  }
  cart.forEach((item, idx) => {
    const row = document.createElement("tr");
    row.innerHTML = `
      <td>${item.name}</td>
      <td>${item.quantity}</td>
      <td>${item.unit || "-"}</td>
      <td class="text-end"><button class="btn btn-sm btn-outline-danger" data-index="${idx}">Hapus</button></td>
    `;
    tbody.appendChild(row);
  });
}

async function loadOrders() {
  orders = await getJson("/admin/orders");
  renderOrders();
}

async function loadCustomers() {
  customers = await getJson("/admin/customers");
  el("orderCustomerSelect").innerHTML = customers.map((c) => `<option value="${c.userId}">${c.name}</option>`).join("");
  if (customers.length) await loadAddresses(customers[0].userId);
}

async function loadAddresses(userId) {
  addresses = await getJson(`/admin/customers/${userId}/addresses`);
  el("orderAddressSelect").innerHTML = addresses.map((a) => `<option value="${a.id}">${a.label} - ${a.addressLine}</option>`).join("");
}

async function loadProducts() {
  products = await getJson("/admin/products");
  el("productSelect").innerHTML = products.map((p) => `<option value="${p.id}">${p.name}</option>`).join("");
}

el("orderCustomerSelect").addEventListener("change", (e) => {
  loadAddresses(e.target.value);
});

el("addItemBtn").addEventListener("click", (e) => {
  e.preventDefault();
  const productId = el("productSelect").value;
  const qty = Number(el("productQty").value || 0);
  const product = products.find((p) => `${p.id}` === productId);
  if (!product || qty <= 0) return;
  const existing = cart.find((c) => c.productId === product.id);
  if (existing) existing.quantity += qty;
  else cart.push({ productId: product.id, name: product.name, unit: product.unit, quantity: qty });
  renderCart();
});

el("cartTable").addEventListener("click", (e) => {
  const btn = e.target.closest("button");
  if (!btn) return;
  const idx = Number(btn.dataset.index);
  cart.splice(idx, 1);
  renderCart();
});

el("createOrderBtn").addEventListener("click", async () => {
  if (!cart.length) return;
  const payload = {
    customerId: Number(el("orderCustomerSelect").value),
    addressId: Number(el("orderAddressSelect").value),
    note: el("orderNote").value.trim(),
    items: cart.map((c) => ({ productId: c.productId, quantity: c.quantity }))
  };
  await postJson("/admin/orders/manual", payload);
  cart = [];
  renderCart();
  el("orderNote").value = "";
  showAlert("orderAlert", "DO berhasil dibuat");
  await loadOrders();
});

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
    await Promise.all([loadOrders(), loadCustomers(), loadProducts()]);
    renderCart();
  } catch (err) {
    console.error(err);
  }
})();
