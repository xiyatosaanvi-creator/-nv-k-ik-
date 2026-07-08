import { useMemo, useState } from "react";
import { T } from "../tokens";
import { Icon } from "../components/Icon";
import { deviceRoot } from "../data/mockFileTree";
import type { FileNode } from "../data/mockFileTree";

const kindIcon: Record<string, string> = {
  pdf: "file",
  image: "photo",
  video: "video",
  doc: "file",
  apk: "apk",
  audio: "zap",
  archive: "folder",
  other: "file",
};

export const FilesBrowser = () => {
  const [path, setPath] = useState<FileNode[]>([deviceRoot]);
  const [toast, setToast] = useState<string | null>(null);

  const current = path[path.length - 1];
  const children = useMemo(() => (current.children || []).slice().sort((a, b) => {
    if (a.type !== b.type) return a.type === "folder" ? -1 : 1;
    return a.name.localeCompare(b.name);
  }), [current]);

  const openFile = (f: FileNode) => {
    setToast(`Opening "${f.name}" with default app…`);
    window.setTimeout(() => setToast(null), 1800);
  };

  return (
    <div style={{ position: "relative", height: "100%", display: "flex", flexDirection: "column" }}>
      {/* Breadcrumb */}
      <div style={{ display: "flex", alignItems: "center", flexWrap: "wrap", gap: 4, padding: "2px 0 12px" }}>
        {path.map((node, i) => (
          <div key={i} style={{ display: "flex", alignItems: "center", gap: 4 }}>
            <span
              onClick={() => setPath(path.slice(0, i + 1))}
              style={{
                fontSize: 12,
                fontWeight: i === path.length - 1 ? 700 : 400,
                color: i === path.length - 1 ? T.white : T.sec,
                cursor: i === path.length - 1 ? "default" : "pointer",
              }}
            >
              {node.name}
            </span>
            {i < path.length - 1 && <Icon name="chevRight" size={11} color={T.muted} />}
          </div>
        ))}
      </div>

      <div style={{ flex: 1, overflowY: "auto", scrollbarWidth: "none" }}>
        {children.length === 0 && (
          <div style={{ fontSize: 12, color: T.muted, textAlign: "center", marginTop: 40 }}>This folder is empty.</div>
        )}
        {children.map((node) => (
          <div
            key={node.name}
            onClick={() => (node.type === "folder" ? setPath([...path, node]) : openFile(node))}
            style={{
              display: "flex",
              alignItems: "center",
              gap: 12,
              padding: "11px 4px",
              borderBottom: `1px solid ${T.borderSub}`,
              cursor: "pointer",
            }}
          >
            <div
              style={{
                width: 36,
                height: 36,
                borderRadius: 10,
                background: T.card,
                border: `1px solid ${T.border}`,
                display: "flex",
                alignItems: "center",
                justifyContent: "center",
                flexShrink: 0,
              }}
            >
              <Icon name={node.type === "folder" ? "folder" : kindIcon[node.kind || "other"]} size={16} color={node.type === "folder" ? T.gold : T.sec} />
            </div>
            <div style={{ flex: 1, minWidth: 0 }}>
              <div style={{ fontSize: 13, fontWeight: 500, whiteSpace: "nowrap", overflow: "hidden", textOverflow: "ellipsis" }}>{node.name}</div>
              <div style={{ fontSize: 10, color: T.muted }}>
                {node.type === "folder" ? `${node.children?.length ?? 0} items` : `${node.size} · ${node.date}`}
              </div>
            </div>
            {node.type === "folder" && <Icon name="chevRight" size={14} color={T.muted} />}
          </div>
        ))}
      </div>

      {toast && (
        <div
          style={{
            position: "absolute",
            bottom: 14,
            left: "50%",
            transform: "translateX(-50%)",
            background: "rgba(20,25,30,0.95)",
            border: `1px solid ${T.border}`,
            borderRadius: 12,
            padding: "8px 14px",
            fontSize: 11,
            color: T.white,
            whiteSpace: "nowrap",
          }}
        >
          {toast}
        </div>
      )}
    </div>
  );
};
