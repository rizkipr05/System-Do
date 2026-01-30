const API_BASE = "http://localhost:8081/api";

async function postJson(path, body) {
  const res = await fetch(`${API_BASE}${path}`, {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify(body),
  });

  let data = null;
  try { data = await res.json(); } catch (_) {}

  if (!res.ok) {
    throw new Error(data?.message || `Request gagal (${res.status})`);
  }
  return data;
}

async function getJson(path) {
  const token = localStorage.getItem("token");
  const res = await fetch(`${API_BASE}${path}`, {
    method: "GET",
    headers: {
      "Content-Type": "application/json",
      ...(token ? { "Authorization": `Bearer ${token}` } : {})
    }
  });

  let data = null;
  try { data = await res.json(); } catch (_) {}

  if (!res.ok) {
    throw new Error(data?.message || `Request gagal (${res.status})`);
  }
  return data;
}

function setSession(token, role, user) {
  localStorage.setItem("token", token);
  localStorage.setItem("role", role);
  localStorage.setItem("user", JSON.stringify(user || {}));
}

function logout(to) {
  localStorage.clear();
  location.href = to;
}

function showErr(el, msg) {
  el.textContent = msg;
  el.style.display = "block";
}
function hideErr(el) {
  el.textContent = "";
  el.style.display = "none";
}
