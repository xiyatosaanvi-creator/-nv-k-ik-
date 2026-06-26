import AsyncStorage from "@react-native-async-storage/async-storage";

const BASE_URL = process.env["EXPO_PUBLIC_API_URL"] ?? "http://localhost:3000/api/v1";

async function getToken(): Promise<string | null> {
  return AsyncStorage.getItem("auth_token");
}

async function request<T>(
  path: string,
  options: RequestInit = {},
): Promise<T> {
  const token = await getToken();
  const headers: Record<string, string> = {
    "Content-Type": "application/json",
    ...(options.headers as Record<string, string>),
  };

  if (token) {
    headers["Authorization"] = `Bearer ${token}`;
  }

  const res = await fetch(`${BASE_URL}${path}`, { ...options, headers });

  if (!res.ok) {
    const body = await res.json().catch(() => ({ error: { message: "Unknown error" } }));
    throw new ApiError(res.status, body?.error?.code ?? "REQUEST_FAILED", body?.error?.message ?? "Request failed");
  }

  return res.json() as Promise<T>;
}

export class ApiError extends Error {
  constructor(
    public status: number,
    public code: string,
    message: string,
  ) {
    super(message);
    this.name = "ApiError";
  }
}

export const authApi = {
  register: (email: string, password: string, displayName?: string) =>
    request<{ user: User; token: string }>("/auth/register", {
      method: "POST",
      body: JSON.stringify({ email, password, displayName }),
    }),

  login: (email: string, password: string) =>
    request<{ user: User; token: string }>("/auth/login", {
      method: "POST",
      body: JSON.stringify({ email, password }),
    }),

  me: () =>
    request<{ user: User }>("/auth/me"),

  updateProfile: (data: { displayName?: string }) =>
    request<{ user: User }>("/auth/me", {
      method: "PATCH",
      body: JSON.stringify(data),
    }),
};

export const settingsApi = {
  get: () =>
    request<{ settings: UserSettings }>("/settings"),

  update: (data: Partial<UserSettings>) =>
    request<{ settings: UserSettings }>("/settings", {
      method: "PATCH",
      body: JSON.stringify(data),
    }),
};

export const devicesApi = {
  list: () =>
    request<{ devices: Device[] }>("/devices"),

  register: (data: Omit<Device, "id" | "userId" | "createdAt" | "lastSeenAt">) =>
    request<{ device: Device }>("/devices", {
      method: "POST",
      body: JSON.stringify(data),
    }),

  update: (deviceId: string, data: Partial<Device>) =>
    request<{ device: Device }>(`/devices/${deviceId}`, {
      method: "PATCH",
      body: JSON.stringify(data),
    }),

  remove: (deviceId: string) =>
    request<void>(`/devices/${deviceId}`, { method: "DELETE" }),
};

export const analyticsApi = {
  postUsage: (deviceId: string, events: AppUsageEvent[]) =>
    request<{ accepted: number }>("/analytics/usage", {
      method: "POST",
      body: JSON.stringify({ deviceId, events }),
    }),

  getUsage: (limit?: number) =>
    request<{ usage: AppUsageEvent[] }>(`/analytics/usage?limit=${limit ?? 50}`),

  getSummary: () =>
    request<{ summary: UsageSummary }>("/analytics/usage/summary"),
};

export interface User {
  id: string;
  email: string;
  displayName: string | null;
  createdAt: string;
  updatedAt: string;
}

export interface UserSettings {
  id: string;
  userId: string;
  theme: string;
  accentColor: string;
  iconShape: string;
  gridDensity: string;
  hapticIntensity: string;
  bedtimeEnabled: boolean;
  bedtimeStart: string;
  bedtimeEnd: string;
  focusCategories: string[];
  hiddenApps: string[];
  pinnedApps: string[];
  customGreeting: string | null;
  updatedAt: string;
}

export interface Device {
  id: string;
  userId: string;
  deviceName: string;
  deviceModel?: string | null;
  androidVersion?: string | null;
  appVersion?: string | null;
  pushToken?: string | null;
  metadata?: Record<string, unknown> | null;
  lastSeenAt: string;
  createdAt: string;
}

export interface AppUsageEvent {
  packageName: string;
  appLabel: string;
  category: string;
  launchCount: number;
  totalUsageMs: number;
  lastLaunchedAt?: string | null;
  dailyStats?: Record<string, number>;
}

export interface UsageSummary {
  totalLaunches: number;
  totalUsageMs: number;
  totalUsageHours: number;
  topApps: Array<{
    packageName: string;
    appLabel: string;
    category: string;
    launchCount: number;
    totalUsageMs: number;
  }>;
  appCount: number;
}
