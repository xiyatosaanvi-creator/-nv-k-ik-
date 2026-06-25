import { T } from "../tokens";
import { CiyatoLogo } from "../components/CiyatoLogo";
import { Icon } from "../components/Icon";

export const BeforeAfter = () => (
  <div style={{ display: "flex", flexDirection: "column", height: "100%", background: T.bgEl, fontFamily: "Inter, system-ui, sans-serif", color: T.white, overflowY: "auto", scrollbarWidth: "none" }}>
    {/* Header */}
    <div style={{ padding: "32px 24px 0", textAlign: "center" }}>
      <div style={{ display: "flex", justifyContent: "center", alignItems: "center", gap: 10, marginBottom: 16 }}>
        <CiyatoLogo size={28} />
        <span style={{ fontSize: 24, fontWeight: 700 }}>Ciyato</span>
      </div>
      <div style={{ display: "inline-block", padding: "6px 18px", borderRadius: 50, background: T.card, border: `1px solid ${T.border}`, fontSize: 12, color: T.sec, marginBottom: 20 }}>AI Phone Organizer for Android</div>
      <div style={{ fontSize: 32, fontWeight: 800, letterSpacing: -1, lineHeight: 1.2, marginBottom: 8 }}>
        From <span style={{ color: T.gold }}>chaos</span> to clarity.
      </div>
      <div style={{ fontSize: 14, color: T.sec, marginBottom: 24 }}>One place for everything. Smarter categories. Faster access.</div>
    </div>
    
    {/* Before/After panels */}
    <div style={{ display: "grid", gridTemplateColumns: "1fr 1fr", gap: 12, padding: "0 16px" }}>
      {/* Before */}
      <div style={{ background: "rgba(255,255,255,0.04)", borderRadius: 20, border: `1px solid ${T.border}`, overflow: "hidden" }}>
        <div style={{ padding: "10px 14px 6px" }}>
          <div style={{ display: "inline-block", padding: "4px 12px", borderRadius: 50, background: "#2a2a2a", border: `1px solid #444`, fontSize: 11, fontWeight: 700, color: T.sec, marginBottom: 4 }}>Before</div>
          <div style={{ fontSize: 10, color: T.muted }}>Cluttered. Scattered. Hard to find.</div>
          <div style={{ width: 24, height: 1.5, background: T.muted, marginTop: 4 }} />
        </div>
        <div style={{ padding: "0 10px 10px" }}>
          <div style={{ display: "grid", gridTemplateColumns: "repeat(4, 1fr)", gap: 4 }}>
            {["🖼️","🧮","🗺️","▶️","🛠️","📁","💼","⚙️","📞","🌐","📧","▶️","🔵","📸","🐦","💼","💬","🕺","💬","🖼️","📅"].map((e, i) => (
              <div key={i} style={{ background: `hsl(${(i * 47) % 360}, 40%, 20%)`, borderRadius: 8, display: "flex", flexDirection: "column", alignItems: "center", justifyContent: "center", aspectRatio: "1", fontSize: 12, gap: 1 }}>
                <span>{e}</span>
              </div>
            ))}
          </div>
          <div style={{ marginTop: 8, fontSize: 9, color: T.muted, borderTop: `1px solid ${T.borderSub}`, paddingTop: 8 }}>Downloads ↗</div>
          {["invoice_78291.pdf", "Screenshot_2024.png", "IMG_2024_05.jpg"].map((f, i) => (
            <div key={i} style={{ padding: "4px 0", fontSize: 9, color: T.muted, display: "flex", gap: 6 }}>
              <span style={{ fontSize: 10 }}>{["📄","🖼️","🖼️"][i]}</span>{f}
            </div>
          ))}
        </div>
      </div>
      
      {/* After */}
      <div style={{ background: "rgba(198,161,91,0.05)", borderRadius: 20, border: `1px solid rgba(198,161,91,0.2)`, overflow: "hidden" }}>
        <div style={{ padding: "10px 14px 6px" }}>
          <div style={{ display: "inline-block", padding: "4px 12px", borderRadius: 50, background: "rgba(198,161,91,0.15)", border: `1px solid ${T.gold}`, fontSize: 11, fontWeight: 700, color: T.gold, marginBottom: 4 }}>After</div>
          <div style={{ fontSize: 10, color: T.sec }}>Organized. Intelligent. Effortless.</div>
          <div style={{ width: 24, height: 1.5, background: T.gold, marginTop: 4 }} />
        </div>
        <div style={{ padding: "0 10px 10px", fontSize: 8 }}>
          <div style={{ marginBottom: 6, color: T.sec }}>Good morning, Alex ☀️</div>
          <div style={{ display: "grid", gridTemplateColumns: "1fr 1fr", gap: 3, marginBottom: 4 }}>
            {["Work 12apps", "Social 9apps", "Finance 6apps", "Daily 8apps"].map(c => (
              <div key={c} style={{ background: T.card, borderRadius: 6, padding: "4px 6px", fontSize: 8, color: T.sec }}>{c}</div>
            ))}
          </div>
          <div style={{ background: T.cardStr, borderRadius: 8, padding: "6px 8px", marginBottom: 4 }}>
            <div style={{ fontSize: 8, color: T.muted, marginBottom: 3 }}>Duplicate smart shortcuts</div>
            <div style={{ display: "flex", gap: 3 }}>
              {["Zoom", "Notion", "WhatsApp", "Drive"].map(a => (
                <div key={a} style={{ width: 18, height: 18, borderRadius: 5, background: T.card, display: "flex", alignItems: "center", justifyContent: "center", fontSize: 8 }}>•</div>
              ))}
            </div>
          </div>
          <div style={{ display: "flex", justifyContent: "space-around", background: T.card, borderRadius: 8, padding: "5px 4px" }}>
            {["📞","💬","🌐","📷"].map((e, i) => (
              <div key={i} style={{ width: 22, height: 22, borderRadius: 7, background: T.bgEl, display: "flex", alignItems: "center", justifyContent: "center", fontSize: 10 }}>{e}</div>
            ))}
          </div>
        </div>
      </div>
    </div>
    
    {/* Bullets */}
    <div style={{ display: "grid", gridTemplateColumns: "1fr 1fr", gap: 0, padding: "24px 16px 0", marginTop: 4 }}>
      <div style={{ padding: "0 0 0 4px" }}>
        {["Too many apps", "Unclear structure", "Hard to find files", "Lost time & focus"].map(b => (
          <div key={b} style={{ display: "flex", alignItems: "center", gap: 6, marginBottom: 12 }}>
            <div style={{ width: 18, height: 18, borderRadius: 9, background: "rgba(239,68,68,0.2)", display: "flex", alignItems: "center", justifyContent: "center" }}>
              <span style={{ fontSize: 10, color: "#EF4444", fontWeight: 700 }}>✕</span>
            </div>
            <span style={{ fontSize: 12, color: T.muted }}>{b}</span>
          </div>
        ))}
      </div>
      <div style={{ padding: "0 0 0 4px" }}>
        {["Smart categories", "Organized files", "Instant access", "More time for you"].map(b => (
          <div key={b} style={{ display: "flex", alignItems: "center", gap: 6, marginBottom: 12 }}>
            <div style={{ width: 18, height: 18, borderRadius: 9, background: "rgba(198,161,91,0.2)", display: "flex", alignItems: "center", justifyContent: "center" }}>
              <span style={{ fontSize: 10, color: T.gold, fontWeight: 700 }}>✓</span>
            </div>
            <span style={{ fontSize: 12, color: T.sec }}>{b}</span>
          </div>
        ))}
      </div>
    </div>
    
    {/* Footer */}
    <div style={{ margin: "24px 16px 24px", padding: "16px 20px", background: T.card, borderRadius: 20, border: `1px solid ${T.border}`, display: "flex", justifyContent: "space-between", alignItems: "center" }}>
      <div style={{ display: "flex", alignItems: "center", gap: 8 }}>
        <Icon name="star4" size={16} color={T.gold} />
        <span style={{ fontSize: 12, color: T.sec }}>Ciyato brings clarity to your digital life.</span>
      </div>
      <span style={{ fontSize: 12, color: T.gold, fontWeight: 600 }}>Smarter. Faster. Yours.</span>
    </div>
  </div>
);
