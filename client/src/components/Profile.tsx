import React, { useState } from "react";
import { useUser, RedirectToSignIn } from "@clerk/clerk-react";
import { useNavigate } from "react-router-dom";

export default function CompleteProfile() {
  const { user, isLoaded } = useUser();
  const [firstName, setFirstName] = useState("");
  const [lastName, setLastName] = useState("");
  const navigate = useNavigate();
  if (!isLoaded) return null;
  if (!user) return <RedirectToSignIn />;

  // Always show profile form, even if names already exist
  React.useEffect(() => {
    setFirstName(user.firstName || "");
    setLastName(user.lastName || "");
  }, [user.firstName, user.lastName]);

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    await user.update({ firstName, lastName });
    navigate("/", { replace: true });
  };

  return (
    <div style={{ maxWidth: 400, margin: "2rem auto" }}>
      <h2>Complete Your Profile</h2>
      <form onSubmit={handleSubmit}>
        <label style={{ display: "block", marginBottom: 8 }}>
          First Name
          <input
            type="text"
            value={firstName}
            onChange={(e) => setFirstName(e.target.value)}
            required
            style={{ width: "100%", padding: 8, marginTop: 4 }}
          />
        </label>

        <label style={{ display: "block", marginBottom: 16 }}>
          Last Name
          <input
            type="text"
            value={lastName}
            onChange={(e) => setLastName(e.target.value)}
            required
            style={{ width: "100%", padding: 8, marginTop: 4 }}
          />
        </label>

        <button type="submit" style={{ padding: "8px 16px" }}>
          Save & Continue
        </button>
      </form>
    </div>
  );
}
