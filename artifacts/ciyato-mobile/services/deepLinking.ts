/**
 * deepLinking.ts — Suggestion #140
 * Deep linking configuration for Ciyato Mobile using Expo Linking.
 * Handles in-app navigation from external URLs and push notification taps.
 *
 * URL scheme: ciyato://
 * HTTPS domain: https://ciyato.app (Universal Links / App Links)
 *
 * Supported routes:
 *   ciyato://home              → Home tab
 *   ciyato://apps              → App grid
 *   ciyato://category/{id}     → Specific category
 *   ciyato://focus             → Focus session
 *   ciyato://vault             → Secure vault (triggers biometric)
 *   ciyato://settings          → Settings screen
 *   ciyato://ai/agenda         → AI daily agenda
 *   ciyato://privacy           → Privacy dashboard
 */

import * as Linking from "expo-linking";
import { Href } from "expo-router";

/** Expo Router linking configuration — add to <Stack> in _layout.tsx. */
export const LINKING_CONFIG = {
  prefixes: [
    Linking.createURL("/"),
    "ciyato://",
    "https://ciyato.app",
    "https://www.ciyato.app",
  ],
  config: {
    screens: {
      "(tabs)": {
        screens: {
          index:     "",
          apps:      "apps",
          insights:  "insights",
          settings:  "settings",
          profile:   "profile",
        },
      },
      "category/[id]":    "category/:id",
      "focus":            "focus",
      "vault":            "vault",
      "ai/agenda":        "ai/agenda",
      "privacy":          "privacy",
      "backup":           "backup",
      "duplicate-photos": "duplicate-photos",
      "voice":            "voice",
    },
  },
};

/** Create a deep link URL for a given route. */
export function createDeepLink(route: string, params?: Record<string, string>): string {
  const base = Linking.createURL(route);
  if (!params || Object.keys(params).length === 0) return base;
  const query = new URLSearchParams(params).toString();
  return `${base}?${query}`;
}

/** Parse an incoming URL and extract route + params. */
export function parseDeepLink(url: string): { route: string; params: Record<string, string> } {
  const parsed = Linking.parse(url);
  return {
    route: parsed.path ?? "",
    params: (parsed.queryParams ?? {}) as Record<string, string>,
  };
}

/** Handle an incoming deep link URL and return the Expo Router path. */
export function deepLinkToHref(url: string): Href | null {
  const { route, params } = parseDeepLink(url);
  const query = Object.keys(params).length > 0
    ? "?" + new URLSearchParams(params).toString()
    : "";

  switch (route) {
    case "":
    case "home":        return `/(tabs)/` as Href;
    case "apps":        return `/(tabs)/apps` as Href;
    case "insights":    return `/(tabs)/insights` as Href;
    case "settings":    return `/(tabs)/settings` as Href;
    case "focus":       return `/focus` as Href;
    case "vault":       return `/vault` as Href;
    case "ai/agenda":   return `/ai/agenda` as Href;
    case "privacy":     return `/privacy` as Href;
    case "backup":      return `/backup` as Href;
    case "voice":       return `/voice` as Href;
    default:
      if (route.startsWith("category/")) {
        const id = route.split("/")[1];
        return `/category/${id}` as Href;
      }
      return null;
  }
}

/** Set up the Linking event listener (call in root _layout.tsx). */
export function useLinkingHandler(onLink: (href: Href) => void) {
  const subscription = Linking.addEventListener("url", ({ url }) => {
    const href = deepLinkToHref(url);
    if (href) onLink(href);
  });
  return () => subscription.remove();
}
