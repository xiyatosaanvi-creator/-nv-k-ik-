import { useState, useId } from "react";
import type { ReactNode } from "react";

// ─── Palette: pure black + white only ─────────────────────────────────────────
const C = {
  bg:        "#000000",
  surface:   "#0D0D0D",
  card:      "rgba(255,255,255,0.03)",
  cardHi:    "rgba(255,255,255,0.06)",
  border:    "rgba(255,255,255,0.07)",
  borderMid: "rgba(255,255,255,0.12)",
  white:     "#FFFFFF",
  cream:     "#F2EDE3",        // warm headline white
  text:      "#D8D0C4",        // body — warm off-white
  sub:       "#707070",        // subdued label
  dim:       "#2E2E2E",        // very dark dividers
  // logo-only gold (brand mark, not UI)
  lGold:     "#C8A44A",
  lGoldHi:   "#F0D880",
  lGoldDim:  "#7A5C18",
  lCream:    "#F2EAD4",
};

const FD = "'Cormorant Garant', Georgia, serif";   // display
const FB = "'DM Sans', Inter, system-ui, sans-serif"; // body
const EA = "cubic-bezier(0.25, 0.46, 0.45, 0.94)";

// ─── Keyframes ─────────────────────────────────────────────────────────────────
if (typeof document !== "undefined" && !document.getElementById("ob3-css")) {
  const s = document.createElement("style");
  s.id = "ob3-css";
  s.textContent = `
    @keyframes ob3-up  { from{opacity:0;transform:translateY(20px)} to{opacity:1;transform:translateY(0)} }
    @keyframes ob3-fwd { from{opacity:0;transform:translateX(26px)} to{opacity:1;transform:translateX(0)} }
    @keyframes ob3-bwd { from{opacity:0;transform:translateX(-26px)}to{opacity:1;transform:translateX(0)} }
    @keyframes ob3-glo { 0%,100%{opacity:.22} 50%{opacity:.50} }
    @keyframes ob3-sin { from{transform:scale(0.92);opacity:0} to{transform:scale(1);opacity:1} }
  `;
  document.head.appendChild(s);
}

// ─── Ciyato Brand Mark — faithful recreation of the uploaded logo ──────────────
//
//  Anatomy (from the image):
//  1. Outer C — thick cream/ivory arc, ±34° opening on the right, butt-capped
//     main arc + tapered spur arms with butt ends (sharp wedge, not rounded).
//  2. Inner golden crescent — filled annular path (not a stroked line) so both
//     ends taper naturally to points, matching the reference brand image.
//  3. 4-pointed sparkle star — strongly concave curved sides via cubic Bézier
//     with BOTH control points at centre; radial gradient cream-gold → amber.
//
//  All SVG definition IDs are scoped with useId() to prevent collisions when
//  the component is mounted more than once in the same document.
//
const CiyatoMark = ({ size = 120 }: { size?: number }) => {
  const uid = useId().replace(/:/g, "_");
  const cx = 100, cy = 100;

  // ── Outer C ──────────────────────────────────────────────────────────────────
  const R   = 85;
  const sw  = 22;
  const ang = 34 * Math.PI / 180;

  const tx  = cx + R * Math.cos(ang);
  const ty1 = cy - R * Math.sin(ang);
  const ty2 = cy + R * Math.sin(ang);

  // Spur: radial extension from each terminal, then inward hook.
  // butt strokeLinecap = sharp wedge end (no rounded blobs).
  const sL = 22, hL = 11;
  const sX  = tx  + sL * Math.cos(ang);
  const sY1 = ty1 - sL * Math.sin(ang);
  const sY2 = ty2 + sL * Math.sin(ang);
  const hX1 = sX  - hL * Math.sin(ang);
  const hY1 = sY1 - hL * Math.cos(ang);
  const hX2 = sX  - hL * Math.sin(ang);
  const hY2 = sY2 + hL * Math.cos(ang);

  // ── Inner golden crescent (filled annular path — tapers to sharp points) ─────
  // Outer and inner arcs share the same angular range; the endpoints of each arc
  // pair land at the same angular position so the shape closes to a sharp point.
  const Ro = 63.5, Ri = 59.5;
  const ca = Math.cos(ang), sa = Math.sin(ang);
  const oSx = cx + Ro * ca, oSy = cy - Ro * sa;
  const oEx = cx + Ro * ca, oEy = cy + Ro * sa;
  const iSx = cx + Ri * ca, iSy = cy - Ri * sa;
  const iEx = cx + Ri * ca, iEy = cy + Ri * sa;
  // Outer arc goes long way CCW (left-side route); inner arc reverses (CW).
  const crescentD = [
    `M ${oSx} ${oSy}`,
    `A ${Ro} ${Ro} 0 1 0 ${oEx} ${oEy}`,
    `L ${iEx} ${iEy}`,
    `A ${Ri} ${Ri} 0 1 1 ${iSx} ${iSy}`,
    `Z`,
  ].join(" ");

  // ── 4-pointed sparkle star ───────────────────────────────────────────────────
  const OR = 48;
  const starD = [
    `M ${cx}      ${cy - OR}`,
    `C ${cx} ${cy} ${cx} ${cy} ${cx + OR} ${cy}`,
    `C ${cx} ${cy} ${cx} ${cy} ${cx}      ${cy + OR}`,
    `C ${cx} ${cy} ${cx} ${cy} ${cx - OR} ${cy}`,
    `C ${cx} ${cy} ${cx} ${cy} ${cx}      ${cy - OR}`,
    "Z",
  ].join(" ");

  const gStar = `${uid}st`;
  const gArc  = `${uid}ar`;
  const fGlow = `${uid}gl`;

  return (
    <svg width={size} height={size} viewBox="0 0 200 200" fill="none" style={{ display:"block" }}>
      <defs>
        <radialGradient id={gStar} cx="50%" cy="50%" r="50%">
          <stop offset="0%"   stopColor="#FFFAE0" />
          <stop offset="18%"  stopColor="#F2D060" />
          <stop offset="55%"  stopColor="#C8A030" />
          <stop offset="100%" stopColor="#6A3E08" />
        </radialGradient>
        <linearGradient id={gArc} x1="0%" y1="0%" x2="100%" y2="100%">
          <stop offset="0%"   stopColor="#D4A830" stopOpacity="0.95"/>
          <stop offset="45%"  stopColor="#B88C18" stopOpacity="0.72"/>
          <stop offset="100%" stopColor="#7A5010" stopOpacity="0.28"/>
        </linearGradient>
        <filter id={fGlow} x="-50%" y="-50%" width="200%" height="200%">
          <feGaussianBlur stdDeviation="5" result="blur"/>
          <feComposite in="SourceGraphic" in2="blur" operator="over"/>
        </filter>
      </defs>

      {/* ── 1. Outer C ── */}
      <path
        d={`M ${tx} ${ty1} A ${R} ${R} 0 1 0 ${tx} ${ty2}`}
        stroke="#F2E8D0" strokeWidth={sw} strokeLinecap="butt" fill="none"
      />
      <line x1={tx}  y1={ty1} x2={sX}  y2={sY1}
            stroke="#F2E8D0" strokeWidth={sw * 0.78} strokeLinecap="butt"/>
      <line x1={sX}  y1={sY1} x2={hX1} y2={hY1}
            stroke="#F2E8D0" strokeWidth={sw * 0.36} strokeLinecap="butt"/>
      <line x1={tx}  y1={ty2} x2={sX}  y2={sY2}
            stroke="#F2E8D0" strokeWidth={sw * 0.78} strokeLinecap="butt"/>
      <line x1={sX}  y1={sY2} x2={hX2} y2={hY2}
            stroke="#F2E8D0" strokeWidth={sw * 0.36} strokeLinecap="butt"/>

      {/* ── 2. Inner golden crescent ── */}
      <path d={crescentD} fill={`url(#${gArc})`} />

      {/* ── 3. Sparkle star ── */}
      <path d={starD} fill="#C8A030" opacity="0.25"
            filter={`url(#${fGlow})`}
            transform={`scale(1.45) translate(${cx*(1-1/1.45)} ${cy*(1-1/1.45)})`}/>
      <path d={starD} fill={`url(#${gStar})`} />
      <circle cx={cx} cy={cy} r="3.5" fill="rgba(255,252,220,0.94)" />
    </svg>
  );
};

// ─── Category icon definitions (all SVG, no emoji) ────────────────────────────
const CatIcon = ({ type, size=20, col }: { type:string; size?:number; col:string }) => {
  const s = { stroke:col, strokeWidth:"1.4", strokeLinecap:"round" as const, strokeLinejoin:"round" as const };
  switch (type) {
    case "work":    return <svg width={size} height={size} viewBox="0 0 24 24" fill="none"><rect x="2" y="7" width="20" height="14" rx="2" {...s}/><path d="M16 7V5a2 2 0 00-2-2h-4a2 2 0 00-2 2v2" {...s}/><line x1="2" y1="12" x2="22" y2="12" {...s}/></svg>;
    case "social":  return <svg width={size} height={size} viewBox="0 0 24 24" fill="none"><path d="M21 15a2 2 0 01-2 2H7l-4 4V5a2 2 0 012-2h14a2 2 0 012 2z" {...s}/></svg>;
    case "finance": return <svg width={size} height={size} viewBox="0 0 24 24" fill="none"><rect x="1" y="4" width="22" height="16" rx="3" {...s}/><line x1="1" y1="10" x2="23" y2="10" {...s}/><line x1="5" y1="15" x2="8" y2="15" {...{...s, strokeWidth:"2"}}/></svg>;
    case "creative":return <svg width={size} height={size} viewBox="0 0 24 24" fill="none"><circle cx="12" cy="12" r="10" {...s}/><polygon points="16.24 7.76 14.12 14.12 7.76 16.24 9.88 9.88 16.24 7.76" {...s}/></svg>;
    case "utility": return <svg width={size} height={size} viewBox="0 0 24 24" fill="none"><path d="M14.7 6.3a1 1 0 000 1.4l1.6 1.6a1 1 0 001.4 0l3.77-3.77a6 6 0 01-7.94 7.94l-6.91 6.91a2.12 2.12 0 01-3-3l6.91-6.91a6 6 0 017.94-7.94l-3.76 3.76z" {...s}/></svg>;
    case "daily":   return <svg width={size} height={size} viewBox="0 0 24 24" fill="none"><rect x="3" y="4" width="18" height="18" rx="2" {...s}/><line x1="16" y1="2" x2="16" y2="6" {...s}/><line x1="8" y1="2" x2="8" y2="6" {...s}/><line x1="3" y1="10" x2="21" y2="10" {...s}/><line x1="8" y1="14" x2="10" y2="14" {...{...s,strokeWidth:"2"}}/></svg>;
    default: return null;
  }
};

// ─── Screen 2 — App Library ────────────────────────────────────────────────────
const HeroLibrary = () => {
  const rows = [
    { type:"work",     label:"Work",      count:12 },
    { type:"social",   label:"Social",    count: 9 },
    { type:"finance",  label:"Finance",   count: 6 },
    { type:"creative", label:"Creative",  count: 8 },
    { type:"utility",  label:"Utilities", count:11 },
    { type:"daily",    label:"Daily",     count: 5 },
  ];
  return (
    <div style={{ width:"100%", borderRadius:20, overflow:"hidden", border:`1px solid ${C.border}` }}>
      {rows.map((r, i) => (
        <div key={r.type} style={{
          display:"flex", alignItems:"center", gap:14,
          padding:"12px 16px",
          background: i % 2 === 0 ? C.card : "transparent",
          borderBottom: i < rows.length-1 ? `1px solid ${C.dim}` : "none",
        }}>
          <div style={{
            width:34, height:34, borderRadius:10,
            background: C.cardHi, border:`1px solid ${C.border}`,
            display:"flex", alignItems:"center", justifyContent:"center", flexShrink:0,
          }}>
            <CatIcon type={r.type} size={16} col={C.sub}/>
          </div>
          <div style={{ flex:1 }}>
            <div style={{ fontFamily:FB, fontSize:12, fontWeight:500, color:C.cream }}>{r.label}</div>
          </div>
          <div style={{ fontFamily:FB, fontSize:11, color:C.sub, fontWeight:300 }}>{r.count} apps</div>
          <svg width="12" height="12" viewBox="0 0 24 24" fill="none">
            <polyline points="9 18 15 12 9 6" stroke={C.dim} strokeWidth="2" strokeLinecap="round" strokeLinejoin="round"/>
          </svg>
        </div>
      ))}
    </div>
  );
};

// ─── Screen 3 — Files & Search ─────────────────────────────────────────────────
const HeroFiles = () => (
  <div style={{ width:"100%", display:"flex", flexDirection:"column", gap:8 }}>
    {/* AI Search */}
    <div style={{
      background:C.surface, borderRadius:16, padding:"12px 14px",
      border:`1px solid ${C.borderMid}`, display:"flex", alignItems:"center", gap:10,
    }}>
      <svg width="15" height="15" viewBox="0 0 24 24" fill="none">
        <circle cx="11" cy="11" r="8" stroke={C.cream} strokeWidth="1.5"/>
        <path d="m21 21-4.35-4.35" stroke={C.cream} strokeWidth="1.5" strokeLinecap="round"/>
      </svg>
      <span style={{ fontFamily:FB, fontSize:12, color:C.sub, letterSpacing:"0.01em" }}>Search apps, files, photos…</span>
      <div style={{ marginLeft:"auto", display:"flex", alignItems:"center", gap:4 }}>
        <svg width="11" height="11" viewBox="0 0 24 24" fill={C.cream} opacity="0.8">
          <path d="M12 2L13.5 10.5L22 12L13.5 13.5L12 22L10.5 13.5L2 12L10.5 10.5Z"/>
        </svg>
        <span style={{ fontFamily:FB, fontSize:9.5, fontWeight:500, color:C.text, letterSpacing:"0.05em" }}>AI</span>
      </div>
    </div>

    {/* File groups */}
    <div style={{ display:"grid", gridTemplateColumns:"1fr 1fr", gap:8 }}>
      {[
        { label:"Documents", count:"24 files",  icon:"doc" },
        { label:"Downloads", count:"18 files",  icon:"dl"  },
        { label:"Projects",  count:"7 folders", icon:"fld" },
        { label:"Photos",    count:"312 items", icon:"img" },
      ].map(({ label, count, icon }) => (
        <div key={label} style={{
          background:C.surface, borderRadius:14, padding:"12px",
          border:`1px solid ${C.border}`,
          display:"flex", flexDirection:"column", gap:6,
        }}>
          <div style={{
            width:28, height:28, borderRadius:8, background:C.card,
            border:`1px solid ${C.border}`, display:"flex", alignItems:"center", justifyContent:"center",
          }}>
            {icon === "doc" && <svg width="13" height="13" viewBox="0 0 24 24" fill="none"><path d="M13 2H6a2 2 0 00-2 2v16a2 2 0 002 2h12a2 2 0 002-2V9z" stroke={C.sub} strokeWidth="1.5" strokeLinejoin="round"/><polyline points="13 2 13 9 20 9" stroke={C.sub} strokeWidth="1.5" strokeLinejoin="round"/></svg>}
            {icon === "dl"  && <svg width="13" height="13" viewBox="0 0 24 24" fill="none"><path d="M21 15v4a2 2 0 01-2 2H5a2 2 0 01-2-2v-4" stroke={C.sub} strokeWidth="1.5" strokeLinecap="round"/><polyline points="7 10 12 15 17 10" stroke={C.sub} strokeWidth="1.5" strokeLinecap="round" strokeLinejoin="round"/><line x1="12" y1="15" x2="12" y2="3" stroke={C.sub} strokeWidth="1.5" strokeLinecap="round"/></svg>}
            {icon === "fld" && <svg width="13" height="13" viewBox="0 0 24 24" fill="none"><path d="M22 19a2 2 0 01-2 2H4a2 2 0 01-2-2V5a2 2 0 012-2h5l2 3h9a2 2 0 012 2z" stroke={C.sub} strokeWidth="1.5" strokeLinejoin="round"/></svg>}
            {icon === "img" && <svg width="13" height="13" viewBox="0 0 24 24" fill="none"><rect x="3" y="3" width="18" height="18" rx="2" stroke={C.sub} strokeWidth="1.5"/><circle cx="8.5" cy="8.5" r="1.5" stroke={C.sub} strokeWidth="1.2"/><polyline points="21 15 16 10 5 21" stroke={C.sub} strokeWidth="1.5" strokeLinecap="round"/></svg>}
          </div>
          <div>
            <div style={{ fontFamily:FB, fontSize:11, fontWeight:500, color:C.cream, marginBottom:2 }}>{label}</div>
            <div style={{ fontFamily:FB, fontSize:9.5, color:C.sub, fontWeight:300 }}>{count}</div>
          </div>
        </div>
      ))}
    </div>
  </div>
);

// ─── Screen 4 — Privacy ────────────────────────────────────────────────────────
const HeroPrivacy = () => (
  <div style={{ width:"100%", display:"flex", flexDirection:"column", gap:0 }}>
    {/* Large shield */}
    <div style={{ display:"flex", justifyContent:"center", marginBottom:20 }}>
      <svg width="60" height="68" viewBox="0 0 60 68" fill="none">
        <path d="M30 2L4 12v20c0 16 11.4 30 26 34 14.6-4 26-18 26-34V12L30 2z"
          stroke={C.borderMid} strokeWidth="1.5" strokeLinejoin="round" fill="rgba(255,255,255,0.02)"/>
        <path d="M30 10L11 18v14c0 11 7.7 20.5 19 23.5 11.3-3 19-12.5 19-23.5V18L30 10z"
          stroke="rgba(255,255,255,0.10)" strokeWidth="1" strokeLinejoin="round"/>
        <path d="M20 34l7 7 14-14"
          stroke={C.cream} strokeWidth="2" strokeLinecap="round" strokeLinejoin="round"/>
      </svg>
    </div>

    {/* Trust list — clean, generous spacing */}
    <div style={{ display:"flex", flexDirection:"column", gap:0 }}>
      {[
        "Your data stays on your device",
        "No silent uploads or background tracking",
        "Permissions requested only when you need them",
        "Ciyato never uninstalls or alters your apps",
        "Switch back to any launcher at any time",
      ].map((pt, i) => (
        <div key={pt} style={{
          display:"flex", alignItems:"center", gap:14,
          padding:"11px 0",
          borderBottom: i < 4 ? `1px solid ${C.dim}` : "none",
        }}>
          <svg width="14" height="14" viewBox="0 0 24 24" fill="none" style={{ flexShrink:0 }}>
            <polyline points="20 6 9 17 4 12" stroke={C.cream} strokeWidth="2.5"
              strokeLinecap="round" strokeLinejoin="round"/>
          </svg>
          <span style={{ fontFamily:FB, fontSize:12, color:C.text, fontWeight:300, lineHeight:1.5 }}>{pt}</span>
        </div>
      ))}
    </div>
  </div>
);

// ─── Screen 5 — Personalization ────────────────────────────────────────────────
const HeroPersonalize = () => (
  <div style={{ width:"100%", display:"flex", flexDirection:"column", gap:10 }}>
    {/* Theme swatches — four large clean cards */}
    <div style={{ display:"flex", gap:7 }}>
      {[
        { name:"Midnight", dot:"#FFFFFF", active:true  },
        { name:"Ocean",    dot:"#6B9EE8", active:false },
        { name:"Forest",   dot:"#52B27A", active:false },
        { name:"Dusk",     dot:"#9B7AE0", active:false },
      ].map(t => (
        <div key={t.name} style={{
          flex:1, background:"#000", borderRadius:14,
          border:`1px solid ${t.active ? C.borderMid : C.border}`,
          padding:"12px 7px",
          display:"flex", flexDirection:"column", alignItems:"center", gap:7,
          position:"relative",
        }}>
          {t.active && (
            <div style={{
              position:"absolute", top:6, right:6, width:5, height:5,
              borderRadius:"50%", background:C.white,
            }}/>
          )}
          {/* Mini launcher preview */}
          <div style={{ width:"100%", display:"flex", flexDirection:"column", gap:3 }}>
            <div style={{ height:6, borderRadius:3, background:`${t.dot}25`, border:`1px solid ${t.dot}40` }}/>
            <div style={{ display:"flex", gap:2 }}>
              {[1,2,3].map(n => (
                <div key={n} style={{ flex:1, height:6, borderRadius:2, background:`${t.dot}14` }}/>
              ))}
            </div>
            <div style={{ height:3, width:"55%", borderRadius:2, background:`${t.dot}50` }}/>
          </div>
          <div style={{ fontFamily:FB, fontSize:7.5, color: t.active ? C.cream : C.sub,
            fontWeight: t.active ? 500 : 400, letterSpacing:"0.04em" }}>
            {t.name}
          </div>
        </div>
      ))}
    </div>

    {/* Density + controls */}
    <div style={{ background:C.surface, borderRadius:16, padding:"12px 14px", border:`1px solid ${C.border}` }}>
      <div style={{ fontFamily:FB, fontSize:9, fontWeight:600, color:C.sub, letterSpacing:"0.1em",
        textTransform:"uppercase", marginBottom:9 }}>
        Layout Density
      </div>
      <div style={{ display:"flex", gap:6 }}>
        {["Compact","Balanced","Spacious"].map((d,i) => (
          <div key={d} style={{
            flex:1, textAlign:"center", borderRadius:9, padding:"8px 4px",
            background: i===1 ? "rgba(255,255,255,0.07)" : "transparent",
            border:`1px solid ${i===1 ? C.borderMid : C.border}`,
          }}>
            <div style={{ fontFamily:FB, fontSize:9, color: i===1 ? C.cream : C.sub,
              fontWeight: i===1 ? 500 : 300 }}>{d}</div>
          </div>
        ))}
      </div>
    </div>

    <div style={{
      background:C.surface, borderRadius:16, padding:"11px 14px",
      border:`1px solid ${C.border}`,
      display:"flex", alignItems:"center", justifyContent:"space-between",
    }}>
      <span style={{ fontFamily:FB, fontSize:11, color:C.sub, fontWeight:300 }}>
        Icon style · Wallpapers · Hidden apps
      </span>
      <span style={{ fontFamily:FB, fontSize:10, color:C.cream, fontWeight:500 }}>Theme Studio →</span>
    </div>
  </div>
);

// ─── Screen 6 — Setup ─────────────────────────────────────────────────────────
const HeroSetup = () => (
  <div style={{ width:"100%", display:"flex", flexDirection:"column", gap:10 }}>
    {/* Home icon, centered */}
    <div style={{ display:"flex", justifyContent:"center", paddingBottom:4 }}>
      <div style={{
        width:72, height:72, borderRadius:36,
        background:C.surface, border:`1px solid ${C.borderMid}`,
        display:"flex", alignItems:"center", justifyContent:"center",
      }}>
        <svg width="36" height="36" viewBox="0 0 44 44" fill="none">
          <path d="M4 20L22 4l18 16" stroke={C.cream} strokeWidth="1.5" strokeLinecap="round" strokeLinejoin="round"/>
          <path d="M8 17v21h28V17" stroke={C.cream} strokeWidth="1.5" strokeLinecap="round" strokeLinejoin="round"/>
          <path d="M17 38V28h10v10" stroke={C.sub} strokeWidth="1.4" strokeLinecap="round" strokeLinejoin="round"/>
          <rect x="13" y="20" width="7" height="6" rx="1" stroke={C.sub} strokeWidth="1.2"/>
          <rect x="24" y="20" width="7" height="6" rx="1" stroke={C.sub} strokeWidth="1.2"/>
        </svg>
      </div>
    </div>

    {/* Explanation */}
    <p style={{
      fontFamily:FB, fontSize:12.5, color:C.sub, fontWeight:300,
      textAlign:"center", margin:0, lineHeight:1.75,
    }}>
      Setting Ciyato as your home app changes how your launcher looks and is organized.{" "}
      <span style={{ color:C.cream, fontWeight:400 }}>Nothing is deleted.</span>{" "}
      You can return to any previous launcher from Android Settings at any time.
    </p>

    {/* Trust row */}
    <div style={{ display:"flex", gap:7 }}>
      {[
        { label:"Replaces home screen",  icon: <svg width="14" height="14" viewBox="0 0 24 24" fill="none"><rect x="5" y="2" width="14" height="20" rx="2" stroke={C.sub} strokeWidth="1.4"/><line x1="12" y1="18" x2="12" y2="18.01" stroke={C.sub} strokeWidth="2" strokeLinecap="round"/></svg> },
        { label:"Switch back anytime",   icon: <svg width="14" height="14" viewBox="0 0 24 24" fill="none"><polyline points="1 4 1 10 7 10" stroke={C.sub} strokeWidth="1.5" strokeLinecap="round" strokeLinejoin="round"/><path d="M3.51 15a9 9 0 102.13-9.36L1 10" stroke={C.sub} strokeWidth="1.5" strokeLinecap="round"/></svg> },
        { label:"No apps removed",       icon: <svg width="14" height="14" viewBox="0 0 24 24" fill="none"><rect x="3" y="11" width="18" height="11" rx="2" stroke={C.sub} strokeWidth="1.4"/><path d="M7 11V7a5 5 0 0110 0v4" stroke={C.sub} strokeWidth="1.4" strokeLinecap="round"/></svg> },
      ].map(item => (
        <div key={item.label} style={{
          flex:1, background:C.surface, borderRadius:12, padding:"11px 7px",
          border:`1px solid ${C.border}`,
          display:"flex", flexDirection:"column", alignItems:"center", gap:7,
          textAlign:"center",
        }}>
          <div style={{ width:30, height:30, borderRadius:15, background:C.card, border:`1px solid ${C.border}`,
            display:"flex", alignItems:"center", justifyContent:"center" }}>
            {item.icon}
          </div>
          <div style={{ fontFamily:FB, fontSize:8.5, color:C.sub, fontWeight:300, lineHeight:1.45 }}>{item.label}</div>
        </div>
      ))}
    </div>
  </div>
);

// ─── Slide definitions ─────────────────────────────────────────────────────────
const SLIDES = [
  {
    badge:    "AI PHONE ORGANIZER",
    headline: "Your phone,\norganized\nbeautifully.",
    sub:      "Clarity, structure, and calm — brought to your Android.",
    body:     "One intelligent launcher. Smart categories. Faster access to everything that matters.",
    cta:      "Get Started",
    hero:     "logo",
  },
  {
    badge:    "SMART APP LIBRARY",
    headline: "Your apps,\norganized\nautomatically.",
    sub:      "Work, Social, Finance — intelligently grouped from day one.",
    body:     "Ciyato creates smart categories and shortcuts. Your apps are never removed or altered.",
    cta:      "Continue",
    hero:     "library",
  },
  {
    badge:    "FILES · PHOTOS · SEARCH",
    headline: "More than\na launcher.",
    sub:      "Organize apps, files, and photos in one elegant system.",
    body:     "AI-powered search across your entire phone. Find anything, instantly.",
    cta:      "Continue",
    hero:     "files",
  },
  {
    badge:    "PRIVATE BY DESIGN",
    headline: "Your data\nstays yours.",
    sub:      "No hidden tracking. No forced upload. No ads-first behavior.",
    body:     "Permissions are requested only when needed, only for features you use.",
    cta:      "Continue",
    hero:     "privacy",
  },
  {
    badge:    "MAKE IT YOURS",
    headline: "Beautiful\nby default.",
    sub:      "Adjust layout, colors, icon style, wallpapers, and app visibility.",
    body:     "Ciyato adapts to how you live — not the other way around.",
    cta:      "Continue",
    hero:     "personalize",
  },
  {
    badge:        "FINAL STEP",
    headline:     "Ready to\nactivate\nCiyato?",
    sub:          "Set Ciyato as your home app to experience your organized launcher.",
    body:         "You can switch back at any time from Android Settings. Nothing is deleted.",
    cta:          "Set Ciyato as Home App",
    ctaSecondary: "Explore first",
    hero:         "setup",
  },
] as const;

// ─── Main component ────────────────────────────────────────────────────────────
export const Onboarding = () => {
  const [cur,  setCur]  = useState(0);
  const [dir,  setDir]  = useState<"fwd"|"bwd">("fwd");
  const [akey, setAkey] = useState(0);

  const go = (n: number, d: "fwd"|"bwd") => {
    if (n < 0 || n >= SLIDES.length) return;
    setDir(d); setAkey(k => k+1); setCur(n);
  };

  const slide  = SLIDES[cur];
  const isLast = cur === SLIDES.length - 1;
  const anim   = dir === "fwd" ? "ob3-fwd" : "ob3-bwd";

  const hero = (): ReactNode => {
    switch (slide.hero) {
      case "logo":        return (
        <div style={{ display:"flex", flexDirection:"column", alignItems:"center", gap:16, position:"relative" }}>
          {/* Very subtle ambient halo — almost invisible */}
          <div style={{
            position:"absolute", width:200, height:200,
            background:"radial-gradient(circle, rgba(200,164,74,0.09) 0%, transparent 65%)",
            filter:"blur(24px)", animation:"ob3-glo 6s ease-in-out infinite",
            pointerEvents:"none", top:-40,
          }}/>
          <CiyatoMark size={116}/>
          <div style={{
            fontFamily:FD, fontSize:30, fontWeight:700,
            letterSpacing:"0.14em", color:C.cream,
            textTransform:"uppercase", position:"relative",
          }}>
            Ciyato
          </div>
        </div>
      );
      case "library":     return <HeroLibrary/>;
      case "files":       return <HeroFiles/>;
      case "privacy":     return <HeroPrivacy/>;
      case "personalize": return <HeroPersonalize/>;
      case "setup":       return <HeroSetup/>;
      default:            return null;
    }
  };

  return (
    <div style={{
      display:"flex", flexDirection:"column", height:"100%",
      background:C.bg, fontFamily:FB, color:C.cream,
      position:"relative", overflow:"hidden",
    }}>
      {/* Skip */}
      {!isLast && (
        <button
          aria-label="Skip onboarding"
          onClick={() => go(SLIDES.length-1, "fwd")}
          style={{
            position:"absolute", top:14, right:16, zIndex:20,
            background:"none", border:"none", color:C.sub,
            fontFamily:FB, fontSize:12, cursor:"pointer",
            padding:"8px 10px", letterSpacing:"0.05em",
          }}
        >
          Skip
        </button>
      )}

      {/* Scrollable area */}
      <div style={{ flex:1, overflowY:"auto", scrollbarWidth:"none", zIndex:1 }}>
        <div style={{ padding:"44px 22px 12px", display:"flex", flexDirection:"column" }}>

          {/* Badge */}
          <div key={`b-${akey}`} style={{ marginBottom:18, animation:`${anim} 0.38s ${EA} both` }}>
            <span style={{
              fontFamily:FB, fontSize:9, fontWeight:600,
              color:C.sub, letterSpacing:"0.14em",
            }}>
              {slide.badge}
            </span>
          </div>

          {/* Hero visual */}
          <div key={`h-${akey}`} style={{ marginBottom:22, animation:`${anim} 0.42s ${EA} 0.04s both` }}>
            {hero()}
          </div>

          {/* Headline — Cormorant Garant, large italic */}
          <div key={`t-${akey}`} style={{ animation:`ob3-up 0.44s ${EA} 0.09s both` }}>
            <h1 style={{
              fontFamily:FD, fontStyle:"italic", fontWeight:700,
              fontSize:34, color:C.cream,
              margin:"0 0 12px", lineHeight:1.14,
              letterSpacing:"-0.01em", whiteSpace:"pre-line",
            }}>
              {slide.headline}
            </h1>
            <p style={{
              fontFamily:FB, fontSize:13, fontWeight:400,
              color:C.text, margin:"0 0 8px", lineHeight:1.55,
            }}>
              {slide.sub}
            </p>
            <p style={{
              fontFamily:FB, fontSize:11.5, fontWeight:300,
              color:C.sub, margin:0, lineHeight:1.7,
            }}>
              {slide.body}
            </p>
          </div>
          <div style={{ height:12 }}/>
        </div>
      </div>

      {/* Bottom controls */}
      <div style={{
        padding:"10px 22px 26px", zIndex:1,
        background:"linear-gradient(to top, #000 72%, transparent)",
      }}>
        {/* Indicators */}
        <div role="tablist" aria-label="Onboarding progress"
          style={{ display:"flex", justifyContent:"center", gap:6, marginBottom:16 }}>
          {SLIDES.map((s, i) => (
            <button
              key={i}
              role="tab"
              aria-selected={i===cur}
              aria-label={`Step ${i+1}: ${s.badge}`}
              onClick={() => go(i, i>cur ? "fwd" : "bwd")}
              style={{
                background:"transparent", border:"none", cursor:"pointer",
                padding:"7px 0", height:18,
                width: i===cur ? 24 : 12,
                transition:`width 0.38s ${EA}`,
              }}
            >
              <span style={{
                display:"block", height:"2px", borderRadius:1,
                background: i===cur ? C.white : i<cur ? "rgba(255,255,255,0.3)" : "rgba(255,255,255,0.13)",
                transition:`background 0.38s ${EA}`,
              }}/>
            </button>
          ))}
        </div>

        {/* Primary CTA — white, premium */}
        <button
          aria-label={slide.cta}
          onClick={() => { if (!isLast) go(cur+1, "fwd"); }}
          style={{
            width:"100%", height:52, borderRadius:26,
            background:C.white, border:"none",
            color:"#000000",
            fontFamily:FB, fontSize:14, fontWeight:600,
            cursor:"pointer", letterSpacing:"0.02em",
            boxShadow:"0 0 0 1px rgba(255,255,255,0.12), 0 8px 32px rgba(255,255,255,0.10)",
            transition:"transform 0.12s, opacity 0.12s",
            marginBottom: "ctaSecondary" in slide ? 9 : 0,
          }}
          onMouseDown={e => { e.currentTarget.style.transform="scale(0.97)"; e.currentTarget.style.opacity="0.92"; }}
          onMouseUp={e   => { e.currentTarget.style.transform="scale(1)";    e.currentTarget.style.opacity="1";    }}
        >
          {slide.cta}
        </button>

        {/* Secondary — last screen */}
        {"ctaSecondary" in slide && (
          <button
            aria-label={slide.ctaSecondary}
            onClick={() => go(0, "bwd")}
            style={{
              width:"100%", height:46, borderRadius:23,
              background:"transparent",
              border:`1px solid rgba(255,255,255,0.10)`,
              color:C.sub, fontFamily:FB, fontSize:13, fontWeight:300,
              cursor:"pointer", letterSpacing:"0.02em",
              transition:"border-color 0.15s, color 0.15s",
            }}
            onMouseEnter={e => { e.currentTarget.style.borderColor="rgba(255,255,255,0.2)"; e.currentTarget.style.color=C.text; }}
            onMouseLeave={e => { e.currentTarget.style.borderColor="rgba(255,255,255,0.10)"; e.currentTarget.style.color=C.sub; }}
          >
            {slide.ctaSecondary}
          </button>
        )}

        {/* Back link */}
        {cur>0 && !isLast && (
          <div style={{ display:"flex", justifyContent:"center", marginTop:8 }}>
            <button
              aria-label="Go back"
              onClick={() => go(cur-1, "bwd")}
              style={{
                background:"none", border:"none", color:C.sub,
                fontFamily:FB, fontSize:11, cursor:"pointer",
                padding:"6px 10px", letterSpacing:"0.05em",
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
