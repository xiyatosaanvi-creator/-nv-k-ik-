import { useState } from "react";
import { T } from "../tokens";
import { StatusBar } from "../components/StatusBar";
import { Icon } from "../components/Icon";
import { FilterChip } from "../components/FilterChip";
import { GlassCard } from "../components/GlassCard";
import { AppIcon } from "../components/AppIcon";
import { mockSearch } from "../data/mockSearch";

export const AISearch = () => {
  const [activeFilter, setActiveFilter] = useState("All");

  return (
    <div style={{ display: "flex", flexDirection: "column", height: "100%", background: T.bg, fontFamily: "Inter, system-ui, sans-serif", color: T.white }}>
      <StatusBar />
      <div style={{ flex: 1, overflowY: "auto", padding: "8px 18px 0", scrollbarWidth: "none" }}>
        <div style={{ fontSize: 22, fontWeight: 700, marginBottom: 14 }}>AI Search</div>
        
        <div style={{ display: "flex", alignItems: "center", background: "rgba(255,255,255,0.08)", borderRadius: 50, padding: "11px 16px", gap: 10, border: `1px solid ${T.border}`, marginBottom: 12 }}>
          <span style={{ flex: 1, fontSize: 14, color: T.muted }}>Search apps, files, photos, and documents</span>
          <Icon name="mic" size={18} color={T.gold} />
        </div>
        
        <div style={{ display: "flex", gap: 7, overflowX: "auto", scrollbarWidth: "none", marginBottom: 16 }}>
          {["All", "Apps", "Files", "Photos", "Documents"].map(f => (
            <FilterChip key={f} label={f} active={activeFilter === f} onClick={() => setActiveFilter(f)} />
          ))}
        </div>

        {/* Prompt pills */}
        <div style={{ marginBottom: 20 }}>
          {mockSearch.promptPills.map((p, i) => (
            <div key={i} style={{ display: "flex", alignItems: "center", gap: 12, padding: "12px 14px", marginBottom: 8, background: T.cardStr, borderRadius: 14, border: `1px solid ${T.border}`, cursor: "pointer" }}>
              <div style={{ width: 34, height: 34, borderRadius: 10, background: T.bgEl2, display: "flex", alignItems: "center", justifyContent: "center", fontSize: 16 }}>
                {["📄", "💳", "💼", "💬"][i]}
              </div>
              <span style={{ fontSize: 14, color: T.sec }}>{p}</span>
            </div>
          ))}
        </div>

        {/* Top match */}
        <div style={{ fontSize: 14, fontWeight: 600, marginBottom: 8 }}>Top match</div>
        <GlassCard style={{ marginBottom: 20, padding: 14, display: "flex", alignItems: "center", gap: 12 }}>
          <div style={{ width: 42, height: 42, borderRadius: 12, background: "#E53935", display: "flex", alignItems: "center", justifyContent: "center", fontSize: 18, flexShrink: 0 }}>📄</div>
          <div style={{ flex: 1 }}>
            <div style={{ fontSize: 14, fontWeight: 600 }}>Project_Proposal.pdf</div>
            <div style={{ fontSize: 11, color: T.muted }}>PDF · 2.4 MB · Yesterday</div>
            <div style={{ fontSize: 11, color: T.muted }}>Documents</div>
          </div>
          <Icon name="moreV" size={16} color={T.muted} />
        </GlassCard>

        {/* Files */}
        <div style={{ display: "flex", justifyContent: "space-between", marginBottom: 8 }}>
          <span style={{ fontSize: 14, fontWeight: 600 }}>Files</span>
          <span style={{ fontSize: 12, color: T.blue }}>View all</span>
        </div>
        <GlassCard style={{ marginBottom: 20, padding: 0, overflow: "hidden" }}>
          {mockSearch.fileResults.map((f, i) => (
            <div key={i} style={{ display: "flex", alignItems: "center", gap: 12, padding: "12px 14px", borderBottom: i < mockSearch.fileResults.length - 1 ? `1px solid ${T.borderSub}` : "none" }}>
              <div style={{ width: 38, height: 38, borderRadius: 10, background: f.bg, display: "flex", alignItems: "center", justifyContent: "center", fontSize: 16, flexShrink: 0 }}>{f.emoji}</div>
              <div style={{ flex: 1 }}>
                <div style={{ fontSize: 13, fontWeight: 500 }}>{f.name}</div>
                <div style={{ fontSize: 10, color: T.muted }}>{f.meta}</div>
                <div style={{ fontSize: 10, color: T.muted }}>{f.loc}</div>
              </div>
              <Icon name="moreV" size={14} color={T.muted} />
            </div>
          ))}
        </GlassCard>

        {/* Screenshots */}
        <div style={{ display: "flex", justifyContent: "space-between", marginBottom: 8 }}>
          <span style={{ fontSize: 14, fontWeight: 600 }}>Screenshots</span>
          <span style={{ fontSize: 12, color: T.blue }}>View all</span>
        </div>
        <div style={{ display: "flex", gap: 8, marginBottom: 20 }}>
          {mockSearch.screenshots.map((s, i) => (
            <div key={i} style={{ flex: 1, background: T.cardStr, borderRadius: 14, border: `1px solid ${T.border}`, overflow: "hidden", minHeight: 90 }}>
              <div style={{ background: T.bgEl, padding: "14px 10px", display: "flex", flexDirection: "column", alignItems: "center", justifyContent: "center", minHeight: 70, fontSize: 24 }}>{s.emoji}</div>
              <div style={{ padding: "6px 8px" }}>
                <div style={{ fontSize: 10, fontWeight: 500, color: T.sec }}>{s.label}</div>
                <div style={{ fontSize: 9, color: T.muted }}>{s.sub}</div>
              </div>
            </div>
          ))}
        </div>

        {/* Apps */}
        <div style={{ display: "flex", justifyContent: "space-between", marginBottom: 10 }}>
          <span style={{ fontSize: 14, fontWeight: 600 }}>Apps</span>
          <span style={{ fontSize: 12, color: T.blue }}>View all</span>
        </div>
        <div style={{ display: "flex", gap: 16, marginBottom: 30 }}>
          {[["WhatsApp", "WhatsApp"], ["Gmail", "Gmail"], ["Drive", "Drive"], ["Slack", "Slack"], ["Notion", "Notion"]].map(([a, l]) => (
            <div key={a} style={{ display: "flex", flexDirection: "column", alignItems: "center", gap: 4 }}>
              <AppIcon app={a} size={44} />
              <span style={{ fontSize: 10, color: T.sec }}>{l}</span>
            </div>
          ))}
        </div>
        
      </div>
    </div>
  );
};
