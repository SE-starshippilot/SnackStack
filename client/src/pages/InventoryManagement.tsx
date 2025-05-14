import { Box, Button, TextField } from "@mui/material";
import { useState, useEffect } from "react";
import axios from "axios";
import { useNavigate } from "react-router-dom";
import { useUserContext } from "../contexts/UserContext";
import "../styles/InventoryManagement.css";

const apiBaseUrl = process.env.REACT_APP_API_BASE_URL;

function InventoryManagement() {
  const [inputValue, setInputValue] = useState("");
  const [ingredients, setIngredients] = useState<Set<string>>(new Set());
  const [clickedItems, setClickedItems] = useState<string[]>([]);
  const [isLoading, setIsLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const navigate = useNavigate();
  const { username, isProfileComplete } = useUserContext();

  useEffect(() => {
    if (username && isProfileComplete) {
      fetchIngredients();
    }
  }, [username, isProfileComplete]);

  const fetchIngredients = async () => {
    setIsLoading(true);
    setError(null);

    try {
      const response = await axios.get(
        `${apiBaseUrl}/api/users/${username}/inventory`
      );

      const ingredientList: string[] = response.data;
      setIngredients(new Set(ingredientList));
    } catch (error) {
      // Axios error handling
      if (axios.isAxiosError(error)) {
        const errorMsg = error.response?.data?.message || error.message;
        setError(`Failed to fetch ingredients: ${errorMsg}`);
        console.error("Axios error:", error.response?.data || error.message);
      } else {
        setError("An unexpected error occurred while fetching ingredients");
        console.error("Unexpected error:", error);
      }
    } finally {
      setIsLoading(false);
    }
  };

  const handleInputChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    setInputValue(e.target.value);
  };

  const handleConfirmClick = async () => {
    if (!username || !isProfileComplete) {
      setError("No user available");
      return;
    }

    const lowerCaseItem = inputValue.toLowerCase();

    if (
      inputValue.trim() !== "" &&
      ![...ingredients].some(
        (ingredient) => ingredient.toLowerCase() === lowerCaseItem
      )
    ) {
      try {
        await axios.post(`${apiBaseUrl}/api/users/${username}/inventory`, {
          ingredientName: inputValue,
        });

        // update front-end
        setIngredients((prev) => new Set(prev.add(inputValue)));
        setInputValue("");
        setError(null);
      } catch (err) {
        if (axios.isAxiosError(error)) {
          const errorMsg = error.response?.data?.message || error.message;
          setError(`Failed to add ingredient: ${errorMsg}`);
          console.error("Axios error:", error.response?.data || error.message);
        } else {
          setError("An unexpected error occurred while adding ingredient");
          console.error("Unexpected error:", error);
        }
      }
    }
  };

  const handleItemClick = (item: string) => {
    if (clickedItems.includes(item)) {
      setClickedItems((prevItems) =>
        prevItems.filter((otherItem) => otherItem !== item)
      );
    } else {
      setClickedItems((prev) => [...prev, item]);
    }
  };

  const handleDeleteItems = async () => {
    if (!username || !isProfileComplete) {
      setError("No user available");
      return;
    }

    if (clickedItems.length === 0) {
      return;
    }

    setError(null);

    try {
      // Create an array of delete requests
      const deleteRequests = clickedItems.map((item) =>
        axios.delete(
          `${apiBaseUrl}/api/users/${username}/inventory/${encodeURIComponent(
            item
          )}`
        )
      );

      // Execute all requests in parallel
      await Promise.all(deleteRequests);

      // update front-end state
      setIngredients(
        (prev) =>
          new Set([...prev].filter((item) => !clickedItems.includes(item)))
      );
      setClickedItems([]);
    } catch (err) {
      if (axios.isAxiosError(error)) {
        const errorMsg = error.response?.data?.message || error.message;
        setError(`Failed to delete ingredient(s): ${errorMsg}`);
        console.error("Axios error:", error.response?.data || error.message);
      } else {
        setError("An unexpected error occurred while deleting ingredient(s)");
        console.error("Unexpected error:", error);
      }
    }
  };

  const handleDoneClick = () => {
    navigate("/");
  };

  if (!isProfileComplete) {
    return (
      <div>
        <h1 className="title">Loading User Profile...</h1>
      </div>
    );
  }

  return (
    <div>
      <h1 className="title">Inventory Management</h1>

      {/* Display error message if there is one */}
      {error && (
        <Box
          sx={{
            margin: 2,
            padding: 2,
            backgroundColor: "#ffebee",
            color: "#c62828",
            borderRadius: 1,
          }}
        >
          {error}
        </Box>
      )}

      {/* Loading indicator */}
      {isLoading ? (
        <Box sx={{ margin: 4, textAlign: "center" }}>
          Loading your ingredients...
        </Box>
      ) : (
        <>
          {/* input box for adding ingredient */}
          <Box
            sx={{
              margin: 2,
              display: "flex",
              justifyContent: "center",
            }}
          >
            <TextField
              aria-label="add-ingredient"
              label="add ingredient"
              variant="outlined"
              value={inputValue}
              onChange={handleInputChange}
              sx={{ marginRight: 2 }}
            />
            <Button
              aria-label="add-btn"
              variant="contained"
              color="primary"
              onClick={handleConfirmClick}
              sx={{
                margin: 1,
                padding: 1,
              }}
            >
              Add
            </Button>
          </Box>

          {/* show ingredients */}
          <Box
            sx={{
              margin: 6,
              display: "flex",
              justifyContent: "center",
              flexWrap: "wrap",
            }}
          >
            {[...ingredients].map((item, index) => (
              <Button
                key={index}
                variant="outlined"
                sx={{
                  margin: 1,
                  boxShadow: clickedItems.includes(item)
                    ? "0px 8px 16px rgba(0, 0, 0, 0.3)"
                    : "0px 4px 8px rgba(0, 0, 0, 0.1)",
                  backgroundColor: clickedItems.includes(item)
                    ? "#4CAF50" // background color for the clicked item
                    : "transparent", // default background color
                  color: clickedItems.includes(item)
                    ? "white" // color for clicked text
                    : "black", // default color for text
                  transform: clickedItems.includes(item)
                    ? "scale(0.95)" // scale effect after clicked
                    : "none", // default scale effect
                  textTransform: "none",
                }}
                onClick={() => handleItemClick(item)}
              >
                {item}
              </Button>
            ))}
          </Box>

          <Box
            sx={{
              marginTop: 10,
              display: "flex",
              justifyContent: "space-around",
            }}
          >
            {/* Delete button for clicked items */}
            <Box sx={{ marginTop: 2 }}>
              <Button
                aria-label="delete-btn"
                variant="contained"
                color="primary"
                onClick={handleDeleteItems}
                disabled={clickedItems.length === 0}
              >
                DELETE
              </Button>
            </Box>

            {/* Done button will bring users back to the home page */}
            <Box sx={{ marginTop: 2 }}>
              <Button
                aria-label="done-btn"
                variant="contained"
                color="secondary"
                onClick={handleDoneClick}
              >
                Done
              </Button>
            </Box>
          </Box>
        </>
      )}
    </div>
  );
}

export default InventoryManagement;
