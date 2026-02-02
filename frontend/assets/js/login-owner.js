const form = document.getElementById("form");
const email = document.getElementById("email");
const password = document.getElementById("password");
const btn = document.getElementById("btn");
const err = document.getElementById("error");

form.addEventListener("submit", async (e) => {
  e.preventDefault();
  hideErr(err);
  btn.disabled = true;

  try {
    const data = await postJson("/auth/login", {
      email: email.value.trim(),
      password: password.value
    });

    if (data.role !== "OWNER") throw new Error("Akun ini bukan Owner.");

    setSession(data.token, data.role, data.user);
    location.href = "owner-dashboard.html";
  } catch (ex) {
    showErr(err, ex.message);
  } finally {
    btn.disabled = false;
  }
});
