import { Ionicons } from "@expo/vector-icons";
import { router } from "expo-router";
import React, { useState } from "react";
import {
  ActivityIndicator,
  KeyboardAvoidingView,
  Platform,
  Pressable,
  StyleSheet,
  Text,
  TextInput,
  View,
} from "react-native";
import { useColors } from "@/hooks/useColors";
import { useAuth } from "@/context/AuthContext";
import { ApiError } from "@/services/api";

export default function LoginScreen() {
  const colors = useColors();
  const { login, register } = useAuth();
  const [mode, setMode] = useState<"login" | "register">("login");
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [displayName, setDisplayName] = useState("");
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [showPassword, setShowPassword] = useState(false);

  const handleSubmit = async () => {
    setError(null);
    if (!email || !password) {
      setError("Email and password are required");
      return;
    }

    setLoading(true);
    try {
      if (mode === "login") {
        await login(email.trim(), password);
      } else {
        await register(email.trim(), password, displayName.trim() || undefined);
      }
      router.replace("/(tabs)");
    } catch (e) {
      if (e instanceof ApiError) {
        setError(e.message);
      } else {
        setError("Something went wrong. Please try again.");
      }
    } finally {
      setLoading(false);
    }
  };

  return (
    <KeyboardAvoidingView
      style={[styles.root, { backgroundColor: colors.background }]}
      behavior={Platform.OS === "ios" ? "padding" : undefined}
    >
      <View style={styles.inner}>
        <View style={styles.logoRow}>
          <View style={[styles.logoBox, { backgroundColor: "#C6A15B22" }]}>
            <Ionicons name="sparkles" size={32} color="#C6A15B" />
          </View>
          <Text style={[styles.logoText, { color: colors.foreground }]}>Ciyato</Text>
        </View>

        <Text style={[styles.title, { color: colors.foreground }]}>
          {mode === "login" ? "Welcome back" : "Create account"}
        </Text>
        <Text style={[styles.subtitle, { color: colors.mutedForeground }]}>
          {mode === "login"
            ? "Sign in to sync your settings and insights"
            : "Start organizing your phone beautifully"}
        </Text>

        <View style={styles.form}>
          {mode === "register" && (
            <View style={[styles.field, { backgroundColor: colors.card, borderColor: colors.border }]}>
              <Ionicons name="person-outline" size={18} color={colors.mutedForeground} />
              <TextInput
                value={displayName}
                onChangeText={setDisplayName}
                placeholder="Display name (optional)"
                placeholderTextColor={colors.mutedForeground}
                style={[styles.input, { color: colors.foreground }]}
                autoCapitalize="words"
              />
            </View>
          )}

          <View style={[styles.field, { backgroundColor: colors.card, borderColor: colors.border }]}>
            <Ionicons name="mail-outline" size={18} color={colors.mutedForeground} />
            <TextInput
              value={email}
              onChangeText={setEmail}
              placeholder="Email address"
              placeholderTextColor={colors.mutedForeground}
              style={[styles.input, { color: colors.foreground }]}
              autoCapitalize="none"
              keyboardType="email-address"
              autoCorrect={false}
            />
          </View>

          <View style={[styles.field, { backgroundColor: colors.card, borderColor: colors.border }]}>
            <Ionicons name="lock-closed-outline" size={18} color={colors.mutedForeground} />
            <TextInput
              value={password}
              onChangeText={setPassword}
              placeholder="Password"
              placeholderTextColor={colors.mutedForeground}
              style={[styles.input, { color: colors.foreground }]}
              secureTextEntry={!showPassword}
              autoCapitalize="none"
              autoCorrect={false}
            />
            <Pressable onPress={() => setShowPassword((v) => !v)}>
              <Ionicons
                name={showPassword ? "eye-off-outline" : "eye-outline"}
                size={18}
                color={colors.mutedForeground}
              />
            </Pressable>
          </View>

          {error && (
            <View style={[styles.errorBox, { backgroundColor: "#EF444422", borderColor: "#EF4444" }]}>
              <Ionicons name="alert-circle-outline" size={16} color="#EF4444" />
              <Text style={[styles.errorText, { color: "#EF4444" }]}>{error}</Text>
            </View>
          )}

          <Pressable
            style={[styles.btn, { backgroundColor: "#C6A15B", opacity: loading ? 0.7 : 1 }]}
            onPress={handleSubmit}
            disabled={loading}
          >
            {loading ? (
              <ActivityIndicator color="#000" />
            ) : (
              <Text style={styles.btnText}>
                {mode === "login" ? "Sign In" : "Create Account"}
              </Text>
            )}
          </Pressable>

          <Pressable
            style={styles.switchRow}
            onPress={() => { setMode(mode === "login" ? "register" : "login"); setError(null); }}
          >
            <Text style={{ color: colors.mutedForeground }}>
              {mode === "login" ? "Don't have an account? " : "Already have an account? "}
            </Text>
            <Text style={{ color: "#C6A15B", fontWeight: "600" }}>
              {mode === "login" ? "Sign Up" : "Sign In"}
            </Text>
          </Pressable>

          <Pressable style={styles.skipRow} onPress={() => router.replace("/(tabs)")}>
            <Text style={{ color: colors.mutedForeground, fontSize: 13 }}>Continue without account</Text>
          </Pressable>
        </View>
      </View>
    </KeyboardAvoidingView>
  );
}

const styles = StyleSheet.create({
  root: { flex: 1 },
  inner: { flex: 1, paddingHorizontal: 24, justifyContent: "center", paddingBottom: 40 },
  logoRow: { flexDirection: "row", alignItems: "center", gap: 12, marginBottom: 32 },
  logoBox: { width: 52, height: 52, borderRadius: 16, alignItems: "center", justifyContent: "center" },
  logoText: { fontSize: 28, fontWeight: "800", letterSpacing: -0.5 },
  title: { fontSize: 26, fontWeight: "700", marginBottom: 8 },
  subtitle: { fontSize: 15, marginBottom: 32, lineHeight: 22 },
  form: { gap: 12 },
  field: { flexDirection: "row", alignItems: "center", gap: 10, paddingHorizontal: 16, paddingVertical: 14, borderRadius: 14, borderWidth: 1 },
  input: { flex: 1, fontSize: 15 },
  errorBox: { flexDirection: "row", alignItems: "center", gap: 8, padding: 12, borderRadius: 10, borderWidth: 1 },
  errorText: { flex: 1, fontSize: 13 },
  btn: { paddingVertical: 16, borderRadius: 14, alignItems: "center", marginTop: 4 },
  btnText: { fontSize: 16, fontWeight: "700", color: "#000" },
  switchRow: { flexDirection: "row", justifyContent: "center", paddingTop: 8 },
  skipRow: { alignItems: "center", paddingTop: 4 },
});
