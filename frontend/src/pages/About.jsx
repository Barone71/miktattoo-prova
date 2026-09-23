import { useEffect, useState } from "react";
import SocialLinks from "../components/SocialLinks";
import { getProfile } from "../services/api";

function About() {
  const [profile, setProfile] = useState(null);
  const [error, setError] = useState("");

  useEffect(() => {
    getProfile()
      .then(setProfile)
      .catch((err) => setError(err.message));
  }, []);

  if (error) {
    return <section className="page"><p className="state-message error">{error}</p></section>;
  }

  if (!profile) {
    return <section className="page"><p className="state-message">Caricamento profilo...</p></section>;
  }

  return (
    <section className="page about-page">
      <div className="about-image" aria-hidden="true">
        <img src={profile.imageUrl} alt="" />
      </div>

      <div className="about-layout">

        <div className="about-content">
          <h1>{profile.name}</h1>

          {profile.bio.map((paragraph) => (
            <p key={paragraph}>{paragraph}</p>
          ))}

          <div className="contact-box">
            <h2>Contatti</h2>
            <p>Email: {profile.email}</p>
            <p>Telefono: {profile.phone}</p>
            <p>Studio: {profile.location}</p>
          </div>

          <SocialLinks />
        </div>
      </div>
    </section>
  );
}

export default About;
