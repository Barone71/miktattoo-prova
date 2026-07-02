import { NavLink, Link } from "react-router-dom";

function Navbar() {
  return (
    <header className="navbar">
      <Link to="/" className="logo" aria-label="Vai alla home di Mik Tattoo Ink">
        <span>Mik</span>
        <span>Tattoo Ink</span>
      </Link>

      <nav className="nav-links" aria-label="Navigazione principale">
        <NavLink to="/lavori">Lavori</NavLink>
        <NavLink to="/prenota">Prenota</NavLink>
        <NavLink to="/chi-sono">Chi sono</NavLink>
      </nav>
    </header>
  );
}

export default Navbar;
