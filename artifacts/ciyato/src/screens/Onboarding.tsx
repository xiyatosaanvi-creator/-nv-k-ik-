import { useState, useEffect } from "react";

// ─── Premium Onboarding Color Palette ─────────────────────────────────────────
const C = {
  bg: "#0A0F18",
  bgEl: "#121927",
  bgCard: "#182133",
  gold: "#D4AF62",
  goldHi: "#E2C57A",
  goldGlow: "rgba(212,175,98,0.15)",
  goldBorder: "rgba(212,175,98,0.25)",
  text: "#F5F2EA",
  textSec: "#B9C1CC",
  textMuted: "#5E6878",
  border: "rgba(255,255,255,0.07)",
  card: "rgba(255,255,255,0.04)",
  cardHi: "rgba(255,255,255,0.08)",
  blue: "#3B82F6",
  green: "#34D399",
  purple: "#A78BFA",
  red: "#F87171",
};

// ─── Inject Keyframe Animations ───────────────────────────────────────────────
if (typeof document !== "undefined" && !document.getElementById("ciyato-ob-styles")) {
  const s = document.createElement("style");
  s.id = "ciyato-ob-styles";
  s.textContent = `
    @keyframes ob-fade-up {
      from { opacity: 0; transform: translateY(14px); }
      to   { opacity: 1; transform: translateY(0); }
    }
    @keyframes ob-slide-right {
      from { opacity: 0; transform: translateX(28px); }
      to   { opacity: 1; transform: translateX(0); }
    }
    @keyframes ob-slide-left {
      from { opacity: 0; transform: translateX(-28px); }
      to   { opacity: 1; transform: translateX(0); }
    }
    @keyframes ob-glow-pulse {
      0%, 100% { opacity: 0.35; }
      50%       { opacity: 0.65; }
    }
    @keyframes ob-scale-in {
      from { opacity: 0; transform: scale(0.9); }
      to   { opacity: 1; transform: scale(1); }
    }
  `;
  document.head.appendChild(s);
}

// ─── Easing helper ────────────────────────────────────────────────────────────
const EASE = "cubic-bezier(0.25, 0.46, 0.45, 0.94)";

// ─── Premium Ciyato Logo SVG ──────────────────────────────────────────────────
const CiyatoLogoHero = () => (
  <div style={{ position: "relative", display: "flex", justifyContent: "center", alignItems: "center" }}>
    {/* Radial glow behind logo */}
    <div style={{
      position: "absolute",
      width: 160, height: 160,
      borderRadius: "50%",
      background: "radial-gradient(circle, rgba(212,175,98,0.18) 0%, transparent 70%)",
      animation: "ob-glow-pulse 3.5s ease-in-out infinite",
      filter: "blur(24px)",
    }} />
    <svg width="100" height="100" viewBox="0 0 120 120" fill="none" style={{ position: "relative", zIndex: 1 }}>
      <defs>
        <radialGradient id="ob-gold-star" cx="50%" cy="50%" r="50%">
          <stop offset="0%" stopColor="#E8D090" />
          <stop offset="60%" stopColor="#D4AF62" />
          <stop offset="100%" stopColor="#A8813A" />
        </radialGradient>
        <radialGradient id="ob-ring-outer" cx="30%" cy="30%" r="70%">
          <stop offset="0%" stopColor="rgba(242,232,200,0.25)" />
          <stop offset="100%" stopColor="rgba(242,232,200,0.05)" />
        </radialGradient>
      </defs>
      {/* Outer subtle ring */}
      <circle cx="60" cy="60" r="58" stroke="rgba(212,175,98,0.15)" strokeWidth="1" fill="rgba(212,175,98,0.03)" />
      <circle cx="60" cy="60" r="48" stroke="rgba(212,175,98,0.10)" strokeWidth="0.75" fill="none" />
      {/* C letterform — elegant serif-inspired arc */}
      <path
        d="M80 26 C62 18, 36 24, 28 46 C20 68, 32 90, 54 96 C66 99, 80 95, 88 86"
        stroke="#F0EAD6" strokeWidth="7" strokeLinecap="round" fill="none"
      />
      {/* Inner arc for depth */}
      <path
        d="M74 34 C60 27, 42 32, 36 50 C30 68, 40 84, 57 89"
        stroke="rgba(212,175,98,0.35)" strokeWidth="2.5" strokeLinecap="round" fill="none"
      />
      {/* 4-pointed star compass */}
      <path
        d="M60 40 L63.5 56.5 L80 60 L63.5 63.5 L60 80 L56.5 63.5 L40 60 L56.5 56.5 Z"
        fill="url(#ob-gold-star)"
      />
    </svg>
  </div>
);

// ─── Screen Hero Components ───────────────────────────────────────────────────

const AppLibraryHero = () => {
  const cats = [
    { label: "Work",     count: "12", color: "#5B9CF6", icon: "💼" },
    { label: "Social",   count: "9",  color: "#F472B6", icon: "💬" },
    { label: "Finance",  count: "6",  color: C.gold,    icon: "💳" },
    { label: "Creative", count: "8",  color: "#A78BFA", icon: "🎨" },
    { label: "Utilities",count: "11", color: "#34D399", icon: "🔧" },
    { label: "Daily",    count: "5",  color: "#FB923C", icon: "📅" },
  ];
  return (
    <div style={{ width: "100%", background: C.bgCard, borderRadius: 18, padding: 14, border: `1px solid ${C.border}` }}>
      <div style={{ fontSize: 9, fontWeight: 700, color: C.textMuted, letterSpacing: 1, marginBottom: 10 }}>SMART CATEGORIES</div>
      <div style={{ display: "grid", gridTemplateColumns: "repeat(3, 1fr)", gap: 7 }}>
        {cats.map(cat => (
          <div key={cat.label} style={{
            background: `${cat.color}10`,
            border: `1px solid ${cat.color}30`,
            borderRadius: 12, padding: "9px 6px", textAlign: "center",
          }}>
            <div style={{ fontSize: 15, marginBottom: 3 }}>{cat.icon}</div>
            <div style={{ fontSize: 9, fontWeight: 700, color: cat.color, marginBottom: 1 }}>{cat.label}</div>
            <div style={{ fontSize: 8, color: C.textMuted }}>{cat.count} apps</div>
          </div>
        ))}
      </div>
      <div style={{
        marginTop: 10, background: "rgba(212,175,98,0.06)", borderRadius: 10,
        padding: "8px 10px", display: "flex", alignItems: "center", gap: 8,
        border: `1px solid rgba(212,175,98,0.14)`,
      }}>
        <div style={{ width: 18, height: 18, borderRadius: 9, background: "rgba(212,175,98,0.15)", display: "flex", alignItems: "center", justifyContent: "center" }}>
          <span style={{ fontSize: 9 }}>✦</span>
        </div>
        <span style={{ fontSize: 9, color: C.textSec, lineHeight: 1.4 }}>Smart shortcuts — find apps faster, without clutter</span>
      </div>
    </div>
  );
};

const FilesSearchHero = () => (
  <div style={{ width: "100%", display: "flex", gap: 8 }}>
    {/* Files panel */}
    <div style={{ flex: 1.1, background: C.bgCard, borderRadius: 16, padding: 11, border: `1px solid ${C.border}` }}>
      <div style={{ fontSize: 8, fontWeight: 700, color: C.textMuted, letterSpacing: 1, marginBottom: 8 }}>SMART FILES</div>
      {[
        { name: "Documents", icon: "📄", count: "24", color: "#5B9CF6" },
        { name: "Downloads", icon: "📥", count: "18", color: C.gold },
        { name: "Projects",  icon: "💼", count: "7",  color: "#A78BFA" },
        { name: "Videos",    icon: "▶️", count: "31", color: "#FB923C" },
      ].map(f => (
        <div key={f.name} style={{ display: "flex", alignItems: "center", gap: 7, marginBottom: 7 }}>
          <span style={{ fontSize: 11 }}>{f.icon}</span>
          <div style={{ flex: 1 }}>
            <div style={{ fontSize: 9, color: C.text, fontWeight: 600 }}>{f.name}</div>
          </div>
          <div style={{ fontSize: 8, fontWeight: 700, color: f.color }}>{f.count}</div>
        </div>
      ))}
    </div>
    {/* Right column: AI search + photos */}
    <div style={{ flex: 1, display: "flex", flexDirection: "column", gap: 7 }}>
      <div style={{ background: C.bgCard, borderRadius: 14, padding: 10, border: `1px solid ${C.border}` }}>
        <div style={{ fontSize: 8, fontWeight: 700, color: C.textMuted, letterSpacing: 1, marginBottom: 6 }}>AI SEARCH</div>
        <div style={{
          background: "rgba(255,255,255,0.05)", borderRadius: 9, padding: "7px 9px",
          display: "flex", alignItems: "center", gap: 6, border: `1px solid ${C.border}`,
        }}>
          <span style={{ fontSize: 10, color: C.gold }}>✦</span>
          <span style={{ fontSize: 9, color: C.textMuted, fontStyle: "italic" }}>Search anything…</span>
        </div>
        <div style={{ marginTop: 6, display: "flex", flexDirection: "column", gap: 3 }}>
          {["Zoom meeting notes", "January receipts"].map(hint => (
            <div key={hint} style={{ fontSize: 8, color: C.textMuted, padding: "3px 6px", background: C.card, borderRadius: 6 }}>{hint}</div>
          ))}
        </div>
      </div>
      <div style={{ background: C.bgCard, borderRadius: 14, padding: 10, border: `1px solid ${C.border}`, flex: 1 }}>
        <div style={{ fontSize: 8, fontWeight: 700, color: C.textMuted, letterSpacing: 1, marginBottom: 6 }}>COLLECTIONS</div>
        <div style={{ display: "grid", gridTemplateColumns: "1fr 1fr", gap: 4 }}>
          {["🌅","🏔️","🎭","🌿"].map((e, i) => (
            <div key={i} style={{
              background: `hsl(${i * 80 + 200}, 35%, 18%)`,
              borderRadius: 7, aspectRatio: "1",
              display: "flex", alignItems: "center", justifyContent: "center", fontSize: 13,
            }}>{e}</div>
          ))}
        </div>
      </div>
    </div>
  </div>
);

const PrivacyHero = () => (
  <div style={{
    width: "100%", background: C.bgCard, borderRadius: 18, padding: 16,
    border: `1px solid rgba(59,130,246,0.18)`,
  }}>
    <div style={{ display: "flex", justifyContent: "center", marginBottom: 14 }}>
      <div style={{
        width: 58, height: 58, borderRadius: 29,
        background: "rgba(59,130,246,0.10)",
        border: "1px solid rgba(59,130,246,0.28)",
        display: "flex", alignItems: "center", justifyContent: "center",
        boxShadow: "0 0 30px rgba(59,130,246,0.12)",
      }}>
        <span style={{ fontSize: 28 }}>🛡️</span>
      </div>
    </div>
    {[
      { text: "Your data stays on your device",       icon: "✓", color: C.green },
      { text: "No silent uploads or background tracking", icon: "✓", color: C.green },
      { text: "Permissions requested only when needed",   icon: "✓", color: C.green },
      { text: "Ciyato never uninstalls your apps",         icon: "✓", color: C.green },
      { text: "Switch back to any launcher, anytime",      icon: "✓", color: C.green },
    ].map(pt => (
      <div key={pt.text} style={{ display: "flex", alignItems: "flex-start", gap: 10, marginBottom: 9 }}>
        <div style={{
          width: 16, height: 16, borderRadius: 8, flexShrink: 0, marginTop: 1,
          background: "rgba(52,211,153,0.12)", border: "1px solid rgba(52,211,153,0.35)",
          display: "flex", alignItems: "center", justifyContent: "center",
        }}>
          <span style={{ fontSize: 9, color: pt.color, fontWeight: 700 }}>{pt.icon}</span>
        </div>
        <span style={{ fontSize: 11, color: C.textSec, lineHeight: 1.5 }}>{pt.text}</span>
      </div>
    ))}
  </div>
);

const PersonalizationHero = () => (
  <div style={{ width: "100%", display: "flex", flexDirection: "column", gap: 9 }}>
    {/* Theme swatches */}
    <div style={{ display: "flex", gap: 7 }}>
      {[
        { label: "Midnight", bg: "#0A0F18", accent: C.gold },
        { label: "Ocean",    bg: "#091628", accent: "#60A5FA" },
        { label: "Forest",   bg: "#0C1A12", accent: "#34D399" },
        { label: "Dusk",     bg: "#150B20", accent: "#A78BFA" },
      ].map((t, idx) => (
        <div key={t.label} style={{
          flex: 1, background: t.bg, borderRadius: 12, padding: "8px 4px",
          border: `1px solid ${idx === 0 ? t.accent + "60" : t.accent + "30"}`,
          textAlign: "center", position: "relative", overflow: "hidden",
        }}>
          {idx === 0 && (
            <div style={{
              position: "absolute", top: 4, right: 4,
              width: 6, height: 6, borderRadius: 3, background: t.accent,
            }} />
          )}
          <div style={{ width: "100%", height: 18, borderRadius: 6, background: `${t.accent}20`, marginBottom: 5 }} />
          <div style={{ display: "flex", gap: 2, justifyContent: "center", marginBottom: 4 }}>
            {[...Array(3)].map((_, i) => (
              <div key={i} style={{ width: 10, height: 10, borderRadius: 3, background: `${t.accent}30` }} />
            ))}
          </div>
          <div style={{ fontSize: 7, color: t.accent, fontWeight: 600 }}>{t.label}</div>
        </div>
      ))}
    </div>
    {/* Density selector */}
    <div style={{ background: C.bgCard, borderRadius: 14, padding: "10px 12px", border: `1px solid ${C.border}` }}>
      <div style={{ fontSize: 8, fontWeight: 700, color: C.textMuted, letterSpacing: 1, marginBottom: 7 }}>LAYOUT DENSITY</div>
      <div style={{ display: "flex", gap: 6 }}>
        {["Compact", "Balanced", "Spacious"].map((d, i) => (
          <div key={d} style={{
            flex: 1, textAlign: "center", borderRadius: 9, padding: "7px 4px",
            background: i === 1 ? "rgba(212,175,98,0.12)" : C.card,
            border: `1px solid ${i === 1 ? C.gold + "50" : C.border}`,
          }}>
            <div style={{ fontSize: 7, color: i === 1 ? C.gold : C.textMuted, fontWeight: i === 1 ? 700 : 400 }}>{d}</div>
          </div>
        ))}
      </div>
    </div>
    {/* Icon style row */}
    <div style={{ background: C.bgCard, borderRadius: 14, padding: "9px 12px", border: `1px solid ${C.border}`, display: "flex", alignItems: "center", gap: 10 }}>
      <div style={{ fontSize: 9, color: C.textSec, flex: 1 }}>Icon style, wallpapers, hidden apps & more</div>
      <div style={{ fontSize: 9, color: C.gold, fontWeight: 600 }}>Theme Studio →</div>
    </div>
  </div>
);

const SetupHero = () => (
  <div style={{
    width: "100%",
    background: "linear-gradient(135deg, rgba(212,175,98,0.09), rgba(212,175,98,0.02))",
    borderRadius: 18, padding: 16,
    border: `1px solid ${C.goldBorder}`,
  }}>
    {/* Icon */}
    <div style={{ display: "flex", justifyContent: "center", marginBottom: 14 }}>
      <div style={{ position: "relative", width: 64, height: 64 }}>
        <div style={{
          position: "absolute", inset: 0, borderRadius: 32,
          background: "rgba(212,175,98,0.10)", border: `1px solid rgba(212,175,98,0.2)`,
          boxShadow: "0 0 28px rgba(212,175,98,0.12)",
        }} />
        <div style={{
          position: "absolute", inset: 8, borderRadius: 24,
          background: "rgba(212,175,98,0.08)", border: `1px solid rgba(212,175,98,0.3)`,
          display: "flex", alignItems: "center", justifyContent: "center",
        }}>
          <span style={{ fontSize: 24 }}>🏠</span>
        </div>
      </div>
    </div>
    {/* Reassurance text */}
    <p style={{ fontSize: 11, color: C.textSec, textAlign: "center", margin: "0 0 14px", lineHeight: 1.65 }}>
      Setting Ciyato as your home app changes how your launcher looks and feels.
      <span style={{ color: C.text }}> Nothing is deleted.</span> You can switch back at any time in Android Settings.
    </p>
    {/* 3 trust pills */}
    <div style={{ display: "flex", gap: 6 }}>
      {[
        { icon: "📱", label: "Replaces home screen view" },
        { icon: "🔄", label: "Switch back anytime" },
        { icon: "🔒", label: "No apps removed" },
      ].map(item => (
        <div key={item.label} style={{
          flex: 1, background: C.card, borderRadius: 11, padding: "9px 5px",
          textAlign: "center", border: `1px solid ${C.border}`,
        }}>
          <div style={{ fontSize: 14, marginBottom: 4 }}>{item.icon}</div>
          <div style={{ fontSize: 7.5, color: C.textMuted, lineHeight: 1.4 }}>{item.label}</div>
        </div>
      ))}
    </div>
  </div>
);

// ─── Slide Data ────────────────────────────────────────────────────────────────
const SLIDES = [
  {
    id: "welcome",
    badge: "AI Phone Organizer for Android",
    headline: "Your phone, organized beautifully.",
    sub: "Ciyato brings clarity, structure, and calm to your Android.",
    body: "One intelligent launcher. Smart categories. Faster access to everything that matters.",
    cta: "Get Started",
    hero: "logo",
  },
  {
    id: "library",
    badge: "Smart App Library",
    headline: "Your apps, organized automatically.",
    sub: "Work, Social, Finance, and more — intelligently grouped for faster access.",
    body: "Ciyato categorizes your apps and creates smart shortcuts. Your apps are never removed or altered.",
    cta: "Next",
    hero: "library",
  },
  {
    id: "files",
    badge: "Files · Photos · Search",
    headline: "More than a launcher.",
    sub: "Organize apps, files, photos, and everyday content in one elegant system.",
    body: "AI-powered search across your entire phone. Find anything, instantly.",
    cta: "Next",
    hero: "files",
  },
  {
    id: "privacy",
    badge: "Private by Design",
    headline: "Your data stays yours.",
    sub: "No hidden tracking. No forced cloud upload. No ads-first behavior.",
    body: "Permissions are requested only when needed and only for features you use.",
    cta: "Next",
    hero: "privacy",
  },
  {
    id: "personalize",
    badge: "Make It Yours",
    headline: "Beautiful by default. Flexible by design.",
    sub: "Adjust layout, colors, icon style, wallpapers, and app visibility.",
    body: "Ciyato adapts to how you live — not the other way around.",
    cta: "Next",
    hero: "personalize",
  },
  {
    id: "setup",
    badge: "Final Step",
    headline: "Ready to activate Ciyato?",
    sub: "Set Ciyato as your home app to experience your new organized launcher.",
    body: "You can switch back at any time from Android Settings. Nothing is deleted or uninstalled.",
    cta: "Set Ciyato as Home App",
    ctaSecondary: "Explore first",
    hero: "setup",
  },
];

// ─── Main Onboarding Component ────────────────────────────────────────────────
export const Onboarding = () => {
  const [current, setCurrent] = useState(0);
  const [dir, setDir] = useState<"fwd" | "bwd">("fwd");
  const [key, setKey] = useState(0);
  const [settled, setSettled] = useState(false);

  // Allow quick look at completed state on mount
  useEffect(() => {
    const t = setTimeout(() => setSettled(true), 80);
    return () => clearTimeout(t);
  }, []);

  const go = (next: number, direction: "fwd" | "bwd") => {
    if (next < 0 || next >= SLIDES.length) return;
    setDir(direction);
    setKey(k => k + 1);
    setCurrent(next);
    setSettled(false);
    setTimeout(() => setSettled(true), 80);
  };

  const slide = SLIDES[current];
  const anim = dir === "fwd" ? "ob-slide-right" : "ob-slide-left";

  const renderHero = (): React.ReactNode => {
    switch (slide.hero) {
      case "logo":        return <CiyatoLogoHero />;
      case "library":     return <AppLibraryHero />;
      case "files":       return <FilesSearchHero />;
      case "privacy":     return <PrivacyHero />;
      case "personalize": return <PersonalizationHero />;
      case "setup":       return <SetupHero />;
      default:            return null;
    }
  };

  return (
    <div style={{
      display: "flex", flexDirection: "column", height: "100%",
      background: `radial-gradient(ellipse at 50% -10%, rgba(212,175,98,0.10) 0%, transparent 55%),
                   linear-gradient(175deg, ${C.bg} 0%, #0B1220 55%, #0A0D16 100%)`,
      fontFamily: "Inter, system-ui, sans-serif", color: C.text,
      position: "relative", overflow: "hidden",
    }}>

      {/* Ambient background glow */}
      <div style={{
        position: "absolute", top: -60, left: "50%", transform: "translateX(-50%)",
        width: 320, height: 320,
        background: "radial-gradient(circle, rgba(212,175,98,0.08) 0%, transparent 70%)",
        pointerEvents: "none", zIndex: 0,
        animation: "ob-glow-pulse 4s ease-in-out infinite",
        filter: "blur(20px)",
      }} />
      {/* Bottom glow */}
      <div style={{
        position: "absolute", bottom: -40, left: "50%", transform: "translateX(-50%)",
        width: 260, height: 200,
        background: "radial-gradient(ellipse, rgba(212,175,98,0.05) 0%, transparent 70%)",
        pointerEvents: "none", zIndex: 0, filter: "blur(30px)",
      }} />

      {/* Skip button */}
      {current < SLIDES.length - 1 && (
        <button
          aria-label="Skip onboarding"
          onClick={() => go(SLIDES.length - 1, "fwd")}
          style={{
            position: "absolute", top: 14, right: 14, zIndex: 10,
            background: "none", border: "none",
            color: C.textMuted, fontSize: 12, cursor: "pointer",
            fontFamily: "inherit", padding: "8px 12px",
            letterSpacing: 0.3,
          }}
        >
          Skip
        </button>
      )}

      {/* ── Scrollable Content ── */}
      <div style={{ flex: 1, overflowY: "auto", scrollbarWidth: "none", zIndex: 1 }}>
        <div style={{ padding: "40px 20px 4px", display: "flex", flexDirection: "column", gap: 0 }}>

          {/* Badge */}
          <div
            key={`badge-${key}`}
            style={{
              display: "flex", justifyContent: "center", marginBottom: 18,
              animation: `${anim} 0.42s ${EASE} both`,
            }}
          >
            <div style={{
              display: "inline-flex", alignItems: "center", gap: 6,
              padding: "5px 14px", borderRadius: 50,
              background: "rgba(212,175,98,0.07)",
              border: `1px solid rgba(212,175,98,0.22)`,
              fontSize: 10, fontWeight: 700, color: C.gold, letterSpacing: 0.5,
            }}>
              <span style={{ fontSize: 8, opacity: 0.8 }}>✦</span>
              {slide.badge}
            </div>
          </div>

          {/* Hero visual */}
          <div
            key={`hero-${key}`}
            style={{
              marginBottom: 20,
              animation: `${anim} 0.44s ${EASE} 0.05s both`,
            }}
          >
            {renderHero()}
          </div>

          {/* Text content */}
          <div
            key={`text-${key}`}
            style={{ animation: `ob-fade-up 0.48s ${EASE} 0.1s both` }}
          >
            <h1 style={{
              fontSize: 21, fontWeight: 800, color: C.text,
              margin: "0 0 8px", letterSpacing: -0.5, lineHeight: 1.2,
            }}>
              {slide.headline}
            </h1>
            <p style={{
              fontSize: 13, fontWeight: 600, color: C.gold,
              margin: "0 0 7px", lineHeight: 1.45,
            }}>
              {slide.sub}
            </p>
            <p style={{
              fontSize: 12, color: C.textSec, margin: 0, lineHeight: 1.65,
            }}>
              {slide.body}
            </p>
          </div>

          {/* Extra breathing room for scroll */}
          <div style={{ height: 16 }} />
        </div>
      </div>

      {/* ── Bottom Navigation ── */}
      <div style={{ padding: "10px 20px 24px", zIndex: 1, background: "linear-gradient(to top, rgba(10,15,24,0.98) 60%, transparent)" }}>

        {/* Page indicators */}
        <div role="tablist" aria-label="Onboarding steps" style={{ display: "flex", justifyContent: "center", alignItems: "center", gap: 5, marginBottom: 14 }}>
          {SLIDES.map((s, i) => (
            <button
              key={i}
              role="tab"
              aria-selected={i === current}
              aria-label={`Go to step ${i + 1}: ${s.badge}`}
              onClick={() => go(i, i > current ? "fwd" : "bwd")}
              style={{
                /* larger hit target via padding; visual dot via inner sizing */
                height: 20, borderRadius: 3, cursor: "pointer",
                width: i === current ? 34 : 16,
                background: "transparent",
                border: "none",
                padding: "8px 0",
                position: "relative",
                transition: `width 0.35s ${EASE}`,
              }}
            >
              <span style={{
                display: "block",
                height: 4,
                borderRadius: 3,
                background: i === current
                  ? C.gold
                  : i < current
                    ? "rgba(212,175,98,0.35)"
                    : "rgba(255,255,255,0.14)",
                boxShadow: i === current ? `0 0 8px ${C.gold}55` : "none",
                transition: `background 0.35s ${EASE}, box-shadow 0.35s ${EASE}`,
              }} />
            </button>
          ))}
        </div>

        {/* Primary CTA */}
        <button
          onClick={() => {
            if (current < SLIDES.length - 1) {
              go(current + 1, "fwd");
            }
            // On last screen: in real Android app this opens system default app picker
          }}
          style={{
            width: "100%", height: 50, borderRadius: 25,
            background: `linear-gradient(135deg, ${C.goldHi} 0%, ${C.gold} 60%, #C49A3C 100%)`,
            border: "none", color: "#0A0F18",
            fontSize: 14, fontWeight: 800, cursor: "pointer",
            letterSpacing: 0.3, fontFamily: "inherit",
            boxShadow: `0 4px 22px rgba(212,175,98,0.38), 0 2px 10px rgba(0,0,0,0.5)`,
            transition: `transform 0.12s, box-shadow 0.12s`,
            marginBottom: slide.ctaSecondary ? 8 : 0,
          }}
          onMouseDown={e => {
            e.currentTarget.style.transform = "scale(0.97)";
            e.currentTarget.style.boxShadow = `0 2px 12px rgba(212,175,98,0.28), 0 1px 6px rgba(0,0,0,0.5)`;
          }}
          onMouseUp={e => {
            e.currentTarget.style.transform = "scale(1)";
            e.currentTarget.style.boxShadow = `0 4px 22px rgba(212,175,98,0.38), 0 2px 10px rgba(0,0,0,0.5)`;
          }}
        >
          {slide.cta}
        </button>

        {/* Secondary CTA (last screen only) */}
        {slide.ctaSecondary && (
          <button
            onClick={() => go(0, "bwd")}
            style={{
              width: "100%", height: 44, borderRadius: 22,
              background: "transparent",
              border: `1px solid rgba(255,255,255,0.10)`,
              color: C.textSec, fontSize: 13, fontWeight: 500,
              cursor: "pointer", fontFamily: "inherit",
              transition: "all 0.15s",
            }}
            onMouseEnter={e => { e.currentTarget.style.background = "rgba(255,255,255,0.04)"; }}
            onMouseLeave={e => { e.currentTarget.style.background = "transparent"; }}
          >
            {slide.ctaSecondary}
          </button>
        )}

        {/* Back link (screens 2–5) */}
        {current > 0 && current < SLIDES.length - 1 && (
          <div style={{ display: "flex", justifyContent: "center", marginTop: 6 }}>
            <button
              onClick={() => go(current - 1, "bwd")}
              style={{
                background: "none", border: "none",
                color: C.textMuted, fontSize: 11, cursor: "pointer",
                fontFamily: "inherit", padding: "4px 8px",
              }}
            >
              ← Back
            </button>
          </div>
        )}
      </div>
    </div>
  );
};
