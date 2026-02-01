const form = document.getElementById("form");
const nameI = document.getElementById("name");
const emailI = document.getElementById("email");
const phoneI = document.getElementById("phone");
const passI = document.getElementById("password");
const btn = document.getElementById("btn");
const err = document.getElementById("error");

form.addEventListener("submit", async (e) => {
  e.preventDefault();
  hideErr(err);
  btn.disabled = true;

  try {
    await postJson("/auth/register", {
      name: nameI.value.trim(),
      email: emailI.value.trim(),
      phone: phoneI.value.trim(),
      password: passI.value
    });
    location.href = "login-owner.html";
  } catch (ex) {
    showErr(err, ex.message);
  } finally {
    btn.disabled = false;
  }
});
