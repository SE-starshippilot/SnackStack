// contexts/UserContext.tsx
import {
  createContext,
  useState,
  useEffect,
  useContext,
  ReactNode,
} from "react";
import axios from "axios";
import { useUser } from "@clerk/clerk-react";

interface UserContextType {
  dbUserId: number | null;
  username: string;
  email: string;
  isProfileComplete: boolean;
  setUserData: (data: { id: number; username: string; email: string }) => void;
  clearUserData: () => void;
}

export const UserContext = createContext<UserContextType | undefined>(
  undefined
);

export const UserProvider = ({ children }: { children: ReactNode }) => {
  const { user, isLoaded, isSignedIn } = useUser();
  const [dbUserId, setDbUserId] = useState<number | null>(null);
  const [username, setUsername] = useState("");
  const [email, setEmail] = useState("");
  const [isProfileComplete, setIsProfileComplete] = useState(false);

  // Clear user data
  const clearUserData = () => {
    setDbUserId(null);
    setUsername("");
    setEmail("");
    setIsProfileComplete(false);
    localStorage.removeItem("userProfile");
  };

  // Monitor Clerk authentication state
  useEffect(() => {
    if (isLoaded) {
      if (!isSignedIn) {
        // User logged out, clear data
        clearUserData();
      } else if (user) {
        // Get user's email from Clerk
        const userEmail = user.primaryEmailAddress?.emailAddress || "";

        // User is logged in, attempt to load profile from localStorage first
        const savedUser = localStorage.getItem("userProfile");
        if (savedUser) {
          try {
            const userData = JSON.parse(savedUser);
            // Verify the saved email matches the current Clerk user's email
            if (userData.email === userEmail) {
              setDbUserId(userData.id);
              setUsername(userData.username);
              setEmail(userData.email);
              setIsProfileComplete(true);
            } else {
              // Stored data is for a different user, clear it
              clearUserData();
            }
          } catch (e) {
            clearUserData();
          }
        }

        // If no profile data is loaded, check the backend
        if (!isProfileComplete && userEmail) {
          checkUserProfileByEmail(userEmail);
        }
      }
    }
  }, [isLoaded, isSignedIn, user]);

  // Check if user exists in backend by email
  const checkUserProfileByEmail = (email: string) => {
    console.log("Checking profile for email:", email);

    axios
      .get(`http://localhost:8080/api/users/email/${email}`)
      .then((response) => {
        console.log("Backend response:", response.data);

        if (response.data && response.data.userId) {
          // CHANGE: id -> userId
          // User exists, store their details
          const userData = {
            id: response.data.userId, // CHANGE: map userId to id
            username: response.data.userName, // CHANGE: map userName to username
            email: response.data.email,
          };

          console.log("Setting user data:", userData);

          setDbUserId(userData.id);
          setUsername(userData.username);
          setEmail(userData.email);
          setIsProfileComplete(true);

          // Save to localStorage for future sessions
          localStorage.setItem("userProfile", JSON.stringify(userData));
        }
      })
      .catch((error) => {
        console.error("Error checking user profile:", error);
        setIsProfileComplete(false);
      });
  };

  const setUserData = (data: {
    id: number;
    username: string;
    email: string;
  }) => {
    setDbUserId(data.id);
    setUsername(data.username);
    setEmail(data.email);
    setIsProfileComplete(true);

    // Persist to localStorage
    localStorage.setItem("userProfile", JSON.stringify(data));
  };

  return (
    <UserContext.Provider
      value={{
        dbUserId,
        username,
        email,
        isProfileComplete,
        setUserData,
        clearUserData,
      }}
    >
      {children}
    </UserContext.Provider>
  );
};

export const useUserContext = () => {
  const context = useContext(UserContext);
  if (context === undefined) {
    throw new Error("useUserContext must be used within a UserProvider");
  }
  return context;
};
