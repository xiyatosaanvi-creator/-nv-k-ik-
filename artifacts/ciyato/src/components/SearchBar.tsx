import { T } from "../tokens";
import { Icon } from "./Icon";

export const SearchBar = ({ placeholder = "Search apps, files, contacts…", light = false, extra }: { placeholder?: string, light?: boolean, extra?: React.ReactNode }) => (
  <div style={{ display: "flex", alignItems: "center", background: light ? "rgba(0,0,0,0.07)" : "rgba(255,255,255,0.08)", borderRadius: 50, padding: "11px 16px", gap: 10, border: `1px solid ${light ? "rgba(0,0,0,0.08)" : T.border}` }}>
    <Icon name="search" size={17} color={light ? T.lightSec : T.muted} />
    <span style={{ flex: 1, fontSize: 14, color: light ? T.lightSec : T.muted }}>{placeholder}</span>
    {extra}
  </div>
);
