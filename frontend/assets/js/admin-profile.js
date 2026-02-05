requireRole("ADMIN", "login-admin.html");
setAdminGreeting();

const el = (id) => document.getElementById(id);

async function loadProfile() {
  const p = await getJson("/qal/profile/admin");
  el("adminName").value = p.name || "";
  el("adminEmail").value = p.email || "";
  el("adminPhone").value = p.phone || "";
  el("adminCode").value = p.code || "";
  el("adminPosition").value = p.position || "";
}

el("adminProfileForm").addEventListener("submit", async (e) => {
  e.preventDefault();
  const pass = el("adminPassword").value.trim();
  const pass2 = el("adminPasswordConfirm").value.trim();
  if (pass || pass2) {
    if (pass.length < 6) {
      showAlert("adminProfileAlert", "Password minimal 6 karakter");
      return;
    }
    if (pass !== pass2) {
      showAlert("adminProfileAlert", "Konfirmasi password tidak sama");
      return;
    }
  }
  const payload = {
    name: el("adminName").value.trim(),
    email: el("adminEmail").value.trim(),
    phone: el("adminPhone").value.trim(),
    code: el("adminCode").value.trim(),
    position: el("adminPosition").value.trim(),
    password: pass || null
  };
  await putJson("/qal/profile/admin", payload);
  showAlert("adminProfileAlert", "Profil Admin tersimpan");
  el("adminPassword").value = "";
  el("adminPasswordConfirm").value = "";
});

const modal = document.getElementById("adminProfileModal");
if (modal) {
  modal.addEventListener("show.bs.modal", () => {
    loadProfile().catch(console.error);
  });
} else {
  loadProfile().catch(console.error);
}
