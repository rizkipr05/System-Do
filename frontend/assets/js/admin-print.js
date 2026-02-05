requireRole("ADMIN", "login-admin.html");

const el = (id) => document.getElementById(id);

function formatDate(value) {
  if (!value) return "-";
  const d = new Date(value);
  return d.toLocaleString("id-ID");
}

async function loadOrder() {
  const params = new URLSearchParams(window.location.search);
  const id = params.get("id");
  if (!id) return;
  const list = await getJson("/admin/orders");
  const order = list.find((o) => `${o.id}` === `${id}`);
  if (!order) return;

  el("doNumber").textContent = order.doNumber || order.id;
  el("doDate").textContent = formatDate(order.createdAt);
  el("custName").textContent = order.customerName || "-";
  el("custAddress").textContent = order.addressLine || "-";
  el("driverName").textContent = order.driverName || "-";
  el("orderStatus").textContent = order.status || "-";
  if (el("orderNote")) el("orderNote").textContent = order.note || "-";

  const showPrice = document.body?.dataset?.showPrice === "1";
  const tbody = el("itemTable");
  tbody.innerHTML = "";
  (order.items || []).forEach((i) => {
    const tr = document.createElement("tr");
    tr.innerHTML = `
      <td>${i.productName}</td>
      <td class="text-end">${i.quantity}</td>
      ${showPrice ? `<td class="text-end">${Number(i.price || 0).toLocaleString("id-ID")}</td>` : ""}
    `;
    tbody.appendChild(tr);
  });
}

loadOrder().catch(console.error);
