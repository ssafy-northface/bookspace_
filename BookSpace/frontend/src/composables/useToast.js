import { ref } from "vue";

/**
 * 전역으로 공유되는 토스트 리스트
 * - [{ id, title, description, action, duration, ... }]
 */
const toasts = ref([]);

// 간단한 id 시드
let idSeed = 0;
const nextId = () => {
  idSeed += 1;
  return idSeed;
};

/**
 * Toast 추가
 * @param {Object} options
 * @param {string} options.title        - 토스트 제목
 * @param {string} options.description  - 토스트 내용
 * @param {any}    options.action       - Vue 컴포넌트 or VNode (선택)
 * @param {number|null} options.duration - 자동 닫힘 ms (null이면 자동 닫힘 없음)
 * @param {string} options.variant      - "default" | "destructive" 등 (UI 확장용)
 */
function toast(options = {}) {
  const {
    title,
    description,
    action = null,
    duration = 3000,
    variant = "default",
  } = options;

  const id = nextId();

  toasts.value.push({
    id,
    title,
    description,
    action,
    duration,
    variant,
  });

  // duration이 null이 아니면 자동으로 닫기
  if (duration !== null) {
    window.setTimeout(() => {
      dismiss(id);
    }, duration);
  }

  return id;
}

/**
 * id 기준으로 토스트 제거
 */
function dismiss(id) {
  toasts.value = toasts.value.filter((t) => t.id !== id);
}

/**
 * 전체 토스트 지우기
 */
function dismissAll() {
  toasts.value = [];
}

/**
 * Vue 컴포넌트에서 사용하는 composable
 * - React의 useToast와 비슷한 역할
 */
export function useToast() {
  return {
    toasts,
    toast,
    dismiss,
    dismissAll,
  };
}

// 라우터 가드 등에서 직접 사용할 수 있도록 toast 함수 export
export { toast, dismiss, dismissAll };
