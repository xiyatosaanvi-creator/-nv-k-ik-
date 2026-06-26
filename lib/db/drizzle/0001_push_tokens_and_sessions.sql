-- Drizzle Kit migration #0001 — Suggestion #117
-- Adds push notification tokens table and focus sessions tracking.

CREATE TABLE IF NOT EXISTS "push_tokens" (
  "id"         SERIAL PRIMARY KEY,
  "user_id"    INTEGER NOT NULL REFERENCES "users"("id") ON DELETE CASCADE,
  "token"      TEXT NOT NULL UNIQUE,
  "platform"   TEXT NOT NULL DEFAULT 'expo',
  "active"     BOOLEAN NOT NULL DEFAULT TRUE,
  "created_at" TIMESTAMPTZ NOT NULL DEFAULT NOW(),
  "updated_at" TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE TABLE IF NOT EXISTS "focus_sessions" (
  "id"              SERIAL PRIMARY KEY,
  "user_id"         INTEGER NOT NULL REFERENCES "users"("id") ON DELETE CASCADE,
  "started_at"      TIMESTAMPTZ NOT NULL DEFAULT NOW(),
  "ended_at"        TIMESTAMPTZ,
  "duration_min"    INTEGER NOT NULL DEFAULT 25,
  "blocked_cats"    TEXT[] NOT NULL DEFAULT '{}',
  "completed"       BOOLEAN NOT NULL DEFAULT FALSE
);

CREATE TABLE IF NOT EXISTS "file_tags" (
  "id"           SERIAL PRIMARY KEY,
  "user_id"      INTEGER NOT NULL REFERENCES "users"("id") ON DELETE CASCADE,
  "file_uri"     TEXT NOT NULL,
  "file_name"    TEXT NOT NULL,
  "tags"         TEXT[] NOT NULL DEFAULT '{}',
  "created_at"   TIMESTAMPTZ NOT NULL DEFAULT NOW(),
  UNIQUE ("user_id", "file_uri")
);

ALTER TABLE "settings"
  ADD COLUMN IF NOT EXISTS "wallpaper_blur"    INTEGER NOT NULL DEFAULT 0,
  ADD COLUMN IF NOT EXISTS "seasonal_themes"   BOOLEAN NOT NULL DEFAULT TRUE,
  ADD COLUMN IF NOT EXISTS "rtl_support"       BOOLEAN NOT NULL DEFAULT FALSE,
  ADD COLUMN IF NOT EXISTS "guest_mode_pin"    TEXT,
  ADD COLUMN IF NOT EXISTS "parental_pin"      TEXT;
