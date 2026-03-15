import express from "express";
import { generate, getPlans } from "../controllers/recommendationController.js";

const router = express.Router();

router.post("/generate", generate);
router.get("/getPlans/", getPlans);

export default router;