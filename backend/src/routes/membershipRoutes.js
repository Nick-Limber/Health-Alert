import express from "express";
import { toggleMembership, getMembership } from "../controllers/membershipController.js";

const router = express.Router();

router.post("/toggle", toggleMembership);
router.get("/:id", getMembership)
export default router;