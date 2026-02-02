requireRole("PROJECT_CONTROL", "login-project-control.html");
setDriverGreeting();

function badge(status) {
  const map = { DRAFT: "secondary", SIGNED: "warning", APPROVED: "success" };
  return `<span class="badge text-bg-${map[status] || "secondary"}">${status}</span>`;
}

async function loadList() {
  const list = await getJson("/qal");
  const tbody = document.getElementById("pcTable");
  tbody.innerHTML = "";
  const draft = list.filter((q) => q.status === "DRAFT");
  if (!draft.length) {
    tbody.innerHTML = `<tr><td colspan="7" class="text-center text-muted">Belum ada QAL untuk TTD.</td></tr>`;
    return;
  }
  draft.forEach((q) => {
    const tr = document.createElement("tr");
    tr.innerHTML = `
      <td>${q.qalNumber}</td>
      <td>${q.qalDate || "-"}</td>
      <td>${q.spkNumber || "-"}</td>
      <td>${q.jobName || "-"}</td>
      <td>${q.qcName || "-"}</td>
      <td>${badge(q.status)}</td>
      <td class="text-end"><button class="btn btn-sm btn-primary">TTD</button></td>
    `;
    tr.querySelector("button").addEventListener("click", async () => {
      await postJson(`/qal/${q.id}/sign`, {});
      await loadList();
    });
    tbody.appendChild(tr);
  });
}

loadList().catch(console.error);
