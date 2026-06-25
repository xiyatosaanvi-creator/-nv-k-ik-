import { T } from "../tokens";
import { StatusBar } from "../components/StatusBar";
import { Icon } from "../components/Icon";
import { SearchBar } from "../components/SearchBar";
import { GlassCard } from "../components/GlassCard";
import { BottomNav } from "../components/BottomNav";
import { mockFiles } from "../data/mockFiles";

export const CiyatoFiles = () => {
  return (
    <div style={{ display: "flex", flexDirection: "column", height: "100%", background: T.bg, fontFamily: "Inter, system-ui, sans-serif", color: T.white }}>
      <StatusBar />
      <div style={{ flex: 1, overflowY: "auto", padding: "8px 18px 0", scrollbarWidth: "none" }}>
        
        {/* Header */}
        <div style={{ display: "flex", justifyContent: "space-between", alignItems: "flex-start", marginBottom: 14 }}>
          <div>
            <div style={{ fontSize: 22, fontWeight: 700 }}>Ciyato Files</div>
            <div style={{ fontSize: 12, color: T.gold, display: "flex", alignItems: "center", gap: 4 }}>AI Phone Organizer <Icon name="star4" size={11} color={T.gold} /></div>
          </div>
          <div style={{ display: "flex", gap: 12, alignItems: "center" }}>
            <Icon name="more" size={20} color={T.sec} />
          </div>
        </div>

        <SearchBar placeholder="Search files, folders…" />

        {/* Storage Overview */}
        <GlassCard style={{ display: "flex", alignItems: "center", gap: 16, marginTop: 16, marginBottom: 16 }}>
          <div style={{ width: 60, height: 60, borderRadius: 30, border: `4px solid ${T.cardMed}`, position: "relative", display: "flex", alignItems: "center", justifyContent: "center" }}>
            <svg width="60" height="60" style={{ position: "absolute", top: -4, left: -4 }}>
              <circle cx="30" cy="30" r="28" fill="none" stroke={T.gold} strokeWidth="4" strokeDasharray="175" strokeDashoffset="82" strokeLinecap="round" transform="rotate(-90 30 30)" />
            </svg>
            <span style={{ fontSize: 14, fontWeight: 700 }}>53%</span>
          </div>
          <div style={{ flex: 1 }}>
            <div style={{ fontSize: 14, fontWeight: 600 }}>68 GB used / 128 GB total</div>
            <div style={{ fontSize: 12, color: T.gold, marginTop: 4 }}>Clean suggestions 2.4 GB →</div>
          </div>
        </GlassCard>

        {/* Categories Grid */}
        <div style={{ display: "grid", gridTemplateColumns: "repeat(4, 1fr)", gap: 10, marginBottom: 20 }}>
          {mockFiles.categories.map((cat, i) => (
            <div key={i} style={{ display: "flex", flexDirection: "column", alignItems: "center", gap: 6, cursor: "pointer" }}>
              <div style={{ width: 50, height: 50, borderRadius: 16, background: cat.color, display: "flex", alignItems: "center", justifyContent: "center", fontSize: 22 }}>
                {cat.emoji}
              </div>
              <div style={{ textAlign: "center" }}>
                <div style={{ fontSize: 11, fontWeight: 500 }}>{cat.label}</div>
                <div style={{ fontSize: 9, color: T.muted }}>{cat.count}</div>
              </div>
            </div>
          ))}
          <div style={{ display: "flex", flexDirection: "column", alignItems: "center", gap: 6, cursor: "pointer" }}>
            <div style={{ width: 50, height: 50, borderRadius: 16, background: T.cardMed, border: `1px dashed ${T.border}`, display: "flex", alignItems: "center", justifyContent: "center", color: T.muted }}>
              <Icon name="plus" size={20} />
            </div>
            <div style={{ textAlign: "center" }}>
              <div style={{ fontSize: 11, fontWeight: 500 }}>More</div>
            </div>
          </div>
        </div>

        {/* Recent Files */}
        <div style={{ display: "flex", justifyContent: "space-between", alignItems: "center", marginBottom: 12 }}>
          <span style={{ fontSize: 15, fontWeight: 600 }}>Recent files</span>
          <span style={{ fontSize: 12, color: T.blue }}>View all +12</span>
        </div>
        <GlassCard style={{ padding: 0, overflow: "hidden", marginBottom: 20 }}>
          {mockFiles.recent.map((f, i) => (
            <div key={i} style={{ display: "flex", alignItems: "center", gap: 12, padding: "12px 14px", borderBottom: i < mockFiles.recent.length - 1 ? `1px solid ${T.borderSub}` : "none" }}>
              <div style={{ width: 38, height: 38, borderRadius: 10, background: f.bg, display: "flex", alignItems: "center", justifyContent: "center", fontSize: 16, flexShrink: 0 }}>{f.emoji}</div>
              <div style={{ flex: 1 }}>
                <div style={{ fontSize: 13, fontWeight: 500 }}>{f.name}</div>
                <div style={{ fontSize: 10, color: T.muted }}>{f.meta}</div>
              </div>
              <Icon name="moreV" size={14} color={T.muted} />
            </div>
          ))}
        </GlassCard>

        {/* Duplicate / Large Files */}
        <div style={{ display: "grid", gridTemplateColumns: "1fr 1fr", gap: 10, marginBottom: 20 }}>
          <GlassCard style={{ padding: 14 }}>
            <div style={{ fontSize: 13, fontWeight: 600, marginBottom: 4 }}>Duplicate files</div>
            <div style={{ fontSize: 18, color: T.gold, fontWeight: 700, marginBottom: 4 }}>1.8 GB</div>
            <div style={{ fontSize: 11, color: T.sec, display: "flex", alignItems: "center", gap: 4 }}>Review <Icon name="chevRight" size={12} /></div>
          </GlassCard>
          <GlassCard style={{ padding: 14 }}>
            <div style={{ fontSize: 13, fontWeight: 600, marginBottom: 4 }}>Large files</div>
            <div style={{ fontSize: 18, color: T.blue, fontWeight: 700, marginBottom: 4 }}>8.3 GB</div>
            <div style={{ fontSize: 11, color: T.sec, display: "flex", alignItems: "center", gap: 4 }}>Review <Icon name="chevRight" size={12} /></div>
          </GlassCard>
        </div>

        <div style={{ marginBottom: 20 }} />
      </div>

      <BottomNav active="files" />
    </div>
  );
};
