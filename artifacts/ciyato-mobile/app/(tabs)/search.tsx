import { Ionicons } from "@expo/vector-icons";
import * as Haptics from "expo-haptics";
import React, { useState, useRef } from "react";
import {
  Keyboard,
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
import { searchSuggestions } from "@/data/mockData";

const RESULT_SECTIONS = [
  {
    title: "Top Match",
    icon: "star" as const,
    color: "#C6A15B",
    items: [
      { label: "Beach Trip — June 2024", sub: "124 photos · Smart Collection", icon: "images" as const },
    ],
  },
  {
    title: "Files",
    icon: "document-text" as const,
    color: "#7DB7FF",
    items: [
      { label: "Project Proposal.pdf", sub: "2.4 MB · Modified today", icon: "document" as const },
      { label: "Budget 2024.xlsx", sub: "1.1 MB · Modified yesterday", icon: "grid" as const },
    ],
  },
  {
    title: "Apps",
    icon: "apps" as const,
    color: "#39C66A",
    items: [
      { label: "Instagram", sub: "Social · Last opened 2h ago", icon: "logo-instagram" as const },
      { label: "Camera", sub: "Tools · Last opened 5m ago", icon: "camera" as const },
    ],
  },
  {
    title: "Photos",
    icon: "image" as const,
    color: "#E1306C",
    items: [
      { label: "87 photos this week", sub: "Organized automatically", icon: "images" as const },
      { label: "12 screenshots", sub: "From last 3 days", icon: "phone-portrait" as const },
    ],
  },
];

export default function SearchScreen() {
  const colors = useColors();
  const insets = useSafeAreaInsets();
  const [query, setQuery] = useState("");
  const [focused, setFocused] = useState(false);
  const inputRef = useRef<TextInput>(null);

  const topPad = Platform.OS === "web" ? 67 : insets.top;
  const hasQuery = query.trim().length > 0;

  return (
    <View style={[styles.root, { backgroundColor: colors.background }]}>
      {/* Search Header */}
      <View style={[styles.searchHeader, { paddingTop: topPad + 12, backgroundColor: colors.background }]}>
        <Text style={[styles.title, { color: colors.foreground }]}>AI Search</Text>
        <View style={[styles.searchBar, { backgroundColor: colors.card, borderColor: focused ? colors.primary : colors.border }]}>
          <Ionicons name="sparkles" size={16} color={colors.primary} />
          <TextInput
            ref={inputRef}
            value={query}
            onChangeText={setQuery}
            onFocus={() => setFocused(true)}
            onBlur={() => setFocused(false)}
            placeholder="Ask Ciyato anything…"
            placeholderTextColor={colors.mutedForeground}
            style={[styles.searchInput, { color: colors.foreground }]}
            returnKeyType="search"
          />
          {query.length > 0 ? (
            <Pressable onPress={() => setQuery("")}>
              <Ionicons name="close-circle" size={18} color={colors.mutedForeground} />
            </Pressable>
          ) : (
            <Ionicons name="mic" size={16} color={colors.mutedForeground} />
          )}
        </View>
      </View>

      <ScrollView
        style={{ flex: 1 }}
        contentContainerStyle={{ paddingBottom: 120 }}
        keyboardShouldPersistTaps="handled"
        showsVerticalScrollIndicator={false}
      >
        {!hasQuery ? (
          <>
            {/* Suggestions */}
            <Text style={[styles.sectionTitle, { color: colors.foreground }]}>Try asking…</Text>
            <View style={styles.pillGrid}>
              {searchSuggestions.map((s) => (
                <Pressable
                  key={s}
                  onPress={() => {
                    setQuery(s);
                    Haptics.impactAsync(Haptics.ImpactFeedbackStyle.Light);
                    inputRef.current?.focus();
                  }}
                  style={[styles.pill, { backgroundColor: colors.card, borderColor: colors.border }]}
                >
                  <Ionicons name="sparkles" size={12} color={colors.primary} />
                  <Text style={[styles.pillText, { color: colors.foreground }]}>{s}</Text>
                </Pressable>
              ))}
            </View>

            {/* Recent searches */}
            <Text style={[styles.sectionTitle, { color: colors.foreground }]}>Recent</Text>
            {["photos from last summer", "large videos", "work documents"].map((r) => (
              <Pressable
                key={r}
                style={[styles.recentRow, { borderColor: colors.border }]}
                onPress={() => { setQuery(r); Haptics.impactAsync(Haptics.ImpactFeedbackStyle.Light); }}
              >
                <View style={[styles.recentIcon, { backgroundColor: colors.card }]}>
                  <Ionicons name="time" size={16} color={colors.mutedForeground} />
                </View>
                <Text style={[styles.recentText, { color: colors.foreground }]}>{r}</Text>
                <Ionicons name="chevron-forward" size={16} color={colors.mutedForeground} />
              </Pressable>
            ))}
          </>
        ) : (
          <>
            {/* Results */}
            <View style={[styles.aiAnswerCard, { backgroundColor: colors.primary + "15", borderColor: colors.primary + "35" }]}>
              <Ionicons name="sparkles" size={18} color={colors.primary} />
              <View style={{ flex: 1, gap: 3 }}>
                <Text style={[styles.aiAnswerTitle, { color: colors.foreground }]}>Ciyato AI Answer</Text>
                <Text style={[styles.aiAnswerText, { color: colors.sec }]}>
                  Found results matching "{query}" across your phone's content.
                </Text>
              </View>
            </View>

            {RESULT_SECTIONS.map((section) => (
              <View key={section.title} style={{ marginBottom: 8 }}>
                <View style={styles.resultSectionHeader}>
                  <View style={[styles.resultSectionIcon, { backgroundColor: section.color + "22" }]}>
                    <Ionicons name={section.icon} size={14} color={section.color} />
                  </View>
                  <Text style={[styles.resultSectionTitle, { color: colors.foreground }]}>{section.title}</Text>
                </View>
                {section.items.map((item) => (
                  <Pressable
                    key={item.label}
                    style={[styles.resultItem, { backgroundColor: colors.card, borderColor: colors.border }]}
                    onPress={() => Haptics.impactAsync(Haptics.ImpactFeedbackStyle.Light)}
                  >
                    <View style={[styles.resultItemIcon, { backgroundColor: section.color + "22" }]}>
                      <Ionicons name={item.icon} size={18} color={section.color} />
                    </View>
                    <View style={{ flex: 1 }}>
                      <Text style={[styles.resultItemLabel, { color: colors.foreground }]}>{item.label}</Text>
                      <Text style={[styles.resultItemSub, { color: colors.mutedForeground }]}>{item.sub}</Text>
                    </View>
                    <Ionicons name="chevron-forward" size={16} color={colors.mutedForeground} />
                  </Pressable>
                ))}
              </View>
            ))}
          </>
        )}
      </ScrollView>
    </View>
  );
}

const styles = StyleSheet.create({
  root: { flex: 1 },
  searchHeader: { paddingHorizontal: 20, paddingBottom: 14, gap: 12 },
  title: { fontSize: 28, fontFamily: "Inter_700Bold" },
  searchBar: { flexDirection: "row", alignItems: "center", gap: 10, borderRadius: 14, borderWidth: 1, paddingHorizontal: 14, paddingVertical: 12 },
  searchInput: { flex: 1, fontSize: 15, fontFamily: "Inter_400Regular" },
  sectionTitle: { fontSize: 17, fontFamily: "Inter_700Bold", marginHorizontal: 20, marginBottom: 12, marginTop: 8 },
  pillGrid: { paddingHorizontal: 20, gap: 8, marginBottom: 20 },
  pill: { flexDirection: "row", alignItems: "center", gap: 8, borderRadius: 20, borderWidth: 1, paddingHorizontal: 14, paddingVertical: 9 },
  pillText: { fontSize: 13, fontFamily: "Inter_400Regular" },
  recentRow: { flexDirection: "row", alignItems: "center", gap: 12, paddingHorizontal: 20, paddingVertical: 14, borderBottomWidth: 1 },
  recentIcon: { width: 32, height: 32, borderRadius: 10, alignItems: "center", justifyContent: "center" },
  recentText: { flex: 1, fontSize: 14, fontFamily: "Inter_400Regular" },
  aiAnswerCard: { marginHorizontal: 20, marginBottom: 20, marginTop: 8, borderRadius: 14, borderWidth: 1, padding: 14, flexDirection: "row", gap: 12, alignItems: "flex-start" },
  aiAnswerTitle: { fontSize: 14, fontFamily: "Inter_600SemiBold" },
  aiAnswerText: { fontSize: 13, fontFamily: "Inter_400Regular", lineHeight: 18 },
  resultSectionHeader: { flexDirection: "row", alignItems: "center", gap: 8, paddingHorizontal: 20, marginBottom: 8, marginTop: 4 },
  resultSectionIcon: { width: 24, height: 24, borderRadius: 7, alignItems: "center", justifyContent: "center" },
  resultSectionTitle: { fontSize: 13, fontFamily: "Inter_600SemiBold" },
  resultItem: { marginHorizontal: 20, marginBottom: 6, borderRadius: 12, borderWidth: 1, padding: 12, flexDirection: "row", alignItems: "center", gap: 12 },
  resultItemIcon: { width: 38, height: 38, borderRadius: 11, alignItems: "center", justifyContent: "center" },
  resultItemLabel: { fontSize: 13, fontFamily: "Inter_500Medium" },
  resultItemSub: { fontSize: 11, fontFamily: "Inter_400Regular", marginTop: 2 },
});
