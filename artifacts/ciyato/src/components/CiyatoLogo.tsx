import { T } from "../tokens";

export const CiyatoLogo = ({ size = 28, showStar = true, light = false }: { size?: number, showStar?: boolean, light?: boolean }) => (
  <div style={{ display: "flex", alignItems: "center", gap: 8 }}>
    <div style={{ width: size + 4, height: size + 4, borderRadius: 10, background: light ? T.lightText : "#1a1f24", display: "flex", alignItems: "center", justifyContent: "center", border: `1px solid ${light ? "rgba(0,0,0,0.15)" : T.border}` }}>
      <span style={{ fontSize: size * 0.65, fontWeight: 700, color: T.white, fontFamily: "Inter, system-ui, sans-serif", letterSpacing: -1 }}>C</span>
      {showStar && <span style={{ fontSize: size * 0.32, color: T.gold, marginLeft: -2, marginBottom: size * 0.1 }}>✦</span>}
    </div>
  </div>
);
