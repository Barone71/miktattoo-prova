import { Link } from "react-router-dom";

function NotFound() {
  return (
    <section className="page not-found-page">
      <div className="page-header">
        <p className="eyebrow">Errore 404</p>
        <h1>Pagina non trovata</h1>
        <p>L'indirizzo che hai aperto non esiste o è stato spostato.</p>
      </div>

      <div className="hero-actions">
        <Link to="/" className="btn btn-light">Torna alla home</Link>
        <Link to="/lavori" className="btn btn-outline">Guarda i lavori</Link>
      </div>
    </section>
  );
}

export default NotFound;
