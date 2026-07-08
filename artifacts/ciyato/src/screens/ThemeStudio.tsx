import { T } from "../tokens";
import { StatusBar } from "../components/StatusBar";
import { Icon } from "../components/Icon";
import { GlassCard } from "../components/GlassCard";
import { AppIcon } from "../components/AppIcon";
import { useSettings, type LayoutMode } from "../context/SettingsContext";
import { getVisibleWithOverflow } from "../lib/appOverflow";
import { mockApps } from "../data/mockApps";

const LAYOUT_OPTIONS: { id: LayoutMode; label: string }[] = [
  { id: "dense", label: "Dense" },
  { id: "spacious", label: "Spacious" },
  { id: "library", label: "Smart App Library" },
];

export const ThemeStudio = () => {
  const {
    layoutMode, setLayoutMode,
    darkMode, setDarkMode,
    accentColor, setAccentColor, accents,
    hiddenApps, restoreApp,
    resetLayout, resetTheme, resetAll,
  } = useSettings();

  const previewSlots = layoutMode === "dense" ? 4 : 6;
  const previewCats = Object.entries(mockApps.categories).slice(0, 4);
  const previewDark = darkMode !== "light";

  return (
    <div style={{ display: "flex", flexDirection: "column", height: "100%", background: T.bg, fontFamily: "Inter, system-ui, sans-serif", color: T.white }}>
      <StatusBar />
      <div style={{ flex: 1, overflowY: "auto", padding: "12px 20px 0", scrollbarWidth: "none" }}>

        <div style={{ display: "flex", justifyContent: "space-between", alignItems: "center", marginBottom: 20 }}>
          <div style={{ fontSize: 22, fontWeight: 700 }}>Ciyato Theme Studio</div>
          <Icon name="settings" size={20} color={T.sec} />
        </div>

        <GlassCard style={{ marginBottom: 16 }}>
          <div style={{ fontSize: 11, fontWeight: 700, letterSpacing: 0.5, textTransform: "uppercase", color: T.muted, marginBottom: 12 }}>Launcher layout</div>
          <div style={{ display: "flex", background: T.card, borderRadius: 10, padding: 4, gap: 4 }}>
            {LAYOUT_OPTIONS.map((opt) => (
              <div
                key={opt.id}
                onClick={() => setLayoutMode(opt.id)}
                style={{
                  flex: 1,
                  textAlign: "center",
                  padding: "8px 4px",
                  borderRadius: 8,
                  fontSize: 11,
                  fontWeight: layoutMode === opt.id ? 700 : 500,
                  background: layoutMode === opt.id ? T.cardMed : "transparent",
                  color: layoutMode === opt.id ? T.white : T.muted,
                  cursor: "pointer",
                }}
              >
                {opt.label}
              </div>
            ))}
          </div>
          <div style={{ fontSize: 11, color: T.muted, marginTop: 10, lineHeight: 1.5 }}>
            Controls how Home and the App Drawer arrange your apps — how much fits on screen, and how compact the categories look.
          </div>
        </GlassCard>

        <GlassCard style={{ marginBottom: 20 }}>
          <div style={{ borderBottom: `1px solid ${T.borderSub}`, paddingBottom: 16, marginBottom: 16, display: "flex", flexDirection: "column", gap: 16 }}>
            <div style={{ display: "flex", justifyContent: "space-between", alignItems: "center" }}>
              <span style={{ fontSize: 14 }}>Icon Style</span>
              <span style={{ fontSize: 13, color: T.sec }}>Glass Rounded ↗</span>
            </div>
            <div style={{ display: "flex", justifyContent: "space-between", alignItems: "center" }}>
              <span style={{ fontSize: 14 }}>Color Accents</span>
              <div style={{ display: "flex", gap: 6 }}>
                {accents.map((c) => (
                  <div
                    key={c}
                    onClick={() => setAccentColor(c)}
                    style={{ width: 18, height: 18, borderRadius: 9, background: c, border: accentColor === c ? "2px solid #fff" : "none", cursor: "pointer" }}
                  />
                ))}
              </div>
            </div>
            <div style={{ display: "flex", justifyContent: "space-between", alignItems: "center" }}>
              <span style={{ fontSize: 14 }}>Dark Mode</span>
              <div style={{ display: "flex", background: T.card, borderRadius: 8, padding: 4 }}>
                {(["auto", "light", "dark"] as const).map((m) => (
                  <div key={m} onClick={() => setDarkMode(m)} style={{ padding: "4px 12px", borderRadius: 6, fontSize: 11, fontWeight: darkMode === m ? 600 : 400, background: darkMode === m ? T.cardMed : "transparent", color: darkMode === m ? T.white : T.muted, cursor: "pointer", textTransform: "capitalize" }}>
                    {m}
                  </div>
                ))}
              </div>
            </div>
          </div>

          <div style={{ display: "flex", gap: 8 }}>
            <div onClick={resetLayout} style={{ flex: 1, textAlign: "center", padding: "8px 4px", borderRadius: 8, fontSize: 11, fontWeight: 600, color: T.sec, border: `1px solid ${T.border}`, cursor: "pointer" }}>Reset layout</div>
            <div onClick={resetTheme} style={{ flex: 1, textAlign: "center", padding: "8px 4px", borderRadius: 8, fontSize: 11, fontWeight: 600, color: T.sec, border: `1px solid ${T.border}`, cursor: "pointer" }}>Reset theme</div>
            <div onClick={resetAll} style={{ flex: 1, textAlign: "center", padding: "8px 4px", borderRadius: 8, fontSize: 11, fontWeight: 600, color: T.sec, border: `1px solid ${T.border}`, cursor: "pointer" }}>Reset all</div>
          </div>
        </GlassCard>

        {/* Live preview reflects the actual layout/theme settings above */}
        <div style={{ display: "flex", justifyContent: "center", marginBottom: 20 }}>
          <div style={{ width: "70%", aspectRatio: "1/2", background: previewDark ? "radial-gradient(ellipse at top, #1A2028 0%, #0B0F12 50%)" : "radial-gradient(ellipse at top, #F5F2EC 0%, #E9E4D8 60%)", borderRadius: 24, border: `4px solid ${T.cardMed}`, padding: 12, overflow: "hidden" }}>
            <div style={{ fontSize: 8, color: previewDark ? T.sec : T.lightSec, marginBottom: 8 }}>Good morning, Alex</div>
            <div style={{ display: "grid", gridTemplateColumns: layoutMode === "dense" ? "1fr 1fr" : "1fr", gap: 6 }}>
              {previewCats.map(([cat, apps]) => {
                const { visible, overflow } = getVisibleWithOverflow(apps, layoutMode === "dense" ? 3 : 5);
                return (
                  <div key={cat} style={{ background: previewDark ? T.card : "rgba(0,0,0,0.04)", borderRadius: 8, padding: 6 }}>
                    <div style={{ fontSize: 7, fontWeight: 700, color: previewDark ? T.white : T.lightText, marginBottom: 4 }}>{cat}</div>
                    <div style={{ display: "flex", gap: 3 }}>
                      {visible.map((a) => <AppIcon key={a} app={a} size={16} />)}
                      {overflow > 0 && <div style={{ width: 16, height: 16, borderRadius: 4, background: "rgba(255,255,255,0.15)", fontSize: 7, display: "flex", alignItems: "center", justifyContent: "center", color: T.sec }}>+{overflow}</div>}
                    </div>
                  </div>
                );
              })}
            </div>
            <div style={{ marginTop: 20, display: "flex", justifyContent: "space-around", background: "rgba(0,0,0,0.5)", padding: 6, borderRadius: 12 }}>
              {[1, 2, 3, 4].map((i) => <div key={i} style={{ width: 14, height: 14, borderRadius: 4, background: accentColor, opacity: 0.6 }} />)}
            </div>
          </div>
        </div>

        {/* Hidden apps management */}
        <GlassCard style={{ marginBottom: 20 }}>
          <div style={{ fontSize: 13, fontWeight: 700, marginBottom: 4 }}>Hidden apps</div>
          <div style={{ fontSize: 11, color: T.muted, marginBottom: 12 }}>
            {hiddenApps.length === 0 ? "Nothing hidden yet — hide any app from Home or the App Drawer without uninstalling it." : `${hiddenApps.length} app(s) hidden from Home and the App Drawer.`}
          </div>
          {hiddenApps.length > 0 && (
            <div style={{ display: "flex", flexWrap: "wrap", gap: 8 }}>
              {hiddenApps.map((a) => (
                <div key={a} onClick={() => restoreApp(a)} style={{ display: "flex", alignItems: "center", gap: 6, fontSize: 11, fontWeight: 600, padding: "6px 10px", borderRadius: 50, background: T.card, border: `1px solid ${T.border}`, cursor: "pointer" }}>
                  <AppIcon app={a} size={16} /> {a} · Restore
                </div>
              ))}
            </div>
          )}
        </GlassCard>

        <div style={{ fontSize: 13, color: T.muted, textAlign: "center", marginBottom: 20 }}>Built for focus. Designed for you.</div>

      </div>
    </div>
  );
};
