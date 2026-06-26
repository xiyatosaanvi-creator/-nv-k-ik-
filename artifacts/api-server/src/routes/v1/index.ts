import { Router } from "express";
import authRouter from "./auth";
import settingsRouter from "./settings";
import devicesRouter from "./devices";
import analyticsRouter from "./analytics";
import { aiRouter } from "./ai.js";

const router = Router();

router.use("/auth", authRouter);
router.use("/settings", settingsRouter);
router.use("/devices", devicesRouter);
router.use("/analytics", analyticsRouter);
router.use("/ai", aiRouter);

export default router;
