# Mik Tattoo Ink

Sito full-stack demo per un salone di tatuaggi moderno, minimale, bianco/nero, con font Helvetica.

## Contenuto

- `frontend`: React + Vite + React Router
- `backend`: Spring Boot REST API
- immagini esempio generate localmente in `frontend/public/images`
- database per orari e prenotazioni (H2 in locale, PostgreSQL online)
- pannello admin protetto per il tatuatore

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
POST   /api/bookings
GET    /api/admin/bookings        (richiede login admin)
DELETE /api/admin/bookings/{id}   (richiede login admin)
GET    /api/admin/slots           (richiede login admin)
POST   /api/admin/slots           (richiede login admin)
DELETE /api/admin/slots/{id}      (richiede login admin)
DELETE /api/admin/days/{data}/slots (richiede login admin: toglie gli orari liberi del giorno)
```

Esempio POST:

```json
{
  "slotId": "1",
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

## Database

Orari e prenotazioni sono salvati in un database e restano anche dopo il riavvio del server.

- **In locale**: H2 su file (`backend/data/`, escluso da git). Non serve installare nulla.
- **Online**: PostgreSQL. Imposta le variabili ambiente `SPRING_DATASOURCE_URL` (es. `jdbc:postgresql://host:5432/nome_db`), `SPRING_DATASOURCE_USERNAME` e `SPRING_DATASOURCE_PASSWORD`.

Le tabelle vengono create in automatico da Flyway con gli script in `backend/src/main/resources/db/migration`. Per modificare lo schema si aggiunge un nuovo file (`V2__...sql`), senza mai modificare quelli già applicati.

Gli orari prenotabili li aggiunge e li toglie il tatuatore dal pannello admin.

## Pannello admin e impostazioni private

La pagina `/admin` del sito è protetta da username e password: solo il tatuatore può vedere e cancellare le prenotazioni e aggiungere o togliere giorni e orari disponibili.

In locale le impostazioni private stanno in `backend/.env.properties` (escluso da git):

```properties
ADMIN_USERNAME=mik
ADMIN_PASSWORD=una-password-lunga-almeno-12-caratteri
NOTIFY_EMAIL=email-del-tatuatore@example.com

# Invio email via SMTP (es. Brevo: smtp-relay.brevo.com)
MAIL_HOST=smtp-relay.brevo.com
MAIL_PORT=587
MAIL_USERNAME=login-smtp
MAIL_PASSWORD=chiave-smtp
MAIL_FROM=indirizzo-mittente-verificato@example.com
```

A ogni nuova prenotazione il tatuatore riceve un'email a `NOTIFY_EMAIL` con tutti i dettagli; rispondendo, scrive direttamente al cliente. Finché `MAIL_HOST` è vuoto l'email non viene spedita ma scritta nel log del server.

Online si impostano le stesse voci come variabili ambiente del servizio di hosting. Il sito deve essere servito in HTTPS.

## Prossimi passi

- pagamento della prenotazione (50 €) con Stripe

## Deploy su Netlify

Il repository contiene sia `frontend` sia `backend`. Netlify deve pubblicare solo il frontend statico.

Configurazione consigliata:

- Base directory: `frontend`
- Build command: `npm run build`
- Publish directory: `dist`

Nel progetto è già presente `netlify.toml` alla root:

```toml
[build]
  base = "frontend"
  command = "npm run build"
  publish = "dist"

[[redirects]]
  from = "/*"
  to = "/index.html"
  status = 200
```

È presente anche `frontend/public/_redirects` per evitare errori 404 sulle rotte React Router come `/lavori`, `/prenota` e `/chi-sono`.

Nota: Netlify ospita il frontend. Il backend Spring Boot va pubblicato separatamente, per esempio su Render, Railway, Fly.io o VPS. Quando avrai l'URL del backend, impostalo su Netlify come variabile ambiente:

```txt
VITE_API_BASE_URL=https://tuo-backend.example.com/api
```
