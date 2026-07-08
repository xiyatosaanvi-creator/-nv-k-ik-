import { useState } from "react";
import type { ReactNode, CSSProperties } from "react";

// ─── Design Tokens ─────────────────────────────────────────────────────────────
const C = {
  black:     "#000000",
  surface:   "#0A0A0A",
  card:      "#111111",
  cardHi:    "#161616",
  border:    "rgba(255,255,255,0.06)",
  borderGold:"rgba(201,166,72,0.22)",
  gold:      "#C9A648",
  goldHi:    "#E4C06A",
  goldDim:   "#9A7D34",
  goldGlow:  "rgba(201,166,72,0.14)",
  cream:     "#F0EAD6",
  creamDim:  "#BDB5A2",
  muted:     "#4A4A4A",
  dimBlue:   "#2C4A6E",
  green:     "#2D7A5A",
  white:     "#FFFFFF",
};

const FONT_DISPLAY  = "'Cormorant Garant', 'Georgia', serif";
const FONT_BODY     = "'DM Sans', 'Inter', system-ui, sans-serif";
const EASE          = "cubic-bezier(0.25, 0.46, 0.45, 0.94)";

// ─── Inject keyframes once ─────────────────────────────────────────────────────
if (typeof document !== "undefined" && !document.getElementById("ob2-styles")) {
  const s = document.createElement("style");
  s.id = "ob2-styles";
  s.textContent = `
    @keyframes ob2-fade-up   { from { opacity:0; transform:translateY(18px) } to { opacity:1; transform:translateY(0) } }
    @keyframes ob2-fwd       { from { opacity:0; transform:translateX(30px) } to { opacity:1; transform:translateX(0) } }
    @keyframes ob2-bwd       { from { opacity:0; transform:translateX(-30px)} to { opacity:1; transform:translateX(0) } }
    @keyframes ob2-scale-in  { from { opacity:0; transform:scale(0.88)      } to { opacity:1; transform:scale(1)    } }
    @keyframes ob2-glow      { 0%,100%{opacity:.3} 50%{opacity:.65} }
    @keyframes ob2-rotate    { from{transform:rotate(0deg)} to{transform:rotate(360deg)} }
  `;
  document.head.appendChild(s);
}

// ─── Primitive helpers ─────────────────────────────────────────────────────────
const Svg = ({ size=24, vb="0 0 24 24", children, style={} }: { size?:number; vb?:string; children:ReactNode; style?:CSSProperties }) => (
  <svg width={size} height={size} viewBox={vb} fill="none" style={style}>{children}</svg>
);

const StrokePath = ({ d, w=1.5, cap="round", join="round", color=C.cream }: { d:string; w?:number; cap?:string; join?:string; color?:string }) => (
  <path d={d} stroke={color} strokeWidth={w} strokeLinecap={cap as any} strokeLinejoin={join as any}/>
);

// ─── Handcrafted SVG Icon Library ─────────────────────────────────────────────
const IconWork = ({ size=20, color=C.cream }: { size?:number; color?:string }) => (
  <Svg size={size}><StrokePath color={color} d="M16 7V5a2 2 0 0 0-2-2h-4a2 2 0 0 0-2 2v2"/><rect x="2" y="7" width="20" height="14" rx="2" stroke={color} strokeWidth="1.5"/><line x1="2" y1="12" x2="22" y2="12" stroke={color} strokeWidth="1.5"/></Svg>
);
const IconChat = ({ size=20, color=C.cream }: { size?:number; color?:string }) => (
  <Svg size={size}><path d="M21 15a2 2 0 0 1-2 2H7l-4 4V5a2 2 0 0 1 2-2h14a2 2 0 0 1 2 2z" stroke={color} strokeWidth="1.5" strokeLinecap="round" strokeLinejoin="round"/></Svg>
);
const IconCard = ({ size=20, color=C.cream }: { size?:number; color?:string }) => (
  <Svg size={size}><rect x="1" y="4" width="22" height="16" rx="3" stroke={color} strokeWidth="1.5"/><line x1="1" y1="10" x2="23" y2="10" stroke={color} strokeWidth="1.5"/><line x1="5" y1="15" x2="9" y2="15" stroke={color} strokeWidth="1.5" strokeLinecap="round"/></Svg>
);
const IconCompass = ({ size=20, color=C.cream }: { size?:number; color?:string }) => (
  <Svg size={size}><circle cx="12" cy="12" r="10" stroke={color} strokeWidth="1.5"/><polygon points="16.24 7.76 14.12 14.12 7.76 16.24 9.88 9.88 16.24 7.76" stroke={color} strokeWidth="1.5" strokeLinejoin="round" fill="none"/></Svg>
);
const IconTool = ({ size=20, color=C.cream }: { size?:number; color?:string }) => (
  <Svg size={size}><path d="M14.7 6.3a1 1 0 0 0 0 1.4l1.6 1.6a1 1 0 0 0 1.4 0l3.77-3.77a6 6 0 0 1-7.94 7.94l-6.91 6.91a2.12 2.12 0 0 1-3-3l6.91-6.91a6 6 0 0 1 7.94-7.94l-3.76 3.76z" stroke={color} strokeWidth="1.5" strokeLinecap="round" strokeLinejoin="round"/></Svg>
);
const IconCalendar = ({ size=20, color=C.cream }: { size?:number; color?:string }) => (
  <Svg size={size}><rect x="3" y="4" width="18" height="18" rx="2" stroke={color} strokeWidth="1.5"/><line x1="16" y1="2" x2="16" y2="6" stroke={color} strokeWidth="1.5" strokeLinecap="round"/><line x1="8" y1="2" x2="8" y2="6" stroke={color} strokeWidth="1.5" strokeLinecap="round"/><line x1="3" y1="10" x2="21" y2="10" stroke={color} strokeWidth="1.5"/></Svg>
);
const IconFile = ({ size=16, color=C.creamDim }: { size?:number; color?:string }) => (
  <Svg size={size}><path d="M13 2H6a2 2 0 0 0-2 2v16a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V9z" stroke={color} strokeWidth="1.4" strokeLinecap="round" strokeLinejoin="round"/><polyline points="13 2 13 9 20 9" stroke={color} strokeWidth="1.4" strokeLinecap="round" strokeLinejoin="round"/></Svg>
);
const IconSearch = ({ size=16, color=C.gold }: { size?:number; color?:string }) => (
  <Svg size={size}><circle cx="11" cy="11" r="8" stroke={color} strokeWidth="1.5"/><path d="m21 21-4.35-4.35" stroke={color} strokeWidth="1.5" strokeLinecap="round"/></Svg>
);
const IconShield = ({ size=52, color=C.creamDim, accent=C.gold }: { size?:number; color?:string; accent?:string }) => (
  <Svg size={size} vb="0 0 52 60">
    {/* Outer shield */}
    <path d="M26 2L4 11v17c0 13.5 9.5 25.5 22 28 12.5-2.5 22-14.5 22-28V11L26 2z" stroke={color} strokeWidth="1.5" strokeLinejoin="round" fill="rgba(255,255,255,0.02)"/>
    {/* Inner shield accent */}
    <path d="M26 10L10 17v11c0 9 6.3 17 16 19.5 9.7-2.5 16-10.5 16-19.5V17L26 10z" stroke={accent} strokeWidth="1" strokeLinejoin="round" fill="rgba(201,166,72,0.04)"/>
    {/* Checkmark */}
    <path d="M18 30l5 5 10-10" stroke={accent} strokeWidth="2" strokeLinecap="round" strokeLinejoin="round"/>
  </Svg>
);
const IconHome = ({ size=44, color=C.creamDim, accent=C.gold }: { size?:number; color?:string; accent?:string }) => (
  <Svg size={size} vb="0 0 44 44">
    {/* House outline */}
    <path d="M4 20L22 4l18 16" stroke={color} strokeWidth="1.5" strokeLinecap="round" strokeLinejoin="round"/>
    <path d="M8 17v21h28V17" stroke={color} strokeWidth="1.5" strokeLinecap="round" strokeLinejoin="round"/>
    {/* Door */}
    <path d="M17 38V28h10v10" stroke={accent} strokeWidth="1.4" strokeLinecap="round" strokeLinejoin="round"/>
    {/* Window */}
    <rect x="13" y="20" width="7" height="6" rx="1" stroke={accent} strokeWidth="1.2"/>
    <rect x="24" y="20" width="7" height="6" rx="1" stroke={accent} strokeWidth="1.2"/>
  </Svg>
);
const IconCheck = ({ size=14, color=C.gold }: { size?:number; color?:string }) => (
  <Svg size={size}><polyline points="20 6 9 17 4 12" stroke={color} strokeWidth="2.5" strokeLinecap="round" strokeLinejoin="round"/></Svg>
);
const IconPhone = ({ size=16, color=C.creamDim }: { size?:number; color?:string }) => (
  <Svg size={size}><rect x="5" y="2" width="14" height="20" rx="2" stroke={color} strokeWidth="1.4"/><line x1="12" y1="18" x2="12" y2="18.01" stroke={color} strokeWidth="2" strokeLinecap="round"/></Svg>
);
const IconRefresh = ({ size=16, color=C.creamDim }: { size?:number; color?:string }) => (
  <Svg size={size}><polyline points="1 4 1 10 7 10" stroke={color} strokeWidth="1.5" strokeLinecap="round" strokeLinejoin="round"/><path d="M3.51 15a9 9 0 1 0 .49-3.51" stroke={color} strokeWidth="1.5" strokeLinecap="round"/></Svg>
);
const IconLock = ({ size=16, color=C.creamDim }: { size?:number; color?:string }) => (
  <Svg size={size}><rect x="3" y="11" width="18" height="11" rx="2" stroke={color} strokeWidth="1.5"/><path d="M7 11V7a5 5 0 0 1 10 0v4" stroke={color} strokeWidth="1.5" strokeLinecap="round"/></Svg>
);
const IconPhoto = ({ size=14, color=C.creamDim }: { size?:number; color?:string }) => (
  <Svg size={size}><rect x="3" y="3" width="18" height="18" rx="2" stroke={color} strokeWidth="1.4"/><circle cx="8.5" cy="8.5" r="1.5" stroke={color} strokeWidth="1.2"/><polyline points="21 15 16 10 5 21" stroke={color} strokeWidth="1.4" strokeLinecap="round"/></Svg>
);

// ─── Ciyato Logo Hero ──────────────────────────────────────────────────────────
const CiyatoLogoHero = () => (
  <div style={{ display:"flex", flexDirection:"column", alignItems:"center", gap:20, position:"relative" }}>
    {/* Ambient halo */}
    <div style={{
      position:"absolute", top:-30, width:220, height:220, borderRadius:"50%",
      background:"radial-gradient(circle, rgba(201,166,72,0.12) 0%, transparent 68%)",
      animation:"ob2-glow 5s ease-in-out infinite", filter:"blur(18px)", pointerEvents:"none",
    }}/>

    {/* Main logo SVG — faithful to the uploaded Ciyato brand mark */}
    <svg width="120" height="120" viewBox="0 0 120 120" fill="none" style={{ position:"relative", zIndex:1 }}>
      <defs>
        <radialGradient id="lg-gold" cx="50%" cy="50%" r="50%">
          <stop offset="0%"   stopColor="#F2DFA0"/>
          <stop offset="45%"  stopColor="#C9A648"/>
          <stop offset="100%" stopColor="#7A5E1A"/>
        </radialGradient>
        <radialGradient id="lg-star" cx="50%" cy="40%" r="60%">
          <stop offset="0%"   stopColor="#FFF0B8"/>
          <stop offset="50%"  stopColor="#D4AF62"/>
          <stop offset="100%" stopColor="#8C6A20"/>
        </radialGradient>
        <filter id="lg-glow">
          <feGaussianBlur stdDeviation="2.5" result="blur"/>
          <feComposite in="SourceGraphic" in2="blur" operator="over"/>
        </filter>
      </defs>

      {/* Outermost decorative ring */}
      <circle cx="60" cy="60" r="57" stroke="rgba(201,166,72,0.12)" strokeWidth="0.75" fill="none"/>
      <circle cx="60" cy="60" r="50" stroke="rgba(201,166,72,0.08)" strokeWidth="0.5"  fill="none"/>

      {/* The C letterform — serif, cream */}
      {/* Main arc of the C */}
      <path
        d="M81 24 C70 16, 48 16, 36 30 C24 44, 24 76, 36 90 C48 104, 70 104, 81 96"
        stroke="#F0EAD6" strokeWidth="8.5" strokeLinecap="round" fill="none"
      />
      {/* Upper serif flick */}
      <path d="M81 24 C85 20, 88 17, 87 14" stroke="#F0EAD6" strokeWidth="5.5" strokeLinecap="round" fill="none"/>
      {/* Lower serif flick */}
      <path d="M81 96 C85 100, 88 103, 87 106" stroke="#F0EAD6" strokeWidth="5.5" strokeLinecap="round" fill="none"/>

      {/* Inner golden arc behind the star */}
      <path
        d="M73 32 C63 25, 47 25, 40 38 C33 51, 33 69, 40 82 C47 95, 63 95, 73 88"
        stroke="url(#lg-gold)" strokeWidth="2.5" strokeLinecap="round" fill="none" filter="url(#lg-glow)"
      />

      {/* 4-pointed compass star */}
      <path
        d="M60 37 L63.8 56.2 L83 60 L63.8 63.8 L60 83 L56.2 63.8 L37 60 L56.2 56.2 Z"
        fill="url(#lg-star)" filter="url(#lg-glow)"
      />
      {/* Star center highlight */}
      <circle cx="60" cy="60" r="3.5" fill="rgba(255,240,180,0.7)"/>
    </svg>

    {/* Wordmark */}
    <div style={{
      fontFamily: FONT_DISPLAY, fontSize:32, fontWeight:700, letterSpacing:"0.06em",
      color: C.cream, textTransform:"uppercase", position:"relative", zIndex:1,
    }}>
      Ciyato
    </div>
  </div>
);

// ─── App Library Hero ──────────────────────────────────────────────────────────
const AppLibraryHero = () => {
  const cats = [
    { label:"Work",      count:"12", icon:<IconWork      size={18} color="#7BA7E0"/>, accent:"#3D6FA0" },
    { label:"Social",    count:"9",  icon:<IconChat      size={18} color="#D07AA0"/>, accent:"#904060" },
    { label:"Finance",   count:"6",  icon:<IconCard      size={18} color={C.gold}/>,  accent:"#6A4E1A" },
    { label:"Creative",  count:"8",  icon:<IconCompass   size={18} color="#9A7AE0"/>, accent:"#5A3A90" },
    { label:"Utilities", count:"11", icon:<IconTool      size={18} color="#5ABF8A"/>, accent:"#2A6040" },
    { label:"Daily",     count:"5",  icon:<IconCalendar  size={18} color="#D0845A"/>, accent:"#804030" },
  ];

  return (
    <div style={{ width:"100%", display:"flex", flexDirection:"column", gap:10 }}>
      {/* Category grid */}
      <div style={{
        background: C.card, borderRadius:18, padding:"14px 12px",
        border:`1px solid ${C.border}`,
      }}>
        <div style={{ fontFamily:FONT_BODY, fontSize:9, fontWeight:600, color:C.muted, letterSpacing:"0.1em", marginBottom:10, textTransform:"uppercase" }}>Smart Categories</div>
        <div style={{ display:"grid", gridTemplateColumns:"repeat(3,1fr)", gap:7 }}>
          {cats.map(cat => (
            <div key={cat.label} style={{
              background: `${cat.accent}18`,
              border:`1px solid ${cat.accent}38`,
              borderRadius:12, padding:"10px 6px", textAlign:"center",
              display:"flex", flexDirection:"column", alignItems:"center", gap:5,
            }}>
              {cat.icon}
              <div style={{ fontFamily:FONT_BODY, fontSize:9, fontWeight:600, color:C.creamDim }}>{cat.label}</div>
              <div style={{ fontFamily:FONT_BODY, fontSize:8, color:C.muted }}>{cat.count} apps</div>
            </div>
          ))}
        </div>
      </div>

      {/* Smart shortcuts strip */}
      <div style={{
        background: C.card, borderRadius:14, padding:"10px 14px",
        border:`1px solid ${C.borderGold}`,
        display:"flex", alignItems:"center", gap:10,
      }}>
        <div style={{
          width:20, height:20, borderRadius:10,
          background:"rgba(201,166,72,0.12)", border:`1px solid ${C.borderGold}`,
          display:"flex", alignItems:"center", justifyContent:"center", flexShrink:0,
        }}>
          <svg width="10" height="10" viewBox="0 0 24 24" fill={C.gold}><path d="M12 2L13.5 10.5L22 12L13.5 13.5L12 22L10.5 13.5L2 12L10.5 10.5Z"/></svg>
        </div>
        <div>
          <div style={{ fontFamily:FONT_BODY, fontSize:10, fontWeight:600, color:C.cream, marginBottom:1 }}>Smart Shortcuts</div>
          <div style={{ fontFamily:FONT_BODY, fontSize:9, color:C.muted, lineHeight:1.4 }}>One app. Multiple places. Zero clutter.</div>
        </div>
      </div>
    </div>
  );
};

// ─── Files & Search Hero ───────────────────────────────────────────────────────
const FilesSearchHero = () => {
  const files = [
    { name:"Documents", detail:"24 files", icon:<IconFile size={14} color="#7BA7E0"/> },
    { name:"Downloads", detail:"18 files", icon:<IconFile size={14} color={C.gold}/> },
    { name:"Projects",  detail:"7 files",  icon:<IconFile size={14} color="#9A7AE0"/> },
    { name:"Archive",   detail:"41 files", icon:<IconFile size={14} color={C.muted}/> },
  ];

  return (
    <div style={{ width:"100%", display:"flex", gap:8 }}>
      {/* Left — Files */}
      <div style={{ flex:1.1, background:C.card, borderRadius:16, padding:12, border:`1px solid ${C.border}`, display:"flex", flexDirection:"column", gap:6 }}>
        <div style={{ fontFamily:FONT_BODY, fontSize:9, fontWeight:600, color:C.muted, letterSpacing:"0.1em", textTransform:"uppercase", marginBottom:2 }}>Smart Files</div>
        {files.map(f => (
          <div key={f.name} style={{ display:"flex", alignItems:"center", gap:8 }}>
            <div style={{ width:26, height:26, borderRadius:7, background:C.cardHi, border:`1px solid ${C.border}`, display:"flex", alignItems:"center", justifyContent:"center", flexShrink:0 }}>
              {f.icon}
            </div>
            <div style={{ flex:1 }}>
              <div style={{ fontFamily:FONT_BODY, fontSize:10, fontWeight:500, color:C.creamDim }}>{f.name}</div>
              <div style={{ fontFamily:FONT_BODY, fontSize:8, color:C.muted }}>{f.detail}</div>
            </div>
          </div>
        ))}
      </div>

      {/* Right — Search + Photos */}
      <div style={{ flex:1, display:"flex", flexDirection:"column", gap:8 }}>
        {/* AI Search */}
        <div style={{ background:C.card, borderRadius:14, padding:11, border:`1px solid ${C.border}` }}>
          <div style={{ fontFamily:FONT_BODY, fontSize:9, fontWeight:600, color:C.muted, letterSpacing:"0.1em", textTransform:"uppercase", marginBottom:7 }}>AI Search</div>
          <div style={{
            background:C.cardHi, borderRadius:9, padding:"7px 9px",
            display:"flex", alignItems:"center", gap:7, border:`1px solid ${C.border}`,
            marginBottom:6,
          }}>
            <IconSearch size={13} color={C.gold}/>
            <span style={{ fontFamily:FONT_BODY, fontSize:9.5, color:C.muted, fontStyle:"italic" }}>Find anything…</span>
          </div>
          <div style={{ display:"flex", flexDirection:"column", gap:3 }}>
            {["January invoices", "Zoom screenshots"].map(h => (
              <div key={h} style={{ fontFamily:FONT_BODY, fontSize:8, color:C.muted, padding:"3px 6px", background:C.card, borderRadius:5, border:`1px solid ${C.border}` }}>{h}</div>
            ))}
          </div>
        </div>

        {/* Photo collections */}
        <div style={{ background:C.card, borderRadius:14, padding:11, border:`1px solid ${C.border}`, flex:1 }}>
          <div style={{ fontFamily:FONT_BODY, fontSize:9, fontWeight:600, color:C.muted, letterSpacing:"0.1em", textTransform:"uppercase", marginBottom:7 }}>Collections</div>
          <div style={{ display:"grid", gridTemplateColumns:"1fr 1fr", gap:4 }}>
            {[
              { bg:"#1A2535", label:"Trips" },
              { bg:"#1C2418", label:"Work" },
              { bg:"#251A1A", label:"Family" },
              { bg:"#1E1A28", label:"Art" },
            ].map(p => (
              <div key={p.label} style={{
                background:p.bg, borderRadius:7, aspectRatio:"1",
                display:"flex", alignItems:"flex-end", justifyContent:"flex-start",
                padding:"4px 5px", border:`1px solid rgba(255,255,255,0.05)`,
              }}>
                <span style={{ fontFamily:FONT_BODY, fontSize:7.5, color:"rgba(255,255,255,0.35)", fontWeight:500 }}>{p.label}</span>
              </div>
            ))}
          </div>
        </div>
      </div>
    </div>
  );
};

// ─── Privacy Hero ──────────────────────────────────────────────────────────────
const PrivacyHero = () => (
  <div style={{ width:"100%", background:C.card, borderRadius:18, padding:"18px 16px", border:`1px solid rgba(45,122,90,0.25)` }}>
    <div style={{ display:"flex", justifyContent:"center", marginBottom:16 }}>
      <div style={{ position:"relative" }}>
        <div style={{
          position:"absolute", inset:-10, borderRadius:"50%",
          background:"radial-gradient(circle, rgba(45,122,90,0.15) 0%, transparent 70%)",
          filter:"blur(8px)", animation:"ob2-glow 4s ease-in-out infinite",
        }}/>
        <IconShield size={52} color="rgba(45,122,90,0.6)" accent={C.gold}/>
      </div>
    </div>

    {[
      "Your data stays on your device — always",
      "No silent uploads or background tracking",
      "Permissions requested only when you need them",
      "Ciyato never uninstalls or alters your apps",
      "Switch back to any launcher at any time",
    ].map(pt => (
      <div key={pt} style={{ display:"flex", alignItems:"flex-start", gap:10, marginBottom:10 }}>
        <div style={{
          width:18, height:18, borderRadius:9, flexShrink:0, marginTop:1,
          background:"rgba(45,122,90,0.12)", border:"1px solid rgba(45,122,90,0.4)",
          display:"flex", alignItems:"center", justifyContent:"center",
        }}>
          <IconCheck size={10} color="#4DAA80"/>
        </div>
        <span style={{ fontFamily:FONT_BODY, fontSize:11.5, color:C.creamDim, lineHeight:1.55 }}>{pt}</span>
      </div>
    ))}
  </div>
);

// ─── Personalization Hero ──────────────────────────────────────────────────────
const PersonalizationHero = () => {
  const themes = [
    { name:"Midnight",  bg:"#000000", bar:C.gold,    ring:true  },
    { name:"Obsidian",  bg:"#080C14", bar:"#5B9CF6", ring:false },
    { name:"Emerald",   bg:"#060E0B", bar:"#2DBF80", ring:false },
    { name:"Amethyst",  bg:"#0A0612", bar:"#9A7AE0", ring:false },
  ];

  return (
    <div style={{ width:"100%", display:"flex", flexDirection:"column", gap:9 }}>
      {/* Theme palette row */}
      <div style={{ display:"flex", gap:7 }}>
        {themes.map(t => (
          <div key={t.name} style={{
            flex:1, background:t.bg, borderRadius:14, padding:"10px 6px",
            border:`1px solid ${t.ring ? C.borderGold : C.border}`,
            display:"flex", flexDirection:"column", gap:5, alignItems:"center",
            position:"relative", overflow:"hidden",
          }}>
            {t.ring && (
              <div style={{
                position:"absolute", top:4, right:4,
                width:6, height:6, borderRadius:3,
                background:C.gold, boxShadow:`0 0 5px ${C.gold}`,
              }}/>
            )}
            {/* Mini launcher mock */}
            <div style={{ width:"100%", height:20, borderRadius:6, background:`${t.bar}20`, border:`1px solid ${t.bar}40` }}/>
            <div style={{ display:"flex", gap:3, justifyContent:"center" }}>
              {[...Array(3)].map((_,i) => (
                <div key={i} style={{ width:10, height:10, borderRadius:3, background:`${t.bar}25`, border:`1px solid ${t.bar}40` }}/>
              ))}
            </div>
            {/* Accent line */}
            <div style={{ width:"60%", height:2, borderRadius:1, background:`${t.bar}60` }}/>
            <div style={{ fontFamily:FONT_BODY, fontSize:7.5, color:t.bar, fontWeight:600, letterSpacing:"0.05em" }}>{t.name}</div>
          </div>
        ))}
      </div>

      {/* Density selector */}
      <div style={{ background:C.card, borderRadius:14, padding:"11px 13px", border:`1px solid ${C.border}` }}>
        <div style={{ fontFamily:FONT_BODY, fontSize:9, fontWeight:600, color:C.muted, letterSpacing:"0.1em", textTransform:"uppercase", marginBottom:7 }}>Layout Density</div>
        <div style={{ display:"flex", gap:6 }}>
          {["Compact","Balanced","Spacious"].map((d, i) => (
            <div key={d} style={{
              flex:1, borderRadius:9, padding:"7px 4px", textAlign:"center",
              background: i===1 ? "rgba(201,166,72,0.10)" : "transparent",
              border:`1px solid ${i===1 ? C.borderGold : C.border}`,
            }}>
              <div style={{ fontFamily:FONT_BODY, fontSize:8.5, color: i===1 ? C.gold : C.muted, fontWeight: i===1 ? 600 : 400 }}>{d}</div>
            </div>
          ))}
        </div>
      </div>

      {/* Icon style */}
      <div style={{ background:C.card, borderRadius:14, padding:"10px 13px", border:`1px solid ${C.border}`, display:"flex", alignItems:"center", justifyContent:"space-between" }}>
        <div style={{ fontFamily:FONT_BODY, fontSize:10, color:C.creamDim }}>Icon style · Wallpapers · Hidden apps</div>
        <div style={{ fontFamily:FONT_BODY, fontSize:9.5, color:C.gold, fontWeight:600 }}>Theme Studio →</div>
      </div>
    </div>
  );
};

// ─── Setup Hero ────────────────────────────────────────────────────────────────
const SetupHero = () => (
  <div style={{
    width:"100%",
    background:"linear-gradient(160deg, rgba(201,166,72,0.07) 0%, rgba(201,166,72,0.02) 100%)",
    borderRadius:18, padding:"18px 16px",
    border:`1px solid ${C.borderGold}`,
  }}>
    {/* Home icon */}
    <div style={{ display:"flex", justifyContent:"center", marginBottom:16, position:"relative" }}>
      <div style={{
        position:"absolute", inset:-8, borderRadius:"50%",
        background:"radial-gradient(circle, rgba(201,166,72,0.12) 0%, transparent 70%)",
        filter:"blur(12px)", animation:"ob2-glow 4s ease-in-out infinite",
      }}/>
      <div style={{
        width:70, height:70, borderRadius:35, position:"relative",
        background:"rgba(201,166,72,0.06)", border:`1px solid ${C.borderGold}`,
        display:"flex", alignItems:"center", justifyContent:"center",
      }}>
        <IconHome size={40} color="rgba(240,234,214,0.45)" accent={C.gold}/>
      </div>
    </div>

    {/* Reassurance text */}
    <p style={{
      fontFamily:FONT_BODY, fontSize:11.5, color:C.creamDim,
      textAlign:"center", margin:"0 0 14px", lineHeight:1.7,
    }}>
      Setting Ciyato as your home app changes how your launcher looks and feels.{" "}
      <span style={{ color:C.cream, fontWeight:500 }}>Nothing is deleted.</span>{" "}
      You can switch back at any time from Android Settings.
    </p>

    {/* Three trust columns */}
    <div style={{ display:"flex", gap:7 }}>
      {[
        { icon:<IconPhone size={15} color={C.creamDim}/>,   label:"Replaces home screen view" },
        { icon:<IconRefresh size={15} color={C.creamDim}/>, label:"Switch back anytime" },
        { icon:<IconLock size={15} color={C.creamDim}/>,    label:"No apps removed" },
      ].map(item => (
        <div key={item.label} style={{
          flex:1, background:C.card, borderRadius:12, padding:"10px 6px",
          textAlign:"center", border:`1px solid ${C.border}`,
          display:"flex", flexDirection:"column", alignItems:"center", gap:6,
        }}>
          <div style={{ width:28, height:28, borderRadius:14, background:C.cardHi, border:`1px solid ${C.border}`, display:"flex", alignItems:"center", justifyContent:"center" }}>
            {item.icon}
          </div>
          <div style={{ fontFamily:FONT_BODY, fontSize:8, color:C.muted, lineHeight:1.45 }}>{item.label}</div>
        </div>
      ))}
    </div>
  </div>
);

// ─── Slide Data ────────────────────────────────────────────────────────────────
const SLIDES = [
  {
    badge:        "AI Phone Organizer for Android",
    headline:     "Your phone,\norganized beautifully.",
    sub:          "Ciyato brings clarity, structure, and calm to your Android.",
    body:         "One intelligent launcher. Smart categories. Faster access to everything that matters.",
    cta:          "Get Started",
    hero:         "logo",
  },
  {
    badge:        "Smart App Library",
    headline:     "Your apps,\norganized automatically.",
    sub:          "Work, Social, Finance, and more — intelligently grouped.",
    body:         "Ciyato categorizes your apps and creates smart shortcuts. Your apps are never removed or altered.",
    cta:          "Continue",
    hero:         "library",
  },
  {
    badge:        "Files · Photos · Search",
    headline:     "More than\na launcher.",
    sub:          "Organize apps, files, photos, and content in one elegant system.",
    body:         "AI-powered search across your entire phone. Find anything, instantly.",
    cta:          "Continue",
    hero:         "files",
  },
  {
    badge:        "Private by Design",
    headline:     "Your data\nstays yours.",
    sub:          "No hidden tracking. No forced upload. No ads-first behavior.",
    body:         "Permissions are requested only when needed and only for features you use.",
    cta:          "Continue",
    hero:         "privacy",
  },
  {
    badge:        "Make It Yours",
    headline:     "Beautiful by default.\nFlexible by design.",
    sub:          "Adjust layout, colors, icon style, wallpapers, and app visibility.",
    body:         "Ciyato adapts to how you live — not the other way around.",
    cta:          "Continue",
    hero:         "personalize",
  },
  {
    badge:        "Final Step",
    headline:     "Ready to\nactivate Ciyato?",
    sub:          "Set Ciyato as your home app to experience your new organized launcher.",
    body:         "You can switch back at any time from Android Settings. Nothing is deleted or uninstalled.",
    cta:          "Set Ciyato as Home App",
    ctaSecondary: "Explore first",
    hero:         "setup",
  },
] as const;

// ─── Main Onboarding Component ─────────────────────────────────────────────────
export const Onboarding = () => {
  const [current, setCurrent]   = useState(0);
  const [dir,     setDir]       = useState<"fwd"|"bwd">("fwd");
  const [animKey, setAnimKey]   = useState(0);

  const go = (next: number, direction: "fwd"|"bwd") => {
    if (next < 0 || next >= SLIDES.length) return;
    setDir(direction);
    setAnimKey(k => k + 1);
    setCurrent(next);
  };

  const slide   = SLIDES[current];
  const anim    = dir === "fwd" ? "ob2-fwd" : "ob2-bwd";
  const isLast  = current === SLIDES.length - 1;

  const renderHero = (): ReactNode => {
    switch (slide.hero) {
      case "logo":        return <CiyatoLogoHero/>;
      case "library":     return <AppLibraryHero/>;
      case "files":       return <FilesSearchHero/>;
      case "privacy":     return <PrivacyHero/>;
      case "personalize": return <PersonalizationHero/>;
      case "setup":       return <SetupHero/>;
      default:            return null;
    }
  };

  return (
    <div style={{
      display:"flex", flexDirection:"column", height:"100%",
      background: C.black,
      fontFamily: FONT_BODY,
      color: C.cream,
      position:"relative",
      overflow:"hidden",
    }}>
      {/* Very subtle gold radial hint — barely visible, luxurious depth */}
      <div style={{
        position:"absolute", top:-40, left:"50%", transform:"translateX(-50%)",
        width:260, height:260, borderRadius:"50%",
        background:"radial-gradient(circle, rgba(201,166,72,0.07) 0%, transparent 65%)",
        filter:"blur(24px)", pointerEvents:"none", zIndex:0,
        animation:"ob2-glow 6s ease-in-out infinite",
      }}/>

      {/* Skip */}
      {!isLast && (
        <button
          aria-label="Skip onboarding"
          onClick={() => go(SLIDES.length - 1, "fwd")}
          style={{
            position:"absolute", top:14, right:16, zIndex:20,
            background:"none", border:"none",
            fontFamily:FONT_BODY, fontSize:12, color:C.muted,
            cursor:"pointer", padding:"8px 12px", letterSpacing:"0.04em",
          }}
        >
          Skip
        </button>
      )}

      {/* ── Scrollable content ── */}
      <div style={{ flex:1, overflowY:"auto", scrollbarWidth:"none", zIndex:1 }}>
        <div style={{ padding:"44px 22px 8px", display:"flex", flexDirection:"column", gap:0 }}>

          {/* Badge */}
          <div key={`badge-${animKey}`} style={{
            display:"flex", justifyContent:"center", marginBottom:20,
            animation:`${anim} 0.4s ${EASE} both`,
          }}>
            <div style={{
              display:"inline-flex", alignItems:"center", gap:7,
              padding:"5px 14px", borderRadius:50,
              background:"rgba(201,166,72,0.07)",
              border:`1px solid rgba(201,166,72,0.2)`,
              fontFamily:FONT_BODY, fontSize:10, fontWeight:600,
              color:C.gold, letterSpacing:"0.06em", textTransform:"uppercase",
            }}>
              <svg width="7" height="7" viewBox="0 0 24 24" fill={C.gold}><path d="M12 2L13.5 10.5L22 12L13.5 13.5L12 22L10.5 13.5L2 12L10.5 10.5Z"/></svg>
              {slide.badge}
            </div>
          </div>

          {/* Hero */}
          <div key={`hero-${animKey}`} style={{
            marginBottom:22,
            animation:`${anim} 0.44s ${EASE} 0.05s both`,
          }}>
            {renderHero()}
          </div>

          {/* Headline — Cormorant Garant display size */}
          <div key={`text-${animKey}`} style={{ animation:`ob2-fade-up 0.46s ${EASE} 0.1s both` }}>
            <h1 style={{
              fontFamily: FONT_DISPLAY,
              fontSize: 30,
              fontWeight: 700,
              fontStyle: "italic",
              color: C.cream,
              margin: "0 0 10px",
              letterSpacing: "0.01em",
              lineHeight: 1.18,
              whiteSpace: "pre-line",
            }}>
              {slide.headline}
            </h1>
            <p style={{
              fontFamily: FONT_BODY,
              fontSize: 13.5,
              fontWeight: 500,
              color: C.gold,
              margin: "0 0 8px",
              lineHeight: 1.5,
              letterSpacing: "0.01em",
            }}>
              {slide.sub}
            </p>
            <p style={{
              fontFamily: FONT_BODY,
              fontSize: 12.5,
              color: C.muted,
              margin: 0,
              lineHeight: 1.7,
              fontWeight: 300,
            }}>
              {slide.body}
            </p>
          </div>

          <div style={{ height:14 }}/>
        </div>
      </div>

      {/* ── Bottom navigation ── */}
      <div style={{
        padding:"12px 22px 26px", zIndex:1,
        background:"linear-gradient(to top, rgba(0,0,0,1) 70%, rgba(0,0,0,0))",
      }}>
        {/* Page indicators */}
        <div
          role="tablist"
          aria-label="Onboarding steps"
          style={{ display:"flex", justifyContent:"center", alignItems:"center", gap:5, marginBottom:16 }}
        >
          {SLIDES.map((s, i) => (
            <button
              key={i}
              role="tab"
              aria-selected={i === current}
              aria-label={`Step ${i+1}: ${s.badge}`}
              onClick={() => go(i, i > current ? "fwd" : "bwd")}
              style={{
                background:"transparent", border:"none", cursor:"pointer",
                padding:"8px 0", position:"relative",
                width: i === current ? 28 : 14, height:20,
                transition:`width 0.4s ${EASE}`,
              }}
            >
              <span style={{
                display:"block", height:3, borderRadius:2,
                background: i === current
                  ? C.gold
                  : i < current
                    ? "rgba(201,166,72,0.3)"
                    : "rgba(255,255,255,0.12)",
                boxShadow: i === current ? `0 0 10px ${C.gold}60` : "none",
                transition:`background 0.4s ${EASE}, box-shadow 0.4s ${EASE}`,
              }}/>
            </button>
          ))}
        </div>

        {/* Primary CTA */}
        <button
          aria-label={slide.cta}
          onClick={() => { if (!isLast) go(current + 1, "fwd"); }}
          style={{
            width:"100%", height:52, borderRadius:26,
            background:`linear-gradient(135deg, ${C.goldHi} 0%, ${C.gold} 55%, ${C.goldDim} 100%)`,
            border:"none", color:"#0A0700",
            fontFamily: FONT_BODY,
            fontSize:14.5, fontWeight:700,
            cursor:"pointer", letterSpacing:"0.03em",
            boxShadow:`0 4px 28px rgba(201,166,72,0.40), 0 1px 0 rgba(255,240,160,0.3) inset`,
            transition:`transform 0.12s, box-shadow 0.12s`,
            marginBottom: ("ctaSecondary" in slide) ? 9 : 0,
          }}
          onMouseDown={e => {
            e.currentTarget.style.transform = "scale(0.97)";
            e.currentTarget.style.boxShadow = `0 2px 14px rgba(201,166,72,0.28)`;
          }}
          onMouseUp={e => {
            e.currentTarget.style.transform = "scale(1)";
            e.currentTarget.style.boxShadow = `0 4px 28px rgba(201,166,72,0.40), 0 1px 0 rgba(255,240,160,0.3) inset`;
          }}
        >
          {slide.cta}
        </button>

        {/* Secondary CTA — last screen */}
        {"ctaSecondary" in slide && (
          <button
            aria-label={slide.ctaSecondary}
            onClick={() => go(0, "bwd")}
            style={{
              width:"100%", height:46, borderRadius:23,
              background:"transparent", border:`1px solid rgba(255,255,255,0.08)`,
              color: C.muted, fontFamily:FONT_BODY, fontSize:13.5,
              fontWeight:400, cursor:"pointer", letterSpacing:"0.02em",
              transition:"all 0.15s",
            }}
            onMouseEnter={e => { e.currentTarget.style.borderColor = "rgba(255,255,255,0.14)"; e.currentTarget.style.color = C.creamDim; }}
            onMouseLeave={e => { e.currentTarget.style.borderColor = "rgba(255,255,255,0.08)"; e.currentTarget.style.color = C.muted; }}
          >
            {slide.ctaSecondary}
          </button>
        )}

        {/* Back — screens 2–5 */}
        {current > 0 && !isLast && (
          <div style={{ display:"flex", justifyContent:"center", marginTop:7 }}>
            <button
              aria-label="Go back"
              onClick={() => go(current - 1, "bwd")}
              style={{
                background:"none", border:"none", color:C.muted,
                fontFamily:FONT_BODY, fontSize:11.5, cursor:"pointer",
                padding:"6px 10px", letterSpacing:"0.04em",
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
