function requireRole(role, redirectTo) {
  const token = localStorage.getItem("token");
  const r = localStorage.getItem("role");
  if (!token || r !== role) {
    location.href = redirectTo;
  }
}
