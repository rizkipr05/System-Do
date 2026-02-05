requireRole("ADMIN", "login-admin.html");
setAdminGreeting();

async function loadReport() {
  const list = await getJson("/qal");
  const tbody = document.getElementById("reportTable");
  tbody.innerHTML = "";
  const approved = list.filter((q) => q.status === "APPROVED");
  if (!approved.length) {
    tbody.innerHTML = `<tr><td colspan="7" class="text-center text-muted">Belum ada QAL ACC.</td></tr>`;
    return;
  }
  approved.forEach((q) => {
    const tr = document.createElement("tr");
    tr.innerHTML = `
      <td>${q.qalNumber}</td>
      <td>${q.qalDate || "-"}</td>
      <td>${q.spkNumber || "-"}</td>
      <td>${q.jobName || "-"}</td>
      <td>${q.driverName || "-"}</td>
      <td>${q.customerName || "-"}</td>
      <td class="d-flex align-items-center gap-2">
        <span class="badge text-bg-success">APPROVED</span>
        <a class="btn btn-sm btn-outline-secondary" href="admin-qal-print.html?id=${q.id}" target="_blank">Print</a>
      </td>
    `;
    tbody.appendChild(tr);
  });
}

loadReport().catch(console.error);
