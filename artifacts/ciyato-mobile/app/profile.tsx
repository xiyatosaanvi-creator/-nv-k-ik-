import { Ionicons } from "@expo/vector-icons";
import { router } from "expo-router";
import React from "react";
import { ActivityIndicator, Pressable, ScrollView, StyleSheet, Text, View } from "react-native";
import { useSafeAreaInsets } from "react-native-safe-area-context";
import { useAuth } from "@/context/AuthContext";
import { useSettings, useUpdateSettings } from "@/hooks/useSettings";
import { useUsageSummary } from "@/hooks/useAnalytics";
import { useColors } from "@/hooks/useColors";

export default function ProfileScreen() {
  const colors = useColors();
  const insets = useSafeAreaInsets();
  const { user, isAuthenticated, logout } = useAuth();
  const { data: settings } = useSettings();
  const { data: summary } = useUsageSummary();
  const { mutate: updateSettings } = useUpdateSettings();

  if (!isAuthenticated) {
    return (
      <View style={[styles.center, { backgroundColor: colors.background }]}>
        <Ionicons name="person-circle-outline" size={64} color={colors.mutedForeground} />
        <Text style={[styles.unauthTitle, { color: colors.foreground }]}>Not signed in</Text>
        <Text style={[styles.unauthSub, { color: colors.mutedForeground }]}>
          Sign in to sync your settings and view insights across devices
        </Text>
        <Pressable style={styles.loginBtn} onPress={() => router.push("/(auth)/login")}>
          <Text style={styles.loginBtnText}>Sign In / Register</Text>
        </Pressable>
      </View>
    );
  }

  return (
    <ScrollView
      style={[styles.root, { backgroundColor: colors.background }]}
      contentContainerStyle={{ paddingBottom: 40 }}
    >
      <View style={[styles.header, { paddingTop: insets.top + 20 }]}>
        <View style={[styles.avatar, { backgroundColor: "#C6A15B22" }]}>
          <Text style={styles.avatarText}>
            {(user?.displayName ?? user?.email ?? "?")[0].toUpperCase()}
          </Text>
        </View>
        <Text style={[styles.name, { color: colors.foreground }]}>
          {user?.displayName ?? "Ciyato User"}
        </Text>
        <Text style={[styles.email, { color: colors.mutedForeground }]}>{user?.email}</Text>
      </View>

      {summary && (
        <View style={styles.section}>
          <Text style={[styles.sectionTitle, { color: colors.mutedForeground }]}>USAGE INSIGHTS</Text>
          <View style={[styles.card, { backgroundColor: colors.card, borderColor: colors.border }]}>
            <Stat label="Total Launches" value={String(summary.totalLaunches)} />
            <Stat label="Screen Time" value={`${summary.totalUsageHours}h`} />
            <Stat label="Apps Tracked" value={String(summary.appCount)} />
          </View>
          {summary.topApps.length > 0 && (
            <>
              <Text style={[styles.sectionTitle, { color: colors.mutedForeground, marginTop: 16 }]}>
                TOP APPS
              </Text>
              <View style={[styles.card, { backgroundColor: colors.card, borderColor: colors.border }]}>
                {summary.topApps.map((app, i) => (
                  <View key={app.packageName} style={styles.topAppRow}>
                    <Text style={[styles.topAppRank, { color: colors.mutedForeground }]}>{i + 1}</Text>
                    <View style={{ flex: 1 }}>
                      <Text style={[styles.topAppName, { color: colors.foreground }]}>{app.appLabel}</Text>
                      <Text style={[styles.topAppCat, { color: colors.mutedForeground }]}>{app.category}</Text>
                    </View>
                    <Text style={[styles.topAppCount, { color: colors.primary }]}>
                      {app.launchCount}x
                    </Text>
                  </View>
                ))}
              </View>
            </>
          )}
        </View>
      )}

      {settings && (
        <View style={styles.section}>
          <Text style={[styles.sectionTitle, { color: colors.mutedForeground }]}>PREFERENCES</Text>
          <View style={[styles.card, { backgroundColor: colors.card, borderColor: colors.border }]}>
            <SettingRow
              label="Theme"
              value={settings.theme}
              onPress={() =>
                updateSettings({ theme: settings.theme === "dark" ? "light" : "dark" })
              }
            />
            <SettingRow label="Grid Density" value={settings.gridDensity} />
            <SettingRow label="Icon Shape" value={settings.iconShape} />
            <SettingRow label="Haptic Intensity" value={settings.hapticIntensity} />
          </View>
        </View>
      )}

      <View style={styles.section}>
        <Pressable
          style={[styles.logoutBtn, { borderColor: "#EF4444" }]}
          onPress={async () => { await logout(); router.replace("/(tabs)"); }}
        >
          <Ionicons name="log-out-outline" size={18} color="#EF4444" />
          <Text style={{ color: "#EF4444", fontWeight: "600" }}>Sign Out</Text>
        </Pressable>
      </View>
    </ScrollView>
  );
}

function Stat({ label, value }: { label: string; value: string }) {
  const colors = useColors();
  return (
    <View style={styles.statRow}>
      <Text style={[styles.statLabel, { color: colors.mutedForeground }]}>{label}</Text>
      <Text style={[styles.statValue, { color: colors.foreground }]}>{value}</Text>
    </View>
  );
}

function SettingRow({ label, value, onPress }: { label: string; value: string; onPress?: () => void }) {
  const colors = useColors();
  return (
    <Pressable style={styles.settingRow} onPress={onPress}>
      <Text style={[styles.settingLabel, { color: colors.foreground }]}>{label}</Text>
      <Text style={[styles.settingValue, { color: colors.mutedForeground }]}>{value}</Text>
    </Pressable>
  );
}

const styles = StyleSheet.create({
  root: { flex: 1 },
  center: { flex: 1, alignItems: "center", justifyContent: "center", padding: 32, gap: 12 },
  unauthTitle: { fontSize: 20, fontWeight: "700", marginTop: 8 },
  unauthSub: { textAlign: "center", lineHeight: 20 },
  loginBtn: { backgroundColor: "#C6A15B", paddingHorizontal: 32, paddingVertical: 14, borderRadius: 14, marginTop: 8 },
  loginBtnText: { color: "#000", fontWeight: "700", fontSize: 15 },
  header: { alignItems: "center", paddingBottom: 24, gap: 8 },
  avatar: { width: 72, height: 72, borderRadius: 36, alignItems: "center", justifyContent: "center" },
  avatarText: { fontSize: 28, fontWeight: "800", color: "#C6A15B" },
  name: { fontSize: 20, fontWeight: "700" },
  email: { fontSize: 14 },
  section: { paddingHorizontal: 20, marginBottom: 8 },
  sectionTitle: { fontSize: 12, fontWeight: "600", letterSpacing: 0.8, marginBottom: 8 },
  card: { borderRadius: 14, borderWidth: 1, overflow: "hidden" },
  statRow: { flexDirection: "row", justifyContent: "space-between", paddingHorizontal: 16, paddingVertical: 12, borderBottomWidth: StyleSheet.hairlineWidth, borderColor: "transparent" },
  statLabel: { fontSize: 14 },
  statValue: { fontSize: 14, fontWeight: "700" },
  topAppRow: { flexDirection: "row", alignItems: "center", gap: 12, paddingHorizontal: 16, paddingVertical: 12 },
  topAppRank: { fontSize: 14, width: 20 },
  topAppName: { fontSize: 14, fontWeight: "600" },
  topAppCat: { fontSize: 12 },
  topAppCount: { fontSize: 14, fontWeight: "700" },
  settingRow: { flexDirection: "row", justifyContent: "space-between", alignItems: "center", paddingHorizontal: 16, paddingVertical: 14 },
  settingLabel: { fontSize: 15 },
  settingValue: { fontSize: 14, textTransform: "capitalize" },
  logoutBtn: { flexDirection: "row", alignItems: "center", justifyContent: "center", gap: 8, paddingVertical: 14, borderRadius: 14, borderWidth: 1 },
});
