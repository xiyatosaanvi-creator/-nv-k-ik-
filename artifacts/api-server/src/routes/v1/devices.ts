import { Router } from "express";
import { eq, and } from "drizzle-orm";
import { db, devicesTable, insertDeviceSchema } from "@workspace/db";
import { requireAuth } from "../../middlewares/auth";
import { AppError } from "../../middlewares/errorHandler";

const router = Router();

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

router.patch("/:deviceId", requireAuth, async (req, res, next) => {
  try {
    const { deviceId } = req.params;
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
    const { deviceId } = req.params;

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
