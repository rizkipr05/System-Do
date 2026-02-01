requireRole("CUSTOMER", "login-owner.html");
setGreeting();

const el = (id) => document.getElementById(id);

async function loadProfile() {
  const p = await getJson("/qal/profile/owner");
  el("ownerName").value = p.name || "";
  el("ownerEmail").value = p.email || "";
  el("ownerPhone").value = p.phone || "";
  el("ownerCode").value = p.code || "";
}

el("ownerProfileForm").addEventListener("submit", async (e) => {
  e.preventDefault();
  const pass = el("ownerPassword").value.trim();
  const pass2 = el("ownerPasswordConfirm").value.trim();
  if (pass || pass2) {
    if (pass.length < 6) {
      showAlert("ownerProfileAlert", "Password minimal 6 karakter");
      return;
    }
    if (pass !== pass2) {
      showAlert("ownerProfileAlert", "Konfirmasi password tidak sama");
      return;
    }
  }
  const payload = {
    name: el("ownerName").value.trim(),
    email: el("ownerEmail").value.trim(),
    phone: el("ownerPhone").value.trim(),
    code: el("ownerCode").value.trim(),
    password: pass || null
  };
  await putJson("/qal/profile/owner", payload);
  showAlert("ownerProfileAlert", "Profil Owner tersimpan");
  el("ownerPassword").value = "";
  el("ownerPasswordConfirm").value = "";
});

const modal = document.getElementById("ownerProfileModal");
if (modal) {
  modal.addEventListener("show.bs.modal", () => {
    loadProfile().catch(console.error);
  });
} else {
  loadProfile().catch(console.error);
}
