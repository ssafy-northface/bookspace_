package com.bookspace.domain.email.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailVerificationService {

    private final JavaMailSender mailSender;

    // 개발 모드 여부 (true면 실제 이메일 발송 안 함)
    @Value("${app.email.dev-mode:true}")
    private boolean devMode;

    // 발신자 이메일 (Gmail 계정)
    @Value("${spring.mail.username:}")
    private String fromEmail;

    // 인증 코드 저장소 (이메일 -> 코드 정보)
    private final Map<String, VerificationCode> codeStorage = new ConcurrentHashMap<>();

    // 인증 완료된 이메일 저장소 (임시, 1시간 유효)
    private final Map<String, LocalDateTime> verifiedEmails = new ConcurrentHashMap<>();

    // 가입 시 영구 인증 완료된 이메일 저장소 (영구)
    private final Set<String> permanentlyVerifiedEmails = ConcurrentHashMap.newKeySet();

    // 비밀번호 재설정 토큰 저장소 (토큰 -> 이메일, 1시간 유효)
    private final Map<String, ResetToken> resetTokens = new ConcurrentHashMap<>();

    // 인증 코드 유효 시간 (5분)
    private static final int CODE_EXPIRY_MINUTES = 5;

    // 재설정 토큰 유효 시간 (1시간)
    private static final int RESET_TOKEN_EXPIRY_HOURS = 1;

    // 인증 코드 정보
    private record VerificationCode(String code, LocalDateTime expiryTime) {
        boolean isExpired() {
            return LocalDateTime.now().isAfter(expiryTime);
        }
    }

    // 재설정 토큰 정보
    private record ResetToken(String email, LocalDateTime expiryTime) {
        boolean isExpired() {
            return LocalDateTime.now().isAfter(expiryTime);
        }
    }

    /**
     * 인증 코드 발송
     */
    public void sendVerificationCode(String email) {
        // 6자리 인증 코드 생성
        String code = generateCode();

        // 저장 (5분 후 만료)
        codeStorage.put(email, new VerificationCode(
                code,
                LocalDateTime.now().plusMinutes(CODE_EXPIRY_MINUTES)
        ));

        // 이메일 발송
        sendEmail(email, code);

        log.info("Verification code sent to: {}", email);
    }

    /**
     * 인증 코드 검증
     */
    public boolean verifyCode(String email, String code) {
        VerificationCode stored = codeStorage.get(email);

        if (stored == null) {
            log.warn("No verification code found for: {}", email);
            return false;
        }

        if (stored.isExpired()) {
            codeStorage.remove(email);
            log.warn("Verification code expired for: {}", email);
            return false;
        }

        if (!stored.code().equals(code)) {
            log.warn("Invalid verification code for: {}", email);
            return false;
        }

        // 인증 성공 - 코드 삭제 및 인증 완료 상태 저장
        codeStorage.remove(email);
        verifiedEmails.put(email, LocalDateTime.now().plusHours(1)); // 1시간 유효

        log.info("Email verified successfully: {}", email);
        return true;
    }

    /**
     * 가입 시 이메일 인증 완료를 영구적으로 저장
     */
    public void markEmailAsPermanentlyVerified(String email) {
        permanentlyVerifiedEmails.add(email.toLowerCase());
        log.info("Email marked as permanently verified: {}", email);
    }

    /**
     * 가입 시 이메일 인증 완료 여부 확인 (영구)
     */
    public boolean isEmailPermanentlyVerified(String email) {
        return permanentlyVerifiedEmails.contains(email.toLowerCase());
    }

    /**
     * 이메일 인증 여부 확인
     */
    public boolean isEmailVerified(String email) {
        LocalDateTime expiryTime = verifiedEmails.get(email);
        if (expiryTime == null) {
            return false;
        }
        if (LocalDateTime.now().isAfter(expiryTime)) {
            verifiedEmails.remove(email);
            return false;
        }
        return true;
    }

    /**
     * 인증 완료 후 정리
     */
    public void clearVerification(String email) {
        verifiedEmails.remove(email);
    }

    /**
     * 비밀번호 재설정 링크 발송
     */
    public String sendPasswordResetLink(String email) {
        // 재설정 토큰 생성
        String token = generateResetToken();
        
        // 저장 (1시간 후 만료)
        resetTokens.put(token, new ResetToken(
                email,
                LocalDateTime.now().plusHours(RESET_TOKEN_EXPIRY_HOURS)
        ));

        // 이메일 발송
        sendPasswordResetLinkEmail(email, token);

        log.info("Password reset link sent to: {}", email);
        return token;
    }

    /**
     * 재설정 토큰 검증
     */
    public String verifyResetToken(String token) {
        ResetToken stored = resetTokens.get(token);

        if (stored == null) {
            log.warn("No reset token found: {}", token);
            return null;
        }

        if (stored.isExpired()) {
            resetTokens.remove(token);
            log.warn("Reset token expired: {}", token);
            return null;
        }

        // 토큰 사용 후 삭제
        String email = stored.email();
        resetTokens.remove(token);
        
        log.info("Reset token verified successfully for: {}", email);
        return email;
    }

    /**
     * 6자리 인증 코드 생성
     */
    private String generateCode() {
        SecureRandom random = new SecureRandom();
        int code = 100000 + random.nextInt(900000); // 100000 ~ 999999
        return String.valueOf(code);
    }

    /**
     * 재설정 토큰 생성 (32자리 랜덤 문자열)
     */
    private String generateResetToken() {
        SecureRandom random = new SecureRandom();
        byte[] bytes = new byte[24];
        random.nextBytes(bytes);
        return java.util.Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }

    /**
     * 이메일 발송
     */
    private void sendEmail(String to, String code) {
        // 개발 모드: 실제 이메일 발송 대신 콘솔에 출력
        if (devMode) {
            log.info("========================================");
            log.info(" [DEV MODE] 이메일 인증 코드");
            log.info("수신자: {}", to);
            log.info("🔑 인증 코드: {}", code);
            log.info("⏰ 유효 시간: 5분");
            log.info("========================================");
            return;
        }

        // 프로덕션 모드: 실제 이메일 발송
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            // fromEmail이 비어있지 않으면 설정
            if (fromEmail != null && !fromEmail.isEmpty()) {
                message.setFrom(fromEmail);
            }
            message.setTo(to);
            message.setSubject("[BookSpace] 이메일 인증 코드");
            message.setText(
                    "안녕하세요, BookSpace입니다.\n\n" +
                    "이메일 인증 코드: " + code + "\n\n" +
                    "이 코드는 5분간 유효합니다.\n" +
                    "본인이 요청하지 않은 경우 이 메일을 무시해주세요."
            );

            mailSender.send(message);
            log.info("Email sent successfully to: {}", to);
        } catch (Exception e) {
            log.error("Failed to send email to {}: {}", to, e.getMessage(), e);
            throw new RuntimeException("이메일 발송에 실패했습니다. 이메일 주소를 확인해주세요.");
        }
    }

    /**
     * 비밀번호 재설정 링크 이메일 발송
     */
    private void sendPasswordResetLinkEmail(String to, String token) {
        // 개발 모드: 실제 이메일 발송 대신 콘솔에 출력이
        if (devMode) {
            String resetLink = "http://localhost:5173/reset-password?token=" + token;
            log.info("========================================");
            log.info("📧 [DEV MODE] 비밀번호 재설정 링크");
            log.info("📬 수신자: {}", to);
            log.info("🔗 재설정 링크: {}", resetLink);
            log.info("⏰ 유효 시간: {}시간", RESET_TOKEN_EXPIRY_HOURS);
            log.info("========================================");
            return;
        }

        // 프로덕션 모드: 실제 이메일 발송
        try {
            String resetLink = "http://localhost:5173/reset-password?token=" + token; // TODO: 프로덕션 URL로 변경 필요
            SimpleMailMessage message = new SimpleMailMessage();
            if (fromEmail != null && !fromEmail.isEmpty()) {
                message.setFrom(fromEmail);
            }
            message.setTo(to);
            message.setSubject("[BookSpace] 비밀번호 재설정 링크");
            message.setText(
                    "안녕하세요, BookSpace입니다.\n\n" +
                    "비밀번호 재설정을 요청하셨습니다.\n\n" +
                    "아래 링크를 클릭하여 비밀번호를 재설정하세요:\n" +
                    resetLink + "\n\n" +
                    "이 링크는 " + RESET_TOKEN_EXPIRY_HOURS + "시간간 유효합니다.\n" +
                    "본인이 요청하지 않은 경우 이 메일을 무시해주세요."
            );

            mailSender.send(message);
            log.info("Password reset link email sent successfully to: {}", to);
        } catch (Exception e) {
            log.error("Failed to send password reset link email to {}: {}", to, e.getMessage(), e);
            throw new RuntimeException("이메일 발송에 실패했습니다. 이메일 주소를 확인해주세요.");
        }
    }
}

