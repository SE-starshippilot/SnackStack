import React from "react";
import ReactDOM from "react-dom/client";
import App from "./components/App";
import "./styles/index.css";
import { ClerkProvider } from "@clerk/clerk-react";


// Import your publishable key
const PUBLISHABLE_KEY = import.meta.env.VITE_CLERK_PUBLISHABLE_KEY


if (!PUBLISHABLE_KEY) {
  throw new Error('Missing Publishable Key')
}


ReactDOM.createRoot(document.getElementById("root")!).render(
  <React.StrictMode>
    <ClerkProvider
      publishableKey={PUBLISHABLE_KEY}
      afterSignInUrl="/complete-profile"
      afterSignOutUrl="/"
    >
      <App />
    </ClerkProvider>
  </React.StrictMode>
);
