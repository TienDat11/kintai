/**
 * 円フォーマット
 */
export function formatYen(amount: number | null | undefined): string {
  if (amount == null) return '';
  return new Intl.NumberFormat('ja-JP', {
    style: 'currency',
    currency: 'JPY',
  }).format(amount);
}
