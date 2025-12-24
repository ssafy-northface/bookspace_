import { ref, computed, watch, nextTick } from "vue";

/**
 * 생년월일 입력 관련 로직을 관리하는 composable
 * @param {string} modelValue - 생년월일 값 (YYYY-MM-DD 형식)
 * @param {Function} emitUpdate - update:modelValue 이벤트 emit 함수
 * @param {Function} onTouch - 유효성 검사 트리거 함수
 */
export function useBirthDate(modelValue, emitUpdate, onTouch) {
  const birthYear = ref("");
  const birthMonth = ref("");
  const birthDay = ref("");

  // modelValue를 parts로 분리
  const parseBirthValue = (value) => {
    if (!value) {
      return { year: "", month: "", day: "" };
    }
    const parts = value.split("-");
    return {
      year: parts[0] || "",
      month: parts[1] || "",
      day: parts[2] || "",
    };
  };

  // 초기값 설정
  const initialValue = parseBirthValue(modelValue);
  birthYear.value = initialValue.year;
  birthMonth.value = initialValue.month;
  birthDay.value = initialValue.day;

  // parts를 합쳐서 modelValue 형식으로 반환
  const getBirthValue = computed(() => {
    if (!birthYear.value || !birthMonth.value || !birthDay.value) return "";
    const year = birthYear.value.padStart(4, "0");
    const month = birthMonth.value.padStart(2, "0");
    const day = birthDay.value.padStart(2, "0");
    return `${year}-${month}-${day}`;
  });

  // birth 값이 변경될 때마다 emit
  watch(getBirthValue, (newValue) => {
    if (newValue !== modelValue) {
      emitUpdate(newValue);
    }
  });

  // modelValue가 외부에서 변경될 때 parts 업데이트
  watch(
    () => modelValue,
    (newValue) => {
      const parsed = parseBirthValue(newValue);
      if (
        parsed.year !== birthYear.value ||
        parsed.month !== birthMonth.value ||
        parsed.day !== birthDay.value
      ) {
        birthYear.value = parsed.year;
        birthMonth.value = parsed.month;
        birthDay.value = parsed.day;
      }
    }
  );

  // 생년월일 변경 감지하여 유효성 검사 트리거
  watch([birthYear, birthMonth, birthDay], () => {
    nextTick(() => {
      if (onTouch) {
        onTouch();
      }
    });
  });

  const handleInput = (type, event, refs) => {
    let value = event.target.value.replace(/[^0-9]/g, "");

    // 최대 길이 제한
    if (type === "year" && value.length > 4) {
      value = value.slice(0, 4);
    } else if (type === "month" && value.length > 2) {
      value = value.slice(0, 2);
      // 월 유효성 검사 (1-12)
      const monthNum = parseInt(value);
      if (monthNum > 12) {
        value = "12";
      } else if (monthNum < 1 && value.length === 2) {
        value = "01";
      }
    } else if (type === "day" && value.length > 2) {
      value = value.slice(0, 2);
      // 일 유효성 검사 (1-31)
      const dayNum = parseInt(value);
      if (dayNum > 31) {
        value = "31";
      } else if (dayNum < 1 && value.length === 2) {
        value = "01";
      }
    }

    if (type === "year") {
      birthYear.value = value;
      // 4자리 입력 완료 시 다음 필드로 이동
      if (value.length === 4 && refs.birthMonthRef) {
        nextTick(() => refs.birthMonthRef.value?.focus());
      }
    } else if (type === "month") {
      birthMonth.value = value;
      // 2자리 입력 완료 시 다음 필드로 이동
      if (value.length === 2 && refs.birthDayRef) {
        nextTick(() => refs.birthDayRef.value?.focus());
      }
    } else if (type === "day") {
      birthDay.value = value;
    }

    event.target.value = value;

    // 유효성 검사 트리거
    nextTick(() => {
      if (onTouch) {
        onTouch();
      }
    });
  };

  const handleKeydown = (type, event, refs) => {
    const currentValue = event.target.value;

    // Backspace 처리 (빈 필드에서 이전 필드로 이동)
    if (event.key === "Backspace" && currentValue.length === 0) {
      if (type === "month" && refs.birthYearRef) {
        event.preventDefault();
        nextTick(() => refs.birthYearRef.value?.focus());
      } else if (type === "day" && refs.birthMonthRef) {
        event.preventDefault();
        nextTick(() => refs.birthMonthRef.value?.focus());
      }
    }

    // ArrowLeft 처리 (이전 필드로 이동)
    if (event.key === "ArrowLeft" && currentValue.length === 0) {
      if (type === "month" && refs.birthYearRef) {
        event.preventDefault();
        refs.birthYearRef.value?.focus();
      } else if (type === "day" && refs.birthMonthRef) {
        event.preventDefault();
        refs.birthMonthRef.value?.focus();
      }
    }

    // ArrowRight 처리 (다음 필드로 이동)
    if (event.key === "ArrowRight") {
      if (type === "year" && currentValue.length === 4 && refs.birthMonthRef) {
        event.preventDefault();
        refs.birthMonthRef.value?.focus();
      } else if (
        type === "month" &&
        currentValue.length === 2 &&
        refs.birthDayRef
      ) {
        event.preventDefault();
        refs.birthDayRef.value?.focus();
      }
    }
  };

  return {
    birthYear,
    birthMonth,
    birthDay,
    handleInput,
    handleKeydown,
  };
}
