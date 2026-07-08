import { useState } from "react";
import { T } from "../tokens";
import { StatusBar } from "../components/StatusBar";
import { Icon } from "../components/Icon";
import { SearchBar } from "../components/SearchBar";
import { FilterChip } from "../components/FilterChip";
import { BottomNav } from "../components/BottomNav";
import { mockPhotos } from "../data/mockPhotos";

const FD = "'Cormorant Garamond', Georgia, serif";

export const CiyatoPhotos = () => {
  const [activeChip, setActiveChip] = useState("Personal");
  const [granted, setGranted] = useState(false);
  const [open, setOpen] = useState<(typeof mockPhotos.collections)[number] | null>(null);
  const chips = ["Personal", "Shared", "Activity", "Work", "Recent"];

  if (!granted) {
    return (
      <div style={{ display: "flex", flexDirection: "column", height: "100%", background: "radial-gradient(ellipse at top, #14100A 0%, #000000 60%)", fontFamily: "'DM Sans', Inter, sans-serif", color: T.white }}>
        <StatusBar />
        <div style={{ flex: 1, display: "flex", flexDirection: "column", alignItems: "center", justifyContent: "center", padding: "0 32px", textAlign: "center" }}>
          <div style={{ width: 72, height: 72, borderRadius: 36, background: "rgba(198,161,91,0.1)", border: `1px solid ${T.gold}55`, display: "flex", alignItems: "center", justifyContent: "center", marginBottom: 24 }}>
            <Icon name="photo" size={30} color={T.goldSoft} />
          </div>
          <div style={{ fontFamily: FD, fontStyle: "italic", fontSize: 26, marginBottom: 10 }}>Ciyato Photos</div>
          <div style={{ fontSize: 13, color: T.sec, lineHeight: 1.6, marginBottom: 28 }}>
            To sort your moments into beautiful collections, Ciyato needs permission to view your photo library.
          </div>
          <div onClick={() => setGranted(true)} style={{ padding: "13px 32px", borderRadius: 50, background: T.gold, color: "#1a1204", fontSize: 13, fontWeight: 700, cursor: "pointer" }}>
            Allow photo access
          </div>
        </div>
      </div>
    );
  }

  if (open) {
    return (
      <div style={{ display: "flex", flexDirection: "column", height: "100%", background: "radial-gradient(ellipse at top, #14100A 0%, #000000 60%)", fontFamily: "'DM Sans', Inter, sans-serif", color: T.white }}>
        <StatusBar />
        <div style={{ flex: 1, overflowY: "auto", padding: "8px 18px 0", scrollbarWidth: "none" }}>
          <div onClick={() => setOpen(null)} style={{ display: "flex", alignItems: "center", gap: 6, marginBottom: 16, cursor: "pointer", color: T.sec, fontSize: 12 }}>
            <Icon name="chevRight" size={12} style={{ transform: "rotate(180deg)" }} /> Back to Photos
          </div>
          <div style={{ borderRadius: 20, overflow: "hidden", position: "relative", height: 160, background: `linear-gradient(135deg, ${open.colors[0]}, ${open.colors[1]})`, marginBottom: 16 }}>
            <div style={{ position: "absolute", inset: 0, display: "flex", alignItems: "center", justifyContent: "center", fontSize: 56, opacity: 0.7 }}>{open.img}</div>
            <div style={{ position: "absolute", inset: 0, background: "linear-gradient(to bottom, transparent 40%, rgba(0,0,0,0.75) 100%)" }} />
            <div style={{ position: "absolute", bottom: 0, left: 0, right: 0, padding: "12px 16px" }}>
              <div style={{ fontFamily: FD, fontStyle: "italic", fontSize: 22, color: "#fff" }}>{open.name}</div>
              <div style={{ fontSize: 11, color: "rgba(255,255,255,0.75)" }}>{open.count}</div>
            </div>
          </div>
          <div style={{ display: "grid", gridTemplateColumns: "repeat(3, 1fr)", gap: 6, marginBottom: 20 }}>
            {Array.from({ length: 9 }).map((_, i) => (
              <div key={i} style={{ aspectRatio: "1", borderRadius: 10, background: `linear-gradient(135deg, ${open.colors[0]}55, ${open.colors[1]}55)`, display: "flex", alignItems: "center", justifyContent: "center", fontSize: 20, opacity: 0.85 }}>
                {open.img}
              </div>
            ))}
          </div>
        </div>
      </div>
    );
  }

  return (
    <div style={{ display: "flex", flexDirection: "column", height: "100%", background: "radial-gradient(ellipse at top, #14100A 0%, #000000 60%)", fontFamily: "'DM Sans', Inter, sans-serif", color: T.white }}>
      <StatusBar />
      <div style={{ flex: 1, overflowY: "auto", scrollbarWidth: "none" }}>
        <div style={{ padding: "8px 18px 0" }}>
          <div style={{ display: "flex", justifyContent: "space-between", alignItems: "flex-start", marginBottom: 14 }}>
            <div>
              <div style={{ fontFamily: FD, fontStyle: "italic", fontSize: 26 }}>Ciyato Photos</div>
              <div style={{ fontSize: 12, color: T.gold, display: "flex", alignItems: "center", gap: 4 }}>AI-sorted moments and media <Icon name="star4" size={11} color={T.gold} /></div>
            </div>
            <div style={{ display: "flex", gap: 8, alignItems: "center" }}>
              <div style={{ width: 34, height: 34, borderRadius: 17, background: "linear-gradient(135deg,#C6A15B,#8B6B2A)", display: "flex", alignItems: "center", justifyContent: "center", fontSize: 14 }}>👤</div>
              <Icon name="moreV" size={18} color={T.sec} />
            </div>
          </div>
          <SearchBar placeholder="Search photos, albums, places…" />
          <div style={{ display: "flex", gap: 8, marginTop: 12, overflowX: "auto", scrollbarWidth: "none" }}>
            {chips.map((c) => <FilterChip key={c} label={c} active={activeChip === c} onClick={() => setActiveChip(c)} />)}
          </div>
        </div>

        {/* Photo grid */}
        <div style={{ display: "grid", gridTemplateColumns: "1fr 1fr", gap: 8, padding: "14px 18px 0" }}>
          {mockPhotos.collections.map((col) => (
            <div key={col.name} onClick={() => setOpen(col)} style={{ borderRadius: 18, overflow: "hidden", position: "relative", height: 170, background: `linear-gradient(135deg, ${col.colors[0]}, ${col.colors[1]})`, cursor: "pointer", border: `1px solid ${T.gold}22` }}>
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
