import { useNavigate } from 'react-router-dom';
import { Button } from '@mui/material';
import "../styles/Home.css";

function Home() {
    const navigate = useNavigate();

    const goToInventory = () => {
        navigate('/inventory');
    }

    return (
        <div>
            <h1 className='title'>Home</h1>
            <div className='btns-home'>
                <Button
                    aria-label='inventory-management-btn'
                    variant="contained"
                    onClick={goToInventory}
                    sx={{
                        textTransform: 'none' // disable converting to capital char
                    }}
                >
                    Inventory Management
                </Button>
            </div>
        </div>

    )
}

export default Home;