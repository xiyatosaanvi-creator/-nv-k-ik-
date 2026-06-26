import { pgTable, text, timestamp, uuid, integer, jsonb } from "drizzle-orm/pg-core";
import { createInsertSchema } from "drizzle-zod";
import { z } from "zod/v4";
import { usersTable } from "./users";
import { devicesTable } from "./devices";

export const appUsageTable = pgTable("app_usage", {
  id: uuid("id").primaryKey().defaultRandom(),
  userId: uuid("user_id")
    .notNull()
    .references(() => usersTable.id, { onDelete: "cascade" }),
  deviceId: uuid("device_id")
    .notNull()
    .references(() => devicesTable.id, { onDelete: "cascade" }),
  packageName: text("package_name").notNull(),
  appLabel: text("app_label").notNull(),
  category: text("category").notNull().default("UNCATEGORIZED"),
  launchCount: integer("launch_count").notNull().default(0),
  totalUsageMs: integer("total_usage_ms").notNull().default(0),
  lastLaunchedAt: timestamp("last_launched_at", { withTimezone: true }),
  dailyStats: jsonb("daily_stats").$type<Record<string, number>>().default({}),
  recordedAt: timestamp("recorded_at", { withTimezone: true }).defaultNow().notNull(),
});

export const insertAppUsageSchema = createInsertSchema(appUsageTable).omit({
  id: true,
  recordedAt: true,
});

export type InsertAppUsage = z.infer<typeof insertAppUsageSchema>;
export type AppUsage = typeof appUsageTable.$inferSelect;
