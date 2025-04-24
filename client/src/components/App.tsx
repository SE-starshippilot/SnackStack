import "../styles/App.css";
import InventoryManagement from './InventoryManagement';
import { Route, BrowserRouter as Router, Routes } from 'react-router-dom';
import Home from './Home';

function App() {
  return (
    <Router>
      <Routes>
        <Route path="/" element={<Home />} />
        <Route path="/inventory" element={<InventoryManagement />} />
      </Routes>
    </Router>
  );
}

export default App;
