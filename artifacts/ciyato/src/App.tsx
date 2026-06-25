import { useState } from "react";
import { T } from "./tokens";
import { PhoneFrame } from "./components/PhoneFrame";
import { CiyatoLogo } from "./components/CiyatoLogo";
import { Icon } from "./components/Icon";
import { HomeDense } from "./screens/HomeDense";
import { HomeSpacious } from "./screens/HomeSpacious";
import { AppDrawer } from "./screens/AppDrawer";
import { ThemeStudio } from "./screens/ThemeStudio";
import { CiyatoFiles } from "./screens/CiyatoFiles";
import { SmartCollections } from "./screens/SmartCollections";
import { CiyatoPhotos } from "./screens/CiyatoPhotos";
import { AISearch } from "./screens/AISearch";
import { BeforeAfter } from "./screens/BeforeAfter";
import { Showcase } from "./screens/Showcase";

export default function App() {
  const [activeScreen, setActiveScreen] = useState("Home Dense");
  const [sidebarOpen, setSidebarOpen] = useState(true);

  const screens = [
    { id: "Home Dense", component: <HomeDense /> },
    { id: "Home Spacious", component: <HomeSpacious /> },
    { id: "App Drawer", component: <AppDrawer /> },
    { id: "Theme Studio", component: <ThemeStudio /> },
    { id: "Ciyato Files", component: <CiyatoFiles /> },
    { id: "Smart Collections", component: <SmartCollections /> },
    { id: "Ciyato Photos", component: <CiyatoPhotos /> },
    { id: "AI Search", component: <AISearch /> },
    { id: "Before / After", component: <BeforeAfter /> },
    { id: "Showcase", component: <Showcase /> }
  ];

  const ActiveComponent = screens.find(s => s.id === activeScreen)?.component || <HomeDense />;

  return (
    <div style={{ display: "flex", height: "100vh", width: "100vw", background: "#06080A", color: "#F4F5F6", fontFamily: "Inter, system-ui, sans-serif", overflow: "hidden" }}>
      
      {/* Sidebar Navigation */}
      <div style={{ width: sidebarOpen ? 240 : 0, transition: "width 0.3s cubic-bezier(0.4, 0, 0.2, 1)", background: T.bgEl, borderRight: `1px solid ${T.border}`, overflow: "hidden", display: "flex", flexDirection: "column", flexShrink: 0 }}>
        <div style={{ padding: "24px 20px" }}>
          <CiyatoLogo size={24} />
          <div style={{ marginTop: 4, fontSize: 12, color: T.muted }}>Prototype Explorer</div>
        </div>
        
        <div style={{ flex: 1, overflowY: "auto", padding: "0 12px", scrollbarWidth: "none" }}>
          {screens.map(s => {
            const isActive = activeScreen === s.id;
            return (
              <div 
                key={s.id} 
                onClick={() => setActiveScreen(s.id)}
                style={{
                  padding: "10px 12px",
                  margin: "4px 0",
                  borderRadius: 8,
                  cursor: "pointer",
                  fontSize: 13,
                  fontWeight: isActive ? 600 : 400,
                  color: isActive ? T.white : T.sec,
                  background: isActive ? T.cardMed : "transparent",
                  borderLeft: isActive ? `3px solid ${T.gold}` : "3px solid transparent",
                  transition: "all 0.15s"
                }}
                onMouseEnter={e => { if(!isActive) e.currentTarget.style.background = T.cardStr; }}
                onMouseLeave={e => { if(!isActive) e.currentTarget.style.background = "transparent"; }}
              >
                {s.id}
              </div>
            );
          })}
        </div>
        
        <div style={{ padding: "20px", borderTop: `1px solid ${T.borderSub}`, fontSize: 11, color: T.muted }}>
          Ciyato UI Concept v1.0
        </div>
      </div>

      {/* Main Canvas Area */}
      <div style={{ flex: 1, display: "flex", flexDirection: "column", position: "relative" }}>
        
        {/* Toggle Button */}
        <div 
          onClick={() => setSidebarOpen(!sidebarOpen)}
          style={{ position: "absolute", top: 20, left: sidebarOpen ? 20 : 20, zIndex: 50, width: 36, height: 36, borderRadius: 18, background: T.cardMed, border: `1px solid ${T.border}`, display: "flex", alignItems: "center", justifyContent: "center", cursor: "pointer", transition: "all 0.3s", backdropFilter: "blur(10px)" }}
        >
          <Icon name="grid" size={16} color={T.sec} />
        </div>

        {/* Centered Device Presentation */}
        <div style={{ flex: 1, display: "flex", alignItems: "center", justifyContent: "center", padding: 40, background: "radial-gradient(circle at center, rgba(198,161,91,0.03) 0%, transparent 60%)" }}>
          <div style={{ position: "relative", height: "100%", maxHeight: 840, display: "flex", alignItems: "center", justifyContent: "center" }}>
            
            {/* Ambient Glow */}
            <div style={{ position: "absolute", inset: -40, background: "radial-gradient(circle at center, rgba(255,255,255,0.05) 0%, transparent 70%)", filter: "blur(40px)", zIndex: 0, pointerEvents: "none" }} />
            
            <PhoneFrame light={activeScreen === "App Drawer"}>
              {ActiveComponent}
            </PhoneFrame>
          </div>
        </div>
      </div>
    </div>
  );
}
