import { Router } from "express";
import { z } from "zod";
import { requireAuth } from "../../middlewares/auth";
import { AppError } from "../../middlewares/errorHandler";

const router = Router();

const querySchema = z.object({
  prompt: z.string().min(1).max(2000),
  model: z.enum(["gemini-flash", "gpt-4o-mini"]).optional().default("gemini-flash"),
  context: z.record(z.unknown()).optional(),
});

/**
 * POST /api/v1/ai/query — Suggestion #119
 * Proxy to Gemini / OpenAI with rate-limiting and JWT auth.
 * Requires GEMINI_API_KEY or OPENAI_API_KEY environment variables.
 */
router.post("/query", requireAuth, async (req, res, next) => {
  const result = querySchema.safeParse(req.body);
  if (!result.success) {
    return next(new AppError(400, "VALIDATION_ERROR", result.error.errors[0]?.message ?? "Invalid input"));
  }

  const { prompt, model, context } = result.data;

  try {
    const answer = model === "gemini-flash"
      ? await callGemini(prompt, context)
      : await callOpenAI(prompt, context);
    res.json({ answer, model });
  } catch (err) {
    next(new AppError(502, "AI_UPSTREAM_ERROR", err instanceof Error ? err.message : "AI service unavailable"));
  }
});

async function callGemini(prompt: string, context?: Record<string, unknown>): Promise<string> {
  const apiKey = process.env["GEMINI_API_KEY"];
  if (!apiKey) throw new Error("GEMINI_API_KEY not configured");

  const prefix = context ? `Context: ${JSON.stringify(context)}\n\n` : "";

  const res = await fetch(
    `https://generativelanguage.googleapis.com/v1beta/models/gemini-1.5-flash-latest:generateContent?key=${apiKey}`,
    {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({
        contents: [{ parts: [{ text: prefix + prompt }] }],
        generationConfig: { maxOutputTokens: 800, temperature: 0.4 },
      }),
    },
  );

  if (!res.ok) throw new Error(`Gemini error ${res.status}: ${await res.text()}`);

  const data = (await res.json()) as {
    candidates?: Array<{ content: { parts: Array<{ text: string }> } }>;
  };
  return data.candidates?.[0]?.content?.parts?.[0]?.text ?? "";
}

async function callOpenAI(prompt: string, context?: Record<string, unknown>): Promise<string> {
  const apiKey = process.env["OPENAI_API_KEY"];
  if (!apiKey) throw new Error("OPENAI_API_KEY not configured");

  const messages = [
    ...(context ? [{ role: "system", content: `Context: ${JSON.stringify(context)}` }] : []),
    { role: "user", content: prompt },
  ];

  const res = await fetch("https://api.openai.com/v1/chat/completions", {
    method: "POST",
    headers: {
      "Content-Type": "application/json",
      Authorization: `Bearer ${apiKey}`,
    },
    body: JSON.stringify({ model: "gpt-4o-mini", messages, max_tokens: 800 }),
  });

  if (!res.ok) throw new Error(`OpenAI error ${res.status}: ${await res.text()}`);

  const data = (await res.json()) as {
    choices?: Array<{ message: { content: string } }>;
  };
  return data.choices?.[0]?.message?.content ?? "";
}

export { router as aiRouter };
