requireRole("PROJECT_CONTROL", "login-project-control.html");
setDriverGreeting();

async function loadSummary() {
  const s = await getJson("/driver/summary");
  document.getElementById("statToday").textContent = s.todayCount ?? 0;
  document.getElementById("statTransit").textContent = s.inTransit ?? 0;
  document.getElementById("statDone").textContent = s.completed ?? 0;
}

loadSummary().catch(console.error);
