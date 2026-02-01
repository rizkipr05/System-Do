requireRole("ADMIN", "login-quality-control.html");
setAdminGreeting();

const el = (id) => document.getElementById(id);

async function loadCustomers() {
  const customers = await getJson("/admin/customers");
  el("addressCustomer").innerHTML = customers.map((c) => {
    return `<option value="${c.userId}" data-customer-id="${c.customerId}">${c.name}</option>`;
  }).join("");
}

el("addressForm").addEventListener("submit", async (e) => {
  e.preventDefault();
  const userId = el("addressCustomer").value;
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
  await postJson(`/admin/customers/${userId}/addresses`, payload);
  showAlert("addressAlert", "Alamat tersimpan");
  setTimeout(() => {
    location.href = "quality-control-master.html";
  }, 800);
});

loadCustomers().catch(console.error);
