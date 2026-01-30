requireRole("CUSTOMER", "login-customer.html");
setGreeting();

const el = (id) => document.getElementById(id);
let addresses = [];

function renderAddresses() {
  const tbody = el("addressTable");
  tbody.innerHTML = "";
  if (!addresses.length) {
    tbody.innerHTML = `<tr><td colspan="4" class="empty-state">Belum ada alamat.</td></tr>`;
    return;
  }
  addresses.forEach((a) => {
    const badge = a.isDefault ? `<span class="status-pill status-ok">Default</span>` : "";
    const row = document.createElement("tr");
    row.innerHTML = `
      <td>${a.label || "-"}</td>
      <td>${a.addressLine || "-"}<div class="text-muted small">${a.city || ""} ${a.province || ""} ${a.postalCode || ""}</div></td>
      <td>${badge}</td>
      <td class="text-end">
        <button class="btn btn-sm btn-outline-primary me-1" data-action="edit" data-id="${a.id}">Edit</button>
        <button class="btn btn-sm btn-outline-secondary me-1" data-action="default" data-id="${a.id}">Default</button>
        <button class="btn btn-sm btn-outline-danger" data-action="delete" data-id="${a.id}">Hapus</button>
      </td>
    `;
    tbody.appendChild(row);
  });
}

async function loadProfile() {
  const data = await getJson("/customer/me");
  el("profileName").value = data.name || "";
  el("profileEmail").value = data.email || "";
  el("profilePhone").value = data.phone || "";
  el("profileCode").value = data.customerCode || "";
  el("profileCompany").value = data.companyName || "";
}

async function loadAddresses() {
  addresses = await getJson("/addresses");
  renderAddresses();
}

el("profileForm").addEventListener("submit", async (e) => {
  e.preventDefault();
  const payload = {
    name: el("profileName").value.trim(),
    phone: el("profilePhone").value.trim(),
    companyName: el("profileCompany").value.trim()
  };
  await putJson("/customer/me", payload);
  showAlert("profileAlert", "Profil berhasil disimpan");
});

el("addressForm").addEventListener("submit", async (e) => {
  e.preventDefault();
  const payload = {
    id: el("addressId").value || null,
    label: el("addressLabel").value.trim(),
    recipientName: el("addressRecipient").value.trim(),
    phone: el("addressPhone").value.trim(),
    addressLine: el("addressLine").value.trim(),
    city: el("addressCity").value.trim(),
    province: el("addressProvince").value.trim(),
    postalCode: el("addressPostal").value.trim(),
    notes: el("addressNotes").value.trim(),
    isDefault: false
  };
  if (payload.id) {
    await putJson(`/addresses/${payload.id}`, payload);
  } else {
    await postJson("/addresses", payload);
  }
  el("addressForm").reset();
  el("addressId").value = "";
  showAlert("addressAlert", "Alamat tersimpan");
  await loadAddresses();
});

el("addressCancelBtn").addEventListener("click", () => {
  el("addressForm").reset();
  el("addressId").value = "";
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
    el("addressRecipient").value = addr.recipientName || "";
    el("addressPhone").value = addr.phone || "";
    el("addressLine").value = addr.addressLine || "";
    el("addressCity").value = addr.city || "";
    el("addressProvince").value = addr.province || "";
    el("addressPostal").value = addr.postalCode || "";
    el("addressNotes").value = addr.notes || "";
  } else if (action === "delete") {
    await deleteJson(`/addresses/${addr.id}`);
    await loadAddresses();
  } else if (action === "default") {
    await postJson(`/addresses/${addr.id}/default`, {});
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
