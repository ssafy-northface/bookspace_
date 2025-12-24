import { ref, computed, watch, nextTick } from "vue";

/**
 * 전화번호 입력 관련 로직을 관리하는 composable
 * @param {string} modelValue - 전화번호 값 (010-1234-5678 형식)
 * @param {Function} emitUpdate - update:modelValue 이벤트 emit 함수
 * @param {Function} onTouch - 유효성 검사 트리거 함수
 */
export function usePhoneNumber(modelValue, emitUpdate, onTouch) {
  const phonePart1 = ref("");
  const phonePart2 = ref("");
  const phonePart3 = ref("");

  // modelValue를 parts로 분리
  const parsePhoneValue = (value) => {
    if (!value) {
      return { part1: "", part2: "", part3: "" };
    }
    const parts = value.split("-");
    return {
      part1: parts[0] || "",
      part2: parts[1] || "",
      part3: parts[2] || "",
    };
  };

  // 초기값 설정
  const initialValue = parsePhoneValue(modelValue);
  phonePart1.value = initialValue.part1 || "010"; // 기본값 "010" 설정
  phonePart2.value = initialValue.part2;
  phonePart3.value = initialValue.part3;

  // parts를 합쳐서 modelValue 형식으로 반환
  const getPhoneValue = computed(() => {
    if (!phonePart1.value && !phonePart2.value && !phonePart3.value) return "";
    return `${phonePart1.value || ""}-${phonePart2.value || ""}-${
      phonePart3.value || ""
    }`;
  });

  // phone 값이 변경될 때마다 emit
  watch(getPhoneValue, (newValue) => {
    if (newValue !== modelValue) {
      emitUpdate(newValue);
    }
  });

  // modelValue가 외부에서 변경될 때 parts 업데이트
  watch(
    () => modelValue,
    (newValue) => {
      const parsed = parsePhoneValue(newValue);
      if (
        parsed.part1 !== phonePart1.value ||
        parsed.part2 !== phonePart2.value ||
        parsed.part3 !== phonePart3.value
      ) {
        phonePart1.value = parsed.part1 || "010"; // 기본값 "010" 설정
        phonePart2.value = parsed.part2;
        phonePart3.value = parsed.part3;
      }
    },
    { immediate: false }
  );

  const handleInput = (part, event, refs) => {
    let value = event.target.value.replace(/[^0-9]/g, "");

    // 최대 길이 제한
    if (part === 1 && value.length > 3) {
      value = value.slice(0, 3);
    } else if (part === 2 && value.length > 4) {
      value = value.slice(0, 4);
    } else if (part === 3 && value.length > 4) {
      value = value.slice(0, 4);
    }

    if (part === 1) {
      phonePart1.value = value;
      // 3자리 입력 완료 시 다음 필드로 이동
      if (value.length === 3 && refs.phonePart2Ref) {
        nextTick(() => refs.phonePart2Ref.value?.focus());
      }
    } else if (part === 2) {
      phonePart2.value = value;
      // 4자리 입력 완료 시 다음 필드로 이동
      if (value.length === 4 && refs.phonePart3Ref) {
        nextTick(() => refs.phonePart3Ref.value?.focus());
      }
    } else if (part === 3) {
      phonePart3.value = value;
    }

    event.target.value = value;

    // 유효성 검사 트리거
    nextTick(() => {
      if (onTouch) {
        onTouch();
      }
    });
  };

  const handleKeydown = (part, event, refs) => {
    const currentValue = event.target.value;

    // Backspace 처리 (빈 필드에서 이전 필드로 이동)
    if (event.key === "Backspace" && currentValue.length === 0) {
      if (part === 2 && refs.phonePart1Ref) {
        event.preventDefault();
        nextTick(() => refs.phonePart1Ref.value?.focus());
      } else if (part === 3 && refs.phonePart2Ref) {
        event.preventDefault();
        nextTick(() => refs.phonePart2Ref.value?.focus());
      }
    }

    // ArrowLeft 처리 (이전 필드로 이동)
    if (event.key === "ArrowLeft" && currentValue.length === 0) {
      if (part === 2 && refs.phonePart1Ref) {
        event.preventDefault();
        refs.phonePart1Ref.value?.focus();
      } else if (part === 3 && refs.phonePart2Ref) {
        event.preventDefault();
        refs.phonePart2Ref.value?.focus();
      }
    }

    // ArrowRight 처리 (다음 필드로 이동)
    if (event.key === "ArrowRight") {
      if (part === 1 && currentValue.length === 3 && refs.phonePart2Ref) {
        event.preventDefault();
        refs.phonePart2Ref.value?.focus();
      } else if (
        part === 2 &&
        currentValue.length === 4 &&
        refs.phonePart3Ref
      ) {
        event.preventDefault();
        refs.phonePart3Ref.value?.focus();
      }
    }
  };

  return {
    phonePart1,
    phonePart2,
    phonePart3,
    handleInput,
    handleKeydown,
  };
}
