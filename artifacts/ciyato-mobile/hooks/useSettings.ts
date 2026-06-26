import { useQuery, useMutation, useQueryClient } from "@tanstack/react-query";
import { settingsApi, type UserSettings } from "@/services/api";
import { useAuth } from "@/context/AuthContext";

export function useSettings() {
  const { isAuthenticated } = useAuth();

  return useQuery({
    queryKey: ["settings"],
    queryFn: () => settingsApi.get().then((r) => r.settings),
    enabled: isAuthenticated,
    staleTime: 5 * 60 * 1000,
  });
}

export function useUpdateSettings() {
  const qc = useQueryClient();

  return useMutation({
    mutationFn: (data: Partial<UserSettings>) => settingsApi.update(data),
    onSuccess: (res) => {
      qc.setQueryData(["settings"], res.settings);
    },
  });
}
