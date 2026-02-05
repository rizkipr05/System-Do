const params = new URLSearchParams(location.search);
const id = params.get("id");

async function load() {
  if (!id) return;
  const q = await getJson(`/qal/${id}`);
  document.getElementById("qalNumber").textContent = q.qalNumber || "-";
  document.getElementById("qalDate").textContent = q.qalDate || "-";
  document.getElementById("spkNumber").textContent = q.spkNumber || "-";
  document.getElementById("jobName").textContent = q.jobName || "-";
  document.getElementById("adminCode").textContent = q.adminCode || "-";
  document.getElementById("adminName").textContent = q.adminName || "-";
  document.getElementById("adminPosition").textContent = q.adminPosition || "-";
  document.getElementById("driverName").textContent = q.driverName || "-";
  document.getElementById("customerName").textContent = q.customerName || "-";
  document.getElementById("customerCode").textContent = q.customerCode || "-";
  document.getElementById("status").textContent = q.status || "-";
  const adminFooter = document.getElementById("adminNameFooter");
  if (adminFooter) adminFooter.textContent = q.adminName || "-";
  const adminCodeFooter = document.getElementById("adminCodeFooter");
  if (adminCodeFooter) adminCodeFooter.textContent = q.adminCode || "-";
  const driverCode = document.getElementById("driverCode");
  if (driverCode) driverCode.textContent = q.driverCode || "-";

  const tbody = document.getElementById("detailTable");
  tbody.innerHTML = "";
  (q.details || []).forEach((d, idx) => {
    const tr = document.createElement("tr");
    tr.innerHTML = `
      <td class="text-center">${idx + 1}</td>
      <td>${d.documentName || "-"}</td>
      <td>${d.documentType || "-"}</td>
      <td>${d.receivedDate || "-"}</td>
      <td>${d.verificationStatus || "-"}</td>
    `;
    tbody.appendChild(tr);
  });
}

load().catch(console.error);
