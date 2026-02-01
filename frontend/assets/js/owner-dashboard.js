requireRole("CUSTOMER", "login-owner.html");
setGreeting();

async function loadSummary() {
  const s = await getJson("/orders/summary");
  document.getElementById("statActive").textContent = s.activeCount ?? 0;
  document.getElementById("statCompleted").textContent = s.completedCount ?? 0;
  document.getElementById("statInTransit").textContent = s.inTransitCount ?? 0;
}

loadSummary().catch(console.error);
