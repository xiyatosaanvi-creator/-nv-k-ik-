import { Router } from "express";
import { z } from "zod";
import { authenticate } from "../../middleware/auth.js";

const router = Router();

const querySchema = z.object({
  prompt: z.string().min(1).max(2000),
  model: z.enum(["gemini-flash", "gpt-4o-mini"]).optional().default("gemini-flash"),
  context: z.record(z.unknown()).optional(),
});

/**
 * POST /api/v1/ai/query — Suggestion #119
 * Proxy to Gemini / OpenAI with rate-limiting and JWT auth.
 * Uses GEMINI_API_KEY or OPENAI_API_KEY environment variables.
 */
router.post("/query", authenticate, async (req, res) => {
  const result = querySchema.safeParse(req.body);
  if (!result.success) {
    res.status(400).json({
      error: { code: "VALIDATION_ERROR", message: result.error.errors[0]?.message ?? "Invalid input" }
    });
    return;
  }

  const { prompt, model, context } = result.data;

  try {
    let answer: string;
    if (model === "gemini-flash") {
      answer = await callGemini(prompt, context);
    } else {
      answer = await callOpenAI(prompt, context);
    }
    res.json({ answer, model });
  } catch (err) {
    const message = err instanceof Error ? err.message : "AI service unavailable";
    res.status(502).json({ error: { code: "AI_UPSTREAM_ERROR", message } });
  }
});

async function callGemini(prompt: string, context?: Record<string, unknown>): Promise<string> {
  const apiKey = process.env["GEMINI_API_KEY"];
  if (!apiKey) throw new Error("GEMINI_API_KEY not configured");

  const systemParts = context
    ? `Context: ${JSON.stringify(context)}\n\n`
    : "";

  const res = await fetch(
    `https://generativelanguage.googleapis.com/v1beta/models/gemini-1.5-flash-latest:generateContent?key=${apiKey}`,
    {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({
        contents: [{ parts: [{ text: systemParts + prompt }] }],
        generationConfig: { maxOutputTokens: 800, temperature: 0.4 },
      }),
    }
  );

  if (!res.ok) {
    const body = await res.text();
    throw new Error(`Gemini error ${res.status}: ${body}`);
  }

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

  if (!res.ok) {
    const body = await res.text();
    throw new Error(`OpenAI error ${res.status}: ${body}`);
  }

  const data = (await res.json()) as {
    choices?: Array<{ message: { content: string } }>;
  };
  return data.choices?.[0]?.message?.content ?? "";
}

export { router as aiRouter };
