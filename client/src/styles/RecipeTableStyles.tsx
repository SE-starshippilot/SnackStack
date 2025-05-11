import { styled } from "@mui/material/styles";
import TableCell, { tableCellClasses } from "@mui/material/TableCell";
import TableRow from "@mui/material/TableRow";
import Pagination from "@mui/material/Pagination";
import { green } from "@mui/material/colors";

export const StyledTableCell = styled(TableCell)(({ theme }) => ({
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

export const StyledTableRow = styled(TableRow)(({ theme }) => ({
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

export const StyledPagination = styled(Pagination)(({ theme }) => ({
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
