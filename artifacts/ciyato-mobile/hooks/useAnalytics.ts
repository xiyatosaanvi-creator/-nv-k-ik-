import { useQuery, useMutation } from "@tanstack/react-query";
import { analyticsApi, type AppUsageEvent } from "@/services/api";
import { useAuth } from "@/context/AuthContext";

export function useUsageSummary() {
  const { isAuthenticated } = useAuth();

  return useQuery({
    queryKey: ["analytics", "summary"],
    queryFn: () => analyticsApi.getSummary().then((r) => r.summary),
    enabled: isAuthenticated,
    staleTime: 10 * 60 * 1000,
  });
}

export function usePostUsage(deviceId: string) {
  return useMutation({
    mutationFn: (events: AppUsageEvent[]) => analyticsApi.postUsage(deviceId, events),
  });
}
