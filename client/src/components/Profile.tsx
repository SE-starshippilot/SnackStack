// components/CompleteProfile.tsx
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
        setLastName(user.lastName || "");
      
    }
  }, [isLoaded, user, navigate]);

  if (!isLoaded) return null;
  if (!user) return <RedirectToSignIn />;

  const onSave = async (e: React.FormEvent) => {
    e.preventDefault();
    await user.update({ firstName, lastName });
    navigate("/", { replace: true });
  };

  return (
    <div style={{ maxWidth: 400, margin: "2rem auto" }}>
      <h2>Complete Your Profile</h2>
      <form onSubmit={onSave}>
        <label>
          First Name
          <input
            value={firstName}
            onChange={e => setFirstName(e.target.value)}
            required
          />
        </label>
        <label style={{ marginTop: 12 }}>
          Last Name
          <input
            value={lastName}
            onChange={e => setLastName(e.target.value)}
            required
          />
        </label>
        <button type="submit" style={{ marginTop: 16 }}>
          Save & Continue
        </button>
      </form>
    </div>
  );
}
