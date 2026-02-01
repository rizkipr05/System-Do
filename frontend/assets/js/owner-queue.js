requireRole("CUSTOMER", "login-owner.html");
setGreeting();

function badge(status) {
  const map = { DRAFT: "secondary", SIGNED: "warning", APPROVED: "success" };
  return `<span class="badge text-bg-${map[status] || "secondary"}">${status}</span>`;
}

async function loadList() {
  const list = await getJson("/qal");
  const tbody = document.getElementById("ownerTable");
  tbody.innerHTML = "";
  const waiting = list.filter((q) => q.status === "SIGNED");
  if (!waiting.length) {
    tbody.innerHTML = `<tr><td colspan="7" class="text-center text-muted">Belum ada QAL menunggu ACC.</td></tr>`;
    return;
  }
  waiting.forEach((q) => {
    const tr = document.createElement("tr");
    tr.innerHTML = `
      <td>${q.qalNumber}</td>
      <td>${q.qalDate || "-"}</td>
      <td>${q.spkNumber || "-"}</td>
      <td>${q.jobName || "-"}</td>
      <td>${q.projectControlName || "-"}</td>
      <td>${badge(q.status)}</td>
      <td class="text-end"><button class="btn btn-sm btn-success">ACC</button></td>
    `;
    tr.querySelector("button").addEventListener("click", async () => {
      await postJson(`/qal/${q.id}/approve`, {});
      await loadList();
    });
    tbody.appendChild(tr);
  });
}

loadList().catch(console.error);
