import "../styles/App.css";
import InventoryManagement from "../pages/InventoryManagement";
import { Route, BrowserRouter as Router, Routes } from "react-router-dom";
import Home from "./Home";
import { Navbar } from "./Navbar";
import { Footer } from "./Footer";
import RecipeGeneration from "../pages/RecipeGeneration";

function App() {
  return (
    <Router>
      <div className="container">
        <Navbar />
        <div className="main-content">
          <Routes>
            <Route path="/" element={<Home />} />
            <Route path="/inventory" element={<InventoryManagement />} />
            <Route path="/cook" element={<RecipeGeneration />} />
          </Routes>
        </div>
        <Footer />
      </div>
    </Router>
  );
}

export default App;
