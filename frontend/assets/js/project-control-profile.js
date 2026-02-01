requireRole("DRIVER", "login-project-control.html");
setDriverGreeting();

const el = (id) => document.getElementById(id);

async function loadProfile() {
  const p = await getJson("/qal/profile/pc");
  el("pcName").value = p.name || "";
  el("pcEmail").value = p.email || "";
  el("pcPhone").value = p.phone || "";
  el("pcCode").value = p.code || "";
}

el("pcProfileForm").addEventListener("submit", async (e) => {
  e.preventDefault();
  const pass = el("pcPassword").value.trim();
  const pass2 = el("pcPasswordConfirm").value.trim();
  if (pass || pass2) {
    if (pass.length < 6) {
      showAlert("pcProfileAlert", "Password minimal 6 karakter");
      return;
    }
    if (pass !== pass2) {
      showAlert("pcProfileAlert", "Konfirmasi password tidak sama");
      return;
    }
  }
  const payload = {
    name: el("pcName").value.trim(),
    email: el("pcEmail").value.trim(),
    phone: el("pcPhone").value.trim(),
    code: el("pcCode").value.trim(),
    password: pass || null
  };
  await putJson("/qal/profile/pc", payload);
  showAlert("pcProfileAlert", "Profil Project Control tersimpan");
  el("pcPassword").value = "";
  el("pcPasswordConfirm").value = "";
});

const modal = document.getElementById("pcProfileModal");
if (modal) {
  modal.addEventListener("show.bs.modal", () => {
    loadProfile().catch(console.error);
  });
} else {
  loadProfile().catch(console.error);
}
