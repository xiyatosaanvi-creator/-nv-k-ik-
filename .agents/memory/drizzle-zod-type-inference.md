---
name: drizzle-zod type inference
description: drizzle-zod@0.8.x schemas are incompatible with z.infer; use Drizzle's native type helpers instead.
---

## Rule
Do NOT use `z.infer<typeof createInsertSchema(...)>` or `z.infer<typeof createSelectSchema(...)>`.
Use `typeof table.$inferInsert` and `typeof table.$inferSelect` instead.

## Why
drizzle-zod@0.8.x generates schemas whose internal TypeScript type does not satisfy `ZodType<any, any, any>`, so `z.infer` fails with "missing _type, _parse, _getType, _getOrReturnCtx, and 7 more properties". This is a known incompatibility with Zod 3.25+.

## How to apply
For insert types: `type InsertFoo = Omit<typeof fooTable.$inferInsert, "id" | "createdAt" | ...>`
For select types: `type Foo = typeof fooTable.$inferSelect`
For partial update types: `type UpdateFoo = Partial<Omit<typeof fooTable.$inferInsert, "id" | "userId" | ...>>`
The Zod schema itself (for `.safeParse()` etc.) still works fine; only `z.infer` on it is broken.
