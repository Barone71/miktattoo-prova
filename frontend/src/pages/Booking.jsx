import { useEffect, useMemo, useState } from "react";
import MonthCalendar from "../components/MonthCalendar";
import { createBooking, getAvailability } from "../services/api";
import { formatLongDate } from "../utils/dates";

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
  const [loading, setLoading] = useState(true);
  const [submitting, setSubmitting] = useState(false);
  const [message, setMessage] = useState("");
  const [error, setError] = useState("");
  const [errorDetails, setErrorDetails] = useState([]);

  function loadData() {
    setLoading(true);
    getAvailability()
      .then((slots) => {
        setAvailability(slots);
        const firstAvailable = slots.find((slot) => slot.available);
        setSelectedDate((current) => current || firstAvailable?.date || "");
      })
      .catch((err) => setError(err.message))
      .finally(() => setLoading(false));
  }

  useEffect(() => {
    loadData();
  }, []);

  // Per ogni giorno: quanti orari ci sono e quanti sono ancora liberi.
  const daySummary = useMemo(() => {
    const summary = {};
    availability.forEach((slot) => {
      summary[slot.date] ??= { total: 0, free: 0 };
      summary[slot.date].total += 1;
      if (slot.available) summary[slot.date].free += 1;
    });
    return summary;
  }, [availability]);

  function getDayState(iso) {
    const day = daySummary[iso];
    if (!day) return { status: "none", disabled: true };
    if (day.free === 0) return { status: "full", disabled: true };
    return { status: "available", disabled: false };
  }

  const hasFreeSlots = availability.some((slot) => slot.available);
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
    setErrorDetails([]);

    if (!selectedSlotId) {
      setError("Scegli un giorno e un orario prima di confermare.");
      return;
    }

    try {
      setSubmitting(true);
      const booking = await createBooking({ ...form, slotId: selectedSlotId });
      setMessage(`Prenotazione confermata per ${formatLongDate(booking.date)} alle ${booking.startTime}. Ti ricontatteremo via email.`);
      setForm(initialForm);
      setSelectedSlotId("");
      loadData();
    } catch (err) {
      setError(err.message);
      setErrorDetails(err.details || []);
    } finally {
      setSubmitting(false);
    }
  }

  return (
    <section className="page booking-page">
      <div className="page-header">
        <h1>Prenota</h1>
        <p>
          Scegli giorno e orario disponibili per una consulenza sul tuo prossimo tatuaggio.
        </p>
      </div>

      {loading && <p className="state-message">Caricamento disponibilità...</p>}

      {!loading && (
        <div className="booking-layout">
          <div className="calendar-box">
            <MonthCalendar
              selectedDate={selectedDate}
              initialDate={selectedDate}
              getDayState={getDayState}
              onSelect={(iso) => {
                setSelectedDate(iso);
                setSelectedSlotId("");
              }}
            />
            <div className="calendar-legend">
              <span><i className="legend-dot available" /> Disponibile</span>
              <span><i className="legend-dot full" /> Tutto prenotato</span>
            </div>
            {!hasFreeSlots && (
              <p className="state-message">Al momento non ci sono orari disponibili. Riprova tra qualche giorno.</p>
            )}
          </div>

          <div className="slots-box">
            <h2>Orari disponibili</h2>
            {selectedDate ? (
              <p className="slots-day">{formatLongDate(selectedDate)}</p>
            ) : (
              <p className="state-message">Scegli un giorno dal calendario.</p>
            )}
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
                  <input name="name" value={form.name} onChange={handleChange} required maxLength={80} autoComplete="name" placeholder="Il tuo nome" />
                </label>

                <label>
                  Email
                  <input name="email" type="email" value={form.email} onChange={handleChange} required maxLength={254} autoComplete="email" placeholder="nome@email.it" />
                </label>
              </div>

              <div className="form-row">
                <label>
                  Telefono
                  <input name="phone" type="tel" value={form.phone} onChange={handleChange} required maxLength={30} autoComplete="tel" placeholder="+39..." />
                </label>

                <label>
                  Zona corpo
                  <input name="placement" value={form.placement} onChange={handleChange} required maxLength={120} placeholder="Braccio, schiena, gamba..." />
                </label>
              </div>

              <label>
                Dimensione indicativa
                <input name="approximateSize" value={form.approximateSize} onChange={handleChange} required maxLength={120} placeholder="Es. 8 cm, mezza manica, piccolo..." />
              </label>

              <label>
                Idea del tatuaggio
                <textarea name="tattooIdea" value={form.tattooIdea} onChange={handleChange} required maxLength={1000} placeholder="Descrivi stile, soggetto, riferimenti e significato." />
              </label>

              {selectedSlot && (
                <p className="selected-slot">
                  Orario scelto: <strong>{formatLongDate(selectedSlot.date)} · {selectedSlot.startTime}</strong>
                </p>
              )}

              {error && (
                <div className="state-message error" role="alert">
                  <p>{error}</p>
                  {errorDetails.length > 0 && (
                    <ul className="error-details">
                      {errorDetails.map((detail) => (
                        <li key={detail}>{detail}</li>
                      ))}
                    </ul>
                  )}
                </div>
              )}
              {message && <p className="state-message success">{message}</p>}

              <button className="btn btn-light full" disabled={submitting}>
                {submitting ? "Conferma in corso..." : "Conferma appuntamento"}
              </button>
            </form>
          </div>
        </div>
      )}
    </section>
  );
}

export default Booking;
