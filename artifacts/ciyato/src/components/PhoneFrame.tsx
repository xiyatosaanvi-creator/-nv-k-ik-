import { T } from "../tokens";

export const PhoneFrame = ({ children, light = false }: { children: React.ReactNode, light?: boolean }) => (
  <div style={{ width: "100%", maxWidth: 390, height: "100%", maxHeight: 840, background: light ? T.lightBg : T.bg, borderRadius: 44, overflow: "hidden", display: "flex", flexDirection: "column", boxShadow: "0 40px 100px rgba(0,0,0,0.5), 0 0 0 1px rgba(255,255,255,0.08), inset 0 0 0 1px rgba(255,255,255,0.04)", position: "relative", border: "8px solid #1a1f24" }}>
    {children}
  </div>
);
