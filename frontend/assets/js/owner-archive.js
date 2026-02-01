requireRole("CUSTOMER", "login-owner.html");
setGreeting();

async function loadArchive() {
  const list = await getJson("/qal");
  const tbody = document.getElementById("archiveTable");
  tbody.innerHTML = "";
  const approved = list.filter((q) => q.status === "APPROVED");
  if (!approved.length) {
    tbody.innerHTML = `<tr><td colspan="5" class="text-center text-muted">Belum ada arsip.</td></tr>`;
    return;
  }
  approved.forEach((q) => {
    const tr = document.createElement("tr");
    tr.innerHTML = `
      <td>${q.qalNumber}</td>
      <td>${q.qalDate || "-"}</td>
      <td>${q.spkNumber || "-"}</td>
      <td>${q.jobName || "-"}</td>
      <td><span class="badge text-bg-success">APPROVED</span></td>
    `;
    tbody.appendChild(tr);
  });
}

loadArchive().catch(console.error);
