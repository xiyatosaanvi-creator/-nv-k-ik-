import { T } from "../tokens";
import { CiyatoLogo } from "../components/CiyatoLogo";
import { Icon } from "../components/Icon";
import { GlassCard } from "../components/GlassCard";

export const Showcase = () => {
  const features = [
    { icon: "ai", label: "AI-Powered", sub: "AI suggestions that learn you." },
    { icon: "lock", label: "Privacy First", sub: "Your data stays private and secure." },
    { icon: "zap", label: "Lightning Fast", sub: "Low latency. High performance." },
    { icon: "android", label: "Made for Android", sub: "Seamless. Powerful. Native." },
  ];
  
  const cards = [
    { title: "Smart App Library", desc: "Automatically organize your apps, your way.", icon: "grid", accent: "#3B82F6" },
    { title: "Duplicate Shortcuts", desc: "One app, multiple places.", icon: "duplicate", accent: T.gold },
    { title: "Theme Studio", desc: "Personalize your experience.", icon: "palette", accent: "#9B8EFF" },
    { title: "Smart Collections", desc: "Find anything faster.", icon: "folder", accent: "#39C66A" },
    { title: "Ciyato Photos", desc: "AI-powered gallery.", icon: "photo", accent: "#F97316" },
    { title: "AI Search", desc: "Find anything, instantly.", icon: "search", accent: T.blue },
  ];

  return (
    <div style={{ display: "flex", flexDirection: "column", minHeight: "100%", background: T.bgEl, fontFamily: "Inter, system-ui, sans-serif", color: T.white, overflowY: "auto", scrollbarWidth: "none" }}>
      <div style={{ padding: "28px 20px 0", textAlign: "center" }}>
        <div style={{ display: "flex", justifyContent: "center", alignItems: "center", gap: 10, marginBottom: 6 }}>
          <CiyatoLogo size={30} />
          <span style={{ fontSize: 26, fontWeight: 800 }}>Ciyato</span>
        </div>
        <div style={{ fontSize: 13, color: T.sec, marginBottom: 20 }}>The complete AI phone organization ecosystem</div>
      </div>
      
      {/* Main phone preview */}
      <div style={{ padding: "0 20px 20px" }}>
        <GlassCard style={{ padding: 16, background: "rgba(198,161,91,0.07)", border: `1px solid rgba(198,161,91,0.18)` }}>
          <div style={{ display: "flex", gap: 10, alignItems: "center", marginBottom: 12 }}>
            <Icon name="home" size={20} color={T.gold} />
            <div>
              <div style={{ fontSize: 15, fontWeight: 700 }}>Ciyato Home</div>
              <div style={{ fontSize: 11, color: T.muted }}>Your smart, personalized launcher</div>
            </div>
          </div>
          {/* Mini home preview */}
          <div style={{ background: T.bg, borderRadius: 14, padding: 12, marginBottom: 4 }}>
            <div style={{ fontSize: 11, color: T.sec, marginBottom: 8 }}>Good morning, Alex ☀️</div>
            <div style={{ display: "grid", gridTemplateColumns: "repeat(3, 1fr)", gap: 5, marginBottom: 8 }}>
              {["Work", "Social", "Finance", "Creativity", "Utilities", "Daily"].map(c => (
                <div key={c} style={{ background: T.card, borderRadius: 8, padding: "6px", fontSize: 9, color: T.sec, fontWeight: 500 }}>{c}</div>
              ))}
            </div>
            <div style={{ display: "flex", gap: 6, background: T.card, borderRadius: 10, padding: 8 }}>
              {["Phone", "Chrome", "Zoom", "Camera", "Copilot"].map(a => (
                <div key={a} style={{ width: 26, height: 26, borderRadius: 8, background: T.cardStr, display: "flex", alignItems: "center", justifyContent: "center", fontSize: 10 }}>•</div>
              ))}
            </div>
          </div>
        </GlassCard>
        
        {/* Feature cards grid */}
        <div style={{ display: "grid", gridTemplateColumns: "1fr 1fr", gap: 10, marginTop: 10 }}>
          {cards.map(card => (
            <GlassCard key={card.title} style={{ padding: 14 }}>
              <div style={{ width: 32, height: 32, borderRadius: 10, background: `${card.accent}22`, border: `1px solid ${card.accent}40`, display: "flex", alignItems: "center", justifyContent: "center", marginBottom: 8 }}>
                <Icon name={card.icon} size={16} color={card.accent} />
              </div>
              <div style={{ fontSize: 12, fontWeight: 700, marginBottom: 4 }}>{card.title}</div>
              <div style={{ fontSize: 11, color: T.muted, lineHeight: 1.4 }}>{card.desc}</div>
            </GlassCard>
          ))}
        </div>
        
        {/* Footer strip */}
        <div style={{ marginTop: 14, padding: "16px", background: T.bg, borderRadius: 20, border: `1px solid ${T.border}` }}>
          <div style={{ display: "grid", gridTemplateColumns: "1fr 1fr", gap: 12 }}>
            {features.map(f => (
              <div key={f.label} style={{ display: "flex", gap: 10, alignItems: "flex-start" }}>
                <div style={{ width: 28, height: 28, borderRadius: 8, background: "rgba(198,161,91,0.1)", display: "flex", alignItems: "center", justifyContent: "center", flexShrink: 0 }}>
                  <Icon name={f.icon} size={14} color={T.gold} />
                </div>
                <div>
                  <div style={{ fontSize: 11, fontWeight: 700, color: T.gold, marginBottom: 2 }}>{f.label}</div>
                  <div style={{ fontSize: 10, color: T.muted, lineHeight: 1.4 }}>{f.sub}</div>
                </div>
              </div>
            ))}
          </div>
        </div>

        <div style={{ marginTop: 30, textAlign: "center", fontSize: 13, fontWeight: 600, color: T.sec }}>
          Ciyato — Organize Smarter. Live Better.
        </div>
        
        <div style={{ marginBottom: 40 }} />
      </div>
    </div>
  );
};
