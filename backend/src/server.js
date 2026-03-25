import "./loadEnv.js";

import express from "express"
import { close_pool, db_pool } from "./config/db.js";

// IMPORT ROUTES
import healthRoutes from "./routes/healthRoutes.js";
import authenticationRoutes from "./routes/authenticationRoutes.js";
import postsRoutes from "./routes/postsRoutes.js";
import recommendationRoutes from "./routes/recommendationRoutes.js";
import membershipRoutes from "./routes/membershipRoutes.js"
// ADD ENV VARIABLES AND CONNECT TO DB

const app = express();
const PORT = 5005;

app.get("/test", (req, res) => {
    console.log("Testing server connectivity...");
    res.send("Server is alive!");
});

// MIDDLEWARE
app.use(express.json());
app.use(express.urlencoded({ extended: true }));

// API ROUTES
app.use("/health", healthRoutes);
app.use("/authentication", authenticationRoutes);
app.use("/posts", postsRoutes);
app.use("/recommendation", recommendationRoutes);

app.use("/membership", membershipRoutes)
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