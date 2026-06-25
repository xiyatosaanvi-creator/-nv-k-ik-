import { Ionicons } from "@expo/vector-icons";
import * as Haptics from "expo-haptics";
import React, { useState } from "react";
import {
  FlatList,
  Platform,
  Pressable,
  ScrollView,
  StyleSheet,
  Text,
  View,
  Dimensions,
} from "react-native";
import { useSafeAreaInsets } from "react-native-safe-area-context";
import { LinearGradient } from "expo-linear-gradient";

import { useColors } from "@/hooks/useColors";
import { mockPhotoCollections } from "@/data/mockData";

const { width } = Dimensions.get("window");
const CARD_W = (width - 20 * 2 - 10) / 2;
const FILTERS = ["All", "Collections", "Recents", "Selfies", "Screenshots", "Videos"];

function CollectionCard({ col }: { col: typeof mockPhotoCollections[0] }) {
  const colors = useColors();

  return (
    <Pressable
      style={[styles.card, { width: CARD_W }]}
      onPress={() => Haptics.impactAsync(Haptics.ImpactFeedbackStyle.Light)}
    >
      <LinearGradient
        colors={col.gradient as [string, string]}
        style={styles.cardGradient}
        start={{ x: 0, y: 0 }}
        end={{ x: 1, y: 1 }}
      >
        <View style={styles.cardBadge}>
          <Text style={styles.cardBadgeText}>{col.count}</Text>
        </View>
        <View style={styles.cardBottom}>
          <Text style={styles.cardName}>{col.name}</Text>
          <Text style={styles.cardMonth}>{col.month}</Text>
        </View>
      </LinearGradient>
    </Pressable>
  );
}

export default function PhotosScreen() {
  const colors = useColors();
  const insets = useSafeAreaInsets();
  const [selectedFilter, setSelectedFilter] = useState("All");

  const topPad = Platform.OS === "web" ? 67 : insets.top;

  return (
    <View style={[styles.root, { backgroundColor: colors.background }]}>
      <ScrollView
        contentContainerStyle={{ paddingTop: topPad + 12, paddingBottom: 120 }}
        showsVerticalScrollIndicator={false}
      >
        {/* Header */}
        <View style={styles.header}>
          <Text style={[styles.title, { color: colors.foreground }]}>Photos</Text>
          <View style={{ flexDirection: "row", gap: 10 }}>
            <Pressable style={[styles.iconBtn, { backgroundColor: colors.card, borderColor: colors.border }]}>
              <Ionicons name="search" size={18} color={colors.foreground} />
            </Pressable>
            <Pressable style={[styles.iconBtn, { backgroundColor: colors.card, borderColor: colors.border }]}>
              <Ionicons name="options" size={18} color={colors.foreground} />
            </Pressable>
          </View>
        </View>

        {/* AI Badge */}
        <View style={[styles.aiBadge, { backgroundColor: colors.primary + "18", borderColor: colors.primary + "40" }]}>
          <Ionicons name="sparkles" size={16} color={colors.primary} />
          <Text style={[styles.aiBadgeText, { color: colors.foreground }]}>
            AI organized <Text style={{ color: colors.primary }}>2,847 photos</Text> into 24 smart collections
          </Text>
        </View>

        {/* Filter chips */}
        <ScrollView
          horizontal
          showsHorizontalScrollIndicator={false}
          contentContainerStyle={{ paddingHorizontal: 20, gap: 8, paddingVertical: 4, marginBottom: 12 }}
        >
          {FILTERS.map((f) => (
            <Pressable
              key={f}
              onPress={() => { setSelectedFilter(f); Haptics.selectionAsync(); }}
              style={[
                styles.chip,
                { backgroundColor: selectedFilter === f ? colors.primary : colors.card, borderColor: selectedFilter === f ? colors.primary : colors.border },
              ]}
            >
              <Text style={[styles.chipText, { color: selectedFilter === f ? colors.primaryForeground : colors.mutedForeground }]}>{f}</Text>
            </Pressable>
          ))}
        </ScrollView>

        {/* Stats row */}
        <View style={styles.statsRow}>
          {[
            { label: "Total", value: "2,847", color: colors.blue },
            { label: "Videos", value: "142", color: "#E1306C" },
            { label: "Duplicates", value: "38", color: colors.primary },
          ].map((s) => (
            <View key={s.label} style={[styles.statCard, { backgroundColor: colors.card, borderColor: colors.border }]}>
              <Text style={[styles.statVal, { color: s.color }]}>{s.value}</Text>
              <Text style={[styles.statLabel, { color: colors.mutedForeground }]}>{s.label}</Text>
            </View>
          ))}
        </View>

        {/* Collections Grid */}
        <Text style={[styles.sectionTitle, { color: colors.foreground }]}>Smart Collections</Text>
        <View style={styles.grid}>
          {mockPhotoCollections.map((col, i) => (
            <CollectionCard key={col.id} col={col} />
          ))}
        </View>

        {/* Memories section */}
        <View style={styles.sectionHeader}>
          <Text style={[styles.sectionTitle, { color: colors.foreground, marginHorizontal: 0 }]}>Memories</Text>
          <Text style={{ color: colors.blue, fontSize: 13, fontFamily: "Inter_500Medium" }}>See all</Text>
        </View>
        <ScrollView horizontal showsHorizontalScrollIndicator={false} contentContainerStyle={{ paddingHorizontal: 20, gap: 12 }}>
          {[
            { label: "Last Year", gradient: ["#7DB7FF", "#4A90D9"] as [string, string] },
            { label: "Summer Vibes", gradient: ["#FFD60A", "#FF9500"] as [string, string] },
            { label: "Best of 2023", gradient: ["#C6A15B", "#8B6914"] as [string, string] },
          ].map((m) => (
            <Pressable key={m.label} style={styles.memoryCard} onPress={() => Haptics.impactAsync(Haptics.ImpactFeedbackStyle.Light)}>
              <LinearGradient colors={m.gradient} style={styles.memoryGrad} start={{ x: 0, y: 0 }} end={{ x: 1, y: 1 }}>
                <Text style={styles.memoryLabel}>{m.label}</Text>
              </LinearGradient>
            </Pressable>
          ))}
        </ScrollView>
      </ScrollView>
    </View>
  );
}

const styles = StyleSheet.create({
  root: { flex: 1 },
  header: { flexDirection: "row", alignItems: "center", justifyContent: "space-between", paddingHorizontal: 20, marginBottom: 14 },
  title: { fontSize: 28, fontFamily: "Inter_700Bold" },
  iconBtn: { width: 38, height: 38, borderRadius: 12, borderWidth: 1, alignItems: "center", justifyContent: "center" },
  aiBadge: { marginHorizontal: 20, marginBottom: 14, borderRadius: 12, borderWidth: 1, padding: 12, flexDirection: "row", gap: 8, alignItems: "center" },
  aiBadgeText: { flex: 1, fontSize: 13, fontFamily: "Inter_400Regular" },
  chip: { paddingHorizontal: 14, paddingVertical: 7, borderRadius: 20, borderWidth: 1 },
  chipText: { fontSize: 13, fontFamily: "Inter_500Medium" },
  statsRow: { flexDirection: "row", gap: 10, paddingHorizontal: 20, marginBottom: 20 },
  statCard: { flex: 1, borderRadius: 12, borderWidth: 1, padding: 12, alignItems: "center" },
  statVal: { fontSize: 20, fontFamily: "Inter_700Bold" },
  statLabel: { fontSize: 11, fontFamily: "Inter_400Regular", marginTop: 2 },
  sectionTitle: { fontSize: 17, fontFamily: "Inter_700Bold", marginHorizontal: 20, marginBottom: 12 },
  grid: { flexDirection: "row", flexWrap: "wrap", paddingHorizontal: 20, gap: 10, marginBottom: 24 },
  card: { borderRadius: 16, overflow: "hidden", height: CARD_W * 1.2 },
  cardGradient: { flex: 1, padding: 12, justifyContent: "space-between" },
  cardBadge: { alignSelf: "flex-start", backgroundColor: "rgba(0,0,0,0.35)", paddingHorizontal: 8, paddingVertical: 3, borderRadius: 10 },
  cardBadgeText: { color: "#fff", fontSize: 11, fontFamily: "Inter_600SemiBold" },
  cardBottom: { gap: 2 },
  cardName: { color: "#fff", fontSize: 14, fontFamily: "Inter_700Bold" },
  cardMonth: { color: "rgba(255,255,255,0.7)", fontSize: 11, fontFamily: "Inter_400Regular" },
  sectionHeader: { flexDirection: "row", alignItems: "center", justifyContent: "space-between", paddingHorizontal: 20, marginBottom: 12 },
  memoryCard: { width: 160, height: 100, borderRadius: 16, overflow: "hidden" },
  memoryGrad: { flex: 1, alignItems: "flex-start", justifyContent: "flex-end", padding: 12 },
  memoryLabel: { color: "#fff", fontSize: 13, fontFamily: "Inter_700Bold" },
});
