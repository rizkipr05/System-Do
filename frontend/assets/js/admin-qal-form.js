requireRole("ADMIN", "login-admin.html");
setAdminGreeting();

const el = (id) => document.getElementById(id);
let customers = [];
let products = [];
let addresses = [];
let cart = [];
const customerIdMap = new Map();

function renderProducts() {
  const select = el("orderProduct");
  if (!products.length) {
    select.innerHTML = `<option value="">Belum ada produk</option>`;
    return;
  }
  select.innerHTML = products.map((p) => {
    const price = p.price ? `Rp${Number(p.price).toLocaleString("id-ID")}` : "";
    return `<option value="${p.id}">${p.name} ${price ? `- ${price}` : ""}</option>`;
  }).join("");
}

function renderAddresses() {
  const select = el("orderAddress");
  if (!addresses.length) {
    select.innerHTML = `<option value="">Belum ada alamat</option>`;
    return;
  }
  select.innerHTML = addresses.map((a) => `<option value="${a.id}">${a.label} - ${a.addressLine}</option>`).join("");
}

function renderCart() {
  const tbody = el("orderItems");
  tbody.innerHTML = "";
  if (!cart.length) {
    tbody.innerHTML = `<tr><td colspan="4" class="text-center text-muted">Belum ada item.</td></tr>`;
    return;
  }
  cart.forEach((item, idx) => {
    const tr = document.createElement("tr");
    tr.innerHTML = `
      <td>${item.name}</td>
      <td>${item.quantity}</td>
      <td>${item.unit || "-"}</td>
      <td class="text-end">
        <button class="btn btn-sm btn-outline-danger" data-index="${idx}">Hapus</button>
      </td>
    `;
    tbody.appendChild(tr);
  });
}

async function loadCustomers() {
  customers = await getJson("/admin/customers");
  customerIdMap.clear();
  customers.forEach((c) => customerIdMap.set(String(c.userId), c.customerId));
  el("orderCustomer").innerHTML = customers.map((c) => {
    return `<option value="${c.userId}" data-customer-id="${c.customerId}">${c.name} (${c.email})</option>`;
  }).join("");
}

async function loadProducts() {
  products = await getJson("/admin/products");
  renderProducts();
}

async function loadAddressesByUser(userId) {
  if (!userId) return;
  addresses = await getJson(`/admin/customers/${userId}/addresses`);
  renderAddresses();
}

el("orderCustomer").addEventListener("change", async (e) => {
  await loadAddressesByUser(e.target.value);
});

el("addItemBtn").addEventListener("click", (e) => {
  e.preventDefault();
  const productId = el("orderProduct").value;
  const qty = Number(el("orderQty").value || 0);
  const product = products.find((p) => `${p.id}` === productId);
  const err = el("formError");
  err.classList.add("d-none");
  if (!product) {
    err.textContent = "Pilih produk terlebih dahulu.";
    err.classList.remove("d-none");
    return;
  }
  if (qty <= 0) {
    err.textContent = "Jumlah harus lebih dari 0.";
    err.classList.remove("d-none");
    return;
  }
  const existing = cart.find((c) => c.productId === product.id);
  if (existing) {
    existing.quantity += qty;
  } else {
    cart.push({
      productId: product.id,
      name: product.name,
      unit: product.unit,
      quantity: qty
    });
  }
  renderCart();
});

el("orderItems").addEventListener("click", (e) => {
  const btn = e.target.closest("button");
  if (!btn) return;
  const idx = Number(btn.dataset.index);
  cart.splice(idx, 1);
  renderCart();
});

el("orderForm").addEventListener("submit", async (e) => {
  e.preventDefault();
  const err = el("formError");
  const ok = el("formSuccess");
  err.classList.add("d-none");
  ok.classList.add("d-none");

  if (!cart.length) {
    err.textContent = "Item order wajib diisi.";
    err.classList.remove("d-none");
    return;
  }
  const customerSelect = el("orderCustomer");
  const userId = customerSelect.value;
  const customerId = customerIdMap.get(String(userId));
  if (!customerId) {
    err.textContent = "Customer wajib dipilih.";
    err.classList.remove("d-none");
    return;
  }
  const addressId = el("orderAddress").value;
  if (!addressId) {
    err.textContent = "Alamat wajib dipilih.";
    err.classList.remove("d-none");
    return;
  }

  const payload = {
    customerId: Number(customerId),
    addressId: Number(addressId),
    note: el("orderNote").value.trim(),
    items: cart.map((c) => ({ productId: c.productId, quantity: c.quantity }))
  };

  try {
    await postJson("/admin/orders/manual", payload);
    ok.textContent = "DO manual berhasil dibuat.";
    ok.classList.remove("d-none");
    cart = [];
    renderCart();
    el("orderNote").value = "";
    await loadAddressesByUser(userId);
  } catch (ex) {
    err.textContent = ex?.message || "Gagal membuat DO.";
    err.classList.remove("d-none");
  }
});

(async function init() {
  try {
    await loadCustomers();
    await loadProducts();
    const first = el("orderCustomer").value;
    if (first) await loadAddressesByUser(first);
    renderCart();
  } catch (err) {
    console.error(err);
  }
})();
