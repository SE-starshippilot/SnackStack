import { useState, useEffect } from "react";
import { useNavigate } from "react-router-dom";
import { useClerk } from "@clerk/clerk-react";
import {
  Dialog,
  DialogTitle,
  DialogContent,
  DialogActions,
  Button,
  Typography,
  Box,
} from "@mui/material";

interface LoginPromptModalProps {
  open: boolean;
  onClose: () => void;
  message?: string;
  returnTo?: string;
}

export default function LoginPromptModal({
  open,
  onClose,
  message = "You need to be logged in to access this feature.",
  returnTo,
}: LoginPromptModalProps) {
  const navigate = useNavigate();
  const { openSignIn } = useClerk();
  const [isClosing, setIsClosing] = useState(false);

  // If dialog is closing, reset state after animation completes
  useEffect(() => {
    if (isClosing) {
      const timer = setTimeout(() => {
        setIsClosing(false);
        onClose();
      }, 300); // Match your dialog close animation duration
      return () => clearTimeout(timer);
    }
  }, [isClosing, onClose]);

  const handleClose = () => {
    setIsClosing(true);
  };

  const handleLogin = () => {
    // Close modal first
    setIsClosing(true);
    
    // Use setTimeout to ensure modal closes before redirect
    setTimeout(() => {
      // Open Clerk's sign-in dialog with a returnUrl if provided
      openSignIn({
        redirectUrl: returnTo || window.location.pathname,
      });
    }, 300);
  };

  const handleCancel = () => {
    setIsClosing(true);
    
    // Redirect to home after dialog closes
    setTimeout(() => {
      navigate("/");
    }, 300);
  };

  return (
    <Dialog 
      open={open && !isClosing} 
      onClose={handleClose}
      maxWidth="sm"
      fullWidth
    >
      <DialogTitle>Sign in required</DialogTitle>
      <DialogContent>
        <Box sx={{ py: 1 }}>
          <Typography variant="body1">
            {message}
          </Typography>
        </Box>
      </DialogContent>
      <DialogActions>
        <Button onClick={handleCancel} color="inherit">
          Cancel
        </Button>
        <Button onClick={handleLogin} variant="contained" color="primary" autoFocus>
          Sign In
        </Button>
      </DialogActions>
    </Dialog>
  );
}