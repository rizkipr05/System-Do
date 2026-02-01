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
  const payload = {
    name: el("ownerName").value.trim(),
    phone: el("ownerPhone").value.trim(),
    code: el("ownerCode").value.trim()
  };
  await putJson("/qal/profile/owner", payload);
  showAlert("ownerProfileAlert", "Profil Owner tersimpan");
});

loadProfile().catch(console.error);
