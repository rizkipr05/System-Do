const params = new URLSearchParams(location.search);
const id = params.get("id");

async function load() {
  if (!id) return;
  const q = await getJson(`/qal/${id}`);
  document.getElementById("qalNumber").textContent = q.qalNumber || "-";
  document.getElementById("qalDate").textContent = q.qalDate || "-";
  document.getElementById("spkNumber").textContent = q.spkNumber || "-";
  document.getElementById("jobName").textContent = q.jobName || "-";
  document.getElementById("qcCode").textContent = q.qcCode || "-";
  document.getElementById("qcName").textContent = q.qcName || "-";
  document.getElementById("qcPosition").textContent = q.qcPosition || "-";
  document.getElementById("pcName").textContent = q.projectControlName || "-";
  document.getElementById("ownerName").textContent = q.ownerName || "-";
  document.getElementById("ownerCode").textContent = q.ownerCode || "-";
  document.getElementById("status").textContent = q.status || "-";
  const qcFooter = document.getElementById("qcNameFooter");
  if (qcFooter) qcFooter.textContent = q.qcName || "-";
  const qcCodeFooter = document.getElementById("qcCodeFooter");
  if (qcCodeFooter) qcCodeFooter.textContent = q.qcCode || "-";
  const pcCode = document.getElementById("pcCode");
  if (pcCode) pcCode.textContent = q.projectControlCode || "-";

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
