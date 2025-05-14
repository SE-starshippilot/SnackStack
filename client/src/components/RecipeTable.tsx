import React from "react";
import {
  TableContainer,
  Table,
  TableHead,
  TableBody,
  TableRow,
  Paper,
  Box,
  Stack,
} from "@mui/material";
import RestaurantMenuIcon from "@mui/icons-material/RestaurantMenu";
import EventIcon from "@mui/icons-material/Event";
import DescriptionIcon from "@mui/icons-material/Description";
import RestaurantIcon from "@mui/icons-material/Restaurant";
import { Recipe } from "../types/recipe";
import { RecipeTableRow } from "./RecipeTableRow/RecipeTableRow";
import { StyledTableCell, StyledPagination } from "../styles/RecipeTableStyles";

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
  {
    id: "favorite",
    label: "",
    minWidth: 50,
  },
];

interface RecipeTableProps {
  recipes: Recipe[];
  page: number;
  rowsPerPage: number;
  onPageChange: (event: React.ChangeEvent<unknown>, value: number) => void;
  onToggleFavorite: (recipe: Recipe) => void;
}

export const RecipeTable: React.FC<RecipeTableProps> = ({
  recipes,
  page,
  rowsPerPage,
  onPageChange,
  onToggleFavorite,
}) => {
  const pageCount = Math.ceil(recipes.length / rowsPerPage);

  return (
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
            {recipes
              .map((row) => (
                <RecipeTableRow
                  key={row.id}
                  row={row}
                  onToggleFavorite={onToggleFavorite}
                />
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
          onChange={onPageChange}
          showFirstButton
          showLastButton
        />
      </Stack>
    </div>
  );
};
