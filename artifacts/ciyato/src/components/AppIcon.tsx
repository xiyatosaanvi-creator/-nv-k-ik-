import { T } from "../tokens";

// Real app icon colors & emojis
const APP_ICONS: Record<string, { bg: string, emoji: string }> = {
  Google: { bg: "#fff", emoji: "🔵" },
  Notion: { bg: "#fff", emoji: "📝" },
  Slack: { bg: "#4A154B", emoji: "💬" },
  WhatsApp: { bg: "#25D366", emoji: "💬" },
  Instagram: { bg: "linear-gradient(135deg,#E1306C,#833AB4)", emoji: "📸" },
  LinkedIn: { bg: "#0077B5", emoji: "💼" },
  Drive: { bg: "#fff", emoji: "🟡" },
  Lightroom: { bg: "#001E36", emoji: "📷" },
  Chrome: { bg: "#fff", emoji: "🌐" },
  Gmail: { bg: "#fff", emoji: "📧" },
  Maps: { bg: "#fff", emoji: "🗺️" },
  Zoom: { bg: "#2D8CFF", emoji: "📹" },
  Figma: { bg: "#1E1E1E", emoji: "🎨" },
  Spotify: { bg: "#1DB954", emoji: "🎵" },
  Netflix: { bg: "#E50914", emoji: "🎬" },
  YouTube: { bg: "#FF0000", emoji: "▶️" },
  Airbnb: { bg: "#FF5A5F", emoji: "🏠" },
  PayPal: { bg: "#003087", emoji: "💳" },
  Phone: { bg: "#4CAF50", emoji: "📞" },
  Camera: { bg: "#000", emoji: "📷" },
  Photos: { bg: "#FF9800", emoji: "🖼️" },
  Copilot: { bg: "#0078D4", emoji: "🤖" },
  Notion2: { bg: "#fff", emoji: "N" },
  Premiere: { bg: "#9999FF", emoji: "🎬" },
  Photoshop: { bg: "#31A8FF", emoji: "🖌️" },
  Default: { bg: "rgba(255,255,255,0.1)", emoji: "📱" },
};

export const AppIcon = ({ app = "Default", size = 44, badge }: { app?: string, size?: number, badge?: string | number }) => {
  const info = APP_ICONS[app] || APP_ICONS.Default;
  const emojiMap: Record<string, string> = {
    Google: "G", Notion: "N", Slack: "#", WhatsApp: "W", Instagram: "In",
    LinkedIn: "in", Drive: "▲", Lightroom: "Lr", Chrome: "⊙", Gmail: "M",
    Maps: "⬡", Zoom: "Z", Figma: "F", Spotify: "♫", Netflix: "N",
    YouTube: "▶", Airbnb: "✦", PayPal: "P", Phone: "✆", Camera: "⊙",
    Photos: "⊡", Copilot: "⬡", Premiere: "Pr", Photoshop: "Ps",
  };
  const colors: Record<string, string> = {
    Google: "#4285F4", Notion: "#000", Slack: "#fff", WhatsApp: "#fff",
    Instagram: "#fff", LinkedIn: "#fff", Drive: "#FBBC04", Lightroom: "#31A8FF",
    Chrome: "#4285F4", Gmail: "#EA4335", Maps: "#34A853", Zoom: "#fff",
    Figma: "#fff", Spotify: "#fff", Netflix: "#fff", YouTube: "#fff",
    Airbnb: "#fff", PayPal: "#fff", Phone: "#fff", Camera: "#fff",
    Photos: "#fff", Copilot: "#fff", Premiere: "#fff", Photoshop: "#fff",
  };
  return (
    <div style={{ position: "relative", display: "inline-block" }}>
      <div style={{ width: size, height: size, borderRadius: size * 0.27, background: info.bg, display: "flex", alignItems: "center", justifyContent: "center", flexShrink: 0, fontSize: size * 0.42, fontWeight: 700, color: colors[app] || T.white, fontFamily: "Inter, system-ui, sans-serif", border: app === "Default" ? `1px solid ${T.border}` : "none", overflow: "hidden" }}>
        {emojiMap[app] || "✦"}
      </div>
      {badge && <div style={{ position: "absolute", top: -4, right: -4, background: T.gold, borderRadius: 10, minWidth: 18, height: 18, display: "flex", alignItems: "center", justifyContent: "center", fontSize: 9, fontWeight: 700, color: "#000", padding: "0 3px" }}>{badge}</div>}
    </div>
  );
};
