// components/Profile.tsx
import { useUser, RedirectToSignIn } from "@clerk/clerk-react";
import { useState, useEffect } from "react";
import { useNavigate } from "react-router-dom";

export default function Profile() {
  const { user, isLoaded } = useUser();
  const [firstName, setFirstName] = useState("");
  const [lastName,  setLastName]  = useState("");
  const navigate = useNavigate();

  
  useEffect(() => {
    if (isLoaded && user) {
      setFirstName(user.firstName || "");
      setLastName(user.lastName   || "");
    }
  }, [isLoaded, user]);

  if (!isLoaded) return null;
  if (!user) return <RedirectToSignIn />;

  const onSave = async (e: React.FormEvent) => {
    e.preventDefault();
    await user.update({ firstName, lastName });
    navigate("/", { replace: true });
  };

  return (
    <div className="profile-container">
      <h2>Complete Your Profile</h2>
      <form onSubmit={onSave} className="profile-form">
        <label>
          First Name
        <input
            type="text"
            value={firstName}
            onChange={e => setFirstName(e.target.value)}
            required
          />
        </label>

        <label>
        Last Name
          <input
            type="text"
            value={lastName}
            onChange={e => setLastName(e.target.value)}
            required
          />
        </label>

        <button type="submit">Save &amp; Continue</button>
      </form>
    </div>
  );
}