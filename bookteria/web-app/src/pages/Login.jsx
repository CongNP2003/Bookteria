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
import { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import { logIn, isAuthenticated } from "../services/authenticationService";

export default function Login() {
  const navigate = useNavigate();

  const [username, setUsername] = useState("");
  const [password, setPassword] = useState("");
  const [snackBarOpen, setSnackBarOpen] = useState(false);
  const [snackBarMessage, setSnackBarMessage] = useState("");

  const handleCloseSnackBar = (_, reason) => {
    if (reason === "clickaway") return;
    setSnackBarOpen(false);
  };

  const handleClick = () => {
    alert(
      "Please refer to OAuth2 implementation guidelines: https://www.youtube.com/playlist?list=PL2xsxmVse9IbweCh6QKqZhousfEWabSeq"
    );
  };

  useEffect(() => {
  const checkAuth = () => {
    if (isAuthenticated()) {
      navigate("/");
    }
  };
  checkAuth();
}, [navigate]);



  const handleSubmit = async (event) => {
    event.preventDefault();
    try {
      const response = await logIn(username, password);
      console.log("Response body:", response.data);
      navigate("/");
    } catch (error) {
      const errorResponse = error?.response?.data || { message: "Login failed" };
      setSnackBarMessage(errorResponse.message);
      setSnackBarOpen(true);
    }
  };

  return (
    <>
      {/* Notification */}
      <Snackbar
        open={snackBarOpen}
        onClose={handleCloseSnackBar}
        autoHideDuration={5000}
        anchorOrigin={{ vertical: "top", horizontal: "right" }}
      >
        <Alert
          onClose={handleCloseSnackBar}
          severity="error"
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
        {/* Card */}
        <Card
          sx={{
            width: "100%",
            maxWidth: 420,
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
              Welcome to Npc
            </Typography>
            <Typography
              variant="body2"
              textAlign="center"
              color="text.secondary"
              mb={3}
            >
              Sign in to access your workspace
            </Typography>

            <Box
              component="form"
              onSubmit={handleSubmit}
              display="flex"
              flexDirection="column"
              gap={2}
            >
              <TextField
                label="Username"
                variant="outlined"
                fullWidth
                value={username}
                onChange={(e) => setUsername(e.target.value)}
              />
              <TextField
                label="Password"
                type="password"
                variant="outlined"
                fullWidth
                value={password}
                onChange={(e) => setPassword(e.target.value)}
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
                Log In
              </Button>
            </Box>

            <Divider sx={{ my: 3 }}>OR</Divider>

            <Button
              type="button"
              variant="outlined"
              size="large"
              startIcon={<GoogleIcon />}
              onClick={handleClick}
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
              Continue with Google
            </Button>

            <Button
              type="button"
              variant="text"
              color="primary"
              size="large"
              fullWidth
              sx={{ mt: 2 }}
              onClick={() => navigate("/Signup")}
            >
              Create an account
            </Button>
          </CardContent>
        </Card>
      </Box>
    </>
  );
}
