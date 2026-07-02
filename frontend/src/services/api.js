const API_BASE_URL = import.meta.env.VITE_API_BASE_URL || "http://localhost:8080/api";

async function request(path, options = {}) {
  const response = await fetch(`${API_BASE_URL}${path}`, {
    headers: {
      "Content-Type": "application/json",
      ...(options.headers || {}),
    },
    ...options,
  });

  const isJson = response.headers.get("content-type")?.includes("application/json");
  const payload = isJson ? await response.json() : null;

  if (!response.ok) {
    const message = payload?.message || "Errore durante la richiesta";
    throw new Error(message);
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

export function getBookings() {
  return request("/bookings");
}
