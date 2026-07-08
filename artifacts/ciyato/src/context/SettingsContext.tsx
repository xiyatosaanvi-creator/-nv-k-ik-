import { createContext, useContext, useEffect, useState, type ReactNode } from "react";
import { T } from "../tokens";

export type LayoutMode = "dense" | "spacious" | "library";
export type DarkMode = "auto" | "light" | "dark";

export interface HomePage {
  id: string;
  name: string;
  // ids of built-in categories (their label, e.g. "Work") and/or custom
  // category ids assigned to this home screen page.
  categoryIds: string[];
}

export interface CustomCategory {
  id: string;
  name: string;
  apps: string[];
}

const STORAGE_KEY = "ciyato-settings-v1";

const ACCENTS = [T.gold, T.blue, T.green, "#E1306C"];

const DEFAULT_PAGE_ID = "page-1";

interface SettingsState {
  layoutMode: LayoutMode;
  accentColor: string;
  darkMode: DarkMode;
  hiddenApps: string[];
  editMode: boolean;
  pages: HomePage[];
  customCategories: CustomCategory[];
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
  addPage: () => string;
  removePage: (pageId: string) => void;
  moveCategoryToPage: (categoryId: string, fromPageId: string, toPageId: string) => void;
  addCustomCategory: (name: string, apps: string[], pageId: string) => void;
  removeCustomCategory: (id: string) => void;
}

const defaults: SettingsState = {
  layoutMode: "dense",
  accentColor: T.gold,
  darkMode: "dark",
  hiddenApps: [],
  editMode: false,
  pages: [
    { id: DEFAULT_PAGE_ID, name: "Home", categoryIds: ["Work", "Social", "Finance", "Creativity", "Utilities", "Daily"] },
  ],
  customCategories: [],
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
    resetLayout: () => setState((s) => ({ ...s, layoutMode: defaults.layoutMode, hiddenApps: [], pages: defaults.pages, customCategories: defaults.customCategories })),
    resetTheme: () => setState((s) => ({ ...s, accentColor: defaults.accentColor, darkMode: defaults.darkMode })),
    resetAll: () => setState(defaults),
    addPage: () => {
      let newId = "";
      setState((s) => {
        newId = `page-${s.pages.length + 1}-${Date.now().toString(36)}`;
        const name = `Screen ${s.pages.length + 1}`;
        return { ...s, pages: [...s.pages, { id: newId, name, categoryIds: [] }] };
      });
      return newId;
    },
    removePage: (pageId) =>
      setState((s) => {
        if (s.pages.length <= 1) return s;
        const removed = s.pages.find((p) => p.id === pageId);
        const remaining = s.pages.filter((p) => p.id !== pageId);
        // don't strand categories that lived only on the removed page — fold
        // them back onto the first remaining screen.
        if (removed && removed.categoryIds.length > 0 && remaining[0]) {
          remaining[0] = { ...remaining[0], categoryIds: [...remaining[0].categoryIds, ...removed.categoryIds] };
        }
        return { ...s, pages: remaining };
      }),
    moveCategoryToPage: (categoryId, fromPageId, toPageId) =>
      setState((s) => {
        if (fromPageId === toPageId) return s;
        const pages = s.pages.map((p) => {
          if (p.id === fromPageId) return { ...p, categoryIds: p.categoryIds.filter((c) => c !== categoryId) };
          if (p.id === toPageId && !p.categoryIds.includes(categoryId)) return { ...p, categoryIds: [...p.categoryIds, categoryId] };
          return p;
        });
        return { ...s, pages };
      }),
    addCustomCategory: (name, apps, pageId) =>
      setState((s) => {
        const id = `custom-${Date.now().toString(36)}`;
        const category: CustomCategory = { id, name, apps };
        const pages = s.pages.map((p) => (p.id === pageId ? { ...p, categoryIds: [...p.categoryIds, id] } : p));
        return { ...s, customCategories: [...s.customCategories, category], pages };
      }),
    removeCustomCategory: (id) =>
      setState((s) => ({
        ...s,
        customCategories: s.customCategories.filter((c) => c.id !== id),
        pages: s.pages.map((p) => ({ ...p, categoryIds: p.categoryIds.filter((c) => c !== id) })),
      })),
  };

  return <SettingsContext.Provider value={value}>{children}</SettingsContext.Provider>;
};

export const useSettings = () => {
  const ctx = useContext(SettingsContext);
  if (!ctx) throw new Error("useSettings must be used within a SettingsProvider");
  return ctx;
};
