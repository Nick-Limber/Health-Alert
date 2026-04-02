import "./loadEnv.js";
import { config } from "dotenv";
import cors from 'cors';
import express from "express"
import { close_pool, db_pool } from "./config/db.js";

// IMPORT MIDDLEWARE
import { verificationMiddleware } from "./middleware/verificationMiddleware.js";

// IMPORT ROUTES
import healthRoutes from "./routes/healthRoutes.js";
import authenticationRoutes from "./routes/authenticationRoutes.js";
import postsRoutes from "./routes/postsRoutes.js";
import recommendationRoutes from "./routes/recommendationRoutes.js";

// ADD ENV VARIABLES AND CONNECT TO DB

config();

const app = express();
const PORT = process.env.PORT;

app.get("/test", (req, res) => {
    console.log("Testing server connectivity...");
    res.send("Server is alive!");
});

// BASIC MIDDLEWARE
app.use(express.json());
app.use(express.urlencoded({ extended: true }));
app.use(cors({
    origin: '*', // Allows your emulator to connect
    allowedHeaders: ['Content-Type', 'Authorization'], // 👈 Crucial: Allow the Auth header
    methods: ['GET', 'POST', 'PUT', 'DELETE']
}));

// AUTHENTICATION ROUTE
app.use("/authentication", authenticationRoutes);

// VERIFICATION MIDDLEWARE
app.use(verificationMiddleware)

// API ROUTES
app.use("/health", healthRoutes);
app.use("/posts", postsRoutes);
app.use("/recommendation", recommendationRoutes);

const server = app.listen(PORT, "0.0.0.0", () => {
    console.log(`server running on PORT ${PORT}`);
});

process.on("unhandledRejection", (err) => {
    console.error("unhandledRejection:", err);
    server.close(async () => {
        await close_pool();
        process.exit(1);
    });
});

process.on("uncaughtException", async (err) => {
    console.error("unhandled rejection:", err);
    await close_pool();
    process.exit(1);
})

process.on("SIGTERM", async () => {
    console.log("SIGTERM recieved, shutting down");
    server.close(async () => {
        await close_pool();
        process.exit(0);
    })
})