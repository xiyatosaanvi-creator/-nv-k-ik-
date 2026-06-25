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
  TextInput,
  View,
} from "react-native";
import { useSafeAreaInsets } from "react-native-safe-area-context";

import { useColors } from "@/hooks/useColors";
import { mockApps, mockCategories } from "@/data/mockData";

const CATEGORIES = ["All", "Social", "Media", "Productivity", "Tools", "Transport", "Food"];

function AppIcon({ app }: { app: typeof mockApps[0] }) {
  const colors = useColors();
  return (
    <Pressable
      style={styles.appItemPressable}
      onPress={() => Haptics.impactAsync(Haptics.ImpactFeedbackStyle.Light)}
    >
      <View style={[styles.appIcon, { backgroundColor: app.color + "22" }]}>
        <Ionicons name={app.icon as any} size={28} color={app.color} />
      </View>
      <Text style={[styles.appName, { color: colors.sec }]} numberOfLines={1}>{app.name}</Text>
    </Pressable>
  );
}

export default function AppsScreen() {
  const colors = useColors();
  const insets = useSafeAreaInsets();
  const [selectedCat, setSelectedCat] = useState("All");
  const [search, setSearch] = useState("");

  const topPad = Platform.OS === "web" ? 67 : insets.top;
  const filtered = mockApps.filter(a =>
    (selectedCat === "All" || a.category === selectedCat) &&
    (search === "" || a.name.toLowerCase().includes(search.toLowerCase()))
  );

  return (
    <View style={[styles.root, { backgroundColor: colors.background }]}>
      {/* Header */}
      <View style={[styles.header, { paddingTop: topPad + 12 }]}>
        <Text style={[styles.title, { color: colors.foreground }]}>App Library</Text>
        <View style={[styles.searchBar, { backgroundColor: colors.card, borderColor: colors.border }]}>
          <Ionicons name="search" size={16} color={colors.mutedForeground} />
          <TextInput
            value={search}
            onChangeText={setSearch}
            placeholder="Search apps…"
            placeholderTextColor={colors.mutedForeground}
            style={[styles.searchInput, { color: colors.foreground }]}
          />
          {search.length > 0 && (
            <Pressable onPress={() => setSearch("")}>
              <Ionicons name="close-circle" size={16} color={colors.mutedForeground} />
            </Pressable>
          )}
        </View>
      </View>

      {/* Category chips */}
      <ScrollView
        horizontal
        showsHorizontalScrollIndicator={false}
        style={styles.chipRow}
        contentContainerStyle={{ paddingHorizontal: 20, gap: 8, paddingVertical: 4 }}
      >
        {CATEGORIES.map((cat) => (
          <Pressable
            key={cat}
            onPress={() => { setSelectedCat(cat); Haptics.selectionAsync(); }}
            style={[
              styles.chip,
              {
                backgroundColor: selectedCat === cat ? colors.primary : colors.card,
                borderColor: selectedCat === cat ? colors.primary : colors.border,
              },
            ]}
          >
            <Text style={[styles.chipText, { color: selectedCat === cat ? colors.primaryForeground : colors.mutedForeground }]}>
              {cat}
            </Text>
          </Pressable>
        ))}
      </ScrollView>

      {/* AI Insight Row */}
      <View style={[styles.insightRow, { backgroundColor: colors.card, borderColor: colors.border, marginHorizontal: 20 }]}>
        <Ionicons name="sparkles" size={16} color={colors.primary} />
        <Text style={[styles.insightText, { color: colors.foreground }]}>
          <Text style={{ color: colors.primary }}>5 apps</Text> haven't been opened in 60+ days
        </Text>
        <Pressable>
          <Text style={{ color: colors.blue, fontSize: 12, fontFamily: "Inter_500Medium" }}>Clean</Text>
        </Pressable>
      </View>

      {/* App Grid */}
      <FlatList
        data={filtered}
        keyExtractor={(item) => item.id}
        numColumns={4}
        contentContainerStyle={{ paddingHorizontal: 20, paddingTop: 8, paddingBottom: 120 }}
        columnWrapperStyle={{ justifyContent: "space-between" }}
        renderItem={({ item }) => <AppIcon app={item} />}
        ListEmptyComponent={() => (
          <View style={styles.emptyState}>
            <Ionicons name="apps" size={40} color={colors.mutedForeground} />
            <Text style={{ color: colors.mutedForeground, marginTop: 12, fontFamily: "Inter_400Regular" }}>
              No apps found
            </Text>
          </View>
        )}
        showsVerticalScrollIndicator={false}
      />
    </View>
  );
}

const styles = StyleSheet.create({
  root: { flex: 1 },
  header: { paddingHorizontal: 20, paddingBottom: 12, gap: 12 },
  title: { fontSize: 28, fontFamily: "Inter_700Bold" },
  searchBar: { flexDirection: "row", alignItems: "center", gap: 8, borderRadius: 12, borderWidth: 1, paddingHorizontal: 12, paddingVertical: 10 },
  searchInput: { flex: 1, fontSize: 14, fontFamily: "Inter_400Regular" },
  chipRow: { flexGrow: 0, marginBottom: 12 },
  chip: { paddingHorizontal: 14, paddingVertical: 7, borderRadius: 20, borderWidth: 1 },
  chipText: { fontSize: 13, fontFamily: "Inter_500Medium" },
  insightRow: { flexDirection: "row", alignItems: "center", gap: 10, borderRadius: 12, borderWidth: 1, padding: 12, marginBottom: 16 },
  insightText: { flex: 1, fontSize: 13, fontFamily: "Inter_400Regular" },
  appItemPressable: { width: "25%", alignItems: "center", marginBottom: 20, paddingHorizontal: 4 },
  appIcon: { width: 60, height: 60, borderRadius: 16, alignItems: "center", justifyContent: "center", marginBottom: 6 },
  appName: { fontSize: 11, fontFamily: "Inter_400Regular", textAlign: "center" },
  emptyState: { alignItems: "center", paddingTop: 60 },
});
