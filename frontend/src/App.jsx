import { Route, Routes, useLocation } from "react-router-dom";
import { useEffect } from "react";
import Navbar from "./components/Navbar";
import Footer from "./components/Footer";
import Home from "./pages/Home";
import Works from "./pages/Works";
import Booking from "./pages/Booking";
import About from "./pages/About";

function ScrollToTop() {
  const { pathname } = useLocation();

  useEffect(() => {
    window.scrollTo({ top: 0, behavior: "smooth" });
  }, [pathname]);

  return null;
}

function App() {
  return (
    <>
      <ScrollToTop />
      <Navbar />
      <main>
        <Routes>
          <Route path="/" element={<Home />} />
          <Route path="/lavori" element={<Works />} />
          <Route path="/prenota" element={<Booking />} />
          <Route path="/chi-sono" element={<About />} />
        </Routes>
      </main>
      <Footer />
    </>
  );
}

export default App;
