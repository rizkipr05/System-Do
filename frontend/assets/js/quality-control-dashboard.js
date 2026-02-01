requireRole("ADMIN", "login-quality-control.html");
setAdminGreeting();

function renderStatusChart(statusCounts) {
  const container = document.getElementById("statusChart");
  container.innerHTML = "";
  const entries = Object.entries(statusCounts || {});
  if (!entries.length) {
    container.innerHTML = `<div class="text-muted">Belum ada data status.</div>`;
    return;
  }
  const total = entries.reduce((sum, [, v]) => sum + (v || 0), 0) || 1;
  entries.forEach(([label, value]) => {
    const percent = Math.round((value / total) * 100);
    const row = document.createElement("div");
    row.innerHTML = `
      <div class="d-flex align-items-center justify-content-between small mb-1">
        <span>${label}</span>
        <span>${value} (${percent}%)</span>
      </div>
      <div class="progress" role="progressbar" aria-valuenow="${percent}" aria-valuemin="0" aria-valuemax="100">
        <div class="progress-bar" style="width: ${percent}%"></div>
      </div>
    `;
    container.appendChild(row);
  });
}

async function loadStats() {
  const list = await getJson("/qal");
  const counts = { DRAFT: 0, SIGNED: 0, APPROVED: 0 };
  list.forEach((q) => {
    if (counts[q.status] !== undefined) counts[q.status] += 1;
  });
  document.getElementById("statTotalQal").textContent = list.length;
  document.getElementById("statSigned").textContent = counts.SIGNED + counts.DRAFT;
  document.getElementById("statApproved").textContent = counts.APPROVED;
  renderStatusChart({
    Draft: counts.DRAFT,
    Signed: counts.SIGNED,
    Approved: counts.APPROVED
  });
}

loadStats().catch(console.error);
