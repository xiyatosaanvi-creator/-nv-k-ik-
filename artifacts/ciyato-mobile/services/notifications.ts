/**
 * notifications.ts — Suggestion #139
 * Expo Push Notifications setup for Ciyato Mobile.
 * Registers for push tokens, sends token to backend, and handles incoming notifications.
 *
 * Usage:
 *   import { setupPushNotifications } from '@/services/notifications';
 *   await setupPushNotifications();
 */

import * as Notifications from "expo-notifications";
import * as Device from "expo-device";
import { Platform } from "react-native";

const API_BASE = process.env.EXPO_PUBLIC_API_URL ?? "https://ciyato-api.example.com";

// Configure how notifications appear when the app is foregrounded
Notifications.setNotificationHandler({
  handleNotification: async () => ({
    shouldShowAlert: true,
    shouldPlaySound: false,
    shouldSetBadge: true,
  }),
});

export interface PushSetupResult {
  token: string | null;
  error: string | null;
}

/**
 * Register for push notifications and upload the Expo push token to the backend.
 * Requires a physical device (simulators/emulators cannot receive push notifications).
 */
export async function setupPushNotifications(userId?: string): Promise<PushSetupResult> {
  if (!Device.isDevice) {
    return { token: null, error: "Push notifications require a physical device." };
  }

  if (Platform.OS === "android") {
    await Notifications.setNotificationChannelAsync("default", {
      name: "Default",
      importance: Notifications.AndroidImportance.HIGH,
      vibrationPattern: [0, 250, 250, 250],
      lightColor: "#FFD700",
    });

    await Notifications.setNotificationChannelAsync("focus", {
      name: "Focus Sessions",
      importance: Notifications.AndroidImportance.HIGH,
      sound: "default",
      lightColor: "#FFD700",
    });

    await Notifications.setNotificationChannelAsync("insights", {
      name: "AI Insights",
      importance: Notifications.AndroidImportance.DEFAULT,
      lightColor: "#FFD700",
    });
  }

  const { status: existingStatus } = await Notifications.getPermissionsAsync();
  let finalStatus = existingStatus;

  if (existingStatus !== "granted") {
    const { status } = await Notifications.requestPermissionsAsync();
    finalStatus = status;
  }

  if (finalStatus !== "granted") {
    return { token: null, error: "Push notification permission denied." };
  }

  const tokenData = await Notifications.getExpoPushTokenAsync({
    projectId: process.env.EXPO_PUBLIC_PROJECT_ID,
  });
  const token = tokenData.data;

  // Register token with backend
  try {
    await fetch(`${API_BASE}/api/v1/devices/push-token`, {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({ token, platform: "expo", userId }),
    });
  } catch {
    // Non-fatal: token is still valid for local use
  }

  return { token, error: null };
}

/** Schedule a local notification (e.g. Focus Session end). */
export async function scheduleLocalNotification(
  title: string,
  body: string,
  delaySeconds: number,
  channelId: string = "default",
): Promise<string> {
  return Notifications.scheduleNotificationAsync({
    content: { title, body, sound: true, data: { channelId } },
    trigger: { type: Notifications.SchedulableTriggerInputTypes.TIME_INTERVAL, seconds: delaySeconds },
  });
}

/** Cancel a previously scheduled notification. */
export async function cancelNotification(notificationId: string): Promise<void> {
  await Notifications.cancelScheduledNotificationAsync(notificationId);
}

/** Add a listener for received notifications (while app is open). */
export function addNotificationListener(
  handler: (notification: Notifications.Notification) => void,
): Notifications.EventSubscription {
  return Notifications.addNotificationReceivedListener(handler);
}

/** Add a listener for notification tap responses. */
export function addResponseListener(
  handler: (response: Notifications.NotificationResponse) => void,
): Notifications.EventSubscription {
  return Notifications.addNotificationResponseReceivedListener(handler);
}

/** Send a push notification to another device via Expo Push API. */
export async function sendExpoPush(
  to: string,
  title: string,
  body: string,
  data?: Record<string, unknown>,
): Promise<void> {
  await fetch("https://exp.host/--/api/v2/push/send", {
    method: "POST",
    headers: {
      "Content-Type": "application/json",
      Accept: "application/json",
    },
    body: JSON.stringify({ to, title, body, data, sound: "default" }),
  });
}
