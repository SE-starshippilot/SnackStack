import {
  Box,
  Button,
  Chip,
  CircularProgress,
  Container,
  Slider,
  TextField,
  Typography,
} from "@mui/material";
import { styled } from "@mui/material/styles";
import React, { useState } from "react";
import "../styles/recipeGeneration.css";

const mealTypes = [
  { id: "main", label: "Main" },
  { id: "appetizer", label: "Appetizer" },
  { id: "dessert", label: "Dessert" },
  { id: "breakfast", label: "Breakfast" },
  { id: "snack", label: "Snack" },
];

const defaultPreferences = [
  "American",
  "Italian",
  "Chinese",
  "Mexican",
  "Japanese",
  "Indian",
  "Middle Eastern",
  "Thai",
];

const defaultAllergies = [
  "Gluten",
  "Dairy",
  "Nuts",
  "Eggs",
  "Soy",
  "Shellfish",
];

const PrettoSlider = styled(Slider)({
  color: "#52af77",
  height: 8,
  maxWidth: 300,
  "& .MuiSlider-track": { border: "none" },
  "& .MuiSlider-thumb": {
    height: 24,
    width: 24,
    backgroundColor: "#fff",
    border: "2px solid currentColor",
    "&:focus, &:hover, &.Mui-active, &.Mui-focusVisible": {
      boxShadow: "inherit",
    },
    "&::before": { display: "none" },
  },
  "& .MuiSlider-valueLabel": {
    lineHeight: 1.2,
    fontSize: 12,
    background: "unset",
    padding: 0,
    width: 32,
    height: 32,
    borderRadius: "50% 50% 50% 0",
    backgroundColor: "#52af77",
    transformOrigin: "bottom left",
    transform: "translate(50%, -100%) rotate(-45deg) scale(0)",
    "&::before": { display: "none" },
    "&.MuiSlider-valueLabelOpen": {
      transform: "translate(50%, -100%) rotate(-45deg) scale(1)",
    },
    "& > *": {
      transform: "rotate(45deg)",
    },
  },
});

function RecipeGeneration() {
  const [isLoading, setIsLoading] = useState(false);
  const [formData, setFormData] = useState({
    servings: 1,
    mealType: "",
    notes: "",
  });
  const [errors, setErrors] = useState({ servings: false, mealType: false });

  const [preferences, setPreferences] = useState<string[]>([]);
  const [preferenceOptions, setPreferenceOptions] = useState<string[]>([
    ...defaultPreferences,
  ]);
  const [customPreferences, setCustomPreferences] = useState<string[]>([]);
  const [preferenceInput, setPreferenceInput] = useState<string>("");

  const [allergies, setAllergies] = useState<string[]>([]);
  const [allergyOptions, setAllergyOptions] = useState<string[]>([
    ...defaultAllergies,
  ]);
  const [customAllergies, setCustomAllergies] = useState<string[]>([]);
  const [allergyInput, setAllergyInput] = useState<string>("");

  const handleChipToggle = (
    value: string,
    list: string[],
    setList: React.Dispatch<React.SetStateAction<string[]>>
  ) => {
    setList((prev) =>
      prev.includes(value) ? prev.filter((v) => v !== value) : [...prev, value]
    );
  };

  const handleAddChip = (
    value: string,
    options: string[],
    setOptions: React.Dispatch<React.SetStateAction<string[]>>,
    custom: string[],
    setCustom: React.Dispatch<React.SetStateAction<string[]>>,
    selected: string[],
    setSelected: React.Dispatch<React.SetStateAction<string[]>>,
    setInput: React.Dispatch<React.SetStateAction<string>>
  ) => {
    const trimmed = value.trim();
    if (trimmed && !options.includes(trimmed) && !custom.includes(trimmed)) {
      setOptions((prev) => [...prev, trimmed]);
      setCustom((prev) => [...prev, trimmed]);
      setSelected((prev) => [...prev, trimmed]);
      setInput("");
    }
  };

  const handleDeleteCustom = (
    item: string,
    setOptions: React.Dispatch<React.SetStateAction<string[]>>,
    setSelected: React.Dispatch<React.SetStateAction<string[]>>,
    setCustom: React.Dispatch<React.SetStateAction<string[]>>
  ) => {
    setOptions((prev) => prev.filter((v) => v !== item));
    setSelected((prev) => prev.filter((v) => v !== item));
    setCustom((prev) => prev.filter((v) => v !== item));
  };

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    const newErrors = {
      servings: formData.servings < 1,
      mealType: !formData.mealType,
    };
    setErrors(newErrors);
    if (newErrors.servings || newErrors.mealType) return;
    setIsLoading(true);
    setTimeout(() => setIsLoading(false), 3000);
  };

  return (
    <Container
      maxWidth="md"
      sx={{ width: "900px", px: { xs: 2, sm: 3, md: 4 } }}
    >
      <Box width="100%">
        <Typography
          variant="h5"
          fontWeight={600}
          textAlign="center"
          gutterBottom
        >
          Generate Your Perfect Recipe
        </Typography>
        <Typography
          variant="body2"
          textAlign="center"
          color="text.secondary"
          mb={2}
        >
          Based on your preferences, dietary needs, and available ingredients.
        </Typography>

        <form onSubmit={handleSubmit} className="form-container">
          <div className="form-section">
            <Typography variant="subtitle1" fontWeight={600}>
              How many servings? <span className="required">*</span>
            </Typography>
            <Box display="flex" alignItems="center" gap={2}>
              <PrettoSlider
                value={formData.servings}
                onChange={(_, val) =>
                  setFormData((prev) => ({ ...prev, servings: val as number }))
                }
                valueLabelDisplay="auto"
                defaultValue={1}
                min={0}
                max={10}
                color={errors.servings ? "error" : "success"}
              />
              <Typography>{formData.servings}</Typography>
            </Box>
          </div>

          <div className="form-section">
            <Typography variant="subtitle1" fontWeight={600}>
              What kind of meal? <span className="required">*</span>
            </Typography>
            <Box display="flex" gap={1} flexWrap="wrap">
              {mealTypes.map((type) => (
                <Chip
                  className="custom-chip"
                  key={type.id}
                  label={type.label}
                  variant={
                    formData.mealType === type.id ? "filled" : "outlined"
                  }
                  color={formData.mealType === type.id ? "success" : "default"}
                  onClick={() =>
                    setFormData((prev) => ({ ...prev, mealType: type.id }))
                  }
                />
              ))}
            </Box>
          </div>

          <div className="form-section">
            <Typography variant="subtitle1" fontWeight={600}>
              Preferences
            </Typography>
            <Box className="chip-row">
              {preferenceOptions.map((pref) => (
                <Chip
                  className="custom-chip"
                  key={pref}
                  label={pref}
                  variant={preferences.includes(pref) ? "filled" : "outlined"}
                  color={preferences.includes(pref) ? "success" : undefined}
                  onClick={() =>
                    handleChipToggle(pref, preferences, setPreferences)
                  }
                  onDelete={
                    customPreferences.includes(pref)
                      ? () =>
                          handleDeleteCustom(
                            pref,
                            setPreferenceOptions,
                            setCustomPreferences,
                            setPreferences
                          )
                      : undefined
                  }
                />
              ))}
            </Box>
            <Box
              display="flex"
              gap={1}
              sx={{ height: 40, alignItems: "center" }}
            >
              <TextField
                label="Add Preference"
                size="small"
                value={preferenceInput}
                color="success"
                focused
                onChange={(e) => setPreferenceInput(e.target.value)}
              />
              <Button
                variant="outlined"
                color="success"
                onClick={() =>
                  handleAddChip(
                    preferenceInput,
                    preferenceOptions,
                    setPreferenceOptions,
                    customPreferences,
                    setCustomPreferences,
                    preferences,
                    setPreferences,
                    setPreferenceInput
                  )
                }
              >
                Add
              </Button>
            </Box>
          </div>

          <div className="form-section">
            <Typography variant="subtitle1" fontWeight={600}>
              Allergies
            </Typography>
            <Box className="chip-row">
              {allergyOptions.map((allergy) => (
                <Chip
                  className="custom-chip"
                  key={allergy}
                  label={allergy}
                  variant={allergies.includes(allergy) ? "filled" : "outlined"}
                  color={allergies.includes(allergy) ? "success" : undefined}
                  onClick={() =>
                    handleChipToggle(allergy, allergies, setAllergies)
                  }
                  onDelete={
                    customAllergies.includes(allergy)
                      ? () =>
                          handleDeleteCustom(
                            allergy,
                            setAllergyOptions,
                            setCustomAllergies,
                            setAllergies
                          )
                      : undefined
                  }
                />
              ))}
            </Box>
            <Box display="flex" gap={1} mt={1}>
              <TextField
                label="Add Allergy"
                size="small"
                focused
                color="success"
                value={allergyInput}
                onChange={(e) => setAllergyInput(e.target.value)}
              />
              <Button
                variant="outlined"
                color="success"
                onClick={() =>
                  handleAddChip(
                    allergyInput,
                    allergyOptions,
                    setAllergyOptions,
                    customAllergies,
                    setCustomAllergies,
                    allergies,
                    setAllergies,
                    setAllergyInput
                  )
                }
              >
                Add
              </Button>
            </Box>
          </div>

          <div className="form-section">
            <Typography variant="subtitle1" fontWeight={600}>
              Notes (Optional)
            </Typography>
            <TextField
              multiline
              rows={3}
              placeholder="Any specific preferences or instructions?"
              fullWidth
              color="success"
              value={formData.notes}
              onChange={(e) =>
                setFormData({ ...formData, notes: e.target.value })
              }
            />
          </div>

          <Box textAlign="center" mt={4}>
            <Button
              type="submit"
              variant="contained"
              color="success"
              sx={{ px: 4, py: 1.5, fontWeight: 500 }}
              disabled={isLoading}
            >
              {isLoading ? (
                <CircularProgress size={24} sx={{ color: "#fff" }} />
              ) : (
                "Generate Recipe"
              )}
            </Button>
          </Box>
        </form>
      </Box>
    </Container>
  );
}

export default RecipeGeneration;
