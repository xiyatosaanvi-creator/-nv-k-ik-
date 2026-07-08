import { useState } from "react";
import { T } from "../tokens";
import { StatusBar } from "../components/StatusBar";
import { Icon } from "../components/Icon";
import { SearchBar } from "../components/SearchBar";
import { AppIcon } from "../components/AppIcon";
import { GlassCard } from "../components/GlassCard";
import { mockApps } from "../data/mockApps";
import { useSettings } from "../context/SettingsContext";
import { getVisibleWithOverflow } from "../lib/appOverflow";

// Home is the launcher's home screen — the first thing you see when Ciyato is
// your Android home app. Its density (Dense / Spacious / Smart App Library)
// is a Theme Studio setting, not a separate screen. The full app catalog
// lives one tap away in the App Drawer.
export const Home = () => {
  const { layoutMode, editMode, toggleEditMode, isHidden, hideApp, accentColor, darkMode } = useSettings();
  const [expanded, setExpanded] = useState<string | null>(null);

  const spacious = layoutMode !== "dense";
  const previewSlots = layoutMode === "dense" ? 4 : 6;
  // "auto" has no real system theme to read from in this mockup, so it falls
  // back to dark (Ciyato's default), matching Theme Studio's own preview logic.
  const isLight = darkMode === "light";
  const textColor = isLight ? T.lightText : T.white;
  const secColor = isLight ? T.lightSec : T.sec;
  const bg = isLight ? "radial-gradient(ellipse at top, #F5F2EC 0%, #E9E4D8 60%)" : "radial-gradient(ellipse at top, #1A2028 0%, #0B0F12 50%)";
  const cardBg = isLight ? "rgba(0,0,0,0.03)" : T.card;
  const cardBorder = isLight ? T.lightBorder : T.border;
  const dockBg = isLight ? "rgba(255,255,255,0.85)" : "rgba(20,25,30,0.85)";

  const categories = Object.entries(mockApps.categories)
    .map(([cat, apps]) => [cat, apps.filter((a) => !isHidden(a))] as const)
    .filter(([, apps]) => apps.length > 0);

  const shortcuts = mockApps.shortcuts.filter((a) => !isHidden(a));

  return (
    <div style={{ display: "flex", flexDirection: "column", height: "100%", background: bg, fontFamily: "Inter, system-ui, sans-serif", color: textColor }}>
      <StatusBar light={isLight} />
      <div style={{ flex: 1, overflowY: "auto", padding: spacious ? "16px 24px 0" : "8px 18px 0", scrollbarWidth: "none" }}>
        {/* Header */}
        <div style={{ display: "flex", justifyContent: "space-between", alignItems: "flex-start", marginBottom: spacious ? 24 : 14 }}>
          <div>
            <div style={{ fontSize: spacious ? 24 : 20, fontWeight: 700 }}>Good morning, Alex ☀️</div>
            <div style={{ fontSize: spacious ? 14 : 13, color: secColor }}>Tuesday, May 20</div>
          </div>
          <div style={{ display: "flex", gap: 8 }}>
            <div onClick={toggleEditMode} style={{ padding: "0 12px", height: 36, borderRadius: 18, background: editMode ? accentColor : cardBg, display: "flex", alignItems: "center", justifyContent: "center", border: `1px solid ${cardBorder}`, fontSize: 11, fontWeight: 700, color: editMode ? "#1a1204" : secColor, cursor: "pointer" }}>
              {editMode ? "Done" : "Edit"}
            </div>
            <div style={{ width: 36, height: 36, borderRadius: 18, background: cardBg, display: "flex", alignItems: "center", justifyContent: "center", border: `1px solid ${cardBorder}` }}>
              <Icon name="settings" size={16} color={textColor} />
            </div>
          </div>
        </div>

        <SearchBar light={isLight} />

        {/* Agenda / Weather */}
        <div style={{ display: "flex", gap: 10, marginTop: spacious ? 24 : 14 }}>
          <GlassCard style={{ flex: 0.8, padding: "14px", display: "flex", flexDirection: "column", justifyContent: "space-between", background: "linear-gradient(135deg, rgba(255,255,255,0.08) 0%, rgba(255,255,255,0.03) 100%)" }}>
            <div style={{ display: "flex", justifyContent: "space-between", alignItems: "center" }}>
              <div style={{ fontSize: 24, fontWeight: 700 }}>24°</div>
              <Icon name="sun" size={20} color={T.goldSoft} />
            </div>
            <div>
              <div style={{ fontSize: 12, fontWeight: 600 }}>Partly sunny</div>
              <div style={{ fontSize: 11, color: T.muted }}>New York · AQI 42</div>
            </div>
          </GlassCard>
          <GlassCard style={{ flex: 1.2, padding: "12px 14px", display: "flex", flexDirection: "column" }}>
            <div style={{ display: "flex", justifyContent: "space-between", alignItems: "center", marginBottom: 8 }}>
              <span style={{ fontSize: 13, fontWeight: 600 }}>Today</span>
              <span style={{ fontSize: 11, color: T.blue }}>View all</span>
            </div>
            <div style={{ display: "flex", flexDirection: "column", gap: 6 }}>
              {[
                { t: "10:00", m: "Design sync" },
                { t: "13:30", m: "Lunch w/ Sarah" },
                { t: "15:00", m: "Review deck" },
              ].map((item, i) => (
                <div key={i} style={{ display: "flex", alignItems: "center", gap: 8 }}>
                  <div style={{ width: 4, height: 4, borderRadius: 2, background: accentColor }} />
                  <span style={{ fontSize: 11, color: T.sec, width: 34 }}>{item.t}</span>
                  <span style={{ fontSize: 12 }}>{item.m}</span>
                </div>
              ))}
            </div>
          </GlassCard>
        </div>

        {/* Smart Categories */}
        <div style={{ display: "flex", justifyContent: "space-between", alignItems: "center", marginTop: 20, marginBottom: 12 }}>
          <span style={{ fontSize: 14, fontWeight: 600 }}>Smart categories</span>
          <span style={{ fontSize: 12, color: T.blue, cursor: "pointer" }}>Edit</span>
        </div>

        <div style={{ display: "grid", gridTemplateColumns: "1fr 1fr", gap: 10 }}>
          {categories.map(([cat, apps]) => {
            const isOpen = expanded === cat;
            const { visible, overflow } = getVisibleWithOverflow(apps, previewSlots);
            return (
              <GlassCard key={cat} style={{ padding: "14px 12px", gridColumn: isOpen ? "1 / -1" : undefined }} onClick={() => setExpanded(isOpen ? null : cat)}>
                <div style={{ display: "flex", justifyContent: "space-between", marginBottom: 10 }}>
                  <span style={{ fontSize: 13, fontWeight: 600 }}>{cat}</span>
                  <Icon name={isOpen ? "chevDown" : "chevRight"} size={13} color={T.muted} />
                </div>
                <div style={{ display: "flex", flexWrap: "wrap", gap: 6 }}>
                  {(isOpen ? apps : visible).map((a) => (
                    <div key={a} style={{ position: "relative" }} onClick={(e) => { if (editMode) { e.stopPropagation(); hideApp(a); } }}>
                      <AppIcon app={a} size={38} />
                      {editMode && (
                        <div style={{ position: "absolute", top: -5, right: -5, width: 15, height: 15, borderRadius: 8, background: "#D64545", display: "flex", alignItems: "center", justifyContent: "center" }}>
                          <Icon name="x" size={8} color="#fff" />
                        </div>
                      )}
                    </div>
                  ))}
                  {!isOpen && overflow > 0 && (
                    <div style={{ width: 38, height: 38, borderRadius: 10, background: "rgba(255,255,255,0.05)", display: "flex", alignItems: "center", justifyContent: "center", fontSize: 12, fontWeight: 600, color: T.sec }}>
                      +{overflow}
                    </div>
                  )}
                </div>
              </GlassCard>
            );
          })}
        </div>

        {/* Shortcuts */}
        {shortcuts.length > 0 && (
          <div style={{ marginTop: 20, padding: 14, background: cardBg, borderRadius: 20, border: `1px solid ${cardBorder}` }}>
            <div style={{ fontSize: 13, fontWeight: 600, marginBottom: 4 }}>Duplicate smart shortcuts</div>
            <div style={{ fontSize: 11, color: secColor, marginBottom: 12 }}>One app, multiple places</div>
            <div style={{ display: "flex", gap: 10 }}>
              {shortcuts.map((a) => (
                <AppIcon key={a} app={a} size={42} />
              ))}
              <div style={{ width: 42, height: 42, borderRadius: 12, border: `1px dashed ${cardBorder}`, display: "flex", alignItems: "center", justifyContent: "center", color: secColor }}>
                <Icon name="plus" size={18} />
              </div>
            </div>
          </div>
        )}

        <div style={{ marginBottom: 90 }} />
      </div>

      {/* Dock */}
      <div style={{ position: "absolute", bottom: 20, left: 16, right: 16, display: "flex", justifyContent: "space-around", padding: "14px 10px", background: dockBg, border: `1px solid ${cardBorder}`, borderRadius: 32, backdropFilter: "blur(20px)" }}>
        {["Phone", "Messages", "Chrome", "Camera", "Copilot"].filter((a) => !isHidden(a)).map((a) => (
          <AppIcon key={a} app={a} size={50} />
        ))}
      </div>
    </div>
  );
};
