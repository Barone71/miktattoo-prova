const API_BASE_URL = import.meta.env.VITE_API_BASE_URL || "http://localhost:8080/api";

async function request(path, options = {}) {
  const response = await fetch(`${API_BASE_URL}${path}`, {
    ...options,
    headers: {
      "Content-Type": "application/json",
      ...(options.headers || {}),
    },
  });

  const isJson = response.headers.get("content-type")?.includes("application/json");
  const payload = isJson ? await response.json() : null;

  if (!response.ok) {
    const error = new Error(payload?.message || "Errore durante la richiesta");
    // Il backend manda i dettagli come "campo: messaggio"; teniamo solo il messaggio leggibile.
    error.details = (payload?.details || []).map((detail) => detail.split(": ").slice(1).join(": ") || detail);
    throw error;
  }

  return payload;
}

export function getWorks() {
  return request("/works");
}

export function getProfile() {
  return request("/profile");
}

export function getAvailability() {
  return request("/availability");
}

export function createBooking(bookingData) {
  return request("/bookings", {
    method: "POST",
    body: JSON.stringify(bookingData),
  });
}

// --- Area admin: ogni chiamata porta le credenziali del tatuatore ---

function adminHeaders(credentials) {
  return { Authorization: `Basic ${credentials}` };
}

export function encodeCredentials(username, password) {
  const bytes = new TextEncoder().encode(`${username}:${password}`);
  return btoa(String.fromCharCode(...bytes));
}

export function getAdminBookings(credentials) {
  return request("/admin/bookings", { headers: adminHeaders(credentials) });
}

export function deleteAdminBooking(credentials, id) {
  return request(`/admin/bookings/${id}`, {
    method: "DELETE",
    headers: adminHeaders(credentials),
  });
}

export function getAdminSlots(credentials) {
  return request("/admin/slots", { headers: adminHeaders(credentials) });
}

export function createAdminSlot(credentials, slot) {
  return request("/admin/slots", {
    method: "POST",
    headers: adminHeaders(credentials),
    body: JSON.stringify(slot),
  });
}

export function deleteAdminSlot(credentials, id) {
  return request(`/admin/slots/${id}`, {
    method: "DELETE",
    headers: adminHeaders(credentials),
  });
}

export function clearAdminDay(credentials, date) {
  return request(`/admin/days/${date}/slots`, {
    method: "DELETE",
    headers: adminHeaders(credentials),
  });
}
