import { T } from "../tokens";

export const FilterChip = ({ label, active, light, onClick }: { label: string, active?: boolean, light?: boolean, onClick?: () => void }) => (
  <div onClick={onClick} style={{ padding: "7px 16px", borderRadius: 50, background: active ? T.gold : (light ? "rgba(0,0,0,0.07)" : T.card), border: `1px solid ${active ? T.gold : (light ? T.lightBorder : T.border)}`, fontSize: 13, fontWeight: active ? 600 : 400, color: active ? "#000" : (light ? T.lightText : T.sec), cursor: "pointer", whiteSpace: "nowrap", flexShrink: 0 }}>
    {label}
  </div>
);
