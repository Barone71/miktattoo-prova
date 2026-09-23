import { useEffect, useState } from "react";
import { getWorks } from "../services/api";

function Works() {
  const [works, setWorks] = useState([]);
  const [activeStyle, setActiveStyle] = useState("Tutti");
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");

  useEffect(() => {
    getWorks()
      .then(setWorks)
      .catch((err) => setError(err.message))
      .finally(() => setLoading(false));
  }, []);

  const styles = ["Tutti", ...new Set(works.map((work) => work.style))];
  const filteredWorks = activeStyle === "Tutti" ? works : works.filter((work) => work.style === activeStyle);

  return (
    <section className="page works-page">
      <div className="page-header">
        <h1>Lavori</h1>
      </div>

      {loading && <p className="state-message">Caricamento lavori...</p>}
      {error && <p className="state-message error">{error}</p>}

      {!loading && !error && (
        <>
          <div className="filter-row">
            {styles.map((style) => (
              <button
                key={style}
                className={activeStyle === style ? "active" : ""}
                onClick={() => setActiveStyle(style)}
              >
                {style}
              </button>
            ))}
          </div>

          <div className="gallery-grid">
            {filteredWorks.map((work) => (
              <article key={work.id} className="gallery-card">
                <img src={work.imageUrl} alt={work.title} />
                <div className="gallery-info">
                  <h3>{work.title}</h3>
                  <span>{work.style}</span>
                </div>
              </article>
            ))}
          </div>
        </>
      )}
    </section>
  );
}

export default Works;
