import express from "express";
import { upgradeUser } from "../controllers/membershipController.js";

const router = express.Router();

router.post("/upgrade", upgradeUser);

export default router;