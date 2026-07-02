# Mik Tattoo Ink

Sito full-stack demo per un salone di tatuaggi moderno, minimale, bianco/nero, con font Helvetica.

## Contenuto

- `frontend`: React + Vite + React Router
- `backend`: Spring Boot REST API
- immagini esempio generate localmente in `frontend/public/images`
- dati demo in memoria per lavori, profilo, disponibilità e prenotazioni

## Requisiti

- Node.js 20+ consigliato
- Java 17+
- Maven 3.9+ oppure Maven Wrapper, se lo aggiungi in seguito

## Avvio backend

```bash
cd backend
mvn spring-boot:run
```

Backend disponibile su:

```txt
http://localhost:8080
```

Endpoint principali:

```txt
GET    /api/works
GET    /api/profile
GET    /api/availability
GET    /api/bookings
POST   /api/bookings
DELETE /api/bookings/{id}
```

Esempio POST:

```json
{
  "slotId": "slot-1",
  "name": "Mario Rossi",
  "email": "mario@email.it",
  "phone": "+39 333 0000000",
  "tattooIdea": "Serpente blackwork sull'avambraccio",
  "placement": "Avambraccio",
  "approximateSize": "12 cm"
}
```

## Avvio frontend

```bash
cd frontend
npm install
npm run dev
```

Frontend disponibile su:

```txt
http://localhost:5173
```

## Collegamento frontend/backend

Il frontend chiama di default:

```txt
http://localhost:8080/api
```

Puoi cambiarlo creando un file `frontend/.env`:

```env
VITE_API_BASE_URL=http://localhost:8080/api
```

## Nota demo

Il backend usa dati in memoria. Le prenotazioni vengono salvate finché il server resta acceso. Al riavvio, disponibilità e prenotazioni tornano allo stato iniziale.

Per passare a una versione reale, il prossimo step è aggiungere:

- database PostgreSQL o MySQL
- entity JPA
- repository Spring Data
- autenticazione admin
- invio email di conferma
- integrazione Google Calendar
