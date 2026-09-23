// Date in formato "YYYY-MM-DD", sempre nel fuso orario locale
// (new Date("2026-09-25") verrebbe letto come UTC e potrebbe slittare di un giorno).

function pad(value) {
  return String(value).padStart(2, "0");
}

export function toIsoDate(date) {
  return `${date.getFullYear()}-${pad(date.getMonth() + 1)}-${pad(date.getDate())}`;
}

export function parseIsoDate(iso) {
  const [year, month, day] = iso.split("-").map(Number);
  return new Date(year, month - 1, day);
}

export function todayIso() {
  return toIsoDate(new Date());
}

export function formatLongDate(iso) {
  return parseIsoDate(iso).toLocaleDateString("it-IT", {
    weekday: "long",
    day: "numeric",
    month: "long",
  });
}

export function addMinutes(time, minutes) {
  const [hours, mins] = time.split(":").map(Number);
  const total = hours * 60 + mins + minutes;
  if (total >= 24 * 60) {
    return null;
  }
  return `${pad(Math.floor(total / 60))}:${pad(total % 60)}`;
}
