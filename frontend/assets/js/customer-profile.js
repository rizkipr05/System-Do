requireRole("CUSTOMER", "login-customer.html");
setGreeting();

const el = (id) => document.getElementById(id);

async function loadProfile() {
  const p = await getJson("/qal/profile/customer");
  el("customerName").value = p.name || "";
  el("customerEmail").value = p.email || "";
  el("customerPhone").value = p.phone || "";
  el("customerCode").value = p.code || "";
}

el("customerProfileForm").addEventListener("submit", async (e) => {
  e.preventDefault();
  const pass = el("customerPassword").value.trim();
  const pass2 = el("customerPasswordConfirm").value.trim();
  if (pass || pass2) {
    if (pass.length < 6) {
      showAlert("customerProfileAlert", "Password minimal 6 karakter");
      return;
    }
    if (pass !== pass2) {
      showAlert("customerProfileAlert", "Konfirmasi password tidak sama");
      return;
    }
  }
  const payload = {
    name: el("customerName").value.trim(),
    email: el("customerEmail").value.trim(),
    phone: el("customerPhone").value.trim(),
    code: el("customerCode").value.trim(),
    password: pass || null
  };
  await putJson("/qal/profile/customer", payload);
  showAlert("customerProfileAlert", "Profil Customer tersimpan");
  el("customerPassword").value = "";
  el("customerPasswordConfirm").value = "";
});

const modal = document.getElementById("customerProfileModal");
if (modal) {
  modal.addEventListener("show.bs.modal", () => {
    loadProfile().catch(console.error);
  });
} else {
  loadProfile().catch(console.error);
}
