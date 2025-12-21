import { computed, unref } from "vue";

/**
 * 날짜를 locale 기준으로 포맷팅
 * - 값이 변경되면 자동으로 다시 계산됨
 */

export function useFormattedDate(value, options = {}) {
  return computed(() => {
    const raw = unref(value); // unref: value가 ref면 value.value, 일반값이면 그대로
    if (!raw) return "";

    const date = new Date(raw); // Date 객체로 변환
    if (Number.isNaN(date.getTime())) return "";

    return date.toLocaleString("ko-KR", {
      dateStyle: "medium",
      timeStyle: "short",
      ...options,
    });
  });
}
