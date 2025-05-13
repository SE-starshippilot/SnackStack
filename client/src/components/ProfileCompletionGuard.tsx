import { Navigate } from "react-router-dom";
import { useUserContext } from "../contexts/UserContext";
import { useUser } from "@clerk/clerk-react";

type ProfileCompletionGuardProps = {
  children: React.ReactNode;
};

const ProfileCompletionGuard = ({ children }: ProfileCompletionGuardProps) => {
  const { isProfileComplete, dbUserId } = useUserContext();
  const { isLoaded, isSignedIn } = useUser();
  
  // Wait until Clerk has loaded
  if (!isLoaded) return <div>Loading...</div>;
  
  // If not signed in, no guard needed
  if (!isSignedIn) return <>{children}</>;
  
  // If profile is complete, show children
  if (isProfileComplete && dbUserId) return <>{children}</>;
  
  // If user is signed in but profile is not complete, redirect
  return <Navigate to="/complete-profile" />;
};

export default ProfileCompletionGuard;