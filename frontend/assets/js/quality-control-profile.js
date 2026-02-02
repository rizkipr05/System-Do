requireRole("QUALITY_CONTROL", "login-quality-control.html");
setAdminGreeting();

const el = (id) => document.getElementById(id);

async function loadProfile() {
  const p = await getJson("/qal/profile/qc");
  el("qcName").value = p.name || "";
  el("qcEmail").value = p.email || "";
  el("qcPhone").value = p.phone || "";
  el("qcCode").value = p.code || "";
  el("qcPosition").value = p.position || "";
}

el("qcProfileForm").addEventListener("submit", async (e) => {
  e.preventDefault();
  const pass = el("qcPassword").value.trim();
  const pass2 = el("qcPasswordConfirm").value.trim();
  if (pass || pass2) {
    if (pass.length < 6) {
      showAlert("qcProfileAlert", "Password minimal 6 karakter");
      return;
    }
    if (pass !== pass2) {
      showAlert("qcProfileAlert", "Konfirmasi password tidak sama");
      return;
    }
  }
  const payload = {
    name: el("qcName").value.trim(),
    email: el("qcEmail").value.trim(),
    phone: el("qcPhone").value.trim(),
    code: el("qcCode").value.trim(),
    position: el("qcPosition").value.trim(),
    password: pass || null
  };
  await putJson("/qal/profile/qc", payload);
  showAlert("qcProfileAlert", "Profil QC tersimpan");
  el("qcPassword").value = "";
  el("qcPasswordConfirm").value = "";
});

const modal = document.getElementById("qcProfileModal");
if (modal) {
  modal.addEventListener("show.bs.modal", () => {
    loadProfile().catch(console.error);
  });
} else {
  loadProfile().catch(console.error);
}
