requireRole("ADMIN", "login-admin.html");
setAdminGreeting();

const el = (id) => document.getElementById(id);

function formatDate(value) {
  if (!value) return "-";
  const d = new Date(value);
  return d.toLocaleString("id-ID");
}

async function loadCustomers() {
  const list = await getJson("/admin/customers");
  const tbody = el("customerTable");
  tbody.innerHTML = "";
  if (!list.length) {
    tbody.innerHTML = `<tr><td colspan="6" class="text-center text-muted">Belum ada customer.</td></tr>`;
    return;
  }
  list.forEach((c) => {
    const tr = document.createElement("tr");
    tr.innerHTML = `
      <td>${c.name || "-"}</td>
      <td>${c.email || "-"}</td>
      <td>${c.phone || "-"}</td>
      <td>${c.companyName || "-"}</td>
      <td>${c.active ? "Aktif" : "Nonaktif"}</td>
      <td class="text-end">
        <a class="btn btn-sm btn-outline-secondary" href="admin-master-form.html?tab=customer&userId=${c.userId}">Edit</a>
        <a class="btn btn-sm btn-outline-primary" href="admin-address-form.html?userId=${c.userId}">Alamat</a>
        <button class="btn btn-sm btn-outline-success" data-action="orders" data-id="${c.userId}">Riwayat</button>
      </td>
    `;
    tbody.appendChild(tr);
  });

  const select = el("orderCustomerSelect");
  select.innerHTML = list.map((c) => `<option value="${c.userId}">${c.name} (${c.email})</option>`).join("");
}

async function loadProducts() {
  const list = await getJson("/admin/products");
  const tbody = el("productTable");
  tbody.innerHTML = "";
  if (!list.length) {
    tbody.innerHTML = `<tr><td colspan="7" class="text-center text-muted">Belum ada produk.</td></tr>`;
    return;
  }
  list.forEach((p) => {
    const tr = document.createElement("tr");
    const price = p.price ? Number(p.price).toLocaleString("id-ID") : "0";
    tr.innerHTML = `
      <td>${p.sku || "-"}</td>
      <td>${p.name || "-"}</td>
      <td>${p.unit || "-"}</td>
      <td>Rp${price}</td>
      <td>${p.stock ?? 0}</td>
      <td>${p.active ? "Aktif" : "Nonaktif"}</td>
      <td class="text-end">
        <a class="btn btn-sm btn-outline-secondary" href="admin-master-form.html?tab=product&productId=${p.id}">Edit</a>
      </td>
    `;
    tbody.appendChild(tr);
  });
}

async function loadCustomerOrders(userId) {
  const tbody = el("customerOrderTable");
  tbody.innerHTML = "";
  if (!userId) {
    tbody.innerHTML = `<tr><td colspan="5" class="text-center text-muted">Pilih customer.</td></tr>`;
    return;
  }
  const list = await getJson(`/admin/customers/${userId}/orders`);
  if (!list.length) {
    tbody.innerHTML = `<tr><td colspan="5" class="text-center text-muted">Belum ada order.</td></tr>`;
    return;
  }
  list.forEach((o) => {
    const items = (o.items || []).map((i) => `${i.productName} (${i.quantity})`).join(", ");
    const tr = document.createElement("tr");
    tr.innerHTML = `
      <td>${o.doNumber || o.id}</td>
      <td>${formatDate(o.createdAt)}</td>
      <td>${o.status || "-"}</td>
      <td>${o.addressLine || "-"}</td>
      <td>${items || "-"}</td>
    `;
    tbody.appendChild(tr);
  });
}

el("orderReloadBtn").addEventListener("click", async () => {
  await loadCustomerOrders(el("orderCustomerSelect").value);
});

el("customerTable").addEventListener("click", async (e) => {
  const btn = e.target.closest("button");
  if (!btn) return;
  if (btn.dataset.action === "orders") {
    const userId = btn.dataset.id;
    el("orderCustomerSelect").value = userId;
    await loadCustomerOrders(userId);
  }
});

Promise.all([loadCustomers(), loadProducts()]).then(() => {
  const selected = el("orderCustomerSelect").value;
  if (selected) loadCustomerOrders(selected).catch(console.error);
}).catch(console.error);
