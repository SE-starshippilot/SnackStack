import "../styles/App.css";
import InventoryManagement from "../pages/InventoryManagement";
import { Route, BrowserRouter as Router, Routes } from "react-router-dom";
import Home from "./Home";
import { Navbar } from "./Navbar";
import { Footer } from "./Footer";
import RecipeGeneration from "../pages/RecipeGeneration";
import Profile from "../components/Profile"; 


import {
  SignedIn,
  SignedOut,
  SignInButton,
  SignOutButton,
  UserButton,
} from "@clerk/clerk-react";

const disableAuth = import.meta.env.VITE_DISABLE_AUTH === "true";

function App() {
  return (
    <Router>
      <div className="container">
      <header className="header">
        <Navbar />

      {!disableAuth && (
        <div className="auth-controls">
        <SignedOut>
        <SignInButton mode="modal">
              <button className="auth-button">Sign In</button>
              </SignInButton>
            </SignedOut>

            <SignedIn>
            <UserButton />{" "}
            <SignOutButton>
                <button className="auth-button">Sign Out</button>
              </SignOutButton>
            </SignedIn>
          </div>
      )}
        </header>

        <div className="main-content">
          <Routes>
            {!disableAuth && (
            <Route path="/complete-profile" element={<Profile />} />
            )}
            <Route path="/" element={<Home />} />
            <Route path="/inventory" element={
              disableAuth ? (
              <InventoryManagement />
  ) : (
  <SignedIn>
    <InventoryManagement />
  </SignedIn>)} />

            <Route path="/cook" element={
              disableAuth ? (<RecipeGeneration />
  ) : (
    <SignedIn>
      <RecipeGeneration />
    </SignedIn>
  )} />
          </Routes>
        </div>

        <Footer />
      </div>
    </Router>
  );
}

export default App;
