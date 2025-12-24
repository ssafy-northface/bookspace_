import { ref, unref } from "vue";
import { sendVerificationCodeApi, verifyCodeApi } from "@/api/emailApi";
import { useUserStore } from "@/stores/userStore";

/**
 * 이메일 인증 관련 로직을 관리하는 composable
 * @param {Ref<string>|Computed<string>|string} email - 이메일 주소
 * @param {Function} onVerified - 인증 완료 시 호출할 콜백
 */
export function useEmailVerification(email, onVerified) {
  const userStore = useUserStore();
  const verificationCode = ref("");
  const isCodeSent = ref(false);
  const isSendingCode = ref(false);
  const isVerifying = ref(false);
  const isEmailVerified = ref(false);
  const emailVerificationMessage = ref("");

  const sendVerificationCode = async () => {
    const emailValue = unref(email);
    if (isSendingCode.value || !emailValue) return;

    isSendingCode.value = true;
    emailVerificationMessage.value = "";

    try {
      // 이메일 중복 체크
      const isAvailable = await userStore.checkDuplicate("email", emailValue);
      if (!isAvailable) {
        // 이미 사용 중인 이메일 - 메시지는 ValidatedInput에서 표시하므로 여기서는 표시하지 않음
        isSendingCode.value = false;
        return;
      }

      // 중복이 아니면 인증번호 발송
      const result = await sendVerificationCodeApi(emailValue);
      if (result.success) {
        isCodeSent.value = true;
        emailVerificationMessage.value =
          "인증번호가 이메일로 발송되었습니다. (5분 내 입력)";
      } else {
        emailVerificationMessage.value = result.message;
      }
    } catch (err) {
      emailVerificationMessage.value =
        err.response?.data?.message || "인증번호 발송에 실패했습니다.";
    } finally {
      isSendingCode.value = false;
    }
  };

  const verifyCode = async () => {
    if (isVerifying.value || verificationCode.value.length !== 6) return false;

    const emailValue = unref(email);
    if (!emailValue) return false;

    isVerifying.value = true;

    try {
      const result = await verifyCodeApi(emailValue, verificationCode.value);
      if (result.success) {
        isEmailVerified.value = true;
        emailVerificationMessage.value = "✓ 이메일 인증이 완료되었습니다.";
        if (onVerified) {
          onVerified();
        }
        return true;
      } else {
        emailVerificationMessage.value = result.message;
        return false;
      }
    } catch (err) {
      emailVerificationMessage.value =
        err.response?.data?.message || "인증번호가 일치하지 않습니다.";
      return false;
    } finally {
      isVerifying.value = false;
    }
  };

  return {
    verificationCode,
    isCodeSent,
    isSendingCode,
    isVerifying,
    isEmailVerified,
    emailVerificationMessage,
    sendVerificationCode,
    verifyCode,
  };
}
