requireRole("ADMIN", "login-admin.html");
setAdminGreeting();

const statusLabels = {
  DRAFT: "Draft",
  APPROVED: "Approved",
  PACKING: "Packing Gudang",
  READY_TO_SHIP: "Siap Kirim",
  IN_TRANSIT: "Dalam Perjalanan",
  DELIVERED: "Terkirim",
  FAILED: "Gagal Kirim",
  CONFIRMED: "Konfirmasi"
};

function formatDate(value) {
  if (!value) return "-";
  const d = new Date(value);
  return d.toLocaleString("id-ID");
}

function renderStatusChart(counts) {
  const container = document.getElementById("repStatusChart");
  container.innerHTML = "";
  const entries = Object.entries(counts || {});
  if (!entries.length) {
    container.innerHTML = `<div class="text-muted">Belum ada data.</div>`;
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

async function loadSummary() {
  const s = await getJson("/admin/reports/summary");
  document.getElementById("repTotalCustomers").textContent = s.totalCustomers ?? 0;
  document.getElementById("repOrdersToday").textContent = s.ordersToday ?? 0;
  document.getElementById("repOrdersMonth").textContent = s.ordersThisMonth ?? 0;
  document.getElementById("repActiveShipments").textContent = s.activeShipments ?? 0;

  const statusCounts = s.statusCounts || {};
  renderStatusChart({
    Draft: statusCounts.DRAFT || 0,
    Approved: statusCounts.APPROVED || 0,
    "Packing Gudang": statusCounts.PACKING || 0,
    "Siap Kirim": statusCounts.READY_TO_SHIP || 0,
    "Dalam Perjalanan": statusCounts.IN_TRANSIT || 0,
    Terkirim: (statusCounts.DELIVERED || 0) + (statusCounts.CONFIRMED || 0),
    "Gagal Kirim": statusCounts.FAILED || 0
  });

  const prodBody = document.getElementById("repTopProducts");
  prodBody.innerHTML = "";
  (s.topProducts || []).forEach((p) => {
    const tr = document.createElement("tr");
    tr.innerHTML = `<td>${p.label}</td><td class="text-end">${p.value}</td>`;
    prodBody.appendChild(tr);
  });
  if (!prodBody.children.length) {
    prodBody.innerHTML = `<tr><td colspan="2" class="text-center text-muted">Belum ada data.</td></tr>`;
  }

  const custBody = document.getElementById("repTopCustomers");
  custBody.innerHTML = "";
  (s.topCustomers || []).forEach((c) => {
    const tr = document.createElement("tr");
    tr.innerHTML = `<td>${c.label}</td><td class="text-end">${c.value}</td>`;
    custBody.appendChild(tr);
  });
  if (!custBody.children.length) {
    custBody.innerHTML = `<tr><td colspan="2" class="text-center text-muted">Belum ada data.</td></tr>`;
  }
}

async function loadOrders() {
  const list = await getJson("/admin/orders");
  const tbody = document.getElementById("reportTable");
  tbody.innerHTML = "";
  if (!list.length) {
    tbody.innerHTML = `<tr><td colspan="7" class="text-center text-muted">Belum ada order.</td></tr>`;
    return;
  }
  list.forEach((o) => {
    const tr = document.createElement("tr");
    tr.innerHTML = `
      <td>${o.doNumber || o.id}</td>
      <td>${formatDate(o.createdAt)}</td>
      <td>${o.customerName || "-"}</td>
      <td>${o.driverName || "-"}</td>
      <td><span class="badge text-bg-secondary">${statusLabels[o.status] || o.status}</span></td>
      <td>${o.addressLine || "-"}</td>
      <td class="text-end">
        <a class="btn btn-sm btn-outline-secondary" href="admin-print-do.html?id=${o.id}" target="_blank">Print DO</a>
        <a class="btn btn-sm btn-outline-primary" href="admin-print-surat.html?id=${o.id}" target="_blank">Surat Jalan</a>
      </td>
    `;
    tbody.appendChild(tr);
  });
}

Promise.all([loadSummary(), loadOrders()]).catch(console.error);
