requireRole("CUSTOMER", "login-customer.html");
setGreeting();

function renderStatusChart(statusCounts) {
  const container = document.getElementById("statusChart");
  if (!container) return;
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
      <div class="progress mb-2" role="progressbar" aria-valuenow="${percent}" aria-valuemin="0" aria-valuemax="100">
        <div class="progress-bar" style="width: ${percent}%"></div>
      </div>
    `;
    container.appendChild(row);
  });
}

async function loadSummary() {
  const s = await getJson("/orders/summary");
  document.getElementById("statActive").textContent = s.activeCount ?? 0;
  document.getElementById("statCompleted").textContent = s.completedCount ?? 0;
  document.getElementById("statInTransit").textContent = s.inTransitCount ?? 0;
}

async function loadStatusChart() {
  const list = await getJson("/orders");
  const map = {
    DRAFT: 0,
    APPROVED: 0,
    PACKING: 0,
    READY_TO_SHIP: 0,
    IN_TRANSIT: 0,
    DELIVERED: 0,
    FAILED: 0,
    CONFIRMED: 0
  };
  (list || []).forEach((o) => {
    if (map[o.status] !== undefined) map[o.status] += 1;
  });
  renderStatusChart({
    "Draft (diproses admin)": map.DRAFT,
    "Approved": map.APPROVED,
    "Packing Gudang": map.PACKING,
    "Siap Kirim": map.READY_TO_SHIP,
    "Dalam Perjalanan": map.IN_TRANSIT,
    "Terkirim": map.DELIVERED + map.CONFIRMED,
    "Gagal Kirim": map.FAILED
  });
}

Promise.all([loadSummary(), loadStatusChart()]).catch(console.error);
