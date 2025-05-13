import { useUser, RedirectToSignIn, useAuth } from "@clerk/clerk-react";
import { useState, useEffect } from "react";
import { useNavigate } from "react-router-dom";
import {
  TextField,
  Button,
  Snackbar,
  Alert,
  Container,
  Typography,
  Box,
  CircularProgress
} from "@mui/material";
import axios from "axios";
import { useUserContext } from "../contexts/UserContext";

// Define the API base URL - adjust to match your backend
const API_URL = "http://localhost:8080/api/users";

export default function Profile() {
  const { user, isLoaded } = useUser();
  const { isProfileComplete, dbUserId, setUserData } = useUserContext();
  const [username, setUsername] = useState("");
  const [email, setEmail] = useState("");
  const [emailError, setEmailError] = useState("");
  const [isSubmitting, setIsSubmitting] = useState(false);
  const [isCheckingProfile, setIsCheckingProfile] = useState(true);
  const [notification, setNotification] = useState({
    open: false,
    message: "",
    severity: "success",
  });
  const navigate = useNavigate();

  // First check if the user already exists in the backend
  useEffect(() => {
    const checkExistingProfile = async () => {
      if (isLoaded && user) {
        const userEmail = user.primaryEmailAddress?.emailAddress;
        
        if (userEmail) {
          try {
            setIsCheckingProfile(true);
            // Check if user exists in backend by email
            const response = await axios.get(`${API_URL}/email/${userEmail}`);
            
            if (response.data && response.data.userId) {
              console.log("Found existing user profile:", response.data);
              // User exists, store their details
              setUserData({
                id: response.data.userId,
                username: response.data.userName,
                email: response.data.email
              });
              
              // Navigate to home page after short delay to allow context to update
              setTimeout(() => {
                navigate("/", { replace: true });
              }, 100);
            } else {
              // Initialize form with user data from Clerk
              setUsername(user.username || "");
              setEmail(userEmail);
            }
          } catch (error) {
            console.log("No existing profile found, showing profile form");
            // Initialize form with user data from Clerk
            setUsername(user.username || "");
            setEmail(userEmail || "");
          } finally {
            setIsCheckingProfile(false);
          }
        } else {
          setIsCheckingProfile(false);
        }
      }
    };

    if (isProfileComplete && dbUserId) {
      console.log("Profile already complete, redirecting to home");
      navigate("/", { replace: true });
    } else if (isLoaded && user) {
      checkExistingProfile();
    } else if (isLoaded) {
      setIsCheckingProfile(false);
    }
  }, [isLoaded, user, isProfileComplete, dbUserId, navigate, setUserData]);

  // Show loading state
  if (!isLoaded || isCheckingProfile) {
    return (
      <Container maxWidth="sm">
        <Box
          sx={{
            my: 4,
            display: "flex",
            flexDirection: "column",
            alignItems: "center",
            justifyContent: "center",
            minHeight: "50vh"
          }}
        >
          <CircularProgress />
          <Typography sx={{ mt: 2 }}>
            {!isLoaded ? "Loading user information..." : "Checking profile status..."}
          </Typography>
        </Box>
      </Container>
    );
  }

  // Redirect if no user
  if (!user) return <RedirectToSignIn />;

  const validateEmail = (email: string) => {
    const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    return emailRegex.test(email);
  };

  const handleEmailChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const newEmail = e.target.value;
    setEmail(newEmail);

    if (newEmail && !validateEmail(newEmail)) {
      setEmailError("Please enter a valid email address");
    } else {
      setEmailError("");
    }
  };

  const onSave = async (e: React.FormEvent) => {
    e.preventDefault();

    if (!validateEmail(email)) {
      setEmailError("Please enter a valid email address");
      return;
    }

    if (!username.trim()) {
      return; // Don't submit if username is empty
    }

    try {
      setIsSubmitting(true);

      console.log("Creating user profile with:", { userName: username, email });
      
      // Create user in your backend using axios
      const response = await axios.post(API_URL, {
        userName: username,
        email: email,
      });

      console.log("Backend response:", response.data);

      // Update the global context with user data
      setUserData({
        id: response.data.id,
        username: username,
        email: email,
      });

      console.log("Profile completed successfully");
      
      setNotification({
        open: true,
        message: "Profile updated successfully!",
        severity: "success",
      });

      // Navigate after short delay to show success notification
      setTimeout(() => {
        navigate("/", { replace: true });
      }, 1500);
    } catch (error) {
      console.error("Error saving profile:", error);
      setNotification({
        open: true,
        message: "Error updating profile. Please try again.",
        severity: "error",
      });
    } finally {
      setIsSubmitting(false);
    }
  };

  const handleCloseNotification = () => {
    setNotification({ ...notification, open: false });
  };

  return (
    <Container maxWidth="sm">
      <Box
        sx={{
          my: 4,
          display: "flex",
          flexDirection: "column",
          alignItems: "center",
        }}
      >
        <Typography component="h1" variant="h4" gutterBottom>
          Complete Your Profile
        </Typography>

        <Box component="form" onSubmit={onSave} sx={{ mt: 1, width: "100%" }}>
          <TextField
            margin="normal"
            required
            fullWidth
            id="username"
            label="Username"
            name="username"
            autoComplete="username"
            value={username}
            onChange={(e) => setUsername(e.target.value)}
            autoFocus
          />

          <TextField
            margin="normal"
            required
            fullWidth
            id="email"
            label="Email Address"
            name="email"
            autoComplete="email"
            value={email}
            onChange={handleEmailChange}
            error={!!emailError}
            helperText={emailError}
          />

          <Button
            type="submit"
            fullWidth
            variant="contained"
            sx={{ mt: 3, mb: 2 }}
            disabled={isSubmitting}
          >
            {isSubmitting ? "Saving..." : "Save & Continue"}
          </Button>
        </Box>
      </Box>

      <Snackbar
        open={notification.open}
        autoHideDuration={6000}
        onClose={handleCloseNotification}
        anchorOrigin={{ vertical: "bottom", horizontal: "center" }}
      >
        <Alert
          onClose={handleCloseNotification}
          severity={notification.severity as "success" | "error"}
          sx={{ width: "100%" }}
        >
          {notification.message}
        </Alert>
      </Snackbar>
    </Container>
  );
}