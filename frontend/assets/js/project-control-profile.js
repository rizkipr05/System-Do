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
  const payload = {
    name: el("pcName").value.trim(),
    phone: el("pcPhone").value.trim(),
    code: el("pcCode").value.trim()
  };
  await putJson("/qal/profile/pc", payload);
  showAlert("pcProfileAlert", "Profil Project Control tersimpan");
});

loadProfile().catch(console.error);
