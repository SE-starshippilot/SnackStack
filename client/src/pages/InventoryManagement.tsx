import { Box, Button, TextField } from "@mui/material";
import { useState } from "react";
import { useNavigate } from "react-router-dom";
import "../styles/InventoryManagement.css";

function InventoryManagement() {
    const [inputValue, setInputValue] = useState('');
    const [ingredients, setIngredients] = useState<Set<string>>(new Set());
    const [clickedItems, setClickedItems] = useState<string[]>([]);
    const navigate = useNavigate();

    const handleInputChange = (e: React.ChangeEvent<HTMLInputElement>) => {
        setInputValue(e.target.value);
    };

    const handleConfirmClick = () => {
        const lowerCaseItem = inputValue.toLowerCase();

        if (inputValue.trim() !== '' && ![...ingredients].some((ingredient) => ingredient.toLowerCase() === lowerCaseItem)) {
            setIngredients((prev) => new Set(prev.add(inputValue)));
            setInputValue('');
        }
    };

    const handleItemClick = (item: string) => {
        if (clickedItems.includes(item)) {
            setClickedItems((prevItems) => prevItems.filter((otherItem) => otherItem !== item));
        } else {
            setClickedItems((prev) => [...prev, item]);
        }
    }

    const handleDeleteItems = () => {
        setIngredients((prev) => new Set([...prev].filter((item) => !clickedItems.includes(item))));
        setClickedItems([]);
    }

    const handleDoneClick = () => {
        navigate('/');
    }

    return (
        <div>
            <h1 className="title">Inventory Management</h1>

            {/* input box for adding ingredient */}
            <Box sx={{
                margin: 2,
                display: 'flex',
                justifyContent: 'center'
            }}>
                <TextField
                    aria-label="add-ingredient"
                    label="add ingredient"
                    variant="outlined"
                    value={inputValue}
                    onChange={handleInputChange}
                    sx={{ marginRight: 2 }}
                />
                <Button
                    variant="contained"
                    color="primary"
                    onClick={handleConfirmClick}
                    sx={{
                        margin: 1,
                        padding: 1
                    }}
                >
                    Add
                </Button>
            </Box>

            {/* show ingredients */}
            <Box sx={{
                margin: 6,
                display: 'flex',
                justifyContent: 'center', 
                flexWrap: 'wrap'
            }}>
                {[...ingredients].map((item, index) => (
                    <Button
                        key={index}
                        variant="outlined"
                        sx={{
                            margin: 1,
                            boxShadow: clickedItems.includes(item)
                                ? '0px 8px 16px rgba(0, 0, 0, 0.3)'
                                : '0px 4px 8px rgba(0, 0, 0, 0.1)',
                            backgroundColor: clickedItems.includes(item)
                                ? '#4CAF50' // background color for the clicked item
                                : 'transparent', // default background color
                            color: clickedItems.includes(item)
                                ? 'white' // color for clicked text
                                : 'black', // default color for text
                            transform: clickedItems.includes(item)
                                ? 'scale(0.95)' // scale effect after clicked
                                : 'none', // default scale effect
                            textTransform: 'none'
                        }}
                        onClick={() => handleItemClick(item)}
                    >
                        {item}
                    </Button>
                ))}
            </Box>

            <Box sx={{
                marginTop: 10,
                display: 'flex', 
                justifyContent: 'space-around'
            }}>
                {/* Delete button for clicked items */}
                <Box sx={{ marginTop: 2 }}>
                    <Button
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
                        variant="contained"
                        color="secondary"
                        onClick={handleDoneClick}
                    >
                        Done

                    </Button>
                </Box>
            </Box>
        </div>
    )
}

export default InventoryManagement;