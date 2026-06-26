import { Router } from "express";
import { eq, desc } from "drizzle-orm";
import { db, appUsageTable, insertAppUsageSchema } from "@workspace/db";
import { requireAuth } from "../../middlewares/auth";
import { AppError } from "../../middlewares/errorHandler";
import { z } from "zod/v4";

const router = Router();

/** Event schema for a single app-usage entry (userId + deviceId added server-side). */
const usageEventSchema = z.object({
  packageName:  z.string().min(1),
  appLabel:     z.string().min(1),
  category:     z.string().default("UNCATEGORIZED"),
  launchCount:  z.number().int().nonnegative().default(0),
  totalUsageMs: z.number().int().nonnegative().default(0),
  lastLaunchedAt: z.string().datetime({ offset: true }).optional().nullable(),
  dailyStats:   z.record(z.string(), z.number()).optional(),
});

const batchUsageSchema = z.object({
  deviceId: z.string().uuid(),
  events:   z.array(usageEventSchema).min(1).max(500),
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
      userId:         req.user!.userId,
      deviceId,
      lastLaunchedAt: e.lastLaunchedAt ? new Date(e.lastLaunchedAt) : null,
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

    const totalLaunches = usage.reduce((s: number, u: any) => s + u.launchCount, 0);
    const totalUsageMs = usage.reduce((s: number, u: any) => s + u.totalUsageMs, 0);
    const topApps = usage.slice(0, 5).map((u: any) => ({
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
