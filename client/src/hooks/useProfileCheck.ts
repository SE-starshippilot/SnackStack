import { useEffect } from "react";
import { useNavigate } from "react-router-dom";
import { useUser } from "@clerk/clerk-react";
import { useUserContext } from "../contexts/UserContext";

export function useProfileCheck() {
  const { isLoaded, isSignedIn } = useUser();
  const { isProfileComplete } = useUserContext();
  const navigate = useNavigate();

  useEffect(() => {
    if (isLoaded && isSignedIn && !isProfileComplete) {
      // User is logged in but hasn't completed profile
      navigate("/complete-profile", { replace: true });
    }
  }, [isLoaded, isSignedIn, isProfileComplete, navigate]);
}