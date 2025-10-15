import {
  Box,
  Button,
  Card,
  CardContent,
  Divider,
  TextField,
  Typography,
  Snackbar,
  Alert,
} from "@mui/material";
import GoogleIcon from "@mui/icons-material/Google";
import { useState } from "react";
import { useNavigate } from "react-router-dom";
import { signUp } from "../services/authenticationService"; // bạn sẽ tạo hàm này tương tự logIn()

export default function Signup() {
  const navigate = useNavigate();

  const [formData, setFormData] = useState({
    username: "",
    email: "",
    password: "",
    confirmPassword: "",
  });
  const [snackBarOpen, setSnackBarOpen] = useState(false);
  const [snackBarMessage, setSnackBarMessage] = useState("");
  const [snackBarSeverity, setSnackBarSeverity] = useState("error");

  const handleCloseSnackBar = (_, reason) => {
    if (reason === "clickaway") return;
    setSnackBarOpen(false);
  };

  const handleChange = (e) => {
    setFormData((prev) => ({
      ...prev,
      [e.target.name]: e.target.value,
    }));
  };

  const handleSubmit = async (event) => {
    event.preventDefault();

    if (formData.password !== formData.confirmPassword) {
      setSnackBarMessage("Passwords do not match!");
      setSnackBarSeverity("error");
      setSnackBarOpen(true);
      return;
    }

    try {
      const response = await signUp(formData); // gọi API backend của bạn
      console.log("Response:", response.data);
      setSnackBarMessage("Account created successfully!");
      setSnackBarSeverity("success");
      setSnackBarOpen(true);
      setTimeout(() => navigate("/login"), 1500);
    } catch (error) {
      const errorResponse = error?.response?.data || { message: "Signup failed" };
      setSnackBarMessage(errorResponse.message);
      setSnackBarSeverity("error");
      setSnackBarOpen(true);
    }
  };

  const handleGoogleSignup = () => {
    alert("Google signup integration coming soon!");
  };

  return (
    <>
      {/* Snackbar Notification */}
      <Snackbar
        open={snackBarOpen}
        onClose={handleCloseSnackBar}
        autoHideDuration={4000}
        anchorOrigin={{ vertical: "top", horizontal: "right" }}
      >
        <Alert
          onClose={handleCloseSnackBar}
          severity={snackBarSeverity}
          variant="filled"
          sx={{ width: "100%" }}
        >
          {snackBarMessage}
        </Alert>
      </Snackbar>

      {/* Background */}
      <Box
        sx={{
          height: "100vh",
          display: "flex",
          alignItems: "center",
          justifyContent: "center",
          background: "linear-gradient(135deg, #667eea, #764ba2)",
          overflow: "hidden",
        }}
      >
        {/* Signup Card */}
        <Card
          sx={{
            width: "100%",
            maxWidth: 450,
            borderRadius: 4,
            boxShadow: "0 10px 30px rgba(0,0,0,0.15)",
            backdropFilter: "blur(12px)",
            bgcolor: "rgba(255,255,255,0.9)",
            p: 4,
          }}
        >
          <CardContent>
            <Typography
              variant="h4"
              fontWeight="700"
              textAlign="center"
              gutterBottom
              sx={{ color: "#3f51b5" }}
            >
              Create your account
            </Typography>

            <Typography
              variant="body2"
              textAlign="center"
              color="text.secondary"
              mb={3}
            >
              Sign up to start your journey with Npc
            </Typography>

            {/* Form */}
            <Box
              component="form"
              onSubmit={handleSubmit}
              display="flex"
              flexDirection="column"
              gap={2}
            >
              <TextField
                label="Username"
                name="username"
                variant="outlined"
                fullWidth
                required
                value={formData.username}
                onChange={handleChange}
              />
              <TextField
                label="Email"
                name="email"
                type="email"
                variant="outlined"
                fullWidth
                required
                value={formData.email}
                onChange={handleChange}
              />
              <TextField
                label="Password"
                name="password"
                type="password"
                variant="outlined"
                fullWidth
                required
                value={formData.password}
                onChange={handleChange}
              />
              <TextField
                label="Confirm Password"
                name="confirmPassword"
                type="password"
                variant="outlined"
                fullWidth
                required
                value={formData.confirmPassword}
                onChange={handleChange}
              />

              <Button
                type="submit"
                variant="contained"
                size="large"
                sx={{
                  mt: 1,
                  background: "linear-gradient(90deg, #667eea, #764ba2)",
                  "&:hover": {
                    background: "linear-gradient(90deg, #5a67d8, #6b46c1)",
                  },
                }}
              >
                Sign Up
              </Button>
            </Box>

            <Divider sx={{ my: 3 }}>OR</Divider>

            <Button
              variant="outlined"
              size="large"
              startIcon={<GoogleIcon />}
              onClick={handleGoogleSignup}
              fullWidth
              sx={{
                color: "#555",
                borderColor: "#ddd",
                textTransform: "none",
                "&:hover": {
                  borderColor: "#aaa",
                  background: "#fafafa",
                },
              }}
            >
              Sign up with Google
            </Button>

            <Typography
              variant="body2"
              textAlign="center"
              color="text.secondary"
              mt={3}
            >
              Already have an account?{" "}
              <Button
                variant="text"
                onClick={() => navigate("/login")}
                sx={{ textTransform: "none", color: "#3f51b5", fontWeight: 600 }}
              >
                Log in
              </Button>
            </Typography>
          </CardContent>
        </Card>
      </Box>
    </>
  );
}
