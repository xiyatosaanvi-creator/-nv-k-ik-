import { T } from "../tokens";
import { StatusBar } from "../components/StatusBar";
import { Icon } from "../components/Icon";
import { SearchBar } from "../components/SearchBar";
import { AppIcon } from "../components/AppIcon";
import { mockApps } from "../data/mockApps";
import { GlassCard } from "../components/GlassCard";

export const HomeSpacious = () => {
  return (
    <div style={{ display: "flex", flexDirection: "column", height: "100%", background: "radial-gradient(ellipse at top, #1A2028 0%, #0B0F12 50%)", fontFamily: "Inter, system-ui, sans-serif", color: T.white }}>
      <StatusBar />
      <div style={{ flex: 1, overflowY: "auto", padding: "16px 24px 0", scrollbarWidth: "none" }}>
        
        {/* Header */}
        <div style={{ display: "flex", justifyContent: "space-between", alignItems: "flex-start", marginBottom: 24 }}>
          <div>
            <div style={{ fontSize: 24, fontWeight: 700, marginBottom: 4 }}>Good morning, Alex ☀️</div>
            <div style={{ fontSize: 14, color: T.sec }}>Tuesday, May 20</div>
          </div>
          <div style={{ display: "flex", gap: 12 }}>
            <div style={{ width: 40, height: 40, borderRadius: 20, background: T.card, display: "flex", alignItems: "center", justifyContent: "center", border: `1px solid ${T.border}` }}>
              <Icon name="star4" size={18} color={T.gold} />
            </div>
          </div>
        </div>

        <SearchBar />

        {/* Agenda / Weather */}
        <div style={{ display: "flex", gap: 12, marginTop: 24 }}>
          <GlassCard style={{ flex: 1, padding: "18px", display: "flex", flexDirection: "column", justifyContent: "space-between", background: "linear-gradient(135deg, rgba(255,255,255,0.08) 0%, rgba(255,255,255,0.03) 100%)" }}>
            <div style={{ display: "flex", justifyContent: "space-between", alignItems: "center", marginBottom: 12 }}>
              <div style={{ fontSize: 32, fontWeight: 700 }}>24°</div>
              <Icon name="sun" size={24} color={T.goldSoft} />
            </div>
            <div>
              <div style={{ fontSize: 14, fontWeight: 600 }}>Partly sunny</div>
              <div style={{ fontSize: 12, color: T.muted }}>New York · AQI 42</div>
            </div>
          </GlassCard>
        </div>

        {/* Smart Categories */}
        <div style={{ display: "flex", justifyContent: "space-between", alignItems: "center", marginTop: 28, marginBottom: 16 }}>
          <span style={{ fontSize: 16, fontWeight: 600 }}>Smart categories</span>
        </div>
        
        <div style={{ display: "grid", gridTemplateColumns: "1fr 1fr", gap: 12 }}>
          {Object.entries(mockApps.categories).slice(0, 4).map(([cat, apps]) => (
            <GlassCard key={cat} style={{ padding: "16px 14px" }}>
              <div style={{ display: "flex", justifyContent: "space-between", marginBottom: 12 }}>
                <span style={{ fontSize: 14, fontWeight: 600 }}>{cat}</span>
              </div>
              <div style={{ display: "flex", flexWrap: "wrap", gap: 8 }}>
                {apps.slice(0, 3).map((a) => (
                  <AppIcon key={a} app={a} size={42} />
                ))}
                <div style={{ width: 42, height: 42, borderRadius: 12, background: "rgba(255,255,255,0.05)", display: "flex", alignItems: "center", justifyContent: "center", fontSize: 13, fontWeight: 600, color: T.sec }}>
                  +{apps.length - 3}
                </div>
              </div>
            </GlassCard>
          ))}
        </div>

        <div style={{ marginBottom: 120 }} />
      </div>

      {/* Dock */}
      <div style={{ position: "absolute", bottom: 24, left: 24, right: 24, display: "flex", justifyContent: "space-around", padding: "18px 14px", background: "rgba(20,25,30,0.85)", border: `1px solid ${T.border}`, borderRadius: 40, backdropFilter: "blur(20px)" }}>
        {["Phone", "Messages", "Chrome", "Camera", "Copilot"].map(a => (
          <AppIcon key={a} app={a} size={56} />
        ))}
      </div>
    </div>
  );
};
