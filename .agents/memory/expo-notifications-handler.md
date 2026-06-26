---
name: Expo Notifications handler shape
description: expo-notifications@0.31+ changed the notification handler return type; shouldSetBadge removed.
---

## Rule
Use `shouldShowBanner` and `shouldShowList` in the `setNotificationHandler` callback.
Do NOT use `shouldSetBadge` — it was removed in expo-notifications@0.31.x.

## Why
The TypeScript type for `NotificationBehavior` was updated in 0.31.x, dropping `shouldSetBadge` and adding `shouldShowBanner` / `shouldShowList`.

## How to apply
```typescript
Notifications.setNotificationHandler({
  handleNotification: async () => ({
    shouldShowAlert: true,
    shouldPlaySound: false,
    shouldSetBadge: false,  // keep false only; use badge count APIs separately
    shouldShowBanner: true,
    shouldShowList: true,
  }),
});
```
