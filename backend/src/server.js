import express from "express";
import healthRoutes from "./routes/healthRoutes.js";
import { config } from "dotenv";
import { close_pool, db_pool } from "./config/db.js";

// IMPORT ROUTES
import authenticationRoutes from "./routes/authenticationRoutes.js";

// ADD ENV VARIABLES AND CONNECT TO DB
config();

const app = express();
const PORT = 5001;

// MIDDLEWARE
app.use(express.json());
app.use(express.urlencoded({ extended: true }));

// API ROUTES
app.use("/authentication", authenticationRoutes);
app.use("/health", healthRoutes);


app.listen(PORT, () => {
    console.log(`server running on PORT ${PORT}`)
})

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