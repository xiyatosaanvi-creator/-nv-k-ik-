/**
 * Integration tests for the auth API routes — Suggestion #146.
 * Run with: pnpm --filter @ciyato/api-server test
 */

import { describe, it, expect } from "vitest";
import request from "supertest";
import app from "../app";

describe("Auth API", () => {
  const testEmail = `test-${Date.now()}@ciyato.test`;
  const testPassword = "TestPass123!";
  let authToken: string;

  describe("POST /api/v1/auth/register", () => {
    it("creates a new account successfully", async () => {
      const res = await request(app)
        .post("/api/v1/auth/register")
        .send({ email: testEmail, password: testPassword, displayName: "Test User" })
        .expect(201);

      expect(res.body).toHaveProperty("token");
      expect(res.body).toHaveProperty("user");
      expect(res.body.user.email).toBe(testEmail);
      authToken = res.body.token;
    });

    it("rejects duplicate email", async () => {
      await request(app)
        .post("/api/v1/auth/register")
        .send({ email: testEmail, password: testPassword })
        .expect(409);
    });

    it("validates email format", async () => {
      await request(app)
        .post("/api/v1/auth/register")
        .send({ email: "not-an-email", password: testPassword })
        .expect(400);
    });

    it("enforces minimum password length", async () => {
      await request(app)
        .post("/api/v1/auth/register")
        .send({ email: "newuser@test.com", password: "short" })
        .expect(400);
    });
  });

  describe("POST /api/v1/auth/login", () => {
    it("signs in with correct credentials", async () => {
      const res = await request(app)
        .post("/api/v1/auth/login")
        .send({ email: testEmail, password: testPassword })
        .expect(200);

      expect(res.body).toHaveProperty("token");
      expect(res.body.user.email).toBe(testEmail);
    });

    it("rejects wrong password", async () => {
      await request(app)
        .post("/api/v1/auth/login")
        .send({ email: testEmail, password: "WrongPassword!" })
        .expect(401);
    });

    it("rejects unknown email", async () => {
      await request(app)
        .post("/api/v1/auth/login")
        .send({ email: "nobody@test.com", password: testPassword })
        .expect(401);
    });
  });

  describe("GET /api/v1/auth/me", () => {
    it("returns user profile with valid token", async () => {
      const res = await request(app)
        .get("/api/v1/auth/me")
        .set("Authorization", `Bearer ${authToken}`)
        .expect(200);

      expect(res.body.user.email).toBe(testEmail);
    });

    it("returns 401 without token", async () => {
      await request(app)
        .get("/api/v1/auth/me")
        .expect(401);
    });

    it("returns 401 with invalid token", async () => {
      await request(app)
        .get("/api/v1/auth/me")
        .set("Authorization", "Bearer not-a-real-token")
        .expect(401);
    });
  });

  describe("PATCH /api/v1/auth/me", () => {
    it("updates display name", async () => {
      const res = await request(app)
        .patch("/api/v1/auth/me")
        .set("Authorization", `Bearer ${authToken}`)
        .send({ displayName: "Updated Name" })
        .expect(200);

      expect(res.body.user.displayName).toBe("Updated Name");
    });
  });

  describe("GET /api/v1/settings", () => {
    it("returns settings for authenticated user", async () => {
      const res = await request(app)
        .get("/api/v1/settings")
        .set("Authorization", `Bearer ${authToken}`)
        .expect(200);

      expect(res.body).toHaveProperty("settings");
      expect(res.body.settings).toHaveProperty("theme");
    });
  });
});

describe("Health Check", () => {
  it("returns healthy status", async () => {
    const res = await request(app)
      .get("/api/healthz")
      .expect(200);

    expect(res.body.status).toBe("ok");
  });
});
