import { Link, useLocation } from "react-router-dom";
import "../styles/Home.css";

const navItems = [
  { name: "Home", path: "/" },
  { name: "Cook", path: "/cook" },
  { name: "Inventory", path: "/inventory" },
];

export const Navbar = () => {
  const location = useLocation();
  return (
    <nav className="navbar">
      <div className="navbar-content">
        <div className="logo">Snack Stack</div>
        <div className="nav-links">
          {navItems.map((item) => (
            <Link
              key={item.path}
              to={item.path}
              className={`nav-link ${
                location.pathname === item.path ? "active" : ""
              }`}
            >
              {item.name}
            </Link>
          ))}
          <button className="nav-link">Account</button>
        </div>
      </div>
    </nav>
  );
};
