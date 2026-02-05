requireRole("ADMIN", "login-admin.html");
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
  const totalEl = document.getElementById("statTotalDo");
  const inTransitEl = document.getElementById("statInTransit");
  const deliveredEl = document.getElementById("statDelivered");
  const chartEl = document.getElementById("statusChart");
  if (!totalEl || !inTransitEl || !deliveredEl || !chartEl) return;

  const list = await getJson("/admin/orders");
  const counts = {
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
    if (counts[o.status] !== undefined) counts[o.status] += 1;
  });
  totalEl.textContent = list.length;
  inTransitEl.textContent = counts.IN_TRANSIT;
  deliveredEl.textContent = counts.DELIVERED + counts.CONFIRMED;
  renderStatusChart({
    Draft: counts.DRAFT,
    Approved: counts.APPROVED,
    "Packing Gudang": counts.PACKING,
    "Siap Kirim": counts.READY_TO_SHIP,
    "Dalam Perjalanan": counts.IN_TRANSIT,
    "Terkirim": counts.DELIVERED + counts.CONFIRMED,
    "Gagal Kirim": counts.FAILED
  });
}

loadStats().catch(console.error);
