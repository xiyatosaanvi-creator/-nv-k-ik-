// Computes which items should render as visible icons vs. a single "+N" overflow
// tile. Never fabricates a fake "+N" — the overflow tile only appears when the
// list genuinely has more items than fit in the given number of slots.
export function getVisibleWithOverflow<T>(items: T[], maxSlots: number): { visible: T[]; overflow: number } {
  if (items.length <= maxSlots) {
    return { visible: items, overflow: 0 };
  }
  const visible = items.slice(0, maxSlots - 1);
  const overflow = items.length - visible.length;
  return { visible, overflow };
}
