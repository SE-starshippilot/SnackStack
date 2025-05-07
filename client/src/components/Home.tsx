import KitchenOutlinedIcon from "@mui/icons-material/KitchenOutlined";
import RestaurantMenuIcon from "@mui/icons-material/RestaurantMenu";
import HistoryIcon from "@mui/icons-material/History";
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

function Home() {
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
                  Personalized recipe suggestions
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
                  Keep track of your ingredients
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

        <Link to="/history" className="feature-card">
          <Card sx={{ borderRadius: 3, boxShadow: 3 }}>
            <CardActionArea>
              <CardContent sx={{ textAlign: "center", p: 4 }}>
                <div className="icon-container">
                  <HistoryIcon
                    sx={{ fontSize: 40, color: green[600], mb: 2 }}
                  />
                </div>
                <Typography
                  variant="h5"
                  component="h2"
                  sx={{ mb: 2, color: "#333", fontWeight: "bold" }}
                >
                  View Cooking History
                </Typography>
                <Typography
                  variant="body2"
                  color="text.secondary"
                  sx={{ mb: 3 }}
                >
                  Track your cooking journey
                </Typography>
                <div className="button-container">
                  <Button
                    variant="outlined"
                    color="success"
                    sx={{ color: green[600] }}
                  >
                    View History
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
