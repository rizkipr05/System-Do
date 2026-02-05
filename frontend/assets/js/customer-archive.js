requireRole("CUSTOMER", "login-customer.html");
setGreeting();

function formatDate(value) {
  if (!value) return "-";
  const d = new Date(value);
  return d.toLocaleString("id-ID");
}

async function loadArchive() {
  const list = await getJson("/orders");
  const tbody = document.getElementById("archiveTable");
  tbody.innerHTML = "";
  if (!list.length) {
    tbody.innerHTML = `<tr><td colspan="5" class="text-center text-muted">Belum ada pesanan.</td></tr>`;
    return;
  }
  list.forEach((o) => {
    const items = (o.items || []).map((i) => `${i.productName} (${i.quantity})`).join(", ");
    const addr = o.address ? `${o.address.addressLine || "-"}, ${o.address.city || "-"}` : "-";
    const label = o.doNumber ? o.doNumber : `#${o.id}`;
    const tr = document.createElement("tr");
    tr.innerHTML = `
      <td>${label}</td>
      <td>${formatDate(o.createdAt)}</td>
      <td>${addr}</td>
      <td>${items || "-"}</td>
      <td><span class="badge text-bg-secondary">${statusLabels[o.status] || o.status}</span></td>
    `;
    tbody.appendChild(tr);
  });
}

loadArchive().catch(console.error);
