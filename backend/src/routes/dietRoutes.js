import express from "express";
import { addDiet, getDiets } from "../controllers/dietController.js";

const router = express.Router();

router.post("/addDiet", addDiet);

export default router;