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

## Pubblicazione online

Il sito è diviso in due parti pubblicate separatamente:

| Parte | Dove | Configurazione |
|---|---|---|
| Frontend (React) | Netlify | `netlify.toml` |
| Backend (Spring Boot) + database PostgreSQL | Render | `render.yaml` + `backend/Dockerfile` |

### Backend su Render

1. Su [render.com](https://render.com) crea un account e collega GitHub.
2. **New → Blueprint** e scegli questo repository: Render legge `render.yaml` e crea il servizio `miktattoo-backend` e il database `miktattoo-db`, già collegati.
3. Alla creazione vengono chiesti:
   - `CORS_ALLOWED_ORIGINS`: l'indirizzo del sito su Netlify (es. `https://miktattoo.netlify.app`); più indirizzi separati da virgola.
   - `NOTIFY_EMAIL`: l'email del tatuatore per le notifiche.
4. La password del pannello admin la genera Render: **Dashboard → miktattoo-backend → Environment → ADMIN_PASSWORD**. Username: `mik`.
5. Per l'invio vero delle email aggiungi in *Environment* `MAIL_HOST`, `MAIL_PORT`, `MAIL_USERNAME`, `MAIL_PASSWORD`, `MAIL_FROM`.

Note sul piano gratuito di Render:

- il backend si "addormenta" dopo 15 minuti senza visite: la prima richiesta dopo una pausa può richiedere fino a un minuto;
- il database PostgreSQL gratuito scade dopo 30 giorni: per il sito definitivo passare a un piano a pagamento o a un database esterno (es. Neon).

`DEMO_SEED_SLOTS=true` crea orari di esempio per l'anteprima; per il sito definitivo va messo a `false` e gli orari si gestiscono dal pannello admin.

### Frontend su Netlify

`netlify.toml` contiene già tutto: cartella `frontend`, comando di build, redirect per le pagine React e l'indirizzo del backend (`VITE_API_BASE_URL`). Se Render assegna al backend un indirizzo diverso da `https://miktattoo-backend.onrender.com`, va aggiornato lì.
