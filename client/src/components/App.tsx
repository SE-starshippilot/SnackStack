import { Route, BrowserRouter as Router, Routes } from "react-router-dom";
import Profile from "../components/Profile";
import InventoryManagement from "../pages/InventoryManagement";
import RecipeGeneration from "../pages/RecipeGeneration";
import "../styles/App.css";
import { Footer } from "./Footer";
import Home from "./Home";
import { Navbar } from "./Navbar";
import RecipesPage from "../pages/RecipesPage";
import HistoryRecipes from "../pages/RecipeHistory";
import ProtectedRoute from "../components/ProtectedRoute";
import { UserProvider } from "../contexts/UserContext";

const disableAuth = import.meta.env.VITE_DISABLE_AUTH === "true";

function App() {
  return (
    <Router>
      <UserProvider>
        <div className="container">
          <header>
            <Navbar disableAuth={disableAuth} />
          </header>

          <div className="main-content">
            <Routes>
              {/* Public routes */}
              <Route path="/" element={<Home />} />
              {!disableAuth && (
                <Route path="/complete-profile" element={<Profile />} />
              )}
              
              {/* Protected routes */}
              <Route
                path="/recipes"
                element={
                  disableAuth ? (
                    <RecipesPage />
                  ) : (
                    <ProtectedRoute>
                      <RecipesPage />
                    </ProtectedRoute>
                  )
                }
              />
              
              <Route
                path="/history"
                element={
                  disableAuth ? (
                    <HistoryRecipes />
                  ) : (
                    <ProtectedRoute>
                      <HistoryRecipes />
                    </ProtectedRoute>
                  )
                }
              />

              <Route
                path="/inventory"
                element={
                  disableAuth ? (
                    <InventoryManagement />
                  ) : (
                    <ProtectedRoute>
                      <InventoryManagement />
                    </ProtectedRoute>
                  )
                }
              />

              <Route
                path="/cook"
                element={
                  disableAuth ? (
                    <RecipeGeneration />
                  ) : (
                    <ProtectedRoute>
                      <RecipeGeneration />
                    </ProtectedRoute>
                  )
                }
              />
            </Routes>
          </div>

          <Footer />
        </div>
      </UserProvider>
    </Router>
  );
}

export default App;