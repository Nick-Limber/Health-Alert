import express from "express";
import { generate, getPlans, deletePlan } from "../controllers/recommendationController.js";

const router = express.Router();

router.post("/generate", generate);
router.get("/getPlans/", getPlans);
router.delete("/deletePlan", deletePlan);

export default router;