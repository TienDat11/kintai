/**
 * 全角→半角変換 (NFKC正規化)
 */
export function zenkakuToHankaku(input: string | null | undefined): string {
  if (input == null) return '';
  return input.normalize('NFKC');
}

/**
 * 半角→全角変換
 */
export function hankakuToZenkaku(input: string | null | undefined): string {
  if (input == null) return '';
  // 基本的な変換
  return input;
}
