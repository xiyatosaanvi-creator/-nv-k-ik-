import { createContext, useContext, useEffect, useState, type ReactNode } from "react";
import { T } from "../tokens";

export type LayoutMode = "dense" | "spacious" | "library";
export type DarkMode = "auto" | "light" | "dark";

const STORAGE_KEY = "ciyato-settings-v1";

const ACCENTS = [T.gold, T.blue, T.green, "#E1306C"];

interface SettingsState {
  layoutMode: LayoutMode;
  accentColor: string;
  darkMode: DarkMode;
  hiddenApps: string[];
  editMode: boolean;
}

interface SettingsContextValue extends SettingsState {
  accents: string[];
  setLayoutMode: (m: LayoutMode) => void;
  setAccentColor: (c: string) => void;
  setDarkMode: (m: DarkMode) => void;
  toggleEditMode: () => void;
  hideApp: (app: string) => void;
  restoreApp: (app: string) => void;
  isHidden: (app: string) => boolean;
  resetLayout: () => void;
  resetTheme: () => void;
  resetAll: () => void;
}

const defaults: SettingsState = {
  layoutMode: "dense",
  accentColor: T.gold,
  darkMode: "dark",
  hiddenApps: [],
  editMode: false,
};

function load(): SettingsState {
  if (typeof window === "undefined") return defaults;
  try {
    const raw = window.localStorage.getItem(STORAGE_KEY);
    if (!raw) return defaults;
    const parsed = JSON.parse(raw);
    return { ...defaults, ...parsed };
  } catch {
    return defaults;
  }
}

const SettingsContext = createContext<SettingsContextValue | null>(null);

export const SettingsProvider = ({ children }: { children: ReactNode }) => {
  const [state, setState] = useState<SettingsState>(load);

  useEffect(() => {
    try {
      window.localStorage.setItem(STORAGE_KEY, JSON.stringify(state));
    } catch {
      // best-effort persistence only
    }
  }, [state]);

  const value: SettingsContextValue = {
    ...state,
    accents: ACCENTS,
    setLayoutMode: (layoutMode) => setState((s) => ({ ...s, layoutMode })),
    setAccentColor: (accentColor) => setState((s) => ({ ...s, accentColor })),
    setDarkMode: (darkMode) => setState((s) => ({ ...s, darkMode })),
    toggleEditMode: () => setState((s) => ({ ...s, editMode: !s.editMode })),
    hideApp: (app) =>
      setState((s) => (s.hiddenApps.includes(app) ? s : { ...s, hiddenApps: [...s.hiddenApps, app] })),
    restoreApp: (app) =>
      setState((s) => ({ ...s, hiddenApps: s.hiddenApps.filter((a) => a !== app) })),
    isHidden: (app) => state.hiddenApps.includes(app),
    resetLayout: () => setState((s) => ({ ...s, layoutMode: defaults.layoutMode, hiddenApps: [] })),
    resetTheme: () => setState((s) => ({ ...s, accentColor: defaults.accentColor, darkMode: defaults.darkMode })),
    resetAll: () => setState(defaults),
  };

  return <SettingsContext.Provider value={value}>{children}</SettingsContext.Provider>;
};

export const useSettings = () => {
  const ctx = useContext(SettingsContext);
  if (!ctx) throw new Error("useSettings must be used within a SettingsProvider");
  return ctx;
};
