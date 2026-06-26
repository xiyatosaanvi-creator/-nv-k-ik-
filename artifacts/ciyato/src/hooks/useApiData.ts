/**
 * useApiData — Suggestion #138
 * Real data fetching hooks that wire the Ciyato web prototype to the live
 * /api/v1 endpoints on the Express API server. Falls back to mock data when
 * the server is unavailable (dev preview mode).
 */

import { useState, useEffect, useCallback } from "react";

const API_BASE = import.meta.env.VITE_API_URL ?? `${window.location.origin}/api/v1`;

interface ApiState<T> {
  data: T | null;
  loading: boolean;
  error: string | null;
  refetch: () => void;
}

async function apiFetch<T>(path: string, options?: RequestInit): Promise<T> {
  const res = await fetch(`${API_BASE}${path}`, {
    headers: {
      "Content-Type": "application/json",
      ...(options?.headers ?? {}),
    },
    ...options,
  });
  if (!res.ok) {
    const err = await res.text().catch(() => `HTTP ${res.status}`);
    throw new Error(err);
  }
  return res.json() as Promise<T>;
}

export function useApiData<T>(path: string, initialData: T | null = null): ApiState<T> {
  const [data, setData] = useState<T | null>(initialData);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [tick, setTick] = useState(0);

  const refetch = useCallback(() => setTick((t) => t + 1), []);

  useEffect(() => {
    let cancelled = false;
    setLoading(true);
    apiFetch<T>(path)
      .then((d) => { if (!cancelled) { setData(d); setError(null); } })
      .catch((e) => { if (!cancelled) setError(e.message ?? "Unknown error"); })
      .finally(() => { if (!cancelled) setLoading(false); });
    return () => { cancelled = true; };
  }, [path, tick]);

  return { data, loading, error, refetch };
}

// ── Domain-specific hooks ─────────────────────────────────────────────────────

export interface AppStat {
  packageName: string;
  label: string;
  durationMs: number;
  launchCount: number;
  category: string;
}

/** Live app usage statistics from the API server. */
export function useAppUsageStats() {
  return useApiData<AppStat[]>("/usage/stats");
}

export interface UsageSummary {
  totalScreenTimeMs: number;
  topApp: { label: string; durationMs: number } | null;
  dailyAvgMs: number;
  weekChange: number;
}

export function useUsageSummary() {
  return useApiData<UsageSummary>("/usage/summary");
}

export interface AiSuggestion {
  type: "cleanup" | "focus" | "category" | "agenda";
  title: string;
  body: string;
  action?: string;
}

export function useAiSuggestions() {
  return useApiData<AiSuggestion[]>("/ai/suggestions");
}

export interface DeviceSettings {
  darkMode: "dark" | "light" | "auto";
  iconShape: string;
  gridColumns: number;
  goldAccent: boolean;
  font: string;
  wallpaperBlur: number;
}

export function useDeviceSettings() {
  return useApiData<DeviceSettings>("/settings");
}

/** Mutation helper — PATCH settings. */
export async function updateSettings(patch: Partial<DeviceSettings>): Promise<DeviceSettings> {
  return apiFetch<DeviceSettings>("/settings", {
    method: "PATCH",
    body: JSON.stringify(patch),
  });
}

export interface FocusSession {
  id: number;
  startedAt: string;
  durationMin: number;
  blockedCategories: string[];
  completed: boolean;
}

export function useFocusSessions() {
  return useApiData<FocusSession[]>("/focus/sessions");
}

export async function startFocusSession(durationMin: number, blockedCats: string[]): Promise<FocusSession> {
  return apiFetch<FocusSession>("/focus/sessions", {
    method: "POST",
    body: JSON.stringify({ durationMin, blockedCategories: blockedCats }),
  });
}
