import { useEffect, useState } from "react";
import AdminAvailability from "../components/AdminAvailability";
import { deleteAdminBooking, encodeCredentials, getAdminBookings } from "../services/api";
import { parseIsoDate } from "../utils/dates";

// Le credenziali restano solo finché la scheda è aperta.
const STORAGE_KEY = "mik-admin";

function readStoredCredentials() {
  try {
    return sessionStorage.getItem(STORAGE_KEY) || "";
  } catch {
    return "";
  }
}

function storeCredentials(value) {
  try {
    if (value) {
      sessionStorage.setItem(STORAGE_KEY, value);
    } else {
      sessionStorage.removeItem(STORAGE_KEY);
    }
  } catch {
    // sessionStorage non disponibile: si resta loggati solo fino al refresh
  }
}

function formatDate(date) {
  return parseIsoDate(date).toLocaleDateString("it-IT", {
    weekday: "short",
    day: "numeric",
    month: "long",
  });
}

function Admin() {
  const [credentials, setCredentials] = useState(readStoredCredentials);
  const [login, setLogin] = useState({ username: "", password: "" });
  const [bookings, setBookings] = useState([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState("");
  const [tab, setTab] = useState("bookings");

  function logout(message = "") {
    storeCredentials("");
    setCredentials("");
    setBookings([]);
    setError(message);
  }

  function handleError(err) {
    if (err.message === "Credenziali non valide") {
      logout("Sessione scaduta: accedi di nuovo.");
    } else {
      setError(err.message);
    }
  }

  async function loadBookings(currentCredentials) {
    setLoading(true);
    setError("");
    try {
      const data = await getAdminBookings(currentCredentials);
      setBookings(data);
      return true;
    } catch (err) {
      if (err.message === "Credenziali non valide") {
        logout("Username o password errati.");
      } else {
        setError(err.message);
      }
      return false;
    } finally {
      setLoading(false);
    }
  }

  useEffect(() => {
    if (credentials) {
      loadBookings(credentials);
    }
  }, []);

  async function handleLogin(event) {
    event.preventDefault();
    const encoded = encodeCredentials(login.username.trim(), login.password);
    const ok = await loadBookings(encoded);
    if (ok) {
      storeCredentials(encoded);
      setCredentials(encoded);
      setLogin({ username: "", password: "" });
    }
  }

  function switchTab(next) {
    setTab(next);
    setError("");
    if (next === "bookings") {
      loadBookings(credentials);
    }
  }

  async function handleDelete(booking) {
    const confirmed = window.confirm(
      `Cancellare la prenotazione di ${booking.name} del ${formatDate(booking.date)} alle ${booking.startTime}?\nL'orario tornerà disponibile.`
    );
    if (!confirmed) return;

    try {
      await deleteAdminBooking(credentials, booking.id);
      setBookings((prev) => prev.filter((item) => item.id !== booking.id));
    } catch (err) {
      handleError(err);
    }
  }

  if (!credentials) {
    return (
      <section className="page admin-page">
        <div className="page-header">
          <p className="eyebrow">Area riservata</p>
          <h1>Admin</h1>
        </div>

        <form className="admin-login" onSubmit={handleLogin}>
          <label>
            Username
            <input
              name="username"
              autoComplete="username"
              value={login.username}
              onChange={(e) => setLogin((prev) => ({ ...prev, username: e.target.value }))}
              required
            />
          </label>

          <label>
            Password
            <input
              name="password"
              type="password"
              autoComplete="current-password"
              value={login.password}
              onChange={(e) => setLogin((prev) => ({ ...prev, password: e.target.value }))}
              required
            />
          </label>

          {error && <p className="state-message error">{error}</p>}

          <button className="btn btn-light full" disabled={loading}>
            {loading ? "Accesso..." : "Accedi"}
          </button>
        </form>
      </section>
    );
  }

  return (
    <section className="page admin-page">
      <div className="page-header admin-header">
        <div>
          <p className="eyebrow">Area riservata</p>
          <h1>Admin</h1>
        </div>
        <div className="admin-actions">
          {tab === "bookings" && (
            <button className="btn btn-outline" onClick={() => loadBookings(credentials)} disabled={loading}>
              Aggiorna
            </button>
          )}
          <button className="btn btn-outline" onClick={() => logout()}>
            Esci
          </button>
        </div>
      </div>

      <div className="filter-row admin-tabs" role="tablist">
        <button role="tab" aria-selected={tab === "bookings"} className={tab === "bookings" ? "active" : ""} onClick={() => switchTab("bookings")}>
          Prenotazioni
        </button>
        <button role="tab" aria-selected={tab === "availability"} className={tab === "availability" ? "active" : ""} onClick={() => switchTab("availability")}>
          Disponibilità
        </button>
      </div>

      {error && <p className="state-message error">{error}</p>}

      {tab === "availability" && <AdminAvailability credentials={credentials} onError={handleError} />}

      {tab === "bookings" && loading && <p className="state-message">Caricamento...</p>}

      {tab === "bookings" && !loading && bookings.length === 0 && <p className="state-message">Nessuna prenotazione al momento.</p>}

      {tab === "bookings" && <div className="admin-list">
        {bookings.map((booking) => (
          <article key={booking.id} className="admin-booking">
            <div className="admin-booking-when">
              <strong>{formatDate(booking.date)}</strong>
              <span>
                {booking.startTime} – {booking.endTime}
              </span>
            </div>

            <div className="admin-booking-who">
              <strong>{booking.name}</strong>
              <a href={`mailto:${booking.email}`}>{booking.email}</a>
              <a href={`tel:${booking.phone}`}>{booking.phone}</a>
            </div>

            <div className="admin-booking-what">
              <p>{booking.tattooIdea}</p>
              <span>
                {booking.placement} · {booking.approximateSize}
              </span>
            </div>

            <button className="btn btn-danger" onClick={() => handleDelete(booking)}>
              Cancella
            </button>
          </article>
        ))}
      </div>}
    </section>
  );
}

export default Admin;
