requireRole("ADMIN", "login-admin.html");
setAdminGreeting();

function badge(status) {
  const map = {
    DRAFT: "secondary",
    APPROVED: "primary",
    PACKING: "info",
    READY_TO_SHIP: "warning",
    IN_TRANSIT: "warning",
    DELIVERED: "success",
    FAILED: "danger",
    CONFIRMED: "success"
  };
  const color = map[status] || "secondary";
  return `<span class="badge text-bg-${color}">${status}</span>`;
}

async function loadList() {
  const list = await getJson("/admin/orders");
  const tbody = document.getElementById("qalTable");
  tbody.innerHTML = "";
  if (!list.length) {
    tbody.innerHTML = `<tr><td colspan="7" class="text-center text-muted">Belum ada data DO.</td></tr>`;
    return;
  }
  list.forEach((o) => {
    const items = (o.items || []).map((i) => `${i.productName} (${i.quantity})`).join(", ");
    const tr = document.createElement("tr");
    tr.innerHTML = `
      <td>${o.doNumber || o.id}</td>
      <td>${o.createdAt ? new Date(o.createdAt).toLocaleString("id-ID") : "-"}</td>
      <td>${o.customerName || "-"}</td>
      <td>${o.addressLine || "-"}</td>
      <td>${o.driverName || "-"}</td>
      <td>${badge(o.status)}</td>
      <td>${items || "-"}</td>
    `;
    tbody.appendChild(tr);
  });
}

loadList().catch(console.error);
