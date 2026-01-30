const API_BASE = "http://localhost:8081/api";

function authHeaders() {
  const token = localStorage.getItem("token");
  return token ? { "Authorization": `Bearer ${token}` } : {};
}

async function requestJson(path, method, body) {
  const res = await fetch(`${API_BASE}${path}`, {
    method,
    headers: {
      "Content-Type": "application/json",
      ...authHeaders(),
    },
    body: body === undefined ? undefined : JSON.stringify(body),
  });

  let data = null;
  try { data = await res.json(); } catch (_) {}

  if (!res.ok) {
    throw new Error(data?.message || `Request gagal (${res.status})`);
  }
  return data;
}

async function postJson(path, body) {
  return requestJson(path, "POST", body);
}

async function putJson(path, body) {
  return requestJson(path, "PUT", body);
}

async function deleteJson(path) {
  return requestJson(path, "DELETE");
}

async function getJson(path) {
  const res = await fetch(`${API_BASE}${path}`, {
    method: "GET",
    headers: {
      "Content-Type": "application/json",
      ...authHeaders()
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
