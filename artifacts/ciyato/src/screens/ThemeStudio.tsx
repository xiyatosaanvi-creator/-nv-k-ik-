import { useState } from "react";
import { T } from "../tokens";
import { StatusBar } from "../components/StatusBar";
import { Icon } from "../components/Icon";
import { GlassCard } from "../components/GlassCard";

export const ThemeStudio = () => {
  const [isActive, setIsActive] = useState(true);
  const [darkMode, setDarkMode] = useState("Dark");

  return (
    <div style={{ display: "flex", flexDirection: "column", height: "100%", background: T.bg, fontFamily: "Inter, system-ui, sans-serif", color: T.white }}>
      <StatusBar />
      <div style={{ flex: 1, overflowY: "auto", padding: "12px 20px 0", scrollbarWidth: "none" }}>
        
        <div style={{ display: "flex", justifyContent: "space-between", alignItems: "center", marginBottom: 20 }}>
          <div style={{ fontSize: 22, fontWeight: 700 }}>Ciyato Theme Studio</div>
          <Icon name="settings" size={20} color={T.sec} />
        </div>

        <GlassCard style={{ marginBottom: 20 }}>
          <div style={{ display: "flex", justifyContent: "space-between", alignItems: "center", marginBottom: 16 }}>
            <div>
              <div style={{ fontSize: 15, fontWeight: 600 }}>Ciyato Home Active</div>
              <div style={{ fontSize: 12, color: T.muted }}>Set as default launcher</div>
            </div>
            <div onClick={() => setIsActive(!isActive)} style={{ width: 44, height: 24, borderRadius: 12, background: isActive ? T.gold : T.card, position: "relative", cursor: "pointer", transition: "all 0.2s" }}>
              <div style={{ width: 20, height: 20, borderRadius: 10, background: "#fff", position: "absolute", top: 2, left: isActive ? 22 : 2, transition: "all 0.2s", boxShadow: "0 2px 4px rgba(0,0,0,0.2)" }} />
            </div>
          </div>

          <div style={{ borderTop: `1px solid ${T.borderSub}`, paddingTop: 16, display: "flex", flexDirection: "column", gap: 16 }}>
            <div style={{ display: "flex", justifyContent: "space-between", alignItems: "center" }}>
              <span style={{ fontSize: 14 }}>Icon Style</span>
              <span style={{ fontSize: 13, color: T.sec }}>Glass Rounded ↗</span>
            </div>
            <div style={{ display: "flex", justifyContent: "space-between", alignItems: "center" }}>
              <span style={{ fontSize: 14 }}>Color Accents</span>
              <div style={{ display: "flex", gap: 6 }}>
                {[T.gold, T.blue, T.green, "#E1306C"].map((c, i) => (
                  <div key={i} style={{ width: 18, height: 18, borderRadius: 9, background: c, border: i === 0 ? "2px solid #fff" : "none" }} />
                ))}
              </div>
            </div>
            <div style={{ display: "flex", justifyContent: "space-between", alignItems: "center" }}>
              <span style={{ fontSize: 14 }}>Dark Mode</span>
              <div style={{ display: "flex", background: T.card, borderRadius: 8, padding: 4 }}>
                {["Auto", "Light", "Dark"].map(m => (
                  <div key={m} onClick={() => setDarkMode(m)} style={{ padding: "4px 12px", borderRadius: 6, fontSize: 11, fontWeight: darkMode === m ? 600 : 400, background: darkMode === m ? T.cardMed : "transparent", color: darkMode === m ? T.white : T.muted, cursor: "pointer" }}>
                    {m}
                  </div>
                ))}
              </div>
            </div>
          </div>
        </GlassCard>

        <div style={{ display: "flex", justifyContent: "center", marginBottom: 24 }}>
          <div style={{ width: "60%", aspectRatio: "1/2", background: "radial-gradient(ellipse at top, #1A2028 0%, #0B0F12 50%)", borderRadius: 24, border: `4px solid ${T.cardMed}`, padding: 12 }}>
            <div style={{ fontSize: 8, color: T.sec, marginBottom: 8 }}>Good morning, Alex</div>
            <div style={{ display: "grid", gridTemplateColumns: "1fr 1fr", gap: 6 }}>
              <div style={{ background: T.card, height: 40, borderRadius: 8 }} />
              <div style={{ background: T.card, height: 40, borderRadius: 8 }} />
              <div style={{ background: T.card, height: 40, borderRadius: 8 }} />
              <div style={{ background: T.card, height: 40, borderRadius: 8 }} />
            </div>
            <div style={{ marginTop: "auto", display: "flex", justifyContent: "space-around", background: "rgba(0,0,0,0.5)", padding: 6, borderRadius: 12, marginTop: 40 }}>
              {[1, 2, 3, 4].map(i => <div key={i} style={{ width: 14, height: 14, borderRadius: 4, background: T.cardStr }} />)}
            </div>
          </div>
        </div>
        
        <div style={{ fontSize: 13, color: T.muted, textAlign: "center", marginBottom: 20 }}>Built for focus. Designed for you.</div>

      </div>
    </div>
  );
};
