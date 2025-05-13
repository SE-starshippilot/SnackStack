import { useState, useEffect } from "react";
import { useLocation } from "react-router-dom";
import { useUser } from "@clerk/clerk-react";
import { useUserContext } from "../contexts/UserContext";
import { useProfileCheck } from "../hooks/useProfileCheck";
import LoginPromptModal from "./LoginPromptModal";
import { Navigate } from "react-router-dom";

interface ProtectedRouteProps {
  children: React.ReactNode;
  requireLogin?: boolean;
  loginMessage?: string;
}

export default function ProtectedRoute({ 
  children, 
  requireLogin = true,
  loginMessage
}: ProtectedRouteProps) {
  const { isLoaded, isSignedIn } = useUser();
  const { isProfileComplete } = useUserContext();
  const location = useLocation();
  const [showLoginPrompt, setShowLoginPrompt] = useState(false);
  
  // Use the hook to check profile
  useProfileCheck();

  // Show login prompt when user is not signed in
  useEffect(() => {
    if (isLoaded && !isSignedIn && requireLogin) {
      setShowLoginPrompt(true);
    }
  }, [isLoaded, isSignedIn, requireLogin]);

  if (!isLoaded) {
    return <div>Loading...</div>;
  }

  if (!isSignedIn && requireLogin) {
    // Return the login prompt modal instead of redirecting
    return (
      <LoginPromptModal
        open={showLoginPrompt}
        onClose={() => setShowLoginPrompt(false)}
        message={loginMessage}
        returnTo={location.pathname}
      />
    );
  }

  if (isSignedIn && !isProfileComplete) {
    return <Navigate to="/complete-profile" replace />;
  }

  return <>{children}</>;
}