requireRole("ADMIN", "login-admin.html");
setAdminGreeting();

async function loadStats() {
  const s = await getJson("/admin/stats");
  document.getElementById("statCustomers").textContent = s.totalCustomers ?? 0;
  document.getElementById("statOrdersMonth").textContent = s.ordersThisMonth ?? 0;
  document.getElementById("statShipments").textContent = s.activeShipments ?? 0;
}

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

async function loadChart() {
  const r = await getJson("/admin/reports/summary");
  renderStatusChart(r.statusCounts || {});
}

Promise.all([loadStats(), loadChart()]).catch(console.error);
