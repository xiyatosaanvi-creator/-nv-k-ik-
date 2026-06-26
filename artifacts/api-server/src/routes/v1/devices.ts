import { Router } from "express";
import { eq, and } from "drizzle-orm";
import { z } from "zod/v4";
import { db, devicesTable, insertDeviceSchema } from "@workspace/db";
import { requireAuth } from "../../middlewares/auth";
import { AppError } from "../../middlewares/errorHandler";

const router = Router();

const pushTokenSchema = z.object({
  token: z.string().min(10),
  platform: z.enum(["expo", "fcm", "apns"]).optional().default("expo"),
});

router.get("/", requireAuth, async (req, res, next) => {
  try {
    const devices = await db
      .select()
      .from(devicesTable)
      .where(eq(devicesTable.userId, req.user!.userId));

    res.json({ devices });
  } catch (err) {
    next(err);
  }
});

router.post("/", requireAuth, async (req, res, next) => {
  try {
    const parsed = insertDeviceSchema.omit({ userId: true }).safeParse(req.body);
    if (!parsed.success) {
      throw new AppError(400, "VALIDATION_ERROR", "Invalid device data", parsed.error.flatten());
    }

    const [device] = await db
      .insert(devicesTable)
      .values({ ...parsed.data, userId: req.user!.userId })
      .returning();

    res.status(201).json({ device });
  } catch (err) {
    next(err);
  }
});

/**
 * POST /api/v1/devices/push-token — Suggestion #139
 * Register an Expo push token for the authenticated user's device.
 */
router.post("/push-token", requireAuth, async (req, res, next) => {
  try {
    const parsed = pushTokenSchema.safeParse(req.body);
    if (!parsed.success) {
      throw new AppError(400, "VALIDATION_ERROR", "Invalid push token payload", parsed.error.flatten());
    }

    const { token, platform } = parsed.data;

    const existing = await db
      .select({ id: devicesTable.id })
      .from(devicesTable)
      .where(
        and(
          eq(devicesTable.userId, req.user!.userId),
          eq(devicesTable.pushToken, token),
        ),
      )
      .limit(1);

    if (existing.length > 0) {
      res.json({ registered: true, message: "Token already registered" });
      return;
    }

    const [device] = await db
      .insert(devicesTable)
      .values({
        userId: req.user!.userId,
        deviceName: `${platform}-device`,
        pushToken: token,
      })
      .returning({ id: devicesTable.id, pushToken: devicesTable.pushToken });

    res.status(201).json({ registered: true, device });
  } catch (err) {
    next(err);
  }
});

router.patch("/:deviceId", requireAuth, async (req, res, next) => {
  try {
    const deviceId = String(req.params["deviceId"] ?? "");
    const parsed = insertDeviceSchema.omit({ userId: true }).partial().safeParse(req.body);
    if (!parsed.success) {
      throw new AppError(400, "VALIDATION_ERROR", "Invalid device data", parsed.error.flatten());
    }

    const [updated] = await db
      .update(devicesTable)
      .set({ ...parsed.data, lastSeenAt: new Date() })
      .where(
        and(
          eq(devicesTable.id, deviceId),
          eq(devicesTable.userId, req.user!.userId),
        ),
      )
      .returning();

    if (!updated) {
      throw new AppError(404, "DEVICE_NOT_FOUND", "Device not found");
    }

    res.json({ device: updated });
  } catch (err) {
    next(err);
  }
});

router.delete("/:deviceId", requireAuth, async (req, res, next) => {
  try {
    const deviceId = String(req.params["deviceId"] ?? "");

    const [deleted] = await db
      .delete(devicesTable)
      .where(
        and(
          eq(devicesTable.id, deviceId),
          eq(devicesTable.userId, req.user!.userId),
        ),
      )
      .returning({ id: devicesTable.id });

    if (!deleted) {
      throw new AppError(404, "DEVICE_NOT_FOUND", "Device not found");
    }

    res.status(204).send();
  } catch (err) {
    next(err);
  }
});

export default router;
