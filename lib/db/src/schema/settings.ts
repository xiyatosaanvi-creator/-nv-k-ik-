import { pgTable, text, timestamp, uuid, jsonb, boolean } from "drizzle-orm/pg-core";
import { createInsertSchema, createSelectSchema } from "drizzle-zod";
import { z } from "zod";
import { usersTable } from "./users";

export const userSettingsTable = pgTable("user_settings", {
  id: uuid("id").primaryKey().defaultRandom(),
  userId: uuid("user_id")
    .notNull()
    .unique()
    .references(() => usersTable.id, { onDelete: "cascade" }),
  theme: text("theme").notNull().default("dark"),
  accentColor: text("accent_color").notNull().default("#6C63FF"),
  iconShape: text("icon_shape").notNull().default("squircle"),
  gridDensity: text("grid_density").notNull().default("comfortable"),
  hapticIntensity: text("haptic_intensity").notNull().default("medium"),
  bedtimeEnabled: boolean("bedtime_enabled").notNull().default(false),
  bedtimeStart: text("bedtime_start").notNull().default("22:00"),
  bedtimeEnd: text("bedtime_end").notNull().default("07:00"),
  focusCategories: jsonb("focus_categories").$type<string[]>().default([]),
  hiddenApps: jsonb("hidden_apps").$type<string[]>().default([]),
  pinnedApps: jsonb("pinned_apps").$type<string[]>().default([]),
  customGreeting: text("custom_greeting"),
  updatedAt: timestamp("updated_at", { withTimezone: true }).defaultNow().notNull(),
});

export const insertUserSettingsSchema = createInsertSchema(userSettingsTable).omit({
  id: true,
  updatedAt: true,
});

export const updateUserSettingsSchema = createInsertSchema(userSettingsTable).omit({
  id: true,
  userId: true,
  updatedAt: true,
}).partial();

export const selectUserSettingsSchema = createSelectSchema(userSettingsTable);

export type InsertUserSettings = z.infer<typeof insertUserSettingsSchema>;
export type UpdateUserSettings = z.infer<typeof updateUserSettingsSchema>;
export type UserSettings = typeof userSettingsTable.$inferSelect;
