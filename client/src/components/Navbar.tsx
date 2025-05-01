import { Link, useLocation } from "react-router-dom";
import "../styles/Home.css";

import {
  SignedIn,
  SignedOut,
  SignInButton,
  SignOutButton,
  UserButton,
} from "@clerk/clerk-react";

const navItems = [
  { name: "Home", path: "/" },
  { name: "Cook", path: "/cook" },
  { name: "Inventory", path: "/inventory" },
];

interface NavbarProps {
  disableAuth: boolean;
}

export const Navbar = ({ disableAuth }: NavbarProps) => {
  const location = useLocation();

  return (
    <nav className="navbar">
      <div className="navbar-content">
        {/* Left: your logo */}
        <div className="logo">Snack Stack</div>

        {/* Right: everything else */}
        <div className="links-wrapper">
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
          </div>

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
        </div>
      </div>
    </nav>
  );
};