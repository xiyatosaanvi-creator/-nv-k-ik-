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
import Animated, { useSharedValue, useAnimatedStyle, withSpring } from "react-native-reanimated";

import { useColors } from "@/hooks/useColors";
import { mockFileCategories, mockFiles } from "@/data/mockData";

function StorageRing({ used, total }: { used: number; total: number }) {
  const colors = useColors();
  const pct = (used / total) * 100;
  const radius = 50;
  const circumference = 2 * Math.PI * radius;
  const dashOffset = circumference * (1 - pct / 100);

  return (
    <View style={styles.ringContainer}>
      <View style={[styles.ringBg, { borderColor: colors.border }]}>
        <View style={[styles.ringFill, { width: `${pct}%`, backgroundColor: colors.primary }]} />
      </View>
      <View style={styles.ringCenter}>
        <Text style={[styles.ringPct, { color: colors.primary }]}>{Math.round(pct)}%</Text>
        <Text style={[styles.ringLabel, { color: colors.mutedForeground }]}>Used</Text>
      </View>
    </View>
  );
}

function CategoryCard({ cat }: { cat: typeof mockFileCategories[0] }) {
  const colors = useColors();
  const scale = useSharedValue(1);
  const animStyle = useAnimatedStyle(() => ({ transform: [{ scale: scale.value }] }));

  return (
    <Pressable
      style={{ flex: 1 }}
      onPressIn={() => { scale.value = withSpring(0.94); Haptics.impactAsync(Haptics.ImpactFeedbackStyle.Light); }}
      onPressOut={() => { scale.value = withSpring(1); }}
    >
      <Animated.View style={[styles.catCard, animStyle, { backgroundColor: colors.card, borderColor: colors.border }]}>
        <View style={[styles.catIcon, { backgroundColor: cat.color + "22" }]}>
          <Ionicons name={cat.icon as any} size={22} color={cat.color} />
        </View>
        <Text style={[styles.catName, { color: colors.foreground }]}>{cat.name}</Text>
        <Text style={[styles.catSize, { color: colors.primary }]}>{cat.size}</Text>
        <Text style={[styles.catCount, { color: colors.mutedForeground }]}>{cat.count} items</Text>
      </Animated.View>
    </Pressable>
  );
}

function fileIcon(type: string, color: string) {
  const map: Record<string, any> = {
    pdf: "document",
    spreadsheet: "grid",
    archive: "archive",
    document: "document-text",
    presentation: "easel",
  };
  return map[type] ?? "document";
}

export default function FilesScreen() {
  const colors = useColors();
  const insets = useSafeAreaInsets();
  const topPad = Platform.OS === "web" ? 67 : insets.top;

  return (
    <View style={[styles.root, { backgroundColor: colors.background }]}>
      <ScrollView
        contentContainerStyle={{ paddingTop: topPad + 12, paddingBottom: 120 }}
        showsVerticalScrollIndicator={false}
      >
        {/* Header */}
        <View style={styles.header}>
          <Text style={[styles.title, { color: colors.foreground }]}>Ciyato Files</Text>
          <Pressable style={[styles.iconBtn, { backgroundColor: colors.card, borderColor: colors.border }]}>
            <Ionicons name="add" size={20} color={colors.foreground} />
          </Pressable>
        </View>

        {/* Storage overview */}
        <View style={[styles.storageCard, { backgroundColor: colors.card, borderColor: colors.border }]}>
          <View style={styles.storageInfo}>
            <Text style={[styles.storageTitle, { color: colors.foreground }]}>128 GB Total</Text>
            <Text style={[styles.storageSub, { color: colors.mutedForeground }]}>34.8 GB used · 93.2 GB free</Text>
            <View style={{ marginTop: 14, gap: 6 }}>
              {[
                { label: "Photos", pct: 52, color: colors.blue },
                { label: "Videos", pct: 35, color: "#E1306C" },
                { label: "Apps", pct: 29, color: "#5E5CE6" },
                { label: "Other", pct: 12, color: colors.primary },
              ].map(({ label, pct, color }) => (
                <View key={label} style={styles.barRow}>
                  <View style={[styles.dot, { backgroundColor: color }]} />
                  <Text style={[styles.barLabel, { color: colors.mutedForeground }]}>{label}</Text>
                  <View style={[styles.miniBar, { backgroundColor: colors.secondary }]}>
                    <View style={[styles.miniBarFill, { width: `${pct}%`, backgroundColor: color }]} />
                  </View>
                </View>
              ))}
            </View>
          </View>
        </View>

        {/* AI Cleanup Banner */}
        <Pressable style={[styles.cleanBanner, { backgroundColor: colors.primary + "18", borderColor: colors.primary + "40" }]}>
          <Ionicons name="sparkles" size={18} color={colors.primary} />
          <View style={{ flex: 1 }}>
            <Text style={[styles.cleanTitle, { color: colors.foreground }]}>AI Cleanup Ready</Text>
            <Text style={[styles.cleanSub, { color: colors.mutedForeground }]}>Free up 8.4 GB with smart suggestions</Text>
          </View>
          <Ionicons name="chevron-forward" size={18} color={colors.primary} />
        </Pressable>

        {/* Categories */}
        <Text style={[styles.sectionTitle, { color: colors.foreground }]}>Categories</Text>
        <View style={styles.catGrid}>
          <View style={styles.catRow}>
            {mockFileCategories.slice(0, 3).map((cat) => <CategoryCard key={cat.id} cat={cat} />)}
          </View>
          <View style={styles.catRow}>
            {mockFileCategories.slice(3).map((cat) => <CategoryCard key={cat.id} cat={cat} />)}
          </View>
        </View>

        {/* Recent Files */}
        <View style={styles.sectionHeader}>
          <Text style={[styles.sectionTitle, { color: colors.foreground, marginHorizontal: 0 }]}>Recent Files</Text>
          <Text style={{ color: colors.blue, fontSize: 13, fontFamily: "Inter_500Medium" }}>See all</Text>
        </View>
        <View style={{ paddingHorizontal: 20 }}>
          {mockFiles.map((file) => (
            <Pressable
              key={file.id}
              style={[styles.fileRow, { backgroundColor: colors.card, borderColor: colors.border }]}
              onPress={() => Haptics.impactAsync(Haptics.ImpactFeedbackStyle.Light)}
            >
              <View style={[styles.fileIcon, { backgroundColor: colors.secondary }]}>
                <Ionicons name={fileIcon(file.type, colors.primary)} size={20} color={colors.primary} />
              </View>
              <View style={{ flex: 1 }}>
                <Text style={[styles.fileName, { color: colors.foreground }]} numberOfLines={1}>{file.name}</Text>
                <Text style={[styles.fileMeta, { color: colors.mutedForeground }]}>{file.size} · {file.modified}</Text>
              </View>
              <Ionicons name="ellipsis-horizontal" size={18} color={colors.mutedForeground} />
            </Pressable>
          ))}
        </View>
      </ScrollView>
    </View>
  );
}

const styles = StyleSheet.create({
  root: { flex: 1 },
  header: { flexDirection: "row", alignItems: "center", justifyContent: "space-between", paddingHorizontal: 20, marginBottom: 18 },
  title: { fontSize: 28, fontFamily: "Inter_700Bold" },
  iconBtn: { width: 38, height: 38, borderRadius: 12, borderWidth: 1, alignItems: "center", justifyContent: "center" },
  storageCard: { marginHorizontal: 20, marginBottom: 12, borderRadius: 16, borderWidth: 1, padding: 16 },
  storageInfo: {},
  storageTitle: { fontSize: 17, fontFamily: "Inter_700Bold" },
  storageSub: { fontSize: 12, fontFamily: "Inter_400Regular", marginTop: 3 },
  barRow: { flexDirection: "row", alignItems: "center", gap: 8 },
  dot: { width: 8, height: 8, borderRadius: 4 },
  barLabel: { fontSize: 12, fontFamily: "Inter_400Regular", width: 52 },
  miniBar: { flex: 1, height: 5, borderRadius: 3, overflow: "hidden" },
  miniBarFill: { height: 5, borderRadius: 3 },
  ringContainer: { alignItems: "center", justifyContent: "center", position: "relative" },
  ringBg: { width: 100, height: 14, borderRadius: 7, overflow: "hidden", borderWidth: 0, backgroundColor: "rgba(255,255,255,0.08)" },
  ringFill: { height: 14, borderRadius: 7 },
  ringCenter: { alignItems: "center" },
  ringPct: { fontSize: 24, fontFamily: "Inter_700Bold" },
  ringLabel: { fontSize: 11, fontFamily: "Inter_400Regular" },
  cleanBanner: { marginHorizontal: 20, marginBottom: 18, borderRadius: 14, borderWidth: 1, padding: 14, flexDirection: "row", alignItems: "center", gap: 12 },
  cleanTitle: { fontSize: 14, fontFamily: "Inter_600SemiBold" },
  cleanSub: { fontSize: 12, fontFamily: "Inter_400Regular", marginTop: 2 },
  sectionTitle: { fontSize: 17, fontFamily: "Inter_700Bold", marginHorizontal: 20, marginBottom: 12 },
  catGrid: { paddingHorizontal: 20, gap: 10, marginBottom: 20 },
  catRow: { flexDirection: "row", gap: 10 },
  catCard: { flex: 1, borderRadius: 14, borderWidth: 1, padding: 14, gap: 4 },
  catIcon: { width: 38, height: 38, borderRadius: 11, alignItems: "center", justifyContent: "center", marginBottom: 4 },
  catName: { fontSize: 13, fontFamily: "Inter_600SemiBold" },
  catSize: { fontSize: 14, fontFamily: "Inter_700Bold" },
  catCount: { fontSize: 11, fontFamily: "Inter_400Regular" },
  sectionHeader: { flexDirection: "row", alignItems: "center", justifyContent: "space-between", paddingHorizontal: 20, marginBottom: 12 },
  fileRow: { flexDirection: "row", alignItems: "center", gap: 12, borderRadius: 12, borderWidth: 1, padding: 12, marginBottom: 8 },
  fileIcon: { width: 40, height: 40, borderRadius: 11, alignItems: "center", justifyContent: "center" },
  fileName: { fontSize: 13, fontFamily: "Inter_500Medium" },
  fileMeta: { fontSize: 11, fontFamily: "Inter_400Regular", marginTop: 2 },
});
