import { useState } from "react";
import { T } from "../tokens";
import { StatusBar } from "../components/StatusBar";
import { Icon } from "../components/Icon";
import { GlassCard } from "../components/GlassCard";
import { FilterChip } from "../components/FilterChip";

const FD = "'Cormorant Garamond', Georgia, serif";

const collections = [
  { name: "Work Files", count: "1,234 files", color: "#1565C0", emoji: "💼" },
  { name: "Receipts", count: "347 files", color: "#2E7D32", emoji: "🧾" },
  { name: "PDFs", count: "567 files", color: "#E53935", emoji: "📄" },
  { name: "Contracts", count: "89 files", color: "#0D47A1", emoji: "✍️" },
  { name: "Screen Recs", count: "124 files", color: "#6A1B9A", emoji: "🎥" },
  { name: "Design Assets", count: "312 files", color: "#F57F17", emoji: "🎨" },
  { name: "WhatsApp Media", count: "2,142 files", color: "#25D366", emoji: "💬" },
  { name: "Travel", count: "233 files", color: "#00838F", emoji: "✈️" },
  { name: "College", count: "445 files", color: "#5D4037", emoji: "🎓" },
  { name: "Recently Added", count: "1,025 files", color: "#424242", emoji: "🕒" },
];

const sampleFilesFor = (name: string) => [
  { n: `${name.split(" ")[0]}_2026_Q2.pdf`, s: "1.2 MB" },
  { n: `${name.split(" ")[0]}_archive.zip`, s: "48 MB" },
  { n: `${name.split(" ")[0]}_notes.docx`, s: "88 KB" },
];

export const SmartCollections = () => {
  const [activeTab, setActiveTab] = useState("Smart Collections");
  const [granted, setGranted] = useState(false);
  const [open, setOpen] = useState<(typeof collections)[number] | null>(null);

  if (!granted) {
    return (
      <div style={{ display: "flex", flexDirection: "column", height: "100%", background: "radial-gradient(ellipse at top, #14100A 0%, #000000 60%)", fontFamily: "'DM Sans', Inter, sans-serif", color: T.white }}>
        <StatusBar />
        <div style={{ flex: 1, display: "flex", flexDirection: "column", alignItems: "center", justifyContent: "center", padding: "0 32px", textAlign: "center" }}>
          <div style={{ width: 72, height: 72, borderRadius: 36, background: "rgba(198,161,91,0.1)", border: `1px solid ${T.gold}55`, display: "flex", alignItems: "center", justifyContent: "center", marginBottom: 24 }}>
            <Icon name="folder" size={30} color={T.goldSoft} />
          </div>
          <div style={{ fontFamily: FD, fontStyle: "italic", fontSize: 26, marginBottom: 10 }}>Smart Collections</div>
          <div style={{ fontSize: 13, color: T.sec, lineHeight: 1.6, marginBottom: 28 }}>
            Ciyato organizes your files into intelligent collections — but it needs permission to read your device storage first.
          </div>
          <div onClick={() => setGranted(true)} style={{ padding: "13px 32px", borderRadius: 50, background: T.gold, color: "#1a1204", fontSize: 13, fontWeight: 700, cursor: "pointer" }}>
            Allow storage access
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
            <Icon name="chevRight" size={12} style={{ transform: "rotate(180deg)" }} /> Back to collections
          </div>
          <div style={{ borderRadius: 20, padding: "20px 18px", background: open.color, marginBottom: 20 }}>
            <div style={{ fontSize: 30, marginBottom: 8 }}>{open.emoji}</div>
            <div style={{ fontFamily: FD, fontStyle: "italic", fontSize: 24, color: "#fff" }}>{open.name}</div>
            <div style={{ fontSize: 12, color: "rgba(255,255,255,0.75)" }}>{open.count}</div>
          </div>
          <GlassCard style={{ padding: 0, overflow: "hidden" }}>
            {sampleFilesFor(open.name).map((f, i, arr) => (
              <div key={f.n} style={{ display: "flex", alignItems: "center", gap: 12, padding: "13px 14px", borderBottom: i < arr.length - 1 ? `1px solid ${T.borderSub}` : "none" }}>
                <Icon name="file" size={16} color={T.gold} />
                <div style={{ flex: 1 }}>
                  <div style={{ fontSize: 13, fontWeight: 500 }}>{f.n}</div>
                  <div style={{ fontSize: 10, color: T.muted }}>{f.s}</div>
                </div>
              </div>
            ))}
          </GlassCard>
          <div style={{ marginBottom: 20 }} />
        </div>
      </div>
    );
  }

  return (
    <div style={{ display: "flex", flexDirection: "column", height: "100%", background: "radial-gradient(ellipse at top, #14100A 0%, #000000 60%)", fontFamily: "'DM Sans', Inter, sans-serif", color: T.white }}>
      <StatusBar />
      <div style={{ flex: 1, overflowY: "auto", padding: "8px 18px 0", scrollbarWidth: "none" }}>

        {/* Header */}
        <div style={{ display: "flex", justifyContent: "space-between", alignItems: "center", marginBottom: 16 }}>
          <div style={{ fontFamily: FD, fontStyle: "italic", fontSize: 26 }}>Smart Collections</div>
          <div style={{ display: "flex", gap: 12 }}>
            <Icon name="search" size={20} color={T.sec} />
            <Icon name="moreV" size={20} color={T.sec} />
          </div>
        </div>

        {/* Tabs */}
        <div style={{ display: "flex", gap: 8, marginBottom: 16 }}>
          {["Smart Collections", "Timeline"].map((t) => (
            <FilterChip key={t} label={t} active={activeTab === t} onClick={() => setActiveTab(t)} />
          ))}
        </div>

        <div style={{ display: "flex", justifyContent: "flex-end", marginBottom: 12 }}>
          <span style={{ fontSize: 12, color: T.sec }}>Most recent ↓</span>
        </div>

        {/* Collections Grid */}
        <div style={{ display: "grid", gridTemplateColumns: "1fr 1fr", gap: 10, marginBottom: 20 }}>
          {collections.map((col) => (
            <div key={col.name} onClick={() => setOpen(col)} style={{ background: col.color, borderRadius: 16, padding: "16px 14px", display: "flex", flexDirection: "column", position: "relative", overflow: "hidden", cursor: "pointer", border: `1px solid ${T.gold}22` }}>
              <div style={{ fontSize: 24, marginBottom: 12 }}>{col.emoji}</div>
              <div style={{ fontSize: 14, fontWeight: 600, color: "#fff" }}>{col.name}</div>
              <div style={{ fontSize: 11, color: "rgba(255,255,255,0.7)", marginTop: 4 }}>{col.count}</div>
              <div style={{ position: "absolute", right: -10, bottom: -10, fontSize: 60, opacity: 0.1 }}>{col.emoji}</div>
            </div>
          ))}
        </div>

        {/* Storage Summary */}
        <GlassCard style={{ marginBottom: 24, border: `1px solid ${T.gold}33` }}>
          <div style={{ display: "flex", justifyContent: "space-between", alignItems: "center", marginBottom: 16 }}>
            <div style={{ display: "flex", alignItems: "center", gap: 12 }}>
              <div style={{ width: 44, height: 44, borderRadius: 22, border: `3px solid ${T.cardMed}`, position: "relative", display: "flex", alignItems: "center", justifyContent: "center" }}>
                <svg width="44" height="44" style={{ position: "absolute", top: -3, left: -3 }}>
                  <circle cx="22" cy="22" r="20" fill="none" stroke={T.gold} strokeWidth="3" strokeDasharray="125" strokeDashoffset="48" strokeLinecap="round" transform="rotate(-90 22 22)" />
                </svg>
                <span style={{ fontSize: 11, fontWeight: 700 }}>62%</span>
              </div>
              <div>
                <div style={{ fontSize: 14, fontWeight: 600 }}>63.2 GB / 128 GB</div>
                <div style={{ fontSize: 11, color: T.muted }}>Internal Storage</div>
              </div>
            </div>
            <div style={{ padding: "6px 14px", borderRadius: 50, background: T.card, border: `1px solid ${T.border}`, fontSize: 12, color: T.sec, cursor: "pointer" }}>Manage</div>
          </div>
          {[["Duplicates found", "1.32 GB"], ["Large files", "1.13 GB"]].map(([label, size]) => (
            <div key={label} style={{ display: "flex", alignItems: "center", padding: "10px 0", borderTop: `1px solid ${T.borderSub}` }}>
              <div style={{ flex: 1, fontSize: 13 }}>{label}</div>
              <div style={{ fontSize: 12, color: T.muted, marginRight: 10 }}>{size}</div>
              <div style={{ padding: "5px 12px", borderRadius: 50, background: T.card, border: `1px solid ${T.border}`, fontSize: 11, color: T.sec, cursor: "pointer" }}>Review</div>
            </div>
          ))}
        </GlassCard>

        <div style={{ marginBottom: 10 }} />
      </div>
    </div>
  );
};
