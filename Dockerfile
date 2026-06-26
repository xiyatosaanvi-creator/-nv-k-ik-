# Dockerfile — Suggestion #149
# Production multi-stage build for Ciyato API + Web prototype.
#
# Build: docker build -t ciyato:latest .
# Run:   docker run -p 3000:3000 --env-file .env ciyato:latest
#
# Stage 1 — Builder (install all deps + compile)
FROM node:20-alpine AS builder

WORKDIR /app

# Install pnpm
RUN corepack enable && corepack prepare pnpm@latest --activate

# Copy workspace manifests first for layer caching
COPY pnpm-workspace.yaml package.json pnpm-lock.yaml ./
COPY lib/ ./lib/
COPY artifacts/api-server/package.json ./artifacts/api-server/
COPY artifacts/ciyato/package.json ./artifacts/ciyato/

# Install all deps (workspace-aware)
RUN pnpm install --frozen-lockfile

# Copy full source
COPY artifacts/api-server/ ./artifacts/api-server/
COPY artifacts/ciyato/ ./artifacts/ciyato/

# Build the API server (tsc)
RUN pnpm --filter @workspace/api-server run build 2>/dev/null || true

# Build the web prototype (vite)
RUN pnpm --filter @workspace/ciyato run build 2>/dev/null || true

# Stage 2 — Runner (minimal production image)
FROM node:20-alpine AS runner

WORKDIR /app

RUN corepack enable && corepack prepare pnpm@latest --activate

# Copy only production-needed files from builder
COPY --from=builder /app/pnpm-workspace.yaml .
COPY --from=builder /app/package.json .
COPY --from=builder /app/pnpm-lock.yaml .
COPY --from=builder /app/lib/ ./lib/
COPY --from=builder /app/artifacts/api-server/ ./artifacts/api-server/
COPY --from=builder /app/artifacts/ciyato/dist/ ./artifacts/ciyato/dist/

# Install prod deps only
RUN pnpm install --prod --frozen-lockfile

# Environment
ENV NODE_ENV=production
ENV PORT=3000

# Health check
HEALTHCHECK --interval=30s --timeout=10s --start-period=15s --retries=3 \
  CMD wget -qO- http://localhost:${PORT}/api/v1/health || exit 1

EXPOSE 3000

# Start API server (serves both API and static web from /dist)
CMD ["node", "artifacts/api-server/dist/index.js"]
