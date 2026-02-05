requireRole("ADMIN", "login-admin.html");
setAdminGreeting();

const el = (id) => document.getElementById(id);

function addRow(data = {}) {
  const tbody = el("detailRows");
  const tr = document.createElement("tr");
  tr.innerHTML = `
    <td><input type="text" class="form-control form-control-sm" value="${data.documentName || ""}"></td>
    <td><input type="text" class="form-control form-control-sm" value="${data.documentType || ""}"></td>
    <td><input type="date" class="form-control form-control-sm" value="${data.receivedDate || ""}"></td>
    <td><input type="text" class="form-control form-control-sm" value="${data.verificationStatus || "Disetujui"}"></td>
    <td class="text-end"><button type="button" class="btn btn-sm btn-outline-danger">Hapus</button></td>
  `;
  tr.querySelector("button").addEventListener("click", () => tr.remove());
  tbody.appendChild(tr);
}

el("addDetailBtn").addEventListener("click", () => addRow());
addRow();

async function loadUsers() {
  const pcs = await getJson("/qal/users/DRIVER");
  const customers = await getJson("/qal/users/CUSTOMER");
  el("driverUser").innerHTML = `<option value="">Pilih Driver</option>` +
    pcs.map((u) => `<option value="${u.id}">${u.name} (${u.email})</option>`).join("");
  el("customerUser").innerHTML = `<option value="">Pilih Customer</option>` +
    customers.map((u) => `<option value="${u.id}">${u.name} (${u.email})</option>`).join("");
}

el("qalForm").addEventListener("submit", async (e) => {
  e.preventDefault();
  const err = el("formError");
  err.classList.add("d-none");

  const details = Array.from(el("detailRows").querySelectorAll("tr")).map((tr) => {
    const inputs = tr.querySelectorAll("input");
    return {
      documentName: inputs[0].value.trim(),
      documentType: inputs[1].value.trim(),
      receivedDate: inputs[2].value,
      verificationStatus: inputs[3].value.trim()
    };
  }).filter((d) => d.documentName);

  const payload = {
    qalNumber: el("qalNumber").value.trim(),
    qalDate: el("qalDate").value,
    spkNumber: el("spkNumber").value.trim(),
    jobName: el("jobName").value.trim(),
    driverUserId: el("driverUser").value ? Number(el("driverUser").value) : null,
    customerUserId: el("customerUser").value ? Number(el("customerUser").value) : null,
    details
  };

  try {
    await postJson("/qal", payload);
    location.href = "admin-qal.html";
  } catch (ex) {
    err.textContent = ex?.message || "Gagal menyimpan QAL.";
    err.classList.remove("d-none");
  }
});

loadUsers().catch(console.error);
