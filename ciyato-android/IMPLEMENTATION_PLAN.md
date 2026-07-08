# Visual Refactoring Plan - 1-to-1 Screenshot Alignment

To align the Ciyoto launcher and organizer with the premium, high-fidelity design screenshots, we will execute a complete layout refactoring of the main screens. We will replace temporary widgets and diagnostic sections with exact copies of the visual cards, folders, and settings panels shown in the mockups.

---

## Target Visual Specifications

### 1. Home Screen Dashboard Refactoring (Image 3 & Image 5)
#### [MODIFY] [HomeScreen.kt](file:///C:/Users/ADMIN/StudioProjects/-nv-k-ik-/ciyato-android/app/src/main/java/com/ciyato/launcher/ui/screens/HomeScreen.kt)
- **Top Greeting**: Labeled "Good morning, Alex ☀️", "Tuesday, May 20". On the right side: Sparkle badge (AI Optimizer) and Notification Bell.
- **Search Bar**: "Search apps, files, contacts..." with search icon on the right, and a filter/menu icon next to it.
- **Top Twin Widgets**:
  - **Weather Card** (Left): "24° Partly sunny, Feels like 26°, New York, AQI 42".
  - **Today Agenda Card** (Right): Timeline format with colors displaying "10:00 AM Design Sync", "02:30 PM Client Call", "06:00 PM Gym Session".
- **Smart Categories Grid**: Two-column layout of rounded folder cards displaying miniature 2x2 grids of app icons.
- **Smart Shortcuts strip**: Horizontal list of app icons with a "+" icon at the end.
- **Bottom Dock**: Pinned icons (Phone, Messages, Chrome, Camera, settings).
- **Bottom Navigation Bar**: 4 icons (App Library, Files, Star, Settings).

---

### 2. File Manager & AI Organizer (Image 2)
#### [MODIFY] [FilesScreen.kt](file:///C:/Users/ADMIN/StudioProjects/-nv-k-ik-/ciyato-android/app/src/main/java/com/ciyato/launcher/ui/screens/FilesScreen.kt)
- **Header**: "Ciyoto Files - AI Phone Organizer". On the right: Sparkle and Bell indicators.
- **Storage Overview Card** (Left column): Circular progress chart displaying "53% Used", "68 GB used / 128 GB total", "Clean suggestions: 2.4 GB >".
- **Cleanup Status Card** (Right column): Green check circle indicating "Excellent - Your device is well organized."
- **Folder Categories Grid**: Two-column grid of 6 folders (Screenshots, Documents, Downloads, Photos, Videos, APKs, WhatsApp).
- **Recent Files Row**: Horizontal row of document thumbnails displaying format badges (PDF, PPT, JPG).
- **Duplicate & Large Files Cards**: Side-by-side cards highlighting duplicate files (1.8 GB) and large files (8.3 GB).
- **Quick Actions Row**: Rounded action buttons (AI Cleanup 2.4 GB, Junk files 1.1 GB, Vault, File transfer).

---

### 3. Smart Collections Screen (Image 4)
#### [MODIFY] [FileCollectionDetailScreen.kt](file:///C:/Users/ADMIN/StudioProjects/-nv-k-ik-/ciyato-android/app/src/main/java/com/ciyato/launcher/ui/screens/FileCollectionDetailScreen.kt)
- **Header**: "Ciyoto Files" with Search and Option icons.
- **Tab Bar**: Custom tabs ("Smart Collections" selected with gold underline, "Timeline").
- **Grid of Folders**:
  - Work Files (Word, Excel, PPT preview badges)
  - Receipts
  - PDFs
  - Contracts
  - Screen Recordings (video thumbnail previews)
  - Design Assets (PS, AI, XD indicators)
  - WhatsApp Media
  - Travel
  - College
  - Recently Added
- **Storage Summary Footer**: Storage used bar (62%), Duplicates found card (1.32 GB), and Large files card (1.13 GB).

---

### 4. Theme Studio Options Panel (Image 3)
#### [MODIFY] [ThemeStudioScreen.kt](file:///C:/Users/ADMIN/StudioProjects/-nv-k-ik-/ciyato-android/app/src/main/java/com/ciyato/launcher/ui/screens/ThemeStudioScreen.kt)
- **Left Control Panel**: Frosted glass card overlay listing:
  - Ciyato Home Active (Switch toggle)
  - Icon Style (Glass Rounded >)
  - Color Accents (Row of colored circle buttons: Gold, Blue, Green, Cyan, etc.)
  - Glass Mode (Medium >)
  - Typography (Inter >)
  - Smart Box Style (Elevated >)
  - Dock Style (Platform >)
  - Dark Mode: Auto | Light | Dark selector buttons.
- **Right Preview Panel**: Live responsive mock of the Home screen updating coordinates instantly.

---

### 5. App Library & Category List (Image 1)
#### [MODIFY] [AppDrawerScreen.kt](file:///C:/Users/ADMIN/StudioProjects/-nv-k-ik-/ciyato-android/app/src/main/java/com/ciyato/launcher/ui/screens/AppDrawerScreen.kt)
- Group drawer listings into nested category cards (e.g. Work contains a sub-drawer "Projects - 4 apps" containing Notion, Dropbox, Docs).
- Style all folder containers with a clean, darkish premium overlay matching the Image 1 reference cards.

---

## Verification Plan

### Manual Layout Inspection
- Compare each screen on a physical test device side-by-side with the screenshots.
- Verify that color highlights, borders, shadows, and paddings match the 1-to-1 aesthetics.
