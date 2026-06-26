import { Router } from "express";
import bcrypt from "bcryptjs";
import { eq } from "drizzle-orm";
import { db, usersTable, userSettingsTable, registerSchema, loginSchema } from "@workspace/db";
import { signToken, requireAuth } from "../../middlewares/auth";
import { AppError } from "../../middlewares/errorHandler";
import { authLimiter } from "../../middlewares/rateLimit";

const router = Router();

router.post("/register", authLimiter, async (req, res, next) => {
  try {
    const parsed = registerSchema.safeParse(req.body);
    if (!parsed.success) {
      throw new AppError(400, "VALIDATION_ERROR", "Invalid request body", parsed.error.flatten());
    }

    const { email, password, displayName } = parsed.data;

    const existing = await db
      .select({ id: usersTable.id })
      .from(usersTable)
      .where(eq(usersTable.email, email.toLowerCase()))
      .limit(1);

    if (existing.length > 0) {
      throw new AppError(409, "EMAIL_TAKEN", "An account with this email already exists");
    }

    const passwordHash = await bcrypt.hash(password, 12);

    const [user] = await db
      .insert(usersTable)
      .values({
        email: email.toLowerCase(),
        passwordHash,
        displayName: displayName ?? null,
      })
      .returning({
        id: usersTable.id,
        email: usersTable.email,
        displayName: usersTable.displayName,
        createdAt: usersTable.createdAt,
      });

    await db.insert(userSettingsTable).values({ userId: user.id });

    const token = signToken({ userId: user.id, email: user.email });

    res.status(201).json({ user, token });
  } catch (err) {
    next(err);
  }
});

router.post("/login", authLimiter, async (req, res, next) => {
  try {
    const parsed = loginSchema.safeParse(req.body);
    if (!parsed.success) {
      throw new AppError(400, "VALIDATION_ERROR", "Invalid request body", parsed.error.flatten());
    }

    const { email, password } = parsed.data;

    const [user] = await db
      .select()
      .from(usersTable)
      .where(eq(usersTable.email, email.toLowerCase()))
      .limit(1);

    if (!user) {
      throw new AppError(401, "INVALID_CREDENTIALS", "Invalid email or password");
    }

    const valid = await bcrypt.compare(password, user.passwordHash);
    if (!valid) {
      throw new AppError(401, "INVALID_CREDENTIALS", "Invalid email or password");
    }

    const token = signToken({ userId: user.id, email: user.email });

    const { passwordHash: _, ...publicUser } = user;

    res.json({ user: publicUser, token });
  } catch (err) {
    next(err);
  }
});

router.get("/me", requireAuth, async (req, res, next) => {
  try {
    const [user] = await db
      .select({
        id: usersTable.id,
        email: usersTable.email,
        displayName: usersTable.displayName,
        createdAt: usersTable.createdAt,
        updatedAt: usersTable.updatedAt,
      })
      .from(usersTable)
      .where(eq(usersTable.id, req.user!.userId))
      .limit(1);

    if (!user) {
      throw new AppError(404, "USER_NOT_FOUND", "User not found");
    }

    res.json({ user });
  } catch (err) {
    next(err);
  }
});

router.patch("/me", requireAuth, async (req, res, next) => {
  try {
    const schema = registerSchema.pick({ displayName: true }).partial();
    const parsed = schema.safeParse(req.body);
    if (!parsed.success) {
      throw new AppError(400, "VALIDATION_ERROR", "Invalid request body", parsed.error.flatten());
    }

    const [updated] = await db
      .update(usersTable)
      .set({ ...parsed.data, updatedAt: new Date() })
      .where(eq(usersTable.id, req.user!.userId))
      .returning({
        id: usersTable.id,
        email: usersTable.email,
        displayName: usersTable.displayName,
        updatedAt: usersTable.updatedAt,
      });

    res.json({ user: updated });
  } catch (err) {
    next(err);
  }
});

export default router;
