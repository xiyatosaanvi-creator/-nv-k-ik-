import { T } from "../tokens";
import { StatusBar } from "../components/StatusBar";
import { Icon } from "../components/Icon";
import { SearchBar } from "../components/SearchBar";
import { AppIcon } from "../components/AppIcon";
import { BottomNav } from "../components/BottomNav";
import { CiyatoLogo } from "../components/CiyatoLogo";
import { mockApps } from "../data/mockApps";
import { useSettings } from "../context/SettingsContext";

export const AppDrawer = () => {
  const { editMode, toggleEditMode, isHidden, hideApp, hiddenApps, restoreApp } = useSettings();

  return (
    <div style={{ display: "flex", flexDirection: "column", height: "100%", background: T.lightBg, fontFamily: "Inter, system-ui, sans-serif", color: T.lightText }}>
      <StatusBar light />
      <div style={{ flex: 1, overflowY: "auto", padding: "12px 18px 0", scrollbarWidth: "none" }}>

        {/* Header */}
        <div style={{ display: "flex", justifyContent: "space-between", alignItems: "center", marginBottom: 16 }}>
          <CiyatoLogo size={24} light />
          <div style={{ display: "flex", alignItems: "center", gap: 8 }}>
            <div style={{ display: "flex", alignItems: "center", gap: 6, background: "rgba(0,0,0,0.05)", padding: "6px 14px", borderRadius: 50 }}>
              <span style={{ fontSize: 12, fontWeight: 600, color: T.lightText }}>Smart App Library</span>
              <Icon name="star4" size={12} color={T.gold} />
            </div>
            <div
              onClick={toggleEditMode}
              style={{
                fontSize: 11,
                fontWeight: 700,
                padding: "6px 12px",
                borderRadius: 50,
                cursor: "pointer",
                background: editMode ? T.gold : "rgba(0,0,0,0.05)",
                color: editMode ? "#1a1204" : T.lightText,
              }}
            >
              {editMode ? "Done" : "Edit"}
            </div>
          </div>
        </div>

        {/* Search */}
        <SearchBar placeholder="Search your apps…" light extra={<Icon name="filter" size={17} color={T.lightSec} />} />

        {/* Sections */}
        {Object.entries(mockApps.categories).map(([cat, apps]) => {
          const visibleApps = apps.filter((a) => !isHidden(a));
          if (visibleApps.length === 0) return null;
          return (
            <div key={cat} style={{ marginTop: 20 }}>
              <div style={{ fontSize: 14, fontWeight: 700, marginBottom: 10, color: T.lightText }}>{cat}</div>
              <div style={{ background: T.lightCard, borderRadius: 20, padding: 16, border: `1px solid ${T.lightBorder}`, boxShadow: "0 4px 20px rgba(0,0,0,0.03)" }}>
                <div style={{ display: "flex", flexWrap: "wrap", gap: 12 }}>
                  {visibleApps.map((a) => (
                    <div key={a} style={{ position: "relative", display: "flex", flexDirection: "column", alignItems: "center", gap: 6, width: "calc(25% - 9px)" }}>
                      <div style={{ position: "relative" }} onClick={() => editMode && hideApp(a)}>
                        <AppIcon app={a} size={48} />
                        {editMode && (
                          <div style={{ position: "absolute", top: -6, right: -6, width: 18, height: 18, borderRadius: 9, background: "#D64545", display: "flex", alignItems: "center", justifyContent: "center", cursor: "pointer" }}>
                            <Icon name="x" size={10} color="#fff" />
                          </div>
                        )}
                      </div>
                      <span style={{ fontSize: 10, color: T.lightSec, textAlign: "center", whiteSpace: "nowrap", overflow: "hidden", textOverflow: "ellipsis", maxWidth: "100%" }}>{a}</span>
                    </div>
                  ))}
                </div>
              </div>
            </div>
          );
        })}

        {/* Duplicate Shortcuts */}
        <div style={{ marginTop: 24, marginBottom: 16, padding: 16, background: T.lightCard, borderRadius: 20, border: `1px solid ${T.gold}55`, boxShadow: "0 4px 20px rgba(198,161,91,0.1)" }}>
          <div style={{ fontSize: 14, fontWeight: 700, marginBottom: 4, color: T.lightText }}>Duplicate smart shortcuts</div>
          <div style={{ fontSize: 12, color: T.lightSec, marginBottom: 14 }}>One app, multiple places</div>
          <div style={{ display: "flex", gap: 12 }}>
            {mockApps.shortcuts.filter((a) => !isHidden(a)).map((a) => (
              <AppIcon key={a} app={a} size={46} />
            ))}
          </div>
        </div>

        {/* Hidden apps management */}
        {hiddenApps.length > 0 && (
          <div style={{ marginBottom: 20, padding: 16, background: "rgba(0,0,0,0.03)", borderRadius: 20, border: `1px dashed ${T.lightBorder}` }}>
            <div style={{ fontSize: 13, fontWeight: 700, marginBottom: 10, color: T.lightText, display: "flex", alignItems: "center", gap: 6 }}>
              <Icon name="eye" size={14} color={T.lightSec} /> Hidden apps ({hiddenApps.length})
            </div>
            <div style={{ display: "flex", flexWrap: "wrap", gap: 8 }}>
              {hiddenApps.map((a) => (
                <div
                  key={a}
                  onClick={() => restoreApp(a)}
                  style={{ fontSize: 11, fontWeight: 600, padding: "6px 12px", borderRadius: 50, background: "#fff", border: `1px solid ${T.lightBorder}`, cursor: "pointer", color: T.lightText }}
                >
                  {a} · Restore
                </div>
              ))}
            </div>
          </div>
        )}

        <div style={{ marginBottom: 20 }} />
      </div>

      <BottomNav active="grid" items={[
        { id: "grid", label: "Apps", icon: "grid" },
        { id: "folder", label: "Files", icon: "folder" },
        { id: "star", label: "Favorites", icon: "star4" },
        { id: "settings", label: "Settings", icon: "settings" },
      ]} light />
    </div>
  );
};
