import { useEffect, useMemo, useState } from "react";
import MonthCalendar from "./MonthCalendar";
import { clearAdminDay, createAdminSlot, deleteAdminBooking, deleteAdminSlot, getAdminSlots } from "../services/api";
import { addMinutes, formatLongDate, todayIso } from "../utils/dates";

const DURATIONS = [30, 45, 60, 90, 120, 180, 240, 300, 360, 480];
const TYPICAL_DAY = ["10:00", "11:30", "15:00", "17:30"];
const TYPICAL_DURATION = 45;

function formatDuration(minutes) {
  if (minutes < 60) return `${minutes} min`;
  const hours = Math.floor(minutes / 60);
  const rest = minutes % 60;
  return rest ? `${hours} h ${rest} min` : `${hours} h`;
}

function AdminAvailability({ credentials, onError }) {
  const [slots, setSlots] = useState([]);
  const [loading, setLoading] = useState(true);
  const [busy, setBusy] = useState(false);
  const [selectedDate, setSelectedDate] = useState(todayIso);
  const [startTime, setStartTime] = useState("10:00");
  const [duration, setDuration] = useState(TYPICAL_DURATION);
  const [notice, setNotice] = useState("");

  async function loadSlots() {
    try {
      setSlots(await getAdminSlots(credentials));
    } catch (err) {
      onError(err);
    } finally {
      setLoading(false);
    }
  }

  useEffect(() => {
    loadSlots();
  }, []);

  const daySummary = useMemo(() => {
    const summary = {};
    slots.forEach((slot) => {
      summary[slot.date] ??= { total: 0, free: 0 };
      summary[slot.date].total += 1;
      if (!slot.booked) summary[slot.date].free += 1;
    });
    return summary;
  }, [slots]);

  // Nel pannello ogni giorno futuro è cliccabile, anche se non ha ancora orari.
  function getDayState(iso) {
    const day = daySummary[iso];
    if (!day) return { status: "none", disabled: false };
    return { status: day.free > 0 ? "available" : "full", disabled: false };
  }

  const daySlots = slots.filter((slot) => slot.date === selectedDate);
  const endTime = addMinutes(startTime, duration);

  async function run(action, successMessage) {
    setBusy(true);
    setNotice("");
    try {
      await action();
      await loadSlots();
      if (successMessage) setNotice(successMessage);
    } catch (err) {
      onError(err);
    } finally {
      setBusy(false);
    }
  }

  function handleAdd(event) {
    event.preventDefault();
    if (!endTime) {
      onError(new Error("L'orario deve finire entro la mezzanotte."));
      return;
    }
    run(
      () => createAdminSlot(credentials, { date: selectedDate, startTime, endTime }),
      `Aggiunto ${startTime} – ${endTime}.`
    );
  }

  function handleTypicalDay() {
    run(async () => {
      // Aggiunge gli orari tipici saltando quelli che si sovrappongono a orari già presenti.
      for (const start of TYPICAL_DAY) {
        try {
          await createAdminSlot(credentials, {
            date: selectedDate,
            startTime: start,
            endTime: addMinutes(start, TYPICAL_DURATION),
          });
        } catch (err) {
          if (err.message === "Credenziali non valide") throw err;
        }
      }
    }, "Giornata tipo aggiunta.");
  }

  function handleDelete(slot) {
    run(() => deleteAdminSlot(credentials, slot.id), `Tolto l'orario delle ${slot.startTime}.`);
  }

  function handleCancelBooking(slot) {
    const confirmed = window.confirm(
      `Cancellare la prenotazione di ${slot.bookedBy} del ${formatLongDate(slot.date)} alle ${slot.startTime}?\nL'orario tornerà libero e prenotabile dai clienti.`
    );
    if (!confirmed) return;
    run(
      () => deleteAdminBooking(credentials, slot.bookingId),
      `Prenotazione di ${slot.bookedBy} cancellata: l'orario delle ${slot.startTime} è di nuovo libero.`
    );
  }

  function handleClearDay() {
    const confirmed = window.confirm(
      `Rendere ${formatLongDate(selectedDate)} non disponibile?\nVerranno tolti tutti gli orari liberi. Quelli già prenotati restano.`
    );
    if (!confirmed) return;
    run(() => clearAdminDay(credentials, selectedDate), "Giorno reso non disponibile.");
  }

  if (loading) {
    return <p className="state-message">Caricamento orari...</p>;
  }

  const freeCount = daySlots.filter((slot) => !slot.booked).length;

  return (
    <div className="booking-layout">
      <div className="calendar-box">
        <h2>Calendario</h2>
        <MonthCalendar
          selectedDate={selectedDate}
          initialDate={selectedDate}
          getDayState={getDayState}
          onSelect={(iso) => {
            setSelectedDate(iso);
            setNotice("");
          }}
        />
        <div className="calendar-legend">
          <span><i className="legend-dot available" /> Con orari liberi</span>
          <span><i className="legend-dot full" /> Tutto prenotato</span>
        </div>
      </div>

      <div className="slots-box">
        <h2>Orari del giorno</h2>
        <p className="slots-day">{formatLongDate(selectedDate)}</p>

        {daySlots.length === 0 ? (
          <p className="state-message">Nessun orario: in questo giorno i clienti non possono prenotare.</p>
        ) : (
          <ul className="admin-slot-list">
            {daySlots.map((slot) => (
              <li key={slot.id} className={slot.booked ? "booked" : ""}>
                <strong>
                  {slot.startTime} – {slot.endTime}
                </strong>
                <span>{slot.booked ? `Prenotato da ${slot.bookedBy}` : "Libero"}</span>
                {slot.booked ? (
                  <button type="button" className="btn btn-danger btn-small" onClick={() => handleCancelBooking(slot)} disabled={busy}>
                    Cancella prenotazione
                  </button>
                ) : (
                  <button type="button" className="btn btn-danger btn-small" onClick={() => handleDelete(slot)} disabled={busy}>
                    Togli
                  </button>
                )}
              </li>
            ))}
          </ul>
        )}

        <form className="admin-slot-form" onSubmit={handleAdd}>
          <label>
            Inizio
            <input type="time" value={startTime} onChange={(e) => setStartTime(e.target.value)} required />
          </label>

          <label>
            Durata
            <select value={duration} onChange={(e) => setDuration(Number(e.target.value))}>
              {DURATIONS.map((minutes) => (
                <option key={minutes} value={minutes}>
                  {formatDuration(minutes)}
                </option>
              ))}
            </select>
          </label>

          <button className="btn btn-light" disabled={busy || !startTime}>
            Aggiungi {startTime && endTime ? `${startTime} – ${endTime}` : "orario"}
          </button>
        </form>

        <div className="admin-day-actions">
          <button type="button" className="btn btn-outline" onClick={handleTypicalDay} disabled={busy}>
            Aggiungi giornata tipo ({TYPICAL_DAY.join(", ")})
          </button>
          {freeCount > 0 && (
            <button type="button" className="btn btn-danger" onClick={handleClearDay} disabled={busy}>
              Rendi il giorno non disponibile
            </button>
          )}
        </div>

        {notice && <p className="state-message success">{notice}</p>}
      </div>
    </div>
  );
}

export default AdminAvailability;
