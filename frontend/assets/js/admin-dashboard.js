requireRole("ADMIN", "login-admin.html");
setAdminGreeting();

async function loadStats() {
  const s = await getJson("/admin/stats");
  document.getElementById("statCustomers").textContent = s.totalCustomers ?? 0;
  document.getElementById("statOrdersMonth").textContent = s.ordersThisMonth ?? 0;
  document.getElementById("statShipments").textContent = s.activeShipments ?? 0;
}

loadStats().catch(console.error);
