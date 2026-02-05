requireRole("CUSTOMER", "login-customer.html");
setGreeting();

const el = (id) => document.getElementById(id);
let addresses = [];
let products = [];
let cart = [];

function renderAddressOptions() {
  const opts = addresses.map((a) => `<option value="${a.id}">${a.label} - ${a.addressLine}</option>`).join("");
  el("orderAddressSelect").innerHTML = opts || `<option value="">Belum ada alamat</option>`;
}

function renderProducts() {
  if (!products.length) {
    el("productSelect").innerHTML = `<option value="">Belum ada produk</option>`;
    return;
  }
  el("productSelect").innerHTML = products.map((p) => {
    const price = p.price ? `Rp${Number(p.price).toLocaleString("id-ID")}` : "";
    return `<option value="${p.id}">${p.name} ${price ? `- ${price}` : ""}</option>`;
  }).join("");
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
      <td class="text-end">
        <button class="btn btn-sm btn-outline-danger" data-index="${idx}">Hapus</button>
      </td>
    `;
    tbody.appendChild(row);
  });
}

async function loadAddresses() {
  addresses = await getJson("/addresses");
  renderAddressOptions();
}

async function loadProducts() {
  products = await getJson("/products");
  renderProducts();
}

el("addItemBtn").addEventListener("click", (e) => {
  e.preventDefault();
  const productId = el("productSelect").value;
  const qty = Number(el("productQty").value || 0);
  const product = products.find((p) => `${p.id}` === productId);
  if (!product) {
    showAlert("orderAlert", "Pilih produk terlebih dahulu.", "danger");
    return;
  }
  if (qty <= 0) {
    showAlert("orderAlert", "Jumlah barang harus lebih dari 0.", "danger");
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

el("cartTable").addEventListener("click", (e) => {
  const btn = e.target.closest("button");
  if (!btn) return;
  const idx = Number(btn.dataset.index);
  cart.splice(idx, 1);
  renderCart();
});

el("submitOrderBtn").addEventListener("click", async () => {
  if (!cart.length) {
    showAlert("orderAlert", "Tambahkan barang terlebih dahulu.", "danger");
    return;
  }
  if (!el("orderAddressSelect").value) {
    showAlert("orderAlert", "Pilih alamat pengiriman.", "danger");
    return;
  }
  const payload = {
    addressId: el("orderAddressSelect").value,
    note: el("orderNote").value.trim(),
    items: cart.map((c) => ({ productId: c.productId, quantity: c.quantity }))
  };
  await postJson("/orders", payload);
  cart = [];
  renderCart();
  el("orderNote").value = "";
  showAlert("orderAlert", "Order berhasil dibuat");
});

(async function init() {
  try {
    await Promise.all([loadAddresses(), loadProducts()]);
    renderCart();
  } catch (err) {
    console.error(err);
  }
})();
