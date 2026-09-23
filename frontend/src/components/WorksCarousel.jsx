import { useEffect, useRef, useState } from "react";
import { Link } from "react-router-dom";
import { getWorks } from "../services/api";

/**
 * Carosello orizzontale con tutti i lavori: frecce su desktop,
 * scorrimento col dito su telefono e trackpad.
 */
function WorksCarousel() {
  const trackRef = useRef(null);
  const [works, setWorks] = useState([]);
  const [canPrev, setCanPrev] = useState(false);
  const [canNext, setCanNext] = useState(false);

  useEffect(() => {
    getWorks()
      .then(setWorks)
      .catch(() => setWorks([]));
  }, []);

  function updateArrows() {
    const track = trackRef.current;
    if (!track) return;
    setCanPrev(track.scrollLeft > 4);
    setCanNext(track.scrollLeft + track.clientWidth < track.scrollWidth - 4);
  }

  useEffect(() => {
    updateArrows();
    window.addEventListener("resize", updateArrows);
    return () => window.removeEventListener("resize", updateArrows);
  }, [works]);

  function scrollByCard(direction) {
    const track = trackRef.current;
    const card = track?.querySelector(".carousel-item");
    if (!card) return;
    const step = card.getBoundingClientRect().width + 1; // 1px = spazio tra le foto
    track.scrollBy({ left: direction * step, behavior: "smooth" });
  }

  // Se i lavori non si caricano, la sezione semplicemente non compare.
  if (works.length === 0) return null;

  return (
    <section className="works-carousel" aria-label="Lavori">
      <div className="carousel-track" ref={trackRef} onScroll={updateArrows} tabIndex={0}>
        {works.map((work) => (
          <Link key={work.id} to="/lavori" className="carousel-item">
            <img src={work.imageUrl} alt={work.title} loading="lazy" />
            <span className="carousel-caption">
              <strong>{work.title}</strong>
              <small>{work.style}</small>
            </span>
          </Link>
        ))}
      </div>

      <button
        type="button"
        className="carousel-arrow prev"
        onClick={() => scrollByCard(-1)}
        disabled={!canPrev}
        aria-label="Lavoro precedente"
      >
        ‹
      </button>
      <button
        type="button"
        className="carousel-arrow next"
        onClick={() => scrollByCard(1)}
        disabled={!canNext}
        aria-label="Lavoro successivo"
      >
        ›
      </button>
    </section>
  );
}

export default WorksCarousel;
