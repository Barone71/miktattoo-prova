import { Link } from "react-router-dom";

function Home() {
  return (
    <section className="home-page">
      <section className="hero">
        <div className="hero-content reveal">
          <p className="eyebrow">Tattoo studio · Vico del Gargano</p>

          <h1>
            Mik Tattoo <br /> Ink
          </h1>

          <p className="hero-text">
            Tatuaggi custom, linee pulite, nero profondo e identità forte. Ogni progetto nasce da una consulenza e diventa un segno progettato su misura.
          </p>

          <div className="hero-actions">
            <Link to="/prenota" className="btn btn-light">Prenota consulenza</Link>
            <Link to="/lavori" className="btn btn-outline">Guarda i lavori</Link>
          </div>
        </div>

        <div className="hero-image reveal delay-1">
          <img src="/images/hero-tattoo.png" alt="Esempio artistico Mik Tattoo Ink" />
        </div>
      </section>

      <section className="marquee" aria-label="Stili tatuaggio">
        <div>
          Blackwork · Fine Line · Realistic · Lettering · Custom · Black & Grey ·
        </div>
      </section>

      <section className="intro-section">
        <p className="section-number">01</p>
        <div>
          <h2>Un tatuaggio non è decorazione. È presenza.</h2>
          <p>
            Mik Tattoo Ink è uno spazio essenziale, diretto, pensato per chi cerca un tatuaggio personale, curato e costruito con attenzione. Dal primo confronto fino alla seduta, ogni dettaglio viene seguito con precisione.
          </p>
        </div>
      </section>

      <section className="services-grid">
        <article>
          <span>Blackwork</span>
          <p>Contrasti netti, campiture piene e composizioni decise.</p>
        </article>

        <article>
          <span>Fine Line</span>
          <p>Linee leggere, segni eleganti e dettagli essenziali.</p>
        </article>

        <article>
          <span>Custom</span>
          <p>Disegni creati da zero partendo dalla tua idea.</p>
        </article>
      </section>

      <section className="featured-strip">
        <img src="/images/tattoo-1.png" alt="Tattoo blackwork snake" />
        <img src="/images/tattoo-2.png" alt="Tattoo fine line angel" />
        <img src="/images/tattoo-3.png" alt="Tattoo realistic eye" />
      </section>

      <section className="cta-section">
        <p className="eyebrow">Next tattoo</p>
        <h2>Hai un’idea da trasformare in pelle?</h2>
        <Link to="/prenota" className="btn btn-light">Blocca una consulenza</Link>
      </section>
    </section>
  );
}

export default Home;
