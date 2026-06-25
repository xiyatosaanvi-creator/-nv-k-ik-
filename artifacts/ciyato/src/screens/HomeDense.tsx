import { T } from "../tokens";
import { StatusBar } from "../components/StatusBar";
import { Icon } from "../components/Icon";
import { SearchBar } from "../components/SearchBar";
import { AppIcon } from "../components/AppIcon";
import { GlassCard } from "../components/GlassCard";
import { mockApps } from "../data/mockApps";

export const HomeDense = () => {
  return (
    <div style={{ display: "flex", flexDirection: "column", height: "100%", background: "radial-gradient(ellipse at top, #1A2028 0%, #0B0F12 50%)", fontFamily: "Inter, system-ui, sans-serif", color: T.white }}>
      <StatusBar />
      <div style={{ flex: 1, overflowY: "auto", padding: "8px 18px 0", scrollbarWidth: "none" }}>
        {/* Header */}
        <div style={{ display: "flex", justifyContent: "space-between", alignItems: "flex-start", marginBottom: 14 }}>
          <div>
            <div style={{ fontSize: 20, fontWeight: 700 }}>Good morning, Alex ☀️</div>
            <div style={{ fontSize: 13, color: T.sec }}>Tuesday, May 20</div>
          </div>
          <div style={{ display: "flex", gap: 8 }}>
            <div style={{ width: 36, height: 36, borderRadius: 18, background: T.card, display: "flex", alignItems: "center", justifyContent: "center", border: `1px solid ${T.border}` }}>
              <Icon name="star4" size={16} color={T.gold} />
            </div>
            <div style={{ width: 36, height: 36, borderRadius: 18, background: T.card, display: "flex", alignItems: "center", justifyContent: "center", border: `1px solid ${T.border}` }}>
              <Icon name="bell" size={16} color={T.white} />
            </div>
          </div>
        </div>

        <SearchBar />

        {/* Agenda / Weather */}
        <div style={{ display: "flex", gap: 10, marginTop: 14 }}>
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
                { t: "10:00", m: "Design sync", i: "video" },
                { t: "13:30", m: "Lunch w/ Sarah", i: "cutlery" },
                { t: "15:00", m: "Review deck", i: "file" }
              ].map((item, i) => (
                <div key={i} style={{ display: "flex", alignItems: "center", gap: 8 }}>
                  <div style={{ width: 4, height: 4, borderRadius: 2, background: T.gold }} />
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
          <span style={{ fontSize: 12, color: T.blue }}>Edit</span>
        </div>
        
        <div style={{ display: "grid", gridTemplateColumns: "1fr 1fr", gap: 10 }}>
          {Object.entries(mockApps.categories).map(([cat, apps]) => (
            <GlassCard key={cat} style={{ padding: "14px 12px" }}>
              <div style={{ display: "flex", justifyContent: "space-between", marginBottom: 10 }}>
                <span style={{ fontSize: 13, fontWeight: 600 }}>{cat}</span>
              </div>
              <div style={{ display: "flex", flexWrap: "wrap", gap: 6 }}>
                {apps.slice(0, 3).map((a) => (
                  <AppIcon key={a} app={a} size={38} />
                ))}
                <div style={{ width: 38, height: 38, borderRadius: 10, background: "rgba(255,255,255,0.05)", display: "flex", alignItems: "center", justifyContent: "center", fontSize: 12, fontWeight: 600, color: T.sec }}>
                  +{apps.length - 3}
                </div>
              </div>
            </GlassCard>
          ))}
        </div>

        {/* Shortcuts */}
        <div style={{ marginTop: 20, padding: 14, background: T.cardStr, borderRadius: 20, border: `1px solid ${T.border}` }}>
          <div style={{ fontSize: 13, fontWeight: 600, marginBottom: 4 }}>Duplicate smart shortcuts</div>
          <div style={{ fontSize: 11, color: T.muted, marginBottom: 12 }}>One app, multiple places</div>
          <div style={{ display: "flex", gap: 10 }}>
            {mockApps.shortcuts.map((a) => (
              <AppIcon key={a} app={a} size={42} />
            ))}
            <div style={{ width: 42, height: 42, borderRadius: 12, border: `1px dashed ${T.border}`, display: "flex", alignItems: "center", justifyContent: "center", color: T.muted }}>
              <Icon name="plus" size={18} />
            </div>
          </div>
        </div>

        <div style={{ marginBottom: 90 }} />
      </div>

      {/* Dock */}
      <div style={{ position: "absolute", bottom: 20, left: 16, right: 16, display: "flex", justifyContent: "space-around", padding: "14px 10px", background: "rgba(20,25,30,0.85)", border: `1px solid ${T.border}`, borderRadius: 32, backdropFilter: "blur(20px)" }}>
        {["Phone", "Messages", "Chrome", "Camera", "Copilot"].map(a => (
          <AppIcon key={a} app={a} size={50} />
        ))}
      </div>
    </div>
  );
};
