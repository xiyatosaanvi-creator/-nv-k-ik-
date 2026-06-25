# BUILD PLAN

## Screens to Extract
1. HomeDense
2. HomeSpacious
3. AppDrawer
4. ThemeStudio
5. CiyatoFiles
6. SmartCollections
7. CiyatoPhotos
8. AISearch
9. BeforeAfter
10. Showcase

## Components to Extract
1. Icon
2. PhoneFrame
3. StatusBar
4. CiyatoLogo
5. AppIcon
6. GlassCard
7. SearchBar
8. BottomNav
9. FilterChip

## Mock Data separation
- `mockApps.ts`: App categories, names, etc.
- `mockFiles.ts`: Categories, recent files, sizes.
- `mockPhotos.ts`: Photo categories.
- `mockSearch.ts`: Search results, screenshots.
- `mockAgenda.ts`: Weather and agenda info.

## Changes/Upgrades Needed
- Set up `tokens.ts` and replace red variables in `index.css`.
- Fix imports across the board to use extracted components.
- Ensure the main `App.tsx` shell acts as the navigator between screens.
- Avoid using `<a>` tags in `wouter` links to prevent hydration errors.
