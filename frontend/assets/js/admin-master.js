requireRole("ADMIN", "login-admin.html");
setAdminGreeting();

const el = (id) => document.getElementById(id);

async function loadUsers() {
  const adminUsers = await getJson("/qal/users/ADMIN");
  const driverUsers = await getJson("/qal/users/DRIVER");
  const customerUsers = await getJson("/qal/users/CUSTOMER");
  el("adminUser").innerHTML = adminUsers.map((u) => `<option value="${u.id}">${u.name} (${u.email})</option>`).join("");
  el("driverUser").innerHTML = driverUsers.map((u) => `<option value="${u.id}">${u.name} (${u.email})</option>`).join("");
  el("customerUser").innerHTML = customerUsers.map((u) => `<option value="${u.id}">${u.name} (${u.email})</option>`).join("");
}

async function loadSpk() {
  const list = await getJson("/qal/spk");
  const tbody = el("spkTable");
  tbody.innerHTML = "";
  if (!list.length) {
    tbody.innerHTML = `<tr><td colspan="2" class="text-center text-muted">Belum ada SPK.</td></tr>`;
    return;
  }
  list.forEach((s) => {
    const tr = document.createElement("tr");
    tr.innerHTML = `<td>${s.spkNumber}</td><td>${s.jobName}</td>`;
    tbody.appendChild(tr);
  });
}

async function loadQc() {
  const list = await getJson("/qal/masters/admin");
  const tbody = el("adminTable");
  tbody.innerHTML = "";
  if (!list.length) {
    tbody.innerHTML = `<tr><td colspan="3" class="text-center text-muted">Belum ada Admin.</td></tr>`;
    return;
  }
  list.forEach((q) => {
    const tr = document.createElement("tr");
    tr.innerHTML = `<td>${q.adminCode}</td><td>${q.name}</td><td>${q.position}</td>`;
    tbody.appendChild(tr);
  });
}

async function loadCustomer() {
  const list = await getJson("/qal/masters/customer");
  const tbody = el("customerTable");
  tbody.innerHTML = "";
  if (!list.length) {
    tbody.innerHTML = `<tr><td colspan="2" class="text-center text-muted">Belum ada Customer.</td></tr>`;
    return;
  }
  list.forEach((o) => {
    const tr = document.createElement("tr");
    tr.innerHTML = `<td>${o.customerCode}</td><td>${o.name}</td>`;
    tbody.appendChild(tr);
  });
}

async function loadPc() {
  const list = await getJson("/qal/masters/driver");
  const tbody = el("driverTable");
  tbody.innerHTML = "";
  if (!list.length) {
    tbody.innerHTML = `<tr><td colspan="2" class="text-center text-muted">Belum ada Driver.</td></tr>`;
    return;
  }
  list.forEach((p) => {
    const tr = document.createElement("tr");
    tr.innerHTML = `<td>${p.driverCode}</td><td>${p.name}</td>`;
    tbody.appendChild(tr);
  });
}

el("spkForm").addEventListener("submit", async (e) => {
  e.preventDefault();
  const err = el("spkError");
  err.classList.add("d-none");
  const payload = {
    spkNumber: el("spkNumber").value.trim(),
    jobName: el("spkJob").value.trim()
  };
  try {
    await postJson("/qal/spk", payload);
    el("spkForm").reset();
    await loadSpk();
  } catch (ex) {
    err.textContent = ex?.message || "Gagal menyimpan SPK.";
    err.classList.remove("d-none");
  }
});

el("adminForm").addEventListener("submit", async (e) => {
  e.preventDefault();
  const err = el("adminError");
  err.classList.add("d-none");
  const payload = {
    userId: Number(el("adminUser").value),
    adminCode: el("adminCode").value.trim(),
    position: el("adminPosition").value.trim()
  };
  try {
    await postJson("/qal/masters/admin", payload);
    el("adminForm").reset();
    await loadQc();
  } catch (ex) {
    err.textContent = ex?.message || "Gagal menyimpan Admin.";
    err.classList.remove("d-none");
  }
});

el("customerForm").addEventListener("submit", async (e) => {
  e.preventDefault();
  const err = el("customerError");
  err.classList.add("d-none");
  const payload = {
    userId: Number(el("customerUser").value),
    customerCode: el("customerCode").value.trim()
  };
  try {
    await postJson("/qal/masters/customer", payload);
    el("customerForm").reset();
    await loadCustomer();
  } catch (ex) {
    err.textContent = ex?.message || "Gagal menyimpan Customer.";
    err.classList.remove("d-none");
  }
});

el("driverForm").addEventListener("submit", async (e) => {
  e.preventDefault();
  const err = el("driverError");
  err.classList.add("d-none");
  const payload = {
    userId: Number(el("driverUser").value),
    driverCode: el("driverCode").value.trim()
  };
  try {
    await postJson("/qal/masters/driver", payload);
    el("driverForm").reset();
    await loadPc();
  } catch (ex) {
    err.textContent = ex?.message || "Gagal menyimpan Driver.";
    err.classList.remove("d-none");
  }
});

Promise.all([loadUsers(), loadSpk(), loadQc(), loadCustomer(), loadPc()]).catch(console.error);
