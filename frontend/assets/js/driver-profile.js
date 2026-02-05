requireRole("DRIVER", "login-driver.html");
setDriverGreeting();

const el = (id) => document.getElementById(id);

async function loadProfile() {
  const p = await getJson("/qal/profile/driver");
  el("driverName").value = p.name || "";
  el("driverEmail").value = p.email || "";
  el("driverPhone").value = p.phone || "";
  el("driverCode").value = p.code || "";
}

el("driverProfileForm").addEventListener("submit", async (e) => {
  e.preventDefault();
  const pass = el("driverPassword").value.trim();
  const pass2 = el("driverPasswordConfirm").value.trim();
  if (pass || pass2) {
    if (pass.length < 6) {
      showAlert("driverProfileAlert", "Password minimal 6 karakter");
      return;
    }
    if (pass !== pass2) {
      showAlert("driverProfileAlert", "Konfirmasi password tidak sama");
      return;
    }
  }
  const payload = {
    name: el("driverName").value.trim(),
    email: el("driverEmail").value.trim(),
    phone: el("driverPhone").value.trim(),
    code: el("driverCode").value.trim(),
    password: pass || null
  };
  await putJson("/qal/profile/driver", payload);
  showAlert("driverProfileAlert", "Profil Driver tersimpan");
  el("driverPassword").value = "";
  el("driverPasswordConfirm").value = "";
});

const modal = document.getElementById("driverProfileModal");
if (modal) {
  modal.addEventListener("show.bs.modal", () => {
    loadProfile().catch(console.error);
  });
} else {
  loadProfile().catch(console.error);
}
