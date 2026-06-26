import { Router } from "express";
import { eq } from "drizzle-orm";
import { db, userSettingsTable, updateUserSettingsSchema } from "@workspace/db";
import { requireAuth } from "../../middlewares/auth";
import { AppError } from "../../middlewares/errorHandler";

const router = Router();

router.get("/", requireAuth, async (req, res, next) => {
  try {
    const [settings] = await db
      .select()
      .from(userSettingsTable)
      .where(eq(userSettingsTable.userId, req.user!.userId))
      .limit(1);

    if (!settings) {
      throw new AppError(404, "SETTINGS_NOT_FOUND", "Settings not found for this user");
    }

    res.json({ settings });
  } catch (err) {
    next(err);
  }
});

router.patch("/", requireAuth, async (req, res, next) => {
  try {
    const parsed = updateUserSettingsSchema.safeParse(req.body);
    if (!parsed.success) {
      throw new AppError(400, "VALIDATION_ERROR", "Invalid settings data", parsed.error.flatten());
    }

    const [updated] = await db
      .update(userSettingsTable)
      .set({ ...parsed.data, updatedAt: new Date() })
      .where(eq(userSettingsTable.userId, req.user!.userId))
      .returning();

    if (!updated) {
      const [created] = await db
        .insert(userSettingsTable)
        .values({ userId: req.user!.userId, ...parsed.data })
        .returning();
      res.json({ settings: created });
      return;
    }

    res.json({ settings: updated });
  } catch (err) {
    next(err);
  }
});

export default router;
