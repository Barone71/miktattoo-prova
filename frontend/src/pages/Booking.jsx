import { useEffect, useMemo, useState } from "react";
import { createBooking, getAvailability, getBookings } from "../services/api";

const initialForm = {
  name: "",
  email: "",
  phone: "",
  tattooIdea: "",
  placement: "",
  approximateSize: "",
};

function Booking() {
  const [availability, setAvailability] = useState([]);
  const [selectedDate, setSelectedDate] = useState("");
  const [selectedSlotId, setSelectedSlotId] = useState("");
  const [form, setForm] = useState(initialForm);
  const [bookings, setBookings] = useState([]);
  const [loading, setLoading] = useState(true);
  const [submitting, setSubmitting] = useState(false);
  const [message, setMessage] = useState("");
  const [error, setError] = useState("");

  function loadData() {
    setLoading(true);
    Promise.all([getAvailability(), getBookings()])
      .then(([slots, demoBookings]) => {
        setAvailability(slots);
        setBookings(demoBookings);
        const firstAvailable = slots.find((slot) => slot.available);
        setSelectedDate((current) => current || firstAvailable?.date || slots[0]?.date || "");
      })
      .catch((err) => setError(err.message))
      .finally(() => setLoading(false));
  }

  useEffect(() => {
    loadData();
  }, []);

  const dates = useMemo(() => [...new Set(availability.map((slot) => slot.date))], [availability]);
  const daySlots = availability.filter((slot) => slot.date === selectedDate);
  const selectedSlot = availability.find((slot) => slot.id === selectedSlotId);

  function handleChange(event) {
    const { name, value } = event.target;
    setForm((prev) => ({ ...prev, [name]: value }));
  }

  async function handleSubmit(event) {
    event.preventDefault();
    setMessage("");
    setError("");

    if (!selectedSlotId) {
      setError("Seleziona uno slot disponibile prima di confermare.");
      return;
    }

    try {
      setSubmitting(true);
      const booking = await createBooking({ ...form, slotId: selectedSlotId });
      setMessage(`Prenotazione confermata per ${booking.date} alle ${booking.startTime}. Ti ricontatteremo via email.`);
      setForm(initialForm);
      setSelectedSlotId("");
      loadData();
    } catch (err) {
      setError(err.message);
    } finally {
      setSubmitting(false);
    }
  }

  return (
    <section className="page booking-page">
      <div className="page-header">
        <p className="eyebrow">Booking</p>
        <h1>Prenota</h1>
        <p>
          Scegli giorno e orario disponibili per una consulenza sul tuo prossimo tatuaggio. Questa demo salva la prenotazione nel backend in memoria.
        </p>
      </div>

      {loading && <p className="state-message">Caricamento disponibilità...</p>}

      {!loading && (
        <div className="booking-layout">
          <div className="calendar-box">
            <h2>Calendario</h2>
            <div className="day-list">
              {dates.map((date) => (
                <button
                  key={date}
                  className={selectedDate === date ? "active" : ""}
                  onClick={() => {
                    setSelectedDate(date);
                    setSelectedSlotId("");
                  }}
                >
                  {new Date(date).toLocaleDateString("it-IT", {
                    weekday: "long",
                    day: "numeric",
                    month: "long",
                  })}
                </button>
              ))}
            </div>
          </div>

          <div className="slots-box">
            <h2>Orari disponibili</h2>
            <div className="slot-list">
              {daySlots.map((slot) => (
                <button
                  key={slot.id}
                  disabled={!slot.available}
                  className={`${selectedSlotId === slot.id ? "active" : ""} ${!slot.available ? "disabled" : ""}`}
                  onClick={() => setSelectedSlotId(slot.id)}
                >
                  <span>{slot.startTime}</span>
                  <small>{slot.available ? "Disponibile" : "Occupato"}</small>
                </button>
              ))}
            </div>

            <form className="booking-form" onSubmit={handleSubmit}>
              <div className="form-row">
                <label>
                  Nome
                  <input name="name" value={form.name} onChange={handleChange} required placeholder="Il tuo nome" />
                </label>

                <label>
                  Email
                  <input name="email" type="email" value={form.email} onChange={handleChange} required placeholder="nome@email.it" />
                </label>
              </div>

              <div className="form-row">
                <label>
                  Telefono
                  <input name="phone" value={form.phone} onChange={handleChange} required placeholder="+39..." />
                </label>

                <label>
                  Zona corpo
                  <input name="placement" value={form.placement} onChange={handleChange} required placeholder="Braccio, schiena, gamba..." />
                </label>
              </div>

              <label>
                Dimensione indicativa
                <input name="approximateSize" value={form.approximateSize} onChange={handleChange} required placeholder="Es. 8 cm, mezza manica, piccolo..." />
              </label>

              <label>
                Idea del tatuaggio
                <textarea name="tattooIdea" value={form.tattooIdea} onChange={handleChange} required placeholder="Descrivi stile, soggetto, riferimenti e significato." />
              </label>

              {selectedSlot && (
                <p className="selected-slot">
                  Slot selezionato: <strong>{selectedSlot.date} · {selectedSlot.startTime}</strong>
                </p>
              )}

              {error && <p className="state-message error">{error}</p>}
              {message && <p className="state-message success">{message}</p>}

              <button className="btn btn-light full" disabled={submitting}>
                {submitting ? "Conferma in corso..." : "Conferma appuntamento"}
              </button>
            </form>
          </div>
        </div>
      )}

      <section className="demo-bookings">
        <h2>Prenotazioni demo</h2>
        {bookings.length === 0 ? (
          <p>Nessuna prenotazione inserita in questa sessione.</p>
        ) : (
          <div className="booking-cards">
            {bookings.map((booking) => (
              <article key={booking.id}>
                <strong>{booking.name}</strong>
                <span>{booking.date} · {booking.startTime}</span>
                <p>{booking.tattooIdea}</p>
              </article>
            ))}
          </div>
        )}
      </section>
    </section>
  );
}

export default Booking;
