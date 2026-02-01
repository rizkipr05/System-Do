requireRole("ADMIN", "login-quality-control.html");
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
  const payload = {
    name: el("qcName").value.trim(),
    phone: el("qcPhone").value.trim(),
    code: el("qcCode").value.trim(),
    position: el("qcPosition").value.trim()
  };
  await putJson("/qal/profile/qc", payload);
  showAlert("qcProfileAlert", "Profil QC tersimpan");
});

loadProfile().catch(console.error);
