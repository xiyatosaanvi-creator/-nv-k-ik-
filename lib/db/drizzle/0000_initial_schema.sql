-- Drizzle Kit migration #0000 — Suggestion #117
-- Auto-generated from schema; run `pnpm --filter @workspace/db push` to apply.

CREATE TABLE IF NOT EXISTS "users" (
  "id"         SERIAL PRIMARY KEY,
  "device_id"  TEXT NOT NULL UNIQUE,
  "created_at" TIMESTAMPTZ NOT NULL DEFAULT NOW(),
  "updated_at" TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE TABLE IF NOT EXISTS "devices" (
  "id"           SERIAL PRIMARY KEY,
  "user_id"      INTEGER NOT NULL REFERENCES "users"("id") ON DELETE CASCADE,
  "name"         TEXT NOT NULL,
  "platform"     TEXT NOT NULL DEFAULT 'android',
  "push_token"   TEXT,
  "created_at"   TIMESTAMPTZ NOT NULL DEFAULT NOW(),
  "updated_at"   TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE TABLE IF NOT EXISTS "settings" (
  "id"           SERIAL PRIMARY KEY,
  "user_id"      INTEGER NOT NULL REFERENCES "users"("id") ON DELETE CASCADE,
  "dark_mode"    TEXT NOT NULL DEFAULT 'auto',
  "icon_shape"   TEXT NOT NULL DEFAULT 'squircle',
  "grid_columns" INTEGER NOT NULL DEFAULT 4,
  "gold_accent"  BOOLEAN NOT NULL DEFAULT TRUE,
  "font"         TEXT NOT NULL DEFAULT 'inter',
  "created_at"   TIMESTAMPTZ NOT NULL DEFAULT NOW(),
  "updated_at"   TIMESTAMPTZ NOT NULL DEFAULT NOW(),
  UNIQUE ("user_id")
);

CREATE TABLE IF NOT EXISTS "app_usage" (
  "id"           SERIAL PRIMARY KEY,
  "user_id"      INTEGER NOT NULL REFERENCES "users"("id") ON DELETE CASCADE,
  "package_name" TEXT NOT NULL,
  "app_label"    TEXT NOT NULL,
  "duration_ms"  BIGINT NOT NULL DEFAULT 0,
  "launch_count" INTEGER NOT NULL DEFAULT 0,
  "date"         DATE NOT NULL DEFAULT CURRENT_DATE,
  "created_at"   TIMESTAMPTZ NOT NULL DEFAULT NOW(),
  UNIQUE ("user_id", "package_name", "date")
);

CREATE INDEX IF NOT EXISTS "idx_app_usage_user_date" ON "app_usage"("user_id", "date");
CREATE INDEX IF NOT EXISTS "idx_devices_user" ON "devices"("user_id");
