import { Link } from "react-router-dom";

const STYLES = ["Blackwork", "Fine Line", "Realistic", "Lettering", "Custom", "Black & Grey"];
// L'elenco è ripetuto più volte per riempire anche gli schermi molto larghi
const MARQUEE_REPEAT = 3;

function MarqueeGroup({ hidden = false }) {
  return (
    <span className="marquee-group" aria-hidden={hidden || undefined}>
      {Array.from({ length: MARQUEE_REPEAT }, () => STYLES)
        .flat()
        .map((style, index) => (
          <span key={index}>{style} ·</span>
        ))}
    </span>
  );
}

function Home() {
  return (
    <section className="home-page">
      <section className="hero">
        <div className="hero-content reveal">
          <h1>
            Mik Tattoo <br /> Ink
          </h1>

          <p className="hero-text">
            Tatuaggi custom, linee pulite, nero profondo e identità forte. Ogni progetto nasce da una consulenza e diventa un segno progettato su misura.
          </p>

          <div className="hero-actions">
            <Link to="/prenota" className="btn btn-light">Prendi un appuntamento</Link>
            <Link to="/lavori" className="btn btn-outline">Guarda i lavori</Link>
          </div>
        </div>

        <div className="hero-image reveal delay-1">
          <img src="/images/hero-mik.jpg" alt="Mik al lavoro su un tatuaggio" />
        </div>
      </section>

      <section className="marquee" aria-label="Stili tatuaggio">
        {/* Due copie identiche: quando la prima esce del tutto, l'animazione riparte senza salti */}
        <div className="marquee-track">
          <MarqueeGroup />
          <MarqueeGroup hidden />
        </div>
      </section>

      <section className="intro-section">
        <div>
          <h2>Un tatuaggio non è decorazione. È presenza.</h2>
          <p>
            Mik Tattoo Ink è uno spazio essenziale, diretto, pensato per chi cerca un tatuaggio personale, curato e costruito con attenzione. Dal primo confronto fino alla seduta, ogni dettaglio viene seguito con precisione.
          </p>
        </div>
      </section>

      <section className="featured-strip">
        <img src="/images/work-1.jpg" alt="Tattoo blackwork serpente" />
        <img src="/images/work-5.jpg" alt="Tattoo leone black & grey" />
        <img src="/images/work-4.jpg" alt="Tattoo occhio realistico" />
      </section>

      <section className="cta-section">
        <h2>Hai un’idea da trasformare in pelle?</h2>
        <Link to="/prenota" className="btn btn-light">Prenota ora</Link>
      </section>
    </section>
  );
}

export default Home;
