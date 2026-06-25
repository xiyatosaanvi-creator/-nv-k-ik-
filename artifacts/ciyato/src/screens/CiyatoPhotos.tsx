import { useState } from "react";
import { T } from "../tokens";
import { StatusBar } from "../components/StatusBar";
import { Icon } from "../components/Icon";
import { SearchBar } from "../components/SearchBar";
import { FilterChip } from "../components/FilterChip";
import { BottomNav } from "../components/BottomNav";
import { mockPhotos } from "../data/mockPhotos";

export const CiyatoPhotos = () => {
  const [activeChip, setActiveChip] = useState("Personal");
  const chips = ["Personal", "Shared", "Activity", "Work", "Recent"];

  return (
    <div style={{ display: "flex", flexDirection: "column", height: "100%", background: T.bg, fontFamily: "Inter, system-ui, sans-serif", color: T.white }}>
      <StatusBar />
      <div style={{ flex: 1, overflowY: "auto", scrollbarWidth: "none" }}>
        <div style={{ padding: "8px 18px 0" }}>
          <div style={{ display: "flex", justifyContent: "space-between", alignItems: "flex-start", marginBottom: 14 }}>
            <div>
              <div style={{ fontSize: 22, fontWeight: 700 }}>Ciyato Photos</div>
              <div style={{ fontSize: 12, color: T.gold, display: "flex", alignItems: "center", gap: 4 }}>AI-sorted moments and media <Icon name="star4" size={11} color={T.gold} /></div>
            </div>
            <div style={{ display: "flex", gap: 8, alignItems: "center" }}>
              <div style={{ width: 34, height: 34, borderRadius: 17, background: "linear-gradient(135deg,#C6A15B,#8B6B2A)", display: "flex", alignItems: "center", justifyContent: "center", fontSize: 14 }}>👤</div>
              <Icon name="moreV" size={18} color={T.sec} />
            </div>
          </div>
          <SearchBar placeholder="Search photos, albums, places…" />
          <div style={{ display: "flex", gap: 8, marginTop: 12, overflowX: "auto", scrollbarWidth: "none" }}>
            {chips.map(c => <FilterChip key={c} label={c} active={activeChip === c} onClick={() => setActiveChip(c)} />)}
          </div>
        </div>
        
        {/* Photo grid */}
        <div style={{ display: "grid", gridTemplateColumns: "1fr 1fr", gap: 8, padding: "14px 18px 0" }}>
          {mockPhotos.collections.map((col, i) => (
            <div key={col.name} style={{ borderRadius: 18, overflow: "hidden", position: "relative", height: 170, background: `linear-gradient(135deg, ${col.colors[0]}, ${col.colors[1]})`, cursor: "pointer" }}>
              <div style={{ position: "absolute", inset: 0, display: "flex", alignItems: "center", justifyContent: "center", fontSize: 56, opacity: 0.7 }}>{col.img}</div>
              <div style={{ position: "absolute", inset: 0, background: "linear-gradient(to bottom, transparent 40%, rgba(0,0,0,0.75) 100%)" }} />
              <div style={{ position: "absolute", bottom: 0, left: 0, right: 0, padding: "10px 12px" }}>
                <div style={{ fontSize: 13, fontWeight: 700, color: "#fff" }}>{col.name}</div>
                <div style={{ fontSize: 10, color: "rgba(255,255,255,0.7)" }}>{col.count}</div>
              </div>
              {col.icon && (
                <div style={{ position: "absolute", bottom: 10, right: 10, width: 22, height: 22, borderRadius: 11, background: "rgba(255,255,255,0.15)", display: "flex", alignItems: "center", justifyContent: "center" }}>
                  <Icon name={col.icon} size={11} color="#fff" />
                </div>
              )}
            </div>
          ))}
        </div>
        
        <div style={{ padding: "16px 18px 24px", display: "flex", justifyContent: "center" }}>
          <div style={{ padding: "10px 24px", borderRadius: 50, background: T.bgEl, border: `1px solid ${T.gold}`, display: "flex", alignItems: "center", gap: 8, fontSize: 13, fontWeight: 600, color: T.gold }}>
            <Icon name="star4" size={14} color={T.gold} />
            AI ORGANIZED
          </div>
        </div>
      </div>
      
      {/* Floating search button */}
      <div style={{ position: "absolute", bottom: 80, right: 20, width: 56, height: 56, borderRadius: 28, background: T.gold, display: "flex", alignItems: "center", justifyContent: "center", boxShadow: "0 4px 20px rgba(198,161,91,0.4)", cursor: "pointer" }}>
        <Icon name="search" size={24} color="#000" />
      </div>

      <BottomNav active="collections" items={[{ id: "photos", label: "Photos", icon: "photo" }, { id: "collections", label: "Collections", icon: "folder" }, { id: "search", label: "Search", icon: "search" }, { id: "map", label: "Map", icon: "map" }]} />
    </div>
  );
};
