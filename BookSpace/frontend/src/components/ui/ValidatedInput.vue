<!-- 
# Validated Input Component
1) Input UI 렌더링
2) blur시 $touch() 호출로 유효성 검사 트리거
3) v$ prop을 통해 전달된 Vuelidate 필드의 $error 여부 체크 (검증 규칙은 부모 컴포넌트에서 정의 -> v$.필드 형태로 전달)
4) 에러 메시지 표시 -> border 색상 변경

부모(SignUpView)
 ├─ 상태(id, email, password...)
 ├─ 규칙(rules)
 ├─ v$ = useVuelidate(...)
 ↓
<ValidatedInput v-model="email" :v$="v$.email">
    ├─ model를 통해 부모 값 읽고 수정
    ├─ blur 시 v$.email.$touch() 실행 → 유효성 검사 시작
    ├─ v$.email.$error로 에러 여부 체크
    ├─ 규칙에 따라 에러 메시지 렌더링
    └─ border 색 변경

[사용]
<ValidatedInput
  v-model="email"
  :v$="v$.email" // validate 필드
  type="email"
  placeholder="이메일"
/>
-->

<template>
  <div class="relative">
    <!-- 일반 HTML input 사용 (PrimeVue 스타일 문제 해결) -->
    <div class="relative">
      <input
        v-model="model"
        :type="inputType"
        :placeholder="placeholder"
        :class="[inputClasses, isPasswordType ? 'pr-12' : '']"
        @blur="onBlur"
        v-bind="attrs"
      />

      <!-- 비밀번호 보기/숨기기 토글 버튼 -->
      <button
        v-if="isPasswordType"
        type="button"
        @click="togglePasswordVisibility"
        class="absolute right-3 top-1/2 -translate-y-1/2 p-1 text-gray-400 hover:text-gray-600 dark:text-gray-500 dark:hover:text-gray-300 transition-colors"
        tabindex="-1"
      >
        <!-- 눈 열림 아이콘 (비밀번호 보이는 상태) -->
        <svg
          v-if="showPassword"
          class="w-5 h-5"
          fill="none"
          stroke="currentColor"
          viewBox="0 0 24 24"
        >
          <path
            stroke-linecap="round"
            stroke-linejoin="round"
            stroke-width="2"
            d="M15 12a3 3 0 11-6 0 3 3 0 016 0z"
          />
          <path
            stroke-linecap="round"
            stroke-linejoin="round"
            stroke-width="2"
            d="M2.458 12C3.732 7.943 7.523 5 12 5c4.478 0 8.268 2.943 9.542 7-1.274 4.057-5.064 7-9.542 7-4.477 0-8.268-2.943-9.542-7z"
          />
        </svg>
        <!-- 눈 닫힘 아이콘 (비밀번호 숨김 상태) -->
        <svg
          v-else
          class="w-5 h-5"
          fill="none"
          stroke="currentColor"
          viewBox="0 0 24 24"
        >
          <path
            stroke-linecap="round"
            stroke-linejoin="round"
            stroke-width="2"
            d="M13.875 18.825A10.05 10.05 0 0112 19c-4.478 0-8.268-2.943-9.543-7a9.97 9.97 0 011.563-3.029m5.858.908a3 3 0 114.243 4.243M9.878 9.878l4.242 4.242M9.88 9.88l-3.29-3.29m7.532 7.532l3.29 3.29M3 3l3.59 3.59m0 0A9.953 9.953 0 0112 5c4.478 0 8.268 2.943 9.543 7a10.025 10.025 0 01-4.132 5.411m0 0L21 21"
          />
        </svg>
      </button>
    </div>

    <!-- 에러 메시지 영역 (고정 높이로 레이아웃 유지) -->
    <div class="h-6 mt-1">
      <p
        v-if="error"
        class="flex items-center gap-1.5 text-sm text-red-500 dark:text-red-400 font-medium"
      >
        <svg
          class="w-4 h-4 shrink-0"
          fill="none"
          stroke="currentColor"
          viewBox="0 0 24 24"
        >
          <path
            stroke-linecap="round"
            stroke-linejoin="round"
            stroke-width="2"
            d="M12 8v4m0 4h.01M21 12a9 9 0 11-18 0 9 9 0 0118 0z"
          />
        </svg>
        {{ errorMessage }}
      </p>
    </div>
  </div>
</template>

<script setup>
// Props 정의
const props = defineProps({
  // v-model 입력값 (부모 컴포넌트에서 바인딩)
  modelValue: {
    type: [String, Number],
    default: "",
  },
  // 부모가 전달하는 Vuelidate 필드
  v$: {
    type: Object, // e.g. v$.email, v$.password
    required: true,
  },
  // input 타입
  type: {
    type: String,
    default: "text",
  },
  placeholder: {
    type: String,
    default: "",
  },
  class: {
    type: String,
    default: "",
  },
  // 필드 구분용 (loginId / email / nickname)
  field: {
    type: String,
    default: "",
  },
});

// 부모 -> 자식으로 v-model sync 가능하도록 emit 정의
const emit = defineEmits(["update:modelValue"]);
// 다른 태그 속성 전달받기
const attrs = useAttrs();

// v-model 양방향 바인딩 처리
const model = computed({
  get: () => props.modelValue, // 부모값 읽기
  set: (val) => emit("update:modelValue", val), // 부모로 값 전달
});

// 비밀번호 표시 여부
const showPassword = ref(false);

// 비밀번호 타입 여부 체크
const isPasswordType = computed(() => props.type === "password");

// 실제 input type (비밀번호 표시 토글에 따라 변경)
const inputType = computed(() => {
  if (isPasswordType.value) {
    return showPassword.value ? "text" : "password";
  }
  return props.type;
});

// 비밀번호 표시/숨김 토글
const togglePasswordVisibility = () => {
  showPassword.value = !showPassword.value;
};

// 에러 여부
const error = computed(() => props.v$.$error);

// type기반 에러 메시지
const errorMessage = computed(() => {
  if (!error.value) return "";

  // 1) 중복(unique) 에러 (아이디 / 이메일 / 닉네임 등)
  if (props.v$.unique?.$invalid) {
    if (props.field === "loginId") {
      return "이미 사용 중인 아이디입니다.";
    }
    if (props.field === "email") {
      return "이미 사용 중인 이메일입니다.";
    }
    if (props.field === "nickname") {
      return "이미 사용 중인 닉네임입니다.";
    }
    return "이미 사용 중인 값입니다.";
  }

  // required
  if (props.v$.required?.$invalid) {
    return "필수 입력 값입니다.";
  }

  // email
  if (props.type === "email" && props.v$.email?.$invalid) {
    return "유효한 이메일을 입력하세요.";
  }

  // password (passwordFormat이 있으면 우선, 없으면 minLength)
  if (props.type === "password") {
    if (props.v$.passwordFormat?.$invalid) {
      return (
        props.v$.passwordFormat.$message ||
        "비밀번호는 8자 이상이어야 합니다. (숫자, 특수문자, 영문 포함)"
      );
    }
    if (props.v$.minLength?.$invalid && !props.v$.passwordFormat) {
      return "비밀번호는 8자 이상이어야 합니다. (숫자, 특수문자, 영문 포함)";
    }
  }

  // password(sameAs)
  if (props.type === "password" && props.v$.sameAs?.$invalid) {
    return "비밀번호가 일치하지 않습니다.";
  }

  if (props.v$.phoneFormat?.$invalid)
    return "전화번호 형식이 올바르지 않습니다. (예: 010-1234-5678)";

  if (props.v$.birthFormat?.$invalid)
    return "생년월일 형식이 올바르지 않습니다. (예: 2025-12-17)";

  // minLength & maxLength
  if (props.v$.minLength?.$invalid)
    return `입력한 값이 너무 짧습니다. (최소 ${props.v$.minLength.$params.min}자리)`;

  if (props.v$.maxLength?.$invalid)
    return `입력한 값이 너무 깁니다. (최대 ${props.v$.maxLength.$params.max}자리)`;

  // 기본
  if (props.v$.minLength?.$invalid) return "유효하지 않은 값입니다.";

  return "유효하지 않은 값입니다.";
});
// blur 이벤트에서 유효성 검사 시작
const onBlur = () => props.v$.$touch();

// 인풋 스타일 계산
const inputClasses = computed(() => {
  // 기본 스타일
  const base = [
    // 레이아웃
    "w-full px-4 py-3",
    // 폰트
    "text-sm font-normal",
    // 라운드
    "rounded-xl",
    // 트랜지션
    "transition-all duration-200 ease-out",
    // 배경 & 텍스트 (라이트/다크 모드)
    "bg-white dark:bg-slate-800",
    "text-gray-900 dark:text-gray-100",
    // 플레이스홀더
    "placeholder:text-gray-400 dark:placeholder:text-gray-500",
    // 포커스 시 아웃라인 제거
    "outline-none",
    // 그림자
    "shadow-sm",
  ];

  // 상태별 border 스타일
  if (error.value) {
    // 에러 상태
    base.push(
      "border-2 border-red-400 dark:border-red-500",
      "bg-red-50/50 dark:bg-red-900/20",
      "focus:border-red-500 focus:ring-2 focus:ring-red-100 dark:focus:ring-red-900/30"
    );
  } else if (props.v$.$dirty && !props.v$.$invalid) {
    // 유효성 통과 상태
    base.push(
      "border-2 border-emerald-400 dark:border-emerald-500",
      "bg-emerald-50/50 dark:bg-emerald-900/20",
      "focus:border-emerald-500 focus:ring-2 focus:ring-emerald-100 dark:focus:ring-emerald-900/30"
    );
  } else {
    // 기본 상태
    base.push(
      "border-2 border-gray-200 dark:border-slate-600",
      "hover:border-gray-300 dark:hover:border-slate-500",
      "focus:border-blue-500 dark:focus:border-blue-400",
      "focus:ring-2 focus:ring-blue-100 dark:focus:ring-blue-900/30"
    );
  }

  // 추가 클래스
  if (props.class) base.push(props.class);

  return base;
});

import { ref, computed, useAttrs } from "vue";
</script>
