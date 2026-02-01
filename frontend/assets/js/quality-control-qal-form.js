requireRole("ADMIN", "login-quality-control.html");
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
  const owners = await getJson("/qal/users/CUSTOMER");
  el("pcUser").innerHTML = `<option value="">Pilih Project Control</option>` +
    pcs.map((u) => `<option value="${u.id}">${u.name} (${u.email})</option>`).join("");
  el("ownerUser").innerHTML = `<option value="">Pilih Owner</option>` +
    owners.map((u) => `<option value="${u.id}">${u.name} (${u.email})</option>`).join("");
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
    projectControlUserId: el("pcUser").value ? Number(el("pcUser").value) : null,
    ownerUserId: el("ownerUser").value ? Number(el("ownerUser").value) : null,
    details
  };

  try {
    await postJson("/qal", payload);
    location.href = "quality-control-qal.html";
  } catch (ex) {
    err.textContent = ex?.message || "Gagal menyimpan QAL.";
    err.classList.remove("d-none");
  }
});

loadUsers().catch(console.error);
