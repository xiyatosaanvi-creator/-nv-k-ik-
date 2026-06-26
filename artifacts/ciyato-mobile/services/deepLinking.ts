/**
 * deepLinking.ts — Suggestion #140
 * Deep linking configuration for Ciyato Mobile using Expo Linking.
 * Handles in-app navigation from external URLs and push notification taps.
 *
 * URL scheme : ciyato://
 * HTTPS domain: https://ciyato.app (Universal Links / App Links)
 *
 * Supported routes (matching actual app/(tabs) structure):
 *   ciyato://              → Home tab  (tabs/index)
 *   ciyato://apps          → Apps tab
 *   ciyato://files         → Files tab
 *   ciyato://photos        → Photos tab
 *   ciyato://search        → Search tab
 *   ciyato://profile       → Profile screen (modal)
 */

import * as Linking from "expo-linking";
import { useEffect } from "react";
import { useRouter } from "expo-router";
import { Href } from "expo-router";

/** Expo Router linking configuration — pass to <Stack> linking prop in _layout.tsx. */
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
          index:   "",
          apps:    "apps",
          files:   "files",
          photos:  "photos",
          search:  "search",
        },
      },
      profile: "profile",
    },
  },
};

/** Create a ciyato:// deep link URL for a given route. */
export function createDeepLink(route: string, params?: Record<string, string>): string {
  const base = Linking.createURL(route);
  if (!params || Object.keys(params).length === 0) return base;
  return `${base}?${new URLSearchParams(params).toString()}`;
}

/** Parse an incoming URL and extract route + params. */
export function parseDeepLink(url: string): { route: string; params: Record<string, string> } {
  const parsed = Linking.parse(url);
  return {
    route: parsed.path ?? "",
    params: (parsed.queryParams ?? {}) as Record<string, string>,
  };
}

/** Map a deep link URL to an Expo Router Href. Returns null for unknown routes. */
export function deepLinkToHref(url: string): Href | null {
  const { route } = parseDeepLink(url);

  switch (route.replace(/^\//, "")) {
    case "":
    case "home":    return "/(tabs)/" as Href;
    case "apps":    return "/(tabs)/apps" as Href;
    case "files":   return "/(tabs)/files" as Href;
    case "photos":  return "/(tabs)/photos" as Href;
    case "search":  return "/(tabs)/search" as Href;
    case "profile": return "/profile" as Href;
    default:        return null;
  }
}

/**
 * Hook: wire up deep link listener. Call inside a component mounted at the root layout.
 * Navigates automatically when a ciyato:// URL is opened while the app is running.
 */
export function useDeepLinkHandler() {
  const router = useRouter();

  useEffect(() => {
    const subscription = Linking.addEventListener("url", ({ url }) => {
      const href = deepLinkToHref(url);
      if (href) router.push(href);
    });

    Linking.getInitialURL().then((url) => {
      if (url) {
        const href = deepLinkToHref(url);
        if (href) router.replace(href);
      }
    });

    return () => subscription.remove();
  }, [router]);
}
