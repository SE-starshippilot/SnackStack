// components/Profile.tsx
import { useUser, RedirectToSignIn } from "@clerk/clerk-react";
import { useState, useEffect } from "react";
import { useNavigate } from "react-router-dom";

import { TextField, Button, Snackbar, Alert, Container, Typography, Box } from "@mui/material";
import axios from "axios";

// Define the API base URL - adjust to match your backend
const API_URL = "http://localhost:8080/api/users";

export default function Profile() {
  const { user, isLoaded } = useUser();
  const [username, setUsername] = useState("");
  const [email, setEmail] = useState("");
  const [emailError, setEmailError] = useState("");
  const [isSubmitting, setIsSubmitting] = useState(false);
  const [notification, setNotification] = useState({ open: false, message: "", severity: "success" });
  const navigate = useNavigate();
    
  useEffect(() => {
    if (isLoaded && user) {
      setUsername(user.username || "");
      setEmail(user.primaryEmailAddress?.emailAddress || "");
    }
  }, [isLoaded, user]);

  if (!isLoaded) return null;
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
    
    try {
      setIsSubmitting(true);
      
      // Check if email needs to be updated
      const currentEmail = user.primaryEmailAddress?.emailAddress;
      if (email !== currentEmail) {
        // Note: Changing primary email in Clerk usually requires verification
        // This might require different handling depending on your Clerk setup
        console.log("Email change detected. This might require email verification in Clerk.");
      }
      
      // Create user in your backend using axios
      const response = await axios.post(API_URL, {
        userName: username,
        email: email
      });
      
      setNotification({
        open: true,
        message: "Profile updated successfully!",
        severity: "success"
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
        severity: "error"
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
      <Box sx={{ my: 4, display: 'flex', flexDirection: 'column', alignItems: 'center' }}>
        <Typography component="h1" variant="h4" gutterBottom>
          Complete Your Profile
        </Typography>
        
        <Box component="form" onSubmit={onSave} sx={{ mt: 1, width: '100%' }}>
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
        anchorOrigin={{ vertical: 'bottom', horizontal: 'center' }}
      >
        <Alert 
          onClose={handleCloseNotification} 
          severity={notification.severity as "success" | "error"} 
          sx={{ width: '100%' }}
        >
          {notification.message}
        </Alert>
      </Snackbar>
    </Container>
  );
}