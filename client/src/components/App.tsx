import { Route, BrowserRouter as Router, Routes } from "react-router-dom";
import Profile from "../components/Profile";
import InventoryManagement from "../pages/InventoryManagement";
import RecipeGeneration from "../pages/RecipeGeneration";
import "../styles/App.css";
import { Footer } from "./Footer";
import Home from "./Home";
import { Navbar } from "./Navbar";

import { SignedIn } from "@clerk/clerk-react";

const disableAuth = import.meta.env.VITE_DISABLE_AUTH === "true";

function App() {
  return (
    <Router>
      <div className="container">
        {/* only render Navbar; it now handles auth-controls itself */}
        <header>
          <Navbar disableAuth={disableAuth} />
        </header>

        <div className="main-content">
          <Routes>
            {!disableAuth && (
              <Route path="/complete-profile" element={<Profile />} />
            )}
            <Route path="/" element={<Home />} />

            <Route
              path="/inventory"
              element={
                disableAuth ? (
                  <InventoryManagement />
                ) : (
                  <SignedIn>
                    <InventoryManagement />
                  </SignedIn>
                )
              }
            />

            <Route
              path="/cook"
              element={
                disableAuth ? (
                  <RecipeGeneration />
                ) : (
                  <SignedIn>
                    <RecipeGeneration />
                  </SignedIn>
                )
              }
            />
          </Routes>
        </div>

        <Footer />
      </div>
    </Router>
  );
}

export default App;
