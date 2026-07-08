import { useMemo, useRef, useState } from "react";
import { T } from "../tokens";
import { StatusBar } from "../components/StatusBar";
import { Icon } from "../components/Icon";
import { SearchBar } from "../components/SearchBar";
import { AppIcon } from "../components/AppIcon";
import { GlassCard } from "../components/GlassCard";
import { mockApps } from "../data/mockApps";
import { useSettings } from "../context/SettingsContext";
import { getVisibleWithOverflow } from "../lib/appOverflow";

type Category = { id: string; label: string; apps: string[]; custom: boolean };

// Home is the launcher's home screen — the first thing you see when Ciyato is
// your Android home app. Its density (Dense / Spacious / Smart App Library)
// is a Theme Studio setting, not a separate screen. Home also supports
// multiple swipeable screens: Smart categories can be moved off the primary
// screen onto a secondary/tertiary screen, and you can create your own
// custom category boxes on any screen. The full app catalog still lives one
// tap away in the App Drawer.
export const Home = () => {
  const {
    layoutMode, editMode, toggleEditMode, isHidden, hideApp, accentColor, darkMode,
    pages, customCategories, addPage, moveCategoryToPage, addCustomCategory,
  } = useSettings();

  const [pageIndex, setPageIndex] = useState(0);
  const [expanded, setExpanded] = useState<string | null>(null);
  const [movingId, setMovingId] = useState<string | null>(null);
  const [creating, setCreating] = useState<{ pageId: string } | null>(null);
  const [newName, setNewName] = useState("");
  const [newApps, setNewApps] = useState<string[]>([]);
  const [toast, setToast] = useState<string | null>(null);
  const scrollerRef = useRef<HTMLDivElement>(null);

  const spacious = layoutMode !== "dense";
  const previewSlots = layoutMode === "dense" ? 4 : 6;
  const isLight = darkMode === "light";
  const textColor = isLight ? T.lightText : T.white;
  const secColor = isLight ? T.lightSec : T.sec;
  const bg = isLight ? "radial-gradient(ellipse at top, #F5F2EC 0%, #E9E4D8 60%)" : "radial-gradient(ellipse at top, #1A2028 0%, #0B0F12 50%)";
  const cardBg = isLight ? "rgba(0,0,0,0.03)" : T.card;
  const cardBorder = isLight ? T.lightBorder : T.border;
  const dockBg = isLight ? "rgba(255,255,255,0.85)" : "rgba(20,25,30,0.85)";

  const showToast = (msg: string) => {
    setToast(msg);
    window.setTimeout(() => setToast(null), 1600);
  };

  const allCategories: Category[] = useMemo(() => [
    ...Object.entries(mockApps.categories).map(([label, apps]) => ({ id: label, label, apps, custom: false })),
    ...customCategories.map((c) => ({ id: c.id, label: c.name, apps: c.apps, custom: true })),
  ], [customCategories]);

  const categoriesById = useMemo(() => new Map(allCategories.map((c) => [c.id, c])), [allCategories]);

  const allAppNames = useMemo(() => {
    const set = new Set<string>();
    Object.values(mockApps.categories).forEach((apps) => apps.forEach((a) => set.add(a)));
    return Array.from(set).sort();
  }, []);

  const shortcuts = mockApps.shortcuts.filter((a) => !isHidden(a));

  const safeIndex = Math.min(pageIndex, pages.length - 1);
  const currentPage = pages[safeIndex];

  const scrollToPage = (i: number) => {
    setPageIndex(i);
    const el = scrollerRef.current;
    if (el) el.scrollTo({ left: i * el.clientWidth, behavior: "smooth" });
  };

  const handleScroll = () => {
    const el = scrollerRef.current;
    if (!el || el.clientWidth === 0) return;
    const i = Math.round(el.scrollLeft / el.clientWidth);
    if (i !== pageIndex) setPageIndex(i);
  };

  const handleAddPage = () => {
    const id = addPage();
    showToast("New screen added");
    window.setTimeout(() => scrollToPage(pages.length), 50);
    return id;
  };

  const openCreator = (pageId: string) => {
    setCreating({ pageId });
    setNewName("");
    setNewApps([]);
  };

  const saveCreator = () => {
    if (!creating || !newName.trim() || newApps.length === 0) return;
    addCustomCategory(newName.trim(), newApps, creating.pageId);
    setCreating(null);
    showToast(`"${newName.trim()}" created`);
  };

  const renderCategoryCard = (cat: Category, pageId: string) => {
    const visibleApps = cat.apps.filter((a) => !isHidden(a));
    if (visibleApps.length === 0) return null;
    const isOpen = expanded === cat.id;
    const { visible, overflow } = getVisibleWithOverflow(visibleApps, previewSlots);
    return (
      <GlassCard key={cat.id} style={{ padding: "14px 12px", gridColumn: isOpen ? "1 / -1" : undefined }} onClick={() => setExpanded(isOpen ? null : cat.id)}>
        <div style={{ display: "flex", justifyContent: "space-between", alignItems: "center", marginBottom: 10 }}>
          <span style={{ fontSize: 13, fontWeight: 600 }}>{cat.label}</span>
          <div style={{ display: "flex", alignItems: "center", gap: 8 }}>
            {editMode && (
              <div
                onClick={(e) => { e.stopPropagation(); setMovingId(movingId === cat.id ? null : cat.id); }}
                style={{ width: 22, height: 22, borderRadius: 11, background: cardBg, border: `1px solid ${cardBorder}`, display: "flex", alignItems: "center", justifyContent: "center" }}
              >
                <Icon name="transfer" size={11} color={secColor} />
              </div>
            )}
            <Icon name={isOpen ? "chevDown" : "chevRight"} size={13} color={T.muted} />
          </div>
        </div>

        {movingId === cat.id && (
          <div onClick={(e) => e.stopPropagation()} style={{ display: "flex", flexWrap: "wrap", gap: 6, marginBottom: 10, padding: 8, borderRadius: 10, background: "rgba(0,0,0,0.15)" }}>
            <span style={{ fontSize: 10, color: secColor, width: "100%" }}>Move to:</span>
            {pages.filter((p) => p.id !== pageId).map((p) => (
              <div
                key={p.id}
                onClick={() => { moveCategoryToPage(cat.id, pageId, p.id); setMovingId(null); showToast(`Moved "${cat.label}" to ${p.name}`); }}
                style={{ fontSize: 11, fontWeight: 600, padding: "5px 10px", borderRadius: 50, background: accentColor, color: "#1a1204", cursor: "pointer" }}
              >
                {p.name}
              </div>
            ))}
            <div
              onClick={() => { const id = handleAddPage(); window.setTimeout(() => { moveCategoryToPage(cat.id, pageId, id); setMovingId(null); }, 60); }}
              style={{ fontSize: 11, fontWeight: 600, padding: "5px 10px", borderRadius: 50, border: `1px dashed ${cardBorder}`, color: secColor, cursor: "pointer" }}
            >
              + New screen
            </div>
          </div>
        )}

        <div style={{ display: "flex", flexWrap: "wrap", gap: 6 }}>
          {(isOpen ? visibleApps : visible).map((a) => (
            <div key={a} style={{ position: "relative" }} onClick={(e) => { if (editMode) { e.stopPropagation(); hideApp(a); } }}>
              <AppIcon app={a} size={38} />
              {editMode && (
                <div style={{ position: "absolute", top: -5, right: -5, width: 15, height: 15, borderRadius: 8, background: "#D64545", display: "flex", alignItems: "center", justifyContent: "center" }}>
                  <Icon name="x" size={8} color="#fff" />
                </div>
              )}
            </div>
          ))}
          {!isOpen && overflow > 0 && (
            <div style={{ width: 38, height: 38, borderRadius: 10, background: "rgba(255,255,255,0.05)", display: "flex", alignItems: "center", justifyContent: "center", fontSize: 12, fontWeight: 600, color: T.sec }}>
              +{overflow}
            </div>
          )}
        </div>
      </GlassCard>
    );
  };

  const renderAddCategoryTile = (pageId: string) => (
    <div
      key="add-category"
      onClick={() => openCreator(pageId)}
      style={{ padding: "14px 12px", borderRadius: 20, border: `1px dashed ${cardBorder}`, display: "flex", flexDirection: "column", alignItems: "center", justifyContent: "center", gap: 6, minHeight: 96, cursor: "pointer", color: secColor }}
    >
      <Icon name="plus" size={18} color={secColor} />
      <span style={{ fontSize: 11, fontWeight: 600 }}>New category</span>
    </div>
  );

  return (
    <div style={{ position: "relative", display: "flex", flexDirection: "column", height: "100%", background: bg, fontFamily: "Inter, system-ui, sans-serif", color: textColor }}>
      <StatusBar light={isLight} />

      <div
        ref={scrollerRef}
        onScroll={handleScroll}
        style={{ flex: 1, display: "flex", overflowX: "auto", overflowY: "hidden", scrollSnapType: "x mandatory", scrollbarWidth: "none" }}
      >
        {pages.map((page, pi) => {
          const pageCategories = page.categoryIds.map((id) => categoriesById.get(id)).filter((c): c is Category => !!c);
          return (
            <div key={page.id} style={{ minWidth: "100%", scrollSnapAlign: "start", overflowY: "auto", scrollbarWidth: "none", padding: spacious ? "16px 24px 0" : "8px 18px 0" }}>
              {pi === 0 ? (
                <>
                  {/* Header */}
                  <div style={{ display: "flex", justifyContent: "space-between", alignItems: "flex-start", marginBottom: spacious ? 24 : 14 }}>
                    <div>
                      <div style={{ fontSize: spacious ? 24 : 20, fontWeight: 700 }}>Good morning, Alex ☀️</div>
                      <div style={{ fontSize: spacious ? 14 : 13, color: secColor }}>Tuesday, May 20</div>
                    </div>
                    <div style={{ display: "flex", gap: 8 }}>
                      <div onClick={toggleEditMode} style={{ padding: "0 12px", height: 36, borderRadius: 18, background: editMode ? accentColor : cardBg, display: "flex", alignItems: "center", justifyContent: "center", border: `1px solid ${cardBorder}`, fontSize: 11, fontWeight: 700, color: editMode ? "#1a1204" : secColor, cursor: "pointer" }}>
                        {editMode ? "Done" : "Edit"}
                      </div>
                      <div style={{ width: 36, height: 36, borderRadius: 18, background: cardBg, display: "flex", alignItems: "center", justifyContent: "center", border: `1px solid ${cardBorder}` }}>
                        <Icon name="settings" size={16} color={textColor} />
                      </div>
                    </div>
                  </div>

                  <SearchBar light={isLight} />

                  {/* Agenda / Weather */}
                  <div style={{ display: "flex", gap: 10, marginTop: spacious ? 24 : 14 }}>
                    <GlassCard style={{ flex: 0.8, padding: "14px", display: "flex", flexDirection: "column", justifyContent: "space-between", background: "linear-gradient(135deg, rgba(255,255,255,0.08) 0%, rgba(255,255,255,0.03) 100%)" }}>
                      <div style={{ display: "flex", justifyContent: "space-between", alignItems: "center" }}>
                        <div style={{ fontSize: 24, fontWeight: 700 }}>24°</div>
                        <Icon name="sun" size={20} color={T.goldSoft} />
                      </div>
                      <div>
                        <div style={{ fontSize: 12, fontWeight: 600 }}>Partly sunny</div>
                        <div style={{ fontSize: 11, color: T.muted }}>New York · AQI 42</div>
                      </div>
                    </GlassCard>
                    <GlassCard style={{ flex: 1.2, padding: "12px 14px", display: "flex", flexDirection: "column" }}>
                      <div style={{ display: "flex", justifyContent: "space-between", alignItems: "center", marginBottom: 8 }}>
                        <span style={{ fontSize: 13, fontWeight: 600 }}>Today</span>
                        <span style={{ fontSize: 11, color: T.blue }}>View all</span>
                      </div>
                      <div style={{ display: "flex", flexDirection: "column", gap: 6 }}>
                        {[
                          { t: "10:00", m: "Design sync" },
                          { t: "13:30", m: "Lunch w/ Sarah" },
                          { t: "15:00", m: "Review deck" },
                        ].map((item, i) => (
                          <div key={i} style={{ display: "flex", alignItems: "center", gap: 8 }}>
                            <div style={{ width: 4, height: 4, borderRadius: 2, background: accentColor }} />
                            <span style={{ fontSize: 11, color: T.sec, width: 34 }}>{item.t}</span>
                            <span style={{ fontSize: 12 }}>{item.m}</span>
                          </div>
                        ))}
                      </div>
                    </GlassCard>
                  </div>
                </>
              ) : (
                <div style={{ display: "flex", justifyContent: "space-between", alignItems: "center", marginBottom: spacious ? 24 : 14, marginTop: spacious ? 8 : 4 }}>
                  <div style={{ fontSize: spacious ? 20 : 17, fontWeight: 700 }}>{page.name}</div>
                  <span style={{ fontSize: 11, color: secColor }}>Screen {pi + 1} of {pages.length}</span>
                </div>
              )}

              {/* Smart Categories */}
              <div style={{ display: "flex", justifyContent: "space-between", alignItems: "center", marginTop: pi === 0 ? 20 : 0, marginBottom: 12 }}>
                <span style={{ fontSize: 14, fontWeight: 600 }}>Smart categories</span>
                <span onClick={toggleEditMode} style={{ fontSize: 12, color: T.blue, cursor: "pointer" }}>{editMode ? "Done" : "Edit"}</span>
              </div>

              <div style={{ display: "grid", gridTemplateColumns: "1fr 1fr", gap: 10 }}>
                {pageCategories.map((cat) => renderCategoryCard(cat, page.id))}
                {editMode && renderAddCategoryTile(page.id)}
              </div>

              {pageCategories.length === 0 && !editMode && (
                <div style={{ marginTop: 20, padding: 20, textAlign: "center", borderRadius: 16, border: `1px dashed ${cardBorder}`, color: secColor, fontSize: 12 }}>
                  Nothing here yet. Tap Edit to move a category here or create a new one.
                </div>
              )}

              {/* Shortcuts + dock spacer only on the first page */}
              {pi === 0 && shortcuts.length > 0 && (
                <div style={{ marginTop: 20, padding: 14, background: cardBg, borderRadius: 20, border: `1px solid ${cardBorder}` }}>
                  <div style={{ fontSize: 13, fontWeight: 600, marginBottom: 4 }}>Duplicate smart shortcuts</div>
                  <div style={{ fontSize: 11, color: secColor, marginBottom: 12 }}>One app, multiple places</div>
                  <div style={{ display: "flex", gap: 10 }}>
                    {shortcuts.map((a) => (
                      <AppIcon key={a} app={a} size={42} />
                    ))}
                    <div style={{ width: 42, height: 42, borderRadius: 12, border: `1px dashed ${cardBorder}`, display: "flex", alignItems: "center", justifyContent: "center", color: secColor }}>
                      <Icon name="plus" size={18} />
                    </div>
                  </div>
                </div>
              )}

              <div style={{ marginBottom: 100 }} />
            </div>
          );
        })}
      </div>

      {/* Page indicator */}
      <div style={{ position: "absolute", bottom: 92, left: 0, right: 0, display: "flex", justifyContent: "center", alignItems: "center", gap: 6 }}>
        {pages.map((p, i) => (
          <div
            key={p.id}
            onClick={() => scrollToPage(i)}
            style={{ width: safeIndex === i ? 16 : 6, height: 6, borderRadius: 3, background: safeIndex === i ? accentColor : (isLight ? "rgba(0,0,0,0.2)" : "rgba(255,255,255,0.25)"), cursor: "pointer", transition: "all 0.2s" }}
          />
        ))}
        {editMode && (
          <div onClick={handleAddPage} style={{ width: 16, height: 16, borderRadius: 8, border: `1px dashed ${isLight ? "rgba(0,0,0,0.3)" : "rgba(255,255,255,0.4)"}`, display: "flex", alignItems: "center", justifyContent: "center", marginLeft: 4, cursor: "pointer" }}>
            <Icon name="plus" size={9} color={secColor} />
          </div>
        )}
      </div>

      {/* Dock */}
      <div style={{ position: "absolute", bottom: 20, left: 16, right: 16, display: "flex", justifyContent: "space-around", padding: "14px 10px", background: dockBg, border: `1px solid ${cardBorder}`, borderRadius: 32, backdropFilter: "blur(20px)" }}>
        {["Phone", "Messages", "Chrome", "Camera", "Copilot"].filter((a) => !isHidden(a)).map((a) => (
          <AppIcon key={a} app={a} size={50} />
        ))}
      </div>

      {/* New category creator */}
      {creating && (
        <div style={{ position: "absolute", inset: 0, background: "rgba(0,0,0,0.6)", backdropFilter: "blur(4px)", display: "flex", alignItems: "flex-end", zIndex: 20 }} onClick={() => setCreating(null)}>
          <div onClick={(e) => e.stopPropagation()} style={{ width: "100%", maxHeight: "78%", background: isLight ? T.lightBg : T.bgEl, borderRadius: "24px 24px 0 0", padding: "18px 18px 24px", display: "flex", flexDirection: "column", gap: 14 }}>
            <div style={{ fontSize: 16, fontWeight: 700, color: textColor }}>New category</div>
            <input
              value={newName}
              onChange={(e) => setNewName(e.target.value)}
              placeholder="Category name"
              style={{ padding: "10px 14px", borderRadius: 12, border: `1px solid ${cardBorder}`, background: cardBg, color: textColor, fontSize: 13, outline: "none" }}
            />
            <div style={{ fontSize: 11, color: secColor }}>Choose apps</div>
            <div style={{ display: "flex", flexWrap: "wrap", gap: 8, overflowY: "auto", maxHeight: 200 }}>
              {allAppNames.map((a) => {
                const selected = newApps.includes(a);
                return (
                  <div
                    key={a}
                    onClick={() => setNewApps(selected ? newApps.filter((x) => x !== a) : [...newApps, a])}
                    style={{ display: "flex", alignItems: "center", gap: 6, padding: "6px 10px 6px 6px", borderRadius: 50, background: selected ? accentColor : cardBg, border: `1px solid ${selected ? accentColor : cardBorder}`, cursor: "pointer" }}
                  >
                    <AppIcon app={a} size={20} />
                    <span style={{ fontSize: 11, fontWeight: 600, color: selected ? "#1a1204" : textColor }}>{a}</span>
                  </div>
                );
              })}
            </div>
            <div style={{ display: "flex", gap: 10, marginTop: 4 }}>
              <div onClick={() => setCreating(null)} style={{ flex: 1, textAlign: "center", padding: "11px 0", borderRadius: 50, border: `1px solid ${cardBorder}`, color: secColor, fontSize: 13, fontWeight: 600, cursor: "pointer" }}>Cancel</div>
              <div
                onClick={saveCreator}
                style={{ flex: 1, textAlign: "center", padding: "11px 0", borderRadius: 50, background: newName.trim() && newApps.length > 0 ? accentColor : cardBg, color: newName.trim() && newApps.length > 0 ? "#1a1204" : secColor, fontSize: 13, fontWeight: 700, cursor: newName.trim() && newApps.length > 0 ? "pointer" : "default" }}
              >
                Create
              </div>
            </div>
          </div>
        </div>
      )}

      {toast && (
        <div style={{ position: "absolute", bottom: 96, left: "50%", transform: "translateX(-50%)", background: "rgba(20,25,30,0.95)", border: `1px solid ${T.border}`, borderRadius: 12, padding: "8px 14px", fontSize: 11, color: T.white, whiteSpace: "nowrap", zIndex: 30 }}>
          {toast}
        </div>
      )}
    </div>
  );
};
