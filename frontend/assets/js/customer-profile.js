requireRole("CUSTOMER", "login-customer.html");
setGreeting();

const el = (id) => document.getElementById(id);
let addresses = [];

async function loadProfile() {
  const p = await getJson("/customer/me");
  el("profileName").value = p.name || "";
  el("profileEmail").value = p.email || "";
  el("profilePhone").value = p.phone || "";
  el("profileCode").value = p.customerCode || "";
  el("profileCompany").value = p.companyName || "";
}

el("profileForm").addEventListener("submit", async (e) => {
  e.preventDefault();
  const payload = {
    name: el("profileName").value.trim(),
    phone: el("profilePhone").value.trim(),
    companyName: el("profileCompany").value.trim()
  };
  await putJson("/customer/me", payload);
  showAlert("profileAlert", "Profil Customer tersimpan");
});

function resetAddressForm() {
  el("addressId").value = "";
  el("addressLabel").value = "";
  el("addressLine").value = "";
  el("addressRecipient").value = "";
  el("addressPhone").value = "";
  el("addressCity").value = "";
  el("addressProvince").value = "";
  el("addressPostal").value = "";
  el("addressNotes").value = "";
}

function renderAddresses() {
  const tbody = el("addressTable");
  tbody.innerHTML = "";
  if (!addresses.length) {
    tbody.innerHTML = `<tr><td colspan="4" class="text-center text-muted">Belum ada alamat.</td></tr>`;
    return;
  }
  addresses.forEach((a) => {
    const status = a.isDefault ? "Utama" : "-";
    const row = document.createElement("tr");
    row.innerHTML = `
      <td>${a.label || "-"}</td>
      <td>${a.addressLine || "-"}</td>
      <td>${status}</td>
      <td class="text-end">
        <div class="btn-group btn-group-sm" role="group">
          <button class="btn btn-outline-secondary" data-action="edit" data-id="${a.id}">Edit</button>
          <button class="btn btn-outline-primary" data-action="default" data-id="${a.id}">Utama</button>
          <button class="btn btn-outline-danger" data-action="delete" data-id="${a.id}">Hapus</button>
        </div>
      </td>
    `;
    tbody.appendChild(row);
  });
}

async function loadAddresses() {
  addresses = await getJson("/addresses");
  renderAddresses();
}

el("addressForm").addEventListener("submit", async (e) => {
  e.preventDefault();
  const id = el("addressId").value;
  const payload = {
    label: el("addressLabel").value.trim(),
    addressLine: el("addressLine").value.trim(),
    recipientName: el("addressRecipient").value.trim(),
    phone: el("addressPhone").value.trim(),
    city: el("addressCity").value.trim(),
    province: el("addressProvince").value.trim(),
    postalCode: el("addressPostal").value.trim(),
    notes: el("addressNotes").value.trim()
  };
  if (id) {
    await putJson(`/addresses/${id}`, payload);
    showAlert("addressAlert", "Alamat diperbarui");
  } else {
    await postJson("/addresses", payload);
    showAlert("addressAlert", "Alamat tersimpan");
  }
  resetAddressForm();
  await loadAddresses();
});

el("addressCancelBtn").addEventListener("click", () => {
  resetAddressForm();
});

el("addressTable").addEventListener("click", async (e) => {
  const btn = e.target.closest("button");
  if (!btn) return;
  const id = btn.dataset.id;
  const action = btn.dataset.action;
  const addr = addresses.find((a) => `${a.id}` === id);
  if (!addr) return;
  if (action === "edit") {
    el("addressId").value = addr.id;
    el("addressLabel").value = addr.label || "";
    el("addressLine").value = addr.addressLine || "";
    el("addressRecipient").value = addr.recipientName || "";
    el("addressPhone").value = addr.phone || "";
    el("addressCity").value = addr.city || "";
    el("addressProvince").value = addr.province || "";
    el("addressPostal").value = addr.postalCode || "";
    el("addressNotes").value = addr.notes || "";
    return;
  }
  if (action === "default") {
    await postJson(`/addresses/${id}/default`, {});
    showAlert("addressAlert", "Alamat utama diperbarui");
    await loadAddresses();
    return;
  }
  if (action === "delete") {
    await deleteJson(`/addresses/${id}`);
    showAlert("addressAlert", "Alamat dihapus");
    await loadAddresses();
  }
});

(async function init() {
  try {
    await Promise.all([loadProfile(), loadAddresses()]);
  } catch (err) {
    console.error(err);
  }
})();
