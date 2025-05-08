import KitchenOutlinedIcon from "@mui/icons-material/KitchenOutlined";
import RestaurantMenuIcon from "@mui/icons-material/RestaurantMenu";
import {
  Card,
  CardActionArea,
  CardContent,
  Typography,
  Button,
} from "@mui/material";
import { green } from "@mui/material/colors";
import { Link } from "react-router-dom";
import "../styles/Home.css";
import { useUser } from "@clerk/clerk-react";
import { useEffect } from "react";

function Home() {
  const { user, isLoaded } = useUser();

  useEffect(() => {
    if (isLoaded && user) {
      const saveUserIfNotExists = async () => {
        const username = user.username || user.firstName || "unknown_user";
        const email = user.primaryEmailAddress?.emailAddress || "no_email";

        try {
          const res = await fetch(`http://localhost:8080/api/users/${username}/id`);

          // user does not exist
          if (res.status === 404) {
            const postRes = await fetch("http://localhost:8080/api/users", {
              method: "POST",
              headers: {
                "Content-Type": "application/json",
              },
              body: JSON.stringify({ userName: username, email }),
            });

            if (!postRes.ok) {
              console.error("Failed to create user:", postRes.status);
            } else {
              console.log("User created successfully");
            }
          } else if (res.ok) {
            console.log("User already exists, skipping creation.");
          } else {
            console.error("Unexpected response when checking user:", res.status);
          }
        } catch (err) {
          console.error("Error checking or creating user:", err);
        }
      };

      saveUserIfNotExists();
    }
  }, [isLoaded, user]);


  return (
    <>
      <div className="hero-section">
        <h1 className="main-title">
          Transform Your Ingredients into Delicious Meals
        </h1>
        <p className="subtitle">
          Discover personalized recipes based on what you already have in your
          kitchen. No more wasted ingredients or last-minute grocery runs.
        </p>
      </div>

      <div className="feature-cards">
        <Link to="/cook" className="feature-card">
          <Card sx={{ borderRadius: 3, boxShadow: 3 }}>
            <CardActionArea>
              <CardContent sx={{ textAlign: "center", p: 4 }}>
                <div className="icon-container">
                  <RestaurantMenuIcon
                    sx={{ fontSize: 40, color: green[600], mb: 2 }}
                  />
                </div>
                <Typography
                  variant="h5"
                  component="h2"
                  sx={{ mb: 2, color: "#333", fontWeight: "bold" }}
                >
                  Cook Something Delicious
                </Typography>
                <Typography
                  variant="body2"
                  color="text.secondary"
                  sx={{ mb: 3 }}
                >
                  Personalized recipe recommendations based on available
                  ingredients, preferences, and dietary needs.
                </Typography>
                <div className="button-container">
                  <Button
                    variant="outlined"
                    color="success"
                    sx={{ color: green[600] }}
                  >
                    Find Recipes
                  </Button>
                </div>
              </CardContent>
            </CardActionArea>
          </Card>
        </Link>

        <Link to="/inventory" className="feature-card">
          <Card sx={{ borderRadius: 3, boxShadow: 3 }}>
            <CardActionArea>
              <CardContent sx={{ textAlign: "center", p: 4 }}>
                <div className="icon-container">
                  <KitchenOutlinedIcon
                    sx={{ fontSize: 40, color: green[600], mb: 2 }}
                  />
                </div>
                <Typography
                  variant="h5"
                  component="h2"
                  sx={{ mb: 2, color: "#333", fontWeight: "bold" }}
                >
                  Manage Your Inventory
                </Typography>
                <Typography
                  variant="body2"
                  color="text.secondary"
                  sx={{ mb: 3 }}
                >
                  Keep track of your ingredients by adding them manually or
                  scanning receipts and food photos.
                </Typography>
                <div className="button-container">
                  <Button
                    variant="outlined"
                    color="success"
                    sx={{ color: green[600] }}
                  >
                    Update Inventory
                  </Button>
                </div>
              </CardContent>
            </CardActionArea>
          </Card>
        </Link>
      </div>
    </>
  );
}

export default Home;
