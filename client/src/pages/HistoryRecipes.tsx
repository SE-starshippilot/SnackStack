import React, { useState } from "react";
import { Box, Button, Typography, Paper } from "@mui/material";
import { styled } from "@mui/material/styles";
import Table from "@mui/material/Table";
import TableBody from "@mui/material/TableBody";
import TableCell, { tableCellClasses } from "@mui/material/TableCell";
import TableContainer from "@mui/material/TableContainer";
import TableHead from "@mui/material/TableHead";
import TableRow from "@mui/material/TableRow";
import Pagination from "@mui/material/Pagination";
import Stack from "@mui/material/Stack";
import { useNavigate } from "react-router-dom";
import "../styles/HistoryRecipes.css";
import { green } from "@mui/material/colors";
import MenuBookIcon from "@mui/icons-material/MenuBook";
import RestaurantMenuIcon from "@mui/icons-material/RestaurantMenu";
import EventIcon from "@mui/icons-material/Event";
import DescriptionIcon from "@mui/icons-material/Description";
import RestaurantIcon from "@mui/icons-material/Restaurant";
import IconButton from "@mui/material/IconButton";
import IcecreamOutlinedIcon from "@mui/icons-material/IcecreamOutlined";
import Collapse from "@mui/material/Collapse";

const StyledTableCell = styled(TableCell)(({ theme }) => ({
  [`&.${tableCellClasses.head}`]: {
    backgroundColor: green[700],
    color: "#f0f4c3",
    fontSize: 16,
    fontWeight: 500,
    fontFamily: "inherit",
  },
  [`&.${tableCellClasses.body}`]: {
    fontSize: 14,
    fontFamily: "inherit",
    color: green[900],
  },
}));

const StyledTableRow = styled(TableRow)(({ theme }) => ({
  "&:nth-of-type(odd)": {
    backgroundColor: green[50],
  },
  "&:nth-of-type(even)": {
    backgroundColor: "white",
  },
  "&:last-child td, &:last-child th": {
    border: 0,
  },
  "& .MuiTableCell-root": {
    fontFamily: "inherit",
  },
  cursor: "pointer",
  "&:hover": {
    backgroundColor: `${green[100]} !important`,
  },
}));

const StyledPagination = styled(Pagination)(({ theme }) => ({
  "& .MuiPaginationItem-root": {
    "&.Mui-selected": {
      backgroundColor: green[500],
      color: theme.palette.common.white,
      "&:hover": {
        backgroundColor: green[600],
      },
    },
  },
}));

interface RecipeIngredient {
  ingredient_name: string;
  quantity: number;
  unit: string;
  note?: string;
}

interface Recipe {
  recipe_name: string;
  servings: number;
  description: string;
  origin_name: string;
  recipe_ingredients: RecipeIngredient[];
  recipe_steps: string[];
  date: string;
}

const columns = [
  {
    id: "expand",
    label: "",
    minWidth: 50,
  },
  {
    id: "recipeName",
    label: "Recipe Name",
    minWidth: 170,
    icon: <RestaurantMenuIcon sx={{ fontSize: 20 }} />,
  },
  {
    id: "date",
    label: "Generated On",
    minWidth: 160,
    icon: <EventIcon sx={{ fontSize: 20 }} />,
  },
  {
    id: "description",
    label: "Description",
    minWidth: 200,
    icon: <DescriptionIcon sx={{ fontSize: 20 }} />,
  },
  {
    id: "ingredients",
    label: "Ingredients Used",
    minWidth: 200,
    icon: <RestaurantIcon sx={{ fontSize: 20 }} />,
  },
];

const historyList: Recipe[] = [
  {
    recipe_name: "Grilled Chicken with Roasted Vegetables and Rice",
    servings: 2,
    description:
      "A simple and flavorful main dish featuring grilled chicken, roasted vegetables, and a side of fluffy rice.",
    origin_name: "",
    recipe_ingredients: [
      { ingredient_name: "Chicken breast", quantity: 300.0, unit: "grams" },
      { ingredient_name: "Rice", quantity: 250.0, unit: "grams" },
      { ingredient_name: "Carrots", quantity: 150.0, unit: "grams" },
      {
        ingredient_name: "Onions",
        quantity: 100.0,
        unit: "grams",
        note: "Chopped.",
      },
      { ingredient_name: "Olive oil", quantity: 20.0, unit: "ml" },
    ],
    recipe_steps: [
      "Preheat grill to medium-high heat.",
      "Season the chicken with salt and cook until browned on both sides, then set aside.",
      "In a separate pan, heat olive oil over medium heat. Add chopped onions and cook until softened.",
      "Add sliced carrots to the pan and cook for an additional 5 minutes.",
      "Serve the grilled chicken with roasted vegetables and a side of fluffy rice.",
    ],
    date: "2024-03-15",
  },
  {
    recipe_name: "Chicken and Vegetable Stir-Fry",
    servings: 2,
    description:
      "A quick and easy main dish featuring sautéed chicken, mixed vegetables, and a hint of garlic powder.",
    origin_name: "",
    recipe_ingredients: [
      { ingredient_name: "Chicken breast", quantity: 300.0, unit: "grams" },
      { ingredient_name: "Carrots", quantity: 150.0, unit: "grams" },
      {
        ingredient_name: "Onions",
        quantity: 100.0,
        unit: "grams",
        note: "Chopped.",
      },
      { ingredient_name: "Spinach", quantity: 50.0, unit: "grams" },
      { ingredient_name: "Garlic powder", quantity: 0.5, unit: "teaspoon" },
    ],
    recipe_steps: [
      "Heat olive oil in a large skillet over medium-high heat.",
      "Add chopped onions and cook until softened, then add sliced carrots and cook for an additional 3 minutes.",
      "Add the chicken to the pan and cook until browned on both sides and cooked through.",
      "Stir in garlic powder and wilted spinach. Serve hot.",
    ],
    date: "2024-03-14",
  },
  {
    recipe_name: "Chicken and Vegetable Fried Rice",
    servings: 2,
    description:
      "A flavorful main dish featuring sautéed chicken, mixed vegetables, and a side of fried rice.",
    origin_name: "",
    recipe_ingredients: [
      { ingredient_name: "Chicken breast", quantity: 300.0, unit: "grams" },
      { ingredient_name: "Rice", quantity: 250.0, unit: "grams" },
      { ingredient_name: "Carrots", quantity: 150.0, unit: "grams" },
      {
        ingredient_name: "Onions",
        quantity: 100.0,
        unit: "grams",
        note: "Chopped.",
      },
      { ingredient_name: "Canned tomatoes", quantity: 150.0, unit: "grams" },
    ],
    recipe_steps: [
      "Heat olive oil in a large skillet over medium-high heat.",
      "Add chopped onions and cook until softened, then add sliced carrots and cook for an additional 3 minutes.",
      "Add the chicken to the pan and cook until browned on both sides and cooked through.",
      "Stir in canned tomatoes and cooked rice. Serve hot.",
    ],
    date: "2024-03-13",
  },
];

function Row({ row }: { row: Recipe }) {
  const [open, setOpen] = useState(false);

  const handleClick = () => {
    setOpen(!open);
  };

  return (
    <React.Fragment>
      <StyledTableRow
        onClick={handleClick}
        sx={{ "& > *": { borderBottom: "unset" } }}
      >
        <StyledTableCell>
          <IconButton
            aria-label="expand row"
            size="small"
            sx={{
              transition: "transform 0.3s",
              transform: open ? "rotate(90deg)" : "rotate(0deg)",
              pointerEvents: "none",
            }}
          >
            <IcecreamOutlinedIcon />
          </IconButton>
        </StyledTableCell>
        <StyledTableCell>{row.recipe_name}</StyledTableCell>
        <StyledTableCell>{row.date}</StyledTableCell>
        <StyledTableCell>{row.description}</StyledTableCell>
        <StyledTableCell>
          {row.recipe_ingredients.map((i) => i.ingredient_name).join(", ")}
        </StyledTableCell>
      </StyledTableRow>
      <TableRow>
        <StyledTableCell
          style={{ paddingBottom: 0, paddingTop: 0 }}
          colSpan={5}
        >
          <Collapse in={open} timeout="auto" unmountOnExit>
            <Box sx={{ margin: 2 }}>
              <Typography
                variant="h6"
                gutterBottom
                component="div"
                color={green[700]}
              >
                Recipe Details
              </Typography>
              <Box sx={{ mb: 2 }}>
                <Table
                  size="small"
                  sx={{
                    maxWidth: "600px",
                    borderCollapse: "collapse",
                    "& th, & td": {
                      fontSize: "0.875rem",
                      fontFamily: "inherit",
                      borderBottom: "none",
                    },
                    "& thead th": {
                      borderBottom: "1px solid rgba(0, 0, 0, 0.12)",
                      fontWeight: 600,
                    },
                    border: "1px solid rgba(0, 0, 0, 0.12)",
                  }}
                >
                  <TableHead>
                    <TableRow>
                      <TableCell sx={{ width: "55%" }}>Ingredient</TableCell>
                      <TableCell sx={{ width: "25%" }}>Quantity</TableCell>
                      <TableCell sx={{ width: "20%" }}>Unit</TableCell>
                    </TableRow>
                  </TableHead>
                  <TableBody>
                    {row.recipe_ingredients.map((ingredient, index) => (
                      <TableRow key={index}>
                        <TableCell>
                          {ingredient.ingredient_name}
                          {ingredient.note && (
                            <span
                              style={{
                                marginLeft: "8px",
                                color: "rgba(0, 0, 0, 0.6)",
                                fontStyle: "italic",
                              }}
                            >
                              (Note: {ingredient.note})
                            </span>
                          )}
                        </TableCell>
                        <TableCell>{ingredient.quantity}</TableCell>
                        <TableCell>{ingredient.unit}</TableCell>
                      </TableRow>
                    ))}
                  </TableBody>
                </Table>
              </Box>
              <Box>
                <Typography
                  variant="subtitle1"
                  gutterBottom
                  component="div"
                  sx={{ color: "rgba(0, 0, 0, 0.87)", fontWeight: 600 }}
                >
                  Steps
                </Typography>
                <Box
                  component="ol"
                  sx={{ mt: 1, pl: 2, color: "rgba(0, 0, 0, 0.87)" }}
                >
                  {row.recipe_steps.map((step, index) => (
                    <li key={index} style={{ marginBottom: "8px" }}>
                      {step}
                    </li>
                  ))}
                </Box>
              </Box>
            </Box>
          </Collapse>
        </StyledTableCell>
      </TableRow>
    </React.Fragment>
  );
}

const HistoryRecipes: React.FC = () => {
  const navigate = useNavigate();
  const [page, setPage] = useState(1);
  const rowsPerPage = 10;

  const handlePageChange = (
    event: React.ChangeEvent<unknown>,
    value: number
  ) => {
    setPage(value);
  };

  const pageCount = Math.ceil(historyList.length / rowsPerPage);

  if (historyList.length === 0) {
    return (
      <Box className="history-container empty">
        <Typography
          variant="h3"
          fontWeight="bold"
          gutterBottom
          sx={{
            display: "flex",
            alignItems: "center",
            justifyContent: "center",
          }}
        >
          <MenuBookIcon sx={{ fontSize: 45, marginRight: 1 }} />
          Cooking History
        </Typography>
        <Typography variant="subtitle1" color="text.secondary" gutterBottom>
          Your cooking journey starts here!
        </Typography>
        <Button
          variant="contained"
          color="success"
          sx={{ color: "white", marginTop: 2 }}
          onClick={() => navigate("/cook")}
        >
          Generate Your First Recipe
        </Button>
      </Box>
    );
  }

  return (
    <Box className="history-container has-content">
      <div className="history-header">
        <Typography
          variant="h3"
          fontWeight="bold"
          gutterBottom
          sx={{
            display: "flex",
            alignItems: "center",
            justifyContent: "center",
          }}
        >
          <MenuBookIcon sx={{ fontSize: 45, marginRight: 1 }} />
          Cooking History
        </Typography>
        <Typography variant="subtitle1" color="text.secondary" gutterBottom>
          View your past recipes and track what you've cooked!
        </Typography>
      </div>

      <div className="history-table-container">
        <TableContainer component={Paper}>
          <Table stickyHeader aria-label="cooking history table">
            <TableHead>
              <TableRow>
                {columns.map((column) => (
                  <StyledTableCell
                    key={column.id}
                    style={{ minWidth: column.minWidth }}
                  >
                    {column.id === "expand" ? null : (
                      <Box
                        sx={{ display: "flex", alignItems: "center", gap: 0.5 }}
                      >
                        {column.icon}
                        {column.label}
                      </Box>
                    )}
                  </StyledTableCell>
                ))}
              </TableRow>
            </TableHead>
            <TableBody>
              {historyList
                .slice((page - 1) * rowsPerPage, page * rowsPerPage)
                .map((row, index) => (
                  <Row key={index} row={row} />
                ))}
            </TableBody>
          </Table>
        </TableContainer>
        <Stack
          spacing={2}
          sx={{
            mt: 2,
            display: "flex",
            alignItems: "flex-end",
            justifyContent: "flex-end",
          }}
        >
          <StyledPagination
            count={pageCount}
            page={page}
            onChange={handlePageChange}
            showFirstButton
            showLastButton
          />
        </Stack>
      </div>
    </Box>
  );
};

export default HistoryRecipes;
