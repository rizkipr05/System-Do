requireRole("ADMIN", "login-quality-control.html");
setAdminGreeting();

const el = (id) => document.getElementById(id);

el("notifyForm").addEventListener("submit", async (e) => {
  e.preventDefault();
  const payload = {
    type: el("notifyType").value,
    orderId: el("notifyOrderId").value || "-"
  };
  await postJson("/admin/notifications/trigger", payload);
  showAlert("notifyAlert", "Notifikasi terkirim");
  el("notifyOrderId").value = "";
});
