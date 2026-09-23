import { useState } from "react";
import { parseIsoDate, toIsoDate, todayIso } from "../utils/dates";

const WEEKDAYS = ["Lun", "Mar", "Mer", "Gio", "Ven", "Sab", "Dom"];

function monthStart(date) {
  return new Date(date.getFullYear(), date.getMonth(), 1);
}

/**
 * Calendario mensile a quadretti.
 * getDayState(iso) restituisce { status, disabled }:
 *   status "available" = ci sono orari liberi, "full" = tutto prenotato, "none" = nessun orario.
 */
function MonthCalendar({ selectedDate, onSelect, getDayState, initialDate }) {
  const today = todayIso();
  const firstAllowedMonth = monthStart(new Date());
  const [visibleMonth, setVisibleMonth] = useState(() =>
    monthStart(initialDate ? parseIsoDate(initialDate) : new Date())
  );

  const year = visibleMonth.getFullYear();
  const month = visibleMonth.getMonth();
  const daysInMonth = new Date(year, month + 1, 0).getDate();
  // In Italia la settimana parte dal lunedì: getDay() usa 0 = domenica.
  const leadingBlanks = (visibleMonth.getDay() + 6) % 7;
  const canGoBack = visibleMonth > firstAllowedMonth;

  const title = visibleMonth.toLocaleDateString("it-IT", { month: "long", year: "numeric" });

  function changeMonth(delta) {
    setVisibleMonth(new Date(year, month + delta, 1));
  }

  const cells = [];
  for (let i = 0; i < leadingBlanks; i += 1) {
    cells.push(<span key={`blank-${i}`} className="calendar-cell blank" aria-hidden="true" />);
  }

  for (let day = 1; day <= daysInMonth; day += 1) {
    const iso = toIsoDate(new Date(year, month, day));
    const isPast = iso < today;
    const { status = "none", disabled = false } = isPast ? { disabled: true } : getDayState(iso);
    const classes = [
      "calendar-cell",
      status,
      isPast ? "past" : "",
      iso === today ? "today" : "",
      iso === selectedDate ? "active" : "",
    ].join(" ");

    cells.push(
      <button
        key={iso}
        type="button"
        className={classes}
        disabled={disabled}
        onClick={() => onSelect(iso)}
        aria-pressed={iso === selectedDate}
        aria-label={parseIsoDate(iso).toLocaleDateString("it-IT", { weekday: "long", day: "numeric", month: "long" })}
      >
        {day}
      </button>
    );
  }

  return (
    <div className="month-calendar">
      <div className="calendar-header">
        <button type="button" onClick={() => changeMonth(-1)} disabled={!canGoBack} aria-label="Mese precedente">
          ‹
        </button>
        <strong>{title}</strong>
        <button type="button" onClick={() => changeMonth(1)} aria-label="Mese successivo">
          ›
        </button>
      </div>

      <div className="calendar-grid">
        {WEEKDAYS.map((weekday) => (
          <span key={weekday} className="calendar-weekday">
            {weekday}
          </span>
        ))}
        {cells}
      </div>
    </div>
  );
}

export default MonthCalendar;
