requireRole("ADMIN", "login-admin.html");
setAdminGreeting();

const el = (id) => document.getElementById(id);
let customers = [];
let customerAddressMap = {};
let products = [];
let customerOrders = [];
let customerAddresses = [];

function renderCustomers() {
  const tbody = el("customerTable");
  tbody.innerHTML = "";
  if (!customers.length) {
    tbody.innerHTML = `<tr><td colspan="7" class="empty-state">Belum ada customer.</td></tr>`;
    return;
  }
  customers.forEach((c) => {
    const status = c.active ? "bg-success-subtle text-success" : "bg-danger-subtle text-danger";
    const address = customerAddressMap[c.userId] || "-";
    const row = document.createElement("tr");
    row.innerHTML = `
      <td>${c.name}</td>
      <td>${c.companyName || "-"}</td>
      <td>${c.phone || "-"}</td>
      <td>${c.email}</td>
      <td>${address}</td>
      <td><span class="badge ${status}">${c.active ? "Active" : "Inactive"}</span></td>
      <td class="text-end">
        <a class="btn btn-sm btn-outline-primary" href="admin-master-form.html?tab=customer&userId=${c.userId}">Edit</a>
      </td>
    `;
    tbody.appendChild(row);
  });
}

function renderProducts() {
  const tbody = el("productTable");
  tbody.innerHTML = "";
  if (!products.length) {
    tbody.innerHTML = `<tr><td colspan="7" class="empty-state">Belum ada produk.</td></tr>`;
    return;
  }
  products.forEach((p) => {
    const status = p.active ? "bg-success-subtle text-success" : "bg-danger-subtle text-danger";
    const row = document.createElement("tr");
    row.innerHTML = `
      <td>${p.name}</td>
      <td>${p.sku || "-"}</td>
      <td>${p.unit || "-"}</td>
      <td>${p.price ?? 0}</td>
      <td>${p.stock ?? 0}</td>
      <td><span class="badge ${status}">${p.active ? "Active" : "Inactive"}</span></td>
      <td class="text-end">
        <a class="btn btn-sm btn-outline-primary" href="admin-master-form.html?tab=product&productId=${p.id}">Edit</a>
      </td>
    `;
    tbody.appendChild(row);
  });
}

function renderCustomerOrders() {
  const tbody = el("customerOrderTable");
  tbody.innerHTML = "";
  if (!customerOrders.length) {
    tbody.innerHTML = `<tr><td colspan="5" class="empty-state">Belum ada order.</td></tr>`;
    return;
  }
  customerOrders.forEach((o) => {
    const row = document.createElement("tr");
    row.innerHTML = `
      <td>#${o.id}</td>
      <td>${o.doNumber || "-"}</td>
      <td>${o.status}</td>
      <td>${o.addressLine || "-"}</td>
      <td>${o.items.map((i) => `${i.productName} (${i.quantity})`).join(", ")}</td>
    `;
    tbody.appendChild(row);
  });
}

function renderCustomerAddresses() {
  const tbody = el("customerAddressTable");
  tbody.innerHTML = "";
  if (!customerAddresses.length) {
    tbody.innerHTML = `<tr><td colspan="4" class="empty-state">Belum ada alamat.</td></tr>`;
    return;
  }
  customerAddresses.forEach((a) => {
    const row = document.createElement("tr");
    row.innerHTML = `
      <td>${a.label || "-"}</td>
      <td>${a.addressLine || "-"}</td>
      <td>${a.city || "-"}</td>
      <td>${a.phone || "-"}</td>
    `;
    tbody.appendChild(row);
  });
}

async function loadCustomers() {
  customers = await getJson("/admin/customers");
  await loadAddressesForCustomers();
  renderCustomers();
  const elTotal = document.getElementById("totalCustomer");
  if (elTotal) elTotal.textContent = customers.length;
}

async function loadAddressesForCustomers() {
  customerAddressMap = {};
  await Promise.all(customers.map(async (c) => {
    try {
      const addrs = await getJson(`/admin/customers/${c.userId}/addresses`);
      const pick = addrs.find((a) => a.isDefault) || addrs[0];
      if (pick) customerAddressMap[c.userId] = pick.addressLine;
    } catch (_) {
      customerAddressMap[c.userId] = "-";
    }
  }));
}

async function loadProducts() {
  products = await getJson("/admin/products");
  renderProducts();
}

async function loadStats() {
  const s = await getJson("/admin/stats");
  const elTotal = document.getElementById("totalCustomer");
  if (elTotal) elTotal.textContent = s.totalCustomers ?? 0;
}

(async function init() {
  try {
    await Promise.all([loadCustomers(), loadProducts(), loadStats()]);
  } catch (err) {
    console.error(err);
    const errBox = document.getElementById("masterError");
    if (errBox) {
      errBox.textContent = err?.message || "Gagal memuat data.";
      errBox.classList.remove("d-none");
    }
  }
})();
