import { T } from "../tokens";

export const StatusBar = ({ light = false }: { light?: boolean }) => (
  <div style={{ display: "flex", justifyContent: "space-between", alignItems: "center", padding: "12px 20px 4px", fontSize: 12, fontWeight: 600, color: light ? T.lightText : T.white, letterSpacing: 0.2 }}>
    <span>9:30</span>
    <div style={{ width: 12, height: 12, borderRadius: "50%", background: "#1a1a1a", border: "2px solid #333", position: "absolute", left: "50%", transform: "translateX(-50%)", top: 8 }} />
    <div style={{ display: "flex", alignItems: "center", gap: 4 }}>
      <svg width={16} height={12} viewBox="0 0 16 12" fill={light ? T.lightText : T.white}><path d="M8 2C4.5 2 1.5 3.5 0 6c1.5 2.5 4.5 4 8 4s6.5-1.5 8-4c-1.5-2.5-4.5-4-8-4zm0 6.5c-1.4 0-2.5-1.1-2.5-2.5S6.6 3.5 8 3.5 10.5 4.6 10.5 6 9.4 8.5 8 8.5z"/></svg>
      <svg width={14} height={12} viewBox="0 0 14 12" fill={light ? T.lightText : T.white}><rect x="0" y="6" width="2" height="6" rx="1"/><rect x="3" y="4" width="2" height="8" rx="1"/><rect x="6" y="2" width="2" height="10" rx="1"/><rect x="9" y="0" width="2" height="12" rx="1"/></svg>
      <svg width={24} height={12} viewBox="0 0 24 12" fill={light ? T.lightText : T.white}><rect x="0" y="1" width="20" height="10" rx="2" stroke={light ? T.lightText : T.white} strokeWidth="1.5" fill="none"/><rect x="20.5" y="3.5" width="2" height="5" rx="1" fill={light ? T.lightText : T.white}/><rect x="1.5" y="2.5" width="17" height="7" rx="1"/></svg>
    </div>
  </div>
);
