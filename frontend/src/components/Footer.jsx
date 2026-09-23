import { Link } from "react-router-dom";
import SocialLinks from "./SocialLinks";

function Footer() {
  return (
    <footer className="footer">
      <div className="footer-brand">
        <Link to="/" aria-label="Vai alla home di Mik Tattoo Ink">
          <img className="footer-logo" src="/images/logo.webp" alt="Mik Tattoo Ink" width="960" height="235" loading="lazy" />
        </Link>
      </div>

      <SocialLinks className="footer-socials" />

      <div className="footer-right">
        <p>Vico del Gargano, Italia</p>
        <p>© 2026</p>
      </div>
    </footer>
  );
}

export default Footer;
