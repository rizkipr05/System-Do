requireRole("ADMIN", "login-admin.html");
setAdminGreeting();

const el = (id) => document.getElementById(id);
let customers = [];
let products = [];
let customerOrders = [];
let customerAddresses = [];

function renderCustomers() {
  const tbody = el("customerTable");
  tbody.innerHTML = "";
  if (!customers.length) {
    tbody.innerHTML = `<tr><td colspan="6" class="empty-state">Belum ada customer.</td></tr>`;
    return;
  }
  customers.forEach((c) => {
    const status = c.active ? "status-ok" : "status-danger";
    const row = document.createElement("tr");
    row.innerHTML = `
      <td>${c.name}</td>
      <td>${c.companyName || "-"}</td>
      <td>${c.phone || "-"}</td>
      <td>${c.email}</td>
      <td><span class="status-pill ${status}">${c.active ? "Active" : "Inactive"}</span></td>
      <td class="text-end">
        <button class="btn btn-sm btn-outline-primary" data-action="edit" data-id="${c.userId}">Edit</button>
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
    const status = p.active ? "status-ok" : "status-danger";
    const row = document.createElement("tr");
    row.innerHTML = `
      <td>${p.name}</td>
      <td>${p.sku || "-"}</td>
      <td>${p.unit || "-"}</td>
      <td>${p.price ?? 0}</td>
      <td>${p.stock ?? 0}</td>
      <td><span class="status-pill ${status}">${p.active ? "Active" : "Inactive"}</span></td>
      <td class="text-end">
        <button class="btn btn-sm btn-outline-primary" data-action="edit" data-id="${p.id}">Edit</button>
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
  renderCustomers();
  el("customerSelect").innerHTML = customers.map((c) => `<option value="${c.userId}">${c.name} - ${c.email}</option>`).join("");
}

async function loadProducts() {
  products = await getJson("/admin/products");
  renderProducts();
}

async function loadCustomerDetail(userId) {
  if (!userId) return;
  customerOrders = await getJson(`/admin/customers/${userId}/orders`);
  customerAddresses = await getJson(`/admin/customers/${userId}/addresses`);
  renderCustomerOrders();
  renderCustomerAddresses();
}

el("customerForm").addEventListener("submit", async (e) => {
  e.preventDefault();
  const payload = {
    name: el("custName").value.trim(),
    email: el("custEmail").value.trim(),
    phone: el("custPhone").value.trim(),
    password: el("custPassword").value.trim(),
    companyName: el("custCompany").value.trim(),
    active: el("custActive").checked
  };
  const userId = el("custUserId").value;
  if (userId) {
    await putJson(`/admin/customers/${userId}`, payload);
  } else {
    await postJson("/admin/customers", payload);
  }
  el("customerForm").reset();
  el("custUserId").value = "";
  showAlert("customerAlert", "Customer tersimpan");
  await loadCustomers();
});

el("customerTable").addEventListener("click", (e) => {
  const btn = e.target.closest("button");
  if (!btn) return;
  const userId = btn.dataset.id;
  const cust = customers.find((c) => `${c.userId}` === userId);
  if (!cust) return;
  el("custUserId").value = cust.userId;
  el("custName").value = cust.name || "";
  el("custEmail").value = cust.email || "";
  el("custPhone").value = cust.phone || "";
  el("custCompany").value = cust.companyName || "";
  el("custActive").checked = !!cust.active;
  el("custPassword").value = "";
});

el("productForm").addEventListener("submit", async (e) => {
  e.preventDefault();
  const payload = {
    id: el("productId").value || null,
    name: el("productName").value.trim(),
    sku: el("productSku").value.trim(),
    unit: el("productUnit").value.trim(),
    price: Number(el("productPrice").value || 0),
    stock: Number(el("productStock").value || 0),
    active: el("productActive").checked
  };
  if (payload.id) {
    await putJson(`/admin/products/${payload.id}`, payload);
  } else {
    await postJson("/admin/products", payload);
  }
  el("productForm").reset();
  el("productId").value = "";
  showAlert("productAlert", "Produk tersimpan");
  await loadProducts();
});

el("productTable").addEventListener("click", (e) => {
  const btn = e.target.closest("button");
  if (!btn) return;
  const id = btn.dataset.id;
  const p = products.find((x) => `${x.id}` === id);
  if (!p) return;
  el("productId").value = p.id;
  el("productName").value = p.name || "";
  el("productSku").value = p.sku || "";
  el("productUnit").value = p.unit || "";
  el("productPrice").value = p.price ?? 0;
  el("productStock").value = p.stock ?? 0;
  el("productActive").checked = !!p.active;
});

el("customerSelect").addEventListener("change", (e) => {
  loadCustomerDetail(e.target.value);
});

(async function init() {
  try {
    await Promise.all([loadCustomers(), loadProducts()]);
    if (customers.length) {
      el("customerSelect").value = customers[0].userId;
      await loadCustomerDetail(customers[0].userId);
    }
  } catch (err) {
    console.error(err);
  }
})();
