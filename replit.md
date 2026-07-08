# Ciyato — AI Phone Organizer

Ciyato is a smart Android launcher concept: a phone-organization app with AI-assisted app/file/photo organization, currently being designed as a set of UI prototypes plus a real backend and native Android app.

## Run & Operate

- Web prototype (screens catalog: onboarding, home dense/spacious, app drawer, theme studio, files, smart collections, photos, AI search, before/after, showcase) — runs automatically via the `artifacts/ciyato: web` workflow, served at `/`.
- API server — runs automatically via the `artifacts/api-server: API Server` workflow, served at `/api`.
- Mobile (Expo) app — runs automatically via the `artifacts/ciyato-mobile: expo` workflow, served at `/mobile/`.
- Mockup sandbox (Canvas) — `artifacts/mockup-sandbox: Component Preview Server`, starts on demand.
- Native Android project (Kotlin/Gradle, separate from the Expo app) lives in `ciyato-android/` at the repo root — not wired into the pnpm workspace or Replit workflows; build/run it with Gradle directly if needed.
- `pnpm run typecheck` — full typecheck across all packages
- `pnpm run build` — typecheck + build all packages
- `pnpm --filter @workspace/api-spec run codegen` — regenerate API hooks and Zod schemas from the OpenAPI spec
- `pnpm --filter @workspace/db run push` — push DB schema changes (dev only)
- Required env: `DATABASE_URL` — Postgres connection string (pre-provisioned)

## Stack

- pnpm workspaces, Node.js, TypeScript
- API: Express 5 (`artifacts/api-server`)
- DB: PostgreSQL + Drizzle ORM (`lib/db`)
- Validation: Zod, `drizzle-zod`
- API codegen: Orval (from `lib/api-spec/openapi.yaml`)
- Web prototype: React + Vite (`artifacts/ciyato`)
- Mobile: Expo/React Native (`artifacts/ciyato-mobile`)
- Native Android (separate, unintegrated): Kotlin (`ciyato-android/`)

## Where things live

- `artifacts/ciyato/src/screens/` — the web prototype screens referenced in `BUILD_PLAN.md` (Onboarding, HomeDense, HomeSpacious, AppDrawer, ThemeStudio, CiyatoFiles, SmartCollections, CiyatoPhotos, AISearch, BeforeAfter, Showcase)
- `lib/api-spec/openapi.yaml` — source of truth for API contracts
- `lib/db/src/schema/` — Drizzle schema (users, devices, settings, app_usage)
- `artifacts/api-server/src/routes/` — Express route handlers
- `artifacts/ciyato-mobile/app/` — Expo Router screens for the real mobile app

## Architecture decisions

- This project was imported from GitHub with pre-existing `artifact.toml` files but no registered artifacts/workflows in this workspace; artifacts were re-registered and `pg`/`drizzle-zod` were added as direct dependencies of `@workspace/api-server` (they're used via `@workspace/db` but esbuild externalizes them, so they must be resolvable from the api-server package at runtime).
- The current web prototype (`artifacts/ciyato`) is a **static screens catalog** ("Prototype Explorer" sidebar), not a functioning app shell — the "App Drawer" screen currently renders as just another catalog page rather than a real overlay/drawer UI.

## Product

- AI-assisted Android launcher: onboarding, two home-screen density modes, an app drawer, theming, a file browser, smart collections, a photo browser, AI search, and before/after comparisons.

## User preferences

_Populate as you build — explicit user instructions worth remembering across sessions._

## Gotchas

- If you add a new dependency to `lib/db` (or another shared lib) that's used transitively by `artifacts/api-server`, also add it directly to `artifacts/api-server/package.json` — esbuild externalizes DB/native packages during the server build, and pnpm doesn't hoist transitive deps into the api-server's own resolution path.

## Pointers

- See the `pnpm-workspace` skill for workspace structure, TypeScript setup, and package details
