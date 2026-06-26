import { Router } from "express";
import { eq, desc } from "drizzle-orm";
import { db, appUsageTable, insertAppUsageSchema } from "@workspace/db";
import { requireAuth } from "../../middlewares/auth";
import { AppError } from "../../middlewares/errorHandler";
import { z } from "zod";

const router = Router();

const batchUsageSchema = z.object({
  deviceId: z.string().uuid(),
  events: z
    .array(insertAppUsageSchema.omit({ userId: true, deviceId: true }))
    .min(1)
    .max(500),
});

router.post("/usage", requireAuth, async (req, res, next) => {
  try {
    const parsed = batchUsageSchema.safeParse(req.body);
    if (!parsed.success) {
      throw new AppError(400, "VALIDATION_ERROR", "Invalid usage data", parsed.error.flatten());
    }

    const { deviceId, events } = parsed.data;

    const rows = events.map((e) => ({
      ...e,
      userId: req.user!.userId,
      deviceId,
    }));

    await db.insert(appUsageTable).values(rows);

    res.status(202).json({ accepted: rows.length });
  } catch (err) {
    next(err);
  }
});

router.get("/usage", requireAuth, async (req, res, next) => {
  try {
    const limit = Math.min(Number(req.query["limit"] ?? 50), 200);

    const usage = await db
      .select()
      .from(appUsageTable)
      .where(eq(appUsageTable.userId, req.user!.userId))
      .orderBy(desc(appUsageTable.recordedAt))
      .limit(limit);

    res.json({ usage });
  } catch (err) {
    next(err);
  }
});

router.get("/usage/summary", requireAuth, async (req, res, next) => {
  try {
    const usage = await db
      .select()
      .from(appUsageTable)
      .where(eq(appUsageTable.userId, req.user!.userId))
      .orderBy(desc(appUsageTable.launchCount))
      .limit(20);

    const totalLaunches = usage.reduce((s: number, u) => s + u.launchCount, 0);
    const totalUsageMs = usage.reduce((s: number, u) => s + u.totalUsageMs, 0);
    const topApps = usage.slice(0, 5).map((u) => ({
      packageName: u.packageName,
      appLabel: u.appLabel,
      category: u.category,
      launchCount: u.launchCount,
      totalUsageMs: u.totalUsageMs,
    }));

    res.json({
      summary: {
        totalLaunches,
        totalUsageMs,
        totalUsageHours: Math.round((totalUsageMs / 3_600_000) * 10) / 10,
        topApps,
        appCount: usage.length,
      },
    });
  } catch (err) {
    next(err);
  }
});

export default router;
