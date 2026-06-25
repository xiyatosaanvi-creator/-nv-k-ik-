import { T } from "../tokens";
import { Icon } from "./Icon";

export const BottomNav = ({ active = "home", items, light = false }: { active?: string, items?: { id: string, label: string, icon: string }[], light?: boolean }) => {
  const defaultItems = [
    { id: "home", label: "Home", icon: "home" },
    { id: "files", label: "Files", icon: "folder" },
    { id: "search", label: "Search", icon: "search" },
    { id: "shared", label: "Shared", icon: "shared" },
    { id: "tools", label: "Tools", icon: "tools" },
  ];
  const nav = items || defaultItems;
  return (
    <div style={{ display: "flex", justifyContent: "space-around", padding: "10px 8px 16px", background: light ? "rgba(245,242,236,0.95)" : "rgba(18,23,27,0.95)", borderTop: `1px solid ${light ? T.lightBorder : T.borderSub}`, backdropFilter: "blur(20px)", gap: 4 }}>
      {nav.map(item => (
        <div key={item.id} style={{ display: "flex", flexDirection: "column", alignItems: "center", gap: 3, padding: "4px 12px", borderRadius: 12, flex: 1, cursor: "pointer" }}>
          <div style={{ position: "relative" }}>
            <Icon name={item.icon} size={22} color={active === item.id ? T.gold : (light ? T.lightSec : T.muted)} />
            {active === item.id && <div style={{ position: "absolute", bottom: -6, left: "50%", transform: "translateX(-50%)", width: 16, height: 2, background: T.gold, borderRadius: 2 }} />}
          </div>
          <span style={{ fontSize: 10, color: active === item.id ? T.gold : (light ? T.lightSec : T.muted), fontWeight: active === item.id ? 600 : 400 }}>{item.label}</span>
        </div>
      ))}
    </div>
  );
};
