import { T } from "../tokens";

export const GlassCard = ({ children, style = {}, onClick }: { children: React.ReactNode, style?: React.CSSProperties, onClick?: React.MouseEventHandler<HTMLDivElement> }) => (
  <div onClick={onClick} style={{ background: T.cardStr, border: `1px solid ${T.border}`, borderRadius: 20, padding: 18, backdropFilter: "blur(12px)", cursor: onClick ? "pointer" : "default", transition: "background 0.2s", ...style }}
    onMouseEnter={e => { if (onClick) e.currentTarget.style.background = T.cardMed; }}
    onMouseLeave={e => { if (onClick) e.currentTarget.style.background = T.cardStr; }}>
    {children}
  </div>
);
