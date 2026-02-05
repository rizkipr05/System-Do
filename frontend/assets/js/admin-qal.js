requireRole("ADMIN", "login-admin.html");
setAdminGreeting();

function badge(status) {
  const map = {
    DRAFT: "secondary",
    SIGNED: "warning",
    APPROVED: "success"
  };
  const color = map[status] || "secondary";
  return `<span class="badge text-bg-${color}">${status}</span>`;
}

async function loadList() {
  const list = await getJson("/qal");
  const tbody = document.getElementById("qalTable");
  tbody.innerHTML = "";
  if (!list.length) {
    tbody.innerHTML = `<tr><td colspan="12" class="text-center text-muted">Belum ada data QAL.</td></tr>`;
    return;
  }
  list.forEach((q) => {
    const tr = document.createElement("tr");
    tr.innerHTML = `
      <td>${q.qalNumber}</td>
      <td>${q.qalDate || "-"}</td>
      <td>${q.spkNumber || "-"}</td>
      <td>${q.jobName || "-"}</td>
      <td>${q.adminCode || "-"}</td>
      <td>${q.adminName || "-"}</td>
      <td>${q.adminPosition || "-"}</td>
      <td>${q.driverName || "-"}</td>
      <td>${q.driverCode || "-"}</td>
      <td>${q.customerCode || "-"}</td>
      <td>${q.customerName || "-"}</td>
      <td>${badge(q.status)}</td>
    `;
    tbody.appendChild(tr);
  });
}

loadList().catch(console.error);
