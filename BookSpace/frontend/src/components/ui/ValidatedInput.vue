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
  <div class="space-y-1">
    <InputText
      v-model="model"
      :type="type"
      :placeholder="placeholder"
      :class="computedClasses"
      @blur="onBlur"
      v-bind="attrs"
    />

    <!-- 에러 메시지 -->
    <p v-if="error" class="text-sm text-red-500">
      {{ errorMessage }}
    </p>
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

// 에러 여부
const error = computed(() => props.v$.$error);

// type기반 에러 메시지
const errorMessage = computed(() => {
  if (!error.value) return "";

  // required
  if (props.v$.required?.$invalid) {
    return "필수 입력 값입니다.";
  }

  // email
  if (props.type === "email" && props.v$.email?.$invalid) {
    return "유효한 이메일을 입력하세요.";
  }

  // password (minLength)
  if (props.type === "password" && props.v$.minLength?.$invalid) {
    return "비밀번호는 최소 8자 이상이어야 합니다.";
  }

  // password(sameAs)
  if (props.type === "password" && props.v$.sameAs?.$invalid) {
    return "비밀번호가 일치하지 않습니다.";
  }

  // number (numeric)
  if (props.type === "number" && props.v$.numeric?.$invalid) {
    return "숫자만 입력할 수 있습니다.";
  }

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

// TODO error 스타일 수정
const computedClasses = computed(() => {
  return [
    "w-full rounded-md px-3 py-2 text-sm transition",
    "placeholder:text-[color:var(--muted-foreground)]",
    "bg-[var(--background)] text-[var(--foreground)]",
    "focus-visible:ring-[3px] focus-visible:ring-[color:var(--ring)]/50",

    // 기본 border
    "border border-[color:var(--border)]",

    // 에러 border
    // error.value ? "!border-red-500 !text-red-600" : "",

    props.class,
  ];
});

import { computed, useAttrs } from "vue";
import InputText from "primevue/inputtext";
</script>
