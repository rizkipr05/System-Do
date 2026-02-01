requireRole("ADMIN", "login-quality-control.html");
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
    tbody.innerHTML = `<tr><td colspan="8" class="text-center text-muted">Belum ada data QAL.</td></tr>`;
    return;
  }
  list.forEach((q) => {
    const tr = document.createElement("tr");
    tr.innerHTML = `
      <td>${q.qalNumber}</td>
      <td>${q.qalDate || "-"}</td>
      <td>${q.spkNumber || "-"}</td>
      <td>${q.jobName || "-"}</td>
      <td>${q.qcCode || "-"}</td>
      <td>${q.qcName || "-"}</td>
      <td>${q.qcPosition || "-"}</td>
      <td>${q.projectControlName || "-"}</td>
      <td>${q.projectControlCode || "-"}</td>
      <td>${q.ownerCode || "-"}</td>
      <td>${q.ownerName || "-"}</td>
      <td>${badge(q.status)}</td>
    `;
    tbody.appendChild(tr);
  });
}

loadList().catch(console.error);
