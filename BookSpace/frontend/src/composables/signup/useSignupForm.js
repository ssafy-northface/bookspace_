import { ref } from "vue";
import { useRouter } from "vue-router";
import useVuelidate from "@vuelidate/core";
import {
  required,
  email as emailValidator,
  minLength,
  sameAs,
  helpers,
} from "@vuelidate/validators";
import { useUserStore } from "@/stores/userStore";

/**
 * 회원가입 폼 로직을 관리하는 composable
 */
export function useSignupForm() {
  const userStore = useUserStore();
  const router = useRouter();

  // 상태
  const loginId = ref("");
  const email = ref("");
  const password = ref("");
  const confirmPassword = ref("");
  const name = ref("");
  const nickname = ref("");
  const phone = ref("");
  const birth = ref("");
  const isSubmitting = ref(false);
  const isEmailVerified = ref(false);

  // 중복 체크용 함수 정의
  const dupCheck = (type) =>
    helpers.withAsync(async (value) => {
      if (!value) return true;

      // 이메일 타입인 경우, 형식이 유효할 때만 중복 체크
      if (type === "email") {
        const emailRegex = /^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\.[A-Za-z]{2,}$/;
        if (!emailRegex.test(value)) return true; // 형식 검사 먼저 통과 필요
      }

      try {
        return await userStore.checkDuplicate(type, value);
      } catch {
        return true;
      }
    });

  // vuelidate 규칙 정의
  const rules = {
    loginId: {
      required,
      minLength: minLength(4),
      unique: dupCheck("loginId"),
    },
    email: {
      required,
      email: emailValidator,
      unique: dupCheck("email"),
    },
    password: {
      required,
      minLength: minLength(8),
      passwordFormat: helpers.withMessage(
        "비밀번호는 8자 이상이어야 합니다. (숫자, 특수문자, 영문 포함)",
        (value) => {
          if (!value) return false;
          // 8자 이상, 숫자, 특수문자, 영문 모두 포함
          const hasNumber = /\d/.test(value);
          const hasSpecialChar = /[!@#$%^&*()_+\-=\[\]{};':"\\|,.<>\/?]/.test(
            value
          );
          const hasLetter = /[a-zA-Z]/.test(value);
          return value.length >= 8 && hasNumber && hasSpecialChar && hasLetter;
        }
      ),
    },
    confirmPassword: { required, sameAs: sameAs(password) },
    name: { required },
    nickname: {
      required,
      unique: dupCheck("nickname"),
    },
    phone: {
      //000-0000-0000
      phoneFormat: helpers.regex(/^\d{3}-\d{4}-\d{4}$/),
      $autoDirty: true,
    },
    birth: {
      // 생년월일은 년/월/일이 모두 입력되어야 함
      required: helpers.withMessage("생년월일을 입력해주세요.", (value) => {
        if (!value) return false;
        const parts = value.split("-");
        if (parts.length !== 3) return false;
        const year = parseInt(parts[0]);
        const month = parseInt(parts[1]);
        const day = parseInt(parts[2]);
        // 기본 유효성 검사
        if (isNaN(year) || isNaN(month) || isNaN(day)) return false;
        if (year < 1900 || year > new Date().getFullYear()) return false;
        if (month < 1 || month > 12) return false;
        if (day < 1 || day > 31) return false;
        // 실제 날짜 유효성 검사
        const date = new Date(year, month - 1, day);
        return (
          date.getFullYear() === year &&
          date.getMonth() === month - 1 &&
          date.getDate() === day
        );
      }),
      $autoDirty: true,
    },
  };

  // vuelidate 인스턴스 생성
  const v$ = useVuelidate(rules, {
    loginId,
    email,
    password,
    confirmPassword,
    name,
    nickname,
    phone,
    birth,
  });

  // 회원가입 확인
  const signup = async () => {
    // 이미 요청 중이면 또 요청 안보내기
    if (isSubmitting.value) return;

    // 이메일 인증 확인
    if (!isEmailVerified.value) {
      alert("이메일 인증을 완료해주세요.");
      return;
    }

    // 유효성 검사
    const valid = await v$.value.$validate();
    if (!valid) {
      alert("입력한 정보를 다시 확인해주세요.");
      return;
    }

    // DTO 형식에 맞게 payload 구성
    const payload = {
      userLoginId: loginId.value,
      userPw: password.value,
      userName: name.value,
      userNickname: nickname.value,
      userEmail: email.value,
      userPhone: phone.value,
      userBirthDate: birth.value,
    };

    try {
      isSubmitting.value = true;

      // 회원가입 API 호출
      await userStore.signup(payload);

      alert("회원가입이 완료되었습니다. 로그인 페이지로 이동합니다.");
      await router.push("/signin");
    } catch (err) {
      const msg =
        err?.response?.data?.message ||
        "회원가입에 실패했습니다. 잠시 후 다시 시도해주세요.";
      alert(msg);
    } finally {
      isSubmitting.value = false;
    }
  };

  return {
    // 상태
    loginId,
    email,
    password,
    confirmPassword,
    name,
    nickname,
    phone,
    birth,
    isSubmitting,
    isEmailVerified,
    // validation
    v$,
    // methods
    signup,
    setIsEmailVerified: (value) => {
      isEmailVerified.value = value;
    },
  };
}
