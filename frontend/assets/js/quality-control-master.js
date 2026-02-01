requireRole("ADMIN", "login-quality-control.html");
setAdminGreeting();

const el = (id) => document.getElementById(id);

async function loadUsers() {
  const qcUsers = await getJson("/qal/users/ADMIN");
  const pcUsers = await getJson("/qal/users/DRIVER");
  const ownerUsers = await getJson("/qal/users/CUSTOMER");
  el("qcUser").innerHTML = qcUsers.map((u) => `<option value="${u.id}">${u.name} (${u.email})</option>`).join("");
  el("pcUser").innerHTML = pcUsers.map((u) => `<option value="${u.id}">${u.name} (${u.email})</option>`).join("");
  el("ownerUser").innerHTML = ownerUsers.map((u) => `<option value="${u.id}">${u.name} (${u.email})</option>`).join("");
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
  const list = await getJson("/qal/masters/qc");
  const tbody = el("qcTable");
  tbody.innerHTML = "";
  if (!list.length) {
    tbody.innerHTML = `<tr><td colspan="3" class="text-center text-muted">Belum ada QC.</td></tr>`;
    return;
  }
  list.forEach((q) => {
    const tr = document.createElement("tr");
    tr.innerHTML = `<td>${q.qcCode}</td><td>${q.name}</td><td>${q.position}</td>`;
    tbody.appendChild(tr);
  });
}

async function loadOwner() {
  const list = await getJson("/qal/masters/owner");
  const tbody = el("ownerTable");
  tbody.innerHTML = "";
  if (!list.length) {
    tbody.innerHTML = `<tr><td colspan="2" class="text-center text-muted">Belum ada Owner.</td></tr>`;
    return;
  }
  list.forEach((o) => {
    const tr = document.createElement("tr");
    tr.innerHTML = `<td>${o.ownerCode}</td><td>${o.name}</td>`;
    tbody.appendChild(tr);
  });
}

async function loadPc() {
  const list = await getJson("/qal/masters/pc");
  const tbody = el("pcTable");
  tbody.innerHTML = "";
  if (!list.length) {
    tbody.innerHTML = `<tr><td colspan="2" class="text-center text-muted">Belum ada Project Control.</td></tr>`;
    return;
  }
  list.forEach((p) => {
    const tr = document.createElement("tr");
    tr.innerHTML = `<td>${p.pcCode}</td><td>${p.name}</td>`;
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

el("qcForm").addEventListener("submit", async (e) => {
  e.preventDefault();
  const err = el("qcError");
  err.classList.add("d-none");
  const payload = {
    userId: Number(el("qcUser").value),
    qcCode: el("qcCode").value.trim(),
    position: el("qcPosition").value.trim()
  };
  try {
    await postJson("/qal/masters/qc", payload);
    el("qcForm").reset();
    await loadQc();
  } catch (ex) {
    err.textContent = ex?.message || "Gagal menyimpan QC.";
    err.classList.remove("d-none");
  }
});

el("ownerForm").addEventListener("submit", async (e) => {
  e.preventDefault();
  const err = el("ownerError");
  err.classList.add("d-none");
  const payload = {
    userId: Number(el("ownerUser").value),
    ownerCode: el("ownerCode").value.trim()
  };
  try {
    await postJson("/qal/masters/owner", payload);
    el("ownerForm").reset();
    await loadOwner();
  } catch (ex) {
    err.textContent = ex?.message || "Gagal menyimpan Owner.";
    err.classList.remove("d-none");
  }
});

el("pcForm").addEventListener("submit", async (e) => {
  e.preventDefault();
  const err = el("pcError");
  err.classList.add("d-none");
  const payload = {
    userId: Number(el("pcUser").value),
    pcCode: el("pcCode").value.trim()
  };
  try {
    await postJson("/qal/masters/pc", payload);
    el("pcForm").reset();
    await loadPc();
  } catch (ex) {
    err.textContent = ex?.message || "Gagal menyimpan Project Control.";
    err.classList.remove("d-none");
  }
});

Promise.all([loadUsers(), loadSpk(), loadQc(), loadOwner(), loadPc()]).catch(console.error);
