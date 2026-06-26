import { Ionicons } from "@expo/vector-icons";
import * as Haptics from "expo-haptics";
import React, { useState } from "react";
import {
  Platform,
  Pressable,
  ScrollView,
  StyleSheet,
  Text,
  View,
} from "react-native";
import { useSafeAreaInsets } from "react-native-safe-area-context";
import Animated, {
  useSharedValue,
  useAnimatedStyle,
  withSpring,
} from "react-native-reanimated";

import { useColors } from "@/hooks/useColors";

const dockApps = [
  { name: "Phone", icon: "call" as const, color: "#39C66A" },
  { name: "Messages", icon: "chatbubble" as const, color: "#7DB7FF" },
  { name: "Camera", icon: "camera" as const, color: "#FF9500" },
  { name: "Settings", icon: "settings" as const, color: "#6E6E73" },
];

const shortcuts = [
  { label: "AI Organize", icon: "sparkles" as const, color: "#C6A15B", desc: "14 suggestions" },
  { label: "Clean Up", icon: "trash" as const, color: "#7DB7FF", desc: "2.3 GB free" },
  { label: "Duplicates", icon: "copy" as const, color: "#E1306C", desc: "8 found" },
  { label: "Smart Album", icon: "albums" as const, color: "#39C66A", desc: "Today's best" },
];

function AnimatedDockItem({ app }: { app: typeof dockApps[0] }) {
  const colors = useColors();
  const scale = useSharedValue(1);

  const animatedStyle = useAnimatedStyle(() => ({
    transform: [{ scale: scale.value }],
  }));

  return (
    <Pressable
      onPressIn={() => {
        scale.value = withSpring(0.88);
        Haptics.impactAsync(Haptics.ImpactFeedbackStyle.Light);
      }}
      onPressOut={() => { scale.value = withSpring(1); }}
    >
      <Animated.View style={[styles.dockItem, animatedStyle, { backgroundColor: app.color + "22" }]}>
        <Ionicons name={app.icon} size={26} color={app.color} />
      </Animated.View>
    </Pressable>
  );
}

function ShortcutCard({ item }: { item: typeof shortcuts[0] }) {
  const colors = useColors();
  const scale = useSharedValue(1);
  const animatedStyle = useAnimatedStyle(() => ({ transform: [{ scale: scale.value }] }));

  return (
    <Pressable
      onPressIn={() => { scale.value = withSpring(0.94); Haptics.impactAsync(Haptics.ImpactFeedbackStyle.Light); }}
      onPressOut={() => { scale.value = withSpring(1); }}
      style={{ flex: 1 }}
    >
      <Animated.View style={[styles.shortcutCard, animatedStyle, { backgroundColor: colors.card, borderColor: colors.border }]}>
        <View style={[styles.shortcutIcon, { backgroundColor: item.color + "22" }]}>
          <Ionicons name={item.icon} size={20} color={item.color} />
        </View>
        <Text style={[styles.shortcutLabel, { color: colors.foreground }]}>{item.label}</Text>
        <Text style={[styles.shortcutDesc, { color: colors.mutedForeground }]}>{item.desc}</Text>
      </Animated.View>
    </Pressable>
  );
}

export default function HomeScreen() {
  const colors = useColors();
  const insets = useSafeAreaInsets();
  const [aiMode, setAiMode] = useState(false);

  const topPad = Platform.OS === "web" ? 67 : insets.top;

  return (
    <View style={[styles.root, { backgroundColor: colors.background }]}>
      <ScrollView
        style={{ flex: 1 }}
        contentContainerStyle={{ paddingTop: topPad + 12, paddingBottom: 120 }}
        showsVerticalScrollIndicator={false}
      >
        {/* Header */}
        <View style={styles.header}>
          <View>
            <Text style={[styles.greeting, { color: colors.mutedForeground }]}>Good morning</Text>
            <Text style={[styles.title, { color: colors.foreground }]}>Ciyato</Text>
          </View>
          <Pressable style={[styles.aiBtn, { backgroundColor: colors.primary + "22", borderColor: colors.primary + "44" }]}>
            <Ionicons name="sparkles" size={16} color={colors.primary} />
            <Text style={[styles.aiBtnText, { color: colors.primary }]}>AI Active</Text>
          </Pressable>
        </View>

        {/* Storage Summary Card */}
        <View style={[styles.storageCard, { backgroundColor: colors.card, borderColor: colors.border }]}>
          <View style={styles.storageLeft}>
            <Text style={[styles.storageTitle, { color: colors.foreground }]}>Phone Storage</Text>
            <Text style={[styles.storageUsed, { color: colors.mutedForeground }]}>34.8 GB used of 128 GB</Text>
            <View style={styles.storageBar}>
              <View style={[styles.storageBarFill, { width: "27%", backgroundColor: colors.primary }]} />
            </View>
          </View>
          <View style={styles.storageRight}>
            <View style={styles.storageStat}>
              <Text style={[styles.storageStatVal, { color: colors.primary }]}>27%</Text>
              <Text style={[styles.storageStatLabel, { color: colors.mutedForeground }]}>Used</Text>
            </View>
          </View>
        </View>

        {/* AI Summary Banner */}
        <View style={[styles.aiBanner, { backgroundColor: colors.primary + "15", borderColor: colors.primary + "30" }]}>
          <Ionicons name="sparkles" size={18} color={colors.primary} />
          <Text style={[styles.aiBannerText, { color: colors.foreground }]}>
            Ciyato found <Text style={{ color: colors.primary }}>8 duplicates</Text> and{" "}
            <Text style={{ color: colors.blue }}>5 unused apps</Text> to clean up
          </Text>
          <Ionicons name="chevron-forward" size={16} color={colors.mutedForeground} />
        </View>

        {/* Quick Actions */}
        <Text style={[styles.sectionTitle, { color: colors.foreground }]}>Quick Actions</Text>
        <View style={styles.shortcutGrid}>
          <View style={styles.shortcutRow}>
            {shortcuts.slice(0, 2).map((item) => (
              <ShortcutCard key={item.label} item={item} />
            ))}
          </View>
          <View style={styles.shortcutRow}>
            {shortcuts.slice(2).map((item) => (
              <ShortcutCard key={item.label} item={item} />
            ))}
          </View>
        </View>

        {/* Recent Activity */}
        <Text style={[styles.sectionTitle, { color: colors.foreground }]}>Recent</Text>
        {[
          { icon: "image-outline" as const, label: "87 new photos organized", time: "2m ago", color: colors.blue },
          { icon: "trash-outline" as const, label: "Cleaned 1.2 GB from Downloads", time: "1h ago", color: colors.green },
          { icon: "copy-outline" as const, label: "3 duplicate apps detected", time: "3h ago", color: "#E1306C" },
        ].map((item) => (
          <View key={item.label} style={[styles.activityRow, { backgroundColor: colors.card, borderColor: colors.border }]}>
            <View style={[styles.activityIcon, { backgroundColor: item.color + "22" }]}>
              <Ionicons name={item.icon} size={18} color={item.color} />
            </View>
            <View style={{ flex: 1 }}>
              <Text style={[styles.activityLabel, { color: colors.foreground }]}>{item.label}</Text>
            </View>
            <Text style={[styles.activityTime, { color: colors.mutedForeground }]}>{item.time}</Text>
          </View>
        ))}

        {/* Weather + Time Widget */}
        <View style={styles.widgetRow}>
          <View style={[styles.weatherWidget, { backgroundColor: colors.card, borderColor: colors.border }]}>
            <Ionicons name="partly-sunny" size={28} color="#FFD60A" />
            <Text style={[styles.tempText, { color: colors.foreground }]}>72°</Text>
            <Text style={[styles.weatherDesc, { color: colors.mutedForeground }]}>Partly Cloudy</Text>
          </View>
          <View style={[styles.agendaWidget, { backgroundColor: colors.card, borderColor: colors.border }]}>
            <Text style={[styles.agendaTitle, { color: colors.mutedForeground }]}>Next up</Text>
            <Text style={[styles.agendaEvent, { color: colors.foreground }]}>Team Standup</Text>
            <Text style={[styles.agendaTime, { color: colors.primary }]}>10:00 AM</Text>
          </View>
        </View>
      </ScrollView>

      {/* Dock */}
      <View style={[styles.dock, { bottom: Platform.OS === "web" ? 84 + 16 : insets.bottom + 80, borderColor: colors.border }]}>
        {dockApps.map((app) => (
          <AnimatedDockItem key={app.name} app={app} />
        ))}
      </View>
    </View>
  );
}

const styles = StyleSheet.create({
  root: { flex: 1 },
  header: { flexDirection: "row", justifyContent: "space-between", alignItems: "center", paddingHorizontal: 20, marginBottom: 20 },
  greeting: { fontSize: 13, fontFamily: "Inter_400Regular" },
  title: { fontSize: 28, fontFamily: "Inter_700Bold", marginTop: 2 },
  aiBtn: { flexDirection: "row", alignItems: "center", gap: 6, paddingHorizontal: 12, paddingVertical: 7, borderRadius: 20, borderWidth: 1 },
  aiBtnText: { fontSize: 12, fontFamily: "Inter_600SemiBold" },
  storageCard: { marginHorizontal: 20, marginBottom: 12, borderRadius: 16, borderWidth: 1, padding: 16, flexDirection: "row", alignItems: "center" },
  storageLeft: { flex: 1 },
  storageTitle: { fontSize: 15, fontFamily: "Inter_600SemiBold", marginBottom: 3 },
  storageUsed: { fontSize: 12, fontFamily: "Inter_400Regular", marginBottom: 10 },
  storageBar: { height: 6, backgroundColor: "rgba(255,255,255,0.08)", borderRadius: 3, overflow: "hidden" },
  storageBarFill: { height: 6, borderRadius: 3 },
  storageRight: { paddingLeft: 20 },
  storageStat: { alignItems: "center" },
  storageStatVal: { fontSize: 24, fontFamily: "Inter_700Bold" },
  storageStatLabel: { fontSize: 11, fontFamily: "Inter_400Regular" },
  aiBanner: { marginHorizontal: 20, marginBottom: 20, borderRadius: 12, borderWidth: 1, padding: 14, flexDirection: "row", alignItems: "center", gap: 10 },
  aiBannerText: { flex: 1, fontSize: 13, fontFamily: "Inter_400Regular", lineHeight: 18 },
  sectionTitle: { fontSize: 17, fontFamily: "Inter_700Bold", marginHorizontal: 20, marginBottom: 12, marginTop: 8 },
  shortcutGrid: { paddingHorizontal: 20, gap: 10, marginBottom: 20 },
  shortcutRow: { flexDirection: "row", gap: 10 },
  shortcutCard: { flex: 1, borderRadius: 14, borderWidth: 1, padding: 14, gap: 6 },
  shortcutIcon: { width: 36, height: 36, borderRadius: 10, alignItems: "center", justifyContent: "center" },
  shortcutLabel: { fontSize: 13, fontFamily: "Inter_600SemiBold" },
  shortcutDesc: { fontSize: 11, fontFamily: "Inter_400Regular" },
  activityRow: { marginHorizontal: 20, marginBottom: 8, borderRadius: 12, borderWidth: 1, padding: 12, flexDirection: "row", alignItems: "center", gap: 12 },
  activityIcon: { width: 36, height: 36, borderRadius: 10, alignItems: "center", justifyContent: "center" },
  activityLabel: { fontSize: 13, fontFamily: "Inter_400Regular" },
  activityTime: { fontSize: 11, fontFamily: "Inter_400Regular" },
  widgetRow: { flexDirection: "row", gap: 10, marginHorizontal: 20, marginTop: 12 },
  weatherWidget: { flex: 1, borderRadius: 16, borderWidth: 1, padding: 16, alignItems: "center", gap: 4 },
  tempText: { fontSize: 28, fontFamily: "Inter_700Bold" },
  weatherDesc: { fontSize: 11, fontFamily: "Inter_400Regular" },
  agendaWidget: { flex: 1, borderRadius: 16, borderWidth: 1, padding: 16, justifyContent: "center", gap: 4 },
  agendaTitle: { fontSize: 11, fontFamily: "Inter_500Medium" },
  agendaEvent: { fontSize: 15, fontFamily: "Inter_600SemiBold" },
  agendaTime: { fontSize: 13, fontFamily: "Inter_500Medium" },
  dock: { position: "absolute", left: 20, right: 20, flexDirection: "row", justifyContent: "space-around", padding: 14, backgroundColor: "rgba(26,32,40,0.92)", borderRadius: 24, borderWidth: 1 },
  dockItem: { width: 52, height: 52, borderRadius: 16, alignItems: "center", justifyContent: "center" },
});
