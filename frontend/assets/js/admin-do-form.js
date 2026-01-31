requireRole("ADMIN", "login-admin.html");
setAdminGreeting();

const el = (id) => document.getElementById(id);
let customers = [];
let addresses = [];
let products = [];
let cart = [];

function renderCart() {
  const tbody = el("cartTable");
  tbody.innerHTML = "";
  if (!cart.length) {
    tbody.innerHTML = `<tr><td colspan="4" class="text-muted">Belum ada item.</td></tr>`;
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

async function loadCustomers() {
  customers = await getJson("/admin/customers");
  el("orderCustomerSelect").innerHTML = customers.map((c) => {
    return `<option value="${c.userId}" data-customer-id="${c.customerId}">${c.name}</option>`;
  }).join("");
  if (customers.length) await loadAddresses(customers[0].userId);
}

async function loadAddresses(userId) {
  addresses = await getJson(`/admin/customers/${userId}/addresses`);
  if (!addresses.length) {
    el("orderAddressSelect").innerHTML = `<option value="">Belum ada alamat</option>`;
    return;
  }
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

el("addAddressBtn").addEventListener("click", async () => {
  const customerId = Number(el("orderCustomerSelect").value);
  if (!customerId) return;
  const payload = {
    label: el("addrLabel").value.trim(),
    recipientName: el("addrRecipient").value.trim(),
    phone: el("addrPhone").value.trim(),
    addressLine: el("addrLine").value.trim(),
    city: el("addrCity").value.trim(),
    province: el("addrProvince").value.trim(),
    postalCode: el("addrPostal").value.trim(),
    notes: el("addrNotes").value.trim()
  };
  if (!payload.addressLine) return;
  const created = await postJson(`/admin/customers/${customerId}/addresses`, payload);
  await loadAddresses(customerId);
  if (created?.id) el("orderAddressSelect").value = created.id;
  el("addrLabel").value = "";
  el("addrRecipient").value = "";
  el("addrPhone").value = "";
  el("addrLine").value = "";
  el("addrCity").value = "";
  el("addrProvince").value = "";
  el("addrPostal").value = "";
  el("addrNotes").value = "";
});

el("cartTable").addEventListener("click", (e) => {
  const btn = e.target.closest("button");
  if (!btn) return;
  const idx = Number(btn.dataset.index);
  cart.splice(idx, 1);
  renderCart();
});

el("createOrderBtn").addEventListener("click", async () => {
  const errBox = el("orderError");
  errBox.classList.add("d-none");
  if (!cart.length) return;
  const payload = {
    customerId: Number(el("orderCustomerSelect").selectedOptions[0]?.dataset.customerId || 0),
    addressId: Number(el("orderAddressSelect").value),
    note: el("orderNote").value.trim(),
    items: cart.map((c) => ({ productId: c.productId, quantity: c.quantity }))
  };
  if (!payload.customerId || !payload.addressId) {
    errBox.textContent = "Customer dan alamat wajib dipilih.";
    errBox.classList.remove("d-none");
    return;
  }
  await postJson("/admin/orders/manual", payload);
  cart = [];
  renderCart();
  el("orderNote").value = "";
  showAlert("orderAlert", "DO berhasil dibuat");
  setTimeout(() => {
    location.href = "admin-do.html";
  }, 800);
});

(async function init() {
  try {
    await Promise.all([loadCustomers(), loadProducts()]);
    renderCart();
  } catch (err) {
    console.error(err);
  }
})();
