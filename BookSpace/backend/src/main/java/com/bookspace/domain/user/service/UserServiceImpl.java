package com.bookspace.domain.user.service;

import com.bookspace.domain.email.service.EmailVerificationService;
import com.bookspace.domain.user.dao.UserDao;
import com.bookspace.domain.user.dto.SignInRequestDto;
import com.bookspace.domain.user.dto.SignupRequestDto;
import com.bookspace.domain.user.dto.UserResponseDto;
import com.bookspace.domain.user.dto.UserUpdateRequestDto;
import com.bookspace.domain.user.vo.UserVo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class UserServiceImpl implements UserService {

    private final UserDao userDao;
    private final PasswordEncoder passwordEncoder;
    private final EmailVerificationService emailVerificationService;

    // 1. 회원가입
    @Override
    @Transactional
    public UserResponseDto signup(SignupRequestDto dto) {

        // 1-1. 중복 체크
        if (userDao.existsByUserLoginId(dto.getUserLoginId())) {
            throw new IllegalArgumentException("Login ID already exists.");
        }
        if (userDao.existsByUserNickname(dto.getUserNickname())) {
            throw new IllegalArgumentException("Nickname already exists.");
        }
        if (userDao.existsByUserEmail(dto.getUserEmail())) {
            throw new IllegalArgumentException("Email already exists.");
        }

        // 1-2. DTO -> VO 변환
        UserVo userVo = new UserVo();
        userVo.setUserLoginId(dto.getUserLoginId());
        userVo.setUserPw(passwordEncoder.encode(dto.getUserPw()));
        userVo.setUserName(dto.getUserName());
        userVo.setUserNickname(dto.getUserNickname());
        userVo.setUserEmail(dto.getUserEmail());
        userVo.setUserPhone(dto.getUserPhone());
        userVo.setUserBirthDate(dto.getUserBirthDate());

        int result = userDao.insertUser(userVo);
        if (result != 1) {
            throw new IllegalStateException("Failed to create user.");
        }

        // 가입 시 이메일 인증 완료를 영구적으로 저장
        emailVerificationService.markEmailAsPermanentlyVerified(dto.getUserEmail());

        // insert 시 useGeneratedKeys 로 userId 세팅됨
        return convertToResponseDto(userVo);
    }



    // 2. 로그인
    // =============================================================
    // Spring Security 구현 시 수정 필요
    // =============================================================
    @Override
    public UserResponseDto login(SignInRequestDto dto) {

        // 2-1. 로그인 아이디로 조회
        UserVo userVo = userDao.selectUserByLoginId(dto.getUserLoginId());
        if (userVo == null) {
            throw new IllegalArgumentException("User not found with loginId: " + dto.getUserLoginId());
        }

        // 2-2. 탈퇴 상태 체크
        if (!"active".equals(userVo.getUserStatus())) {
            throw new IllegalArgumentException("User is inactive with loginId: " + dto.getUserLoginId());
        }

        // 2-3. 비밀번호 검증 (평문 비교, 나중에 암호화)
        if (!dto.getUserPw().equals(userVo.getUserPw())) {
            throw new IllegalArgumentException("Invalid password for loginId: " + dto.getUserLoginId());
        }

        return convertToResponseDto(userVo);
    }


    // 3. 회원 조회
    @Override
    public UserResponseDto getUserById(long userId) {
        UserVo userVo = userDao.selectUserById(userId);
        if (userVo == null) {
            throw new IllegalArgumentException("User not found with id: " + userId);
        }
        if (!"active".equals(userVo.getUserStatus())) {
            throw new IllegalArgumentException("User is inactive with id: " + userId);
        }
        return convertToResponseDto(userVo);
    }


    // 4. 회원 정보 수정
    @Override
    @Transactional
    public UserResponseDto updateUser(long userId, UserUpdateRequestDto dto) {

        UserVo userVo = userDao.selectUserById(userId);
        if (userVo == null) {
            throw new IllegalArgumentException("User not found with id: " + userId);
        }
        if (!"active".equals(userVo.getUserStatus())) {
            throw new IllegalArgumentException("User is inactive with id: " + userId);
        }

        userVo.setUserNickname(dto.getUserNickname());
        userVo.setUserPhone(dto.getUserPhone());
        userVo.setUserBirthDate(dto.getUserBirthDate());

        int result = userDao.updateUser(userVo);
        if (result != 1) {
            throw new IllegalStateException("Failed to update user.");
        }

        return convertToResponseDto(userVo);
    }


    // 5. 회원 탈퇴 (soft delete)
    @Override
    @Transactional
    public void softDeleteUser(long userId) {

        UserVo userVo = userDao.selectUserById(userId);

        if (userVo == null) {
            throw new IllegalArgumentException("User not found with id: " + userId);
        }
        if (!"active".equals(userVo.getUserStatus())) {
            throw new IllegalArgumentException("User is already inactive with id: " + userId);
        }

        int result = userDao.softDeleteUser(userId);
        if (result != 1) {
            throw new IllegalStateException("Failed to delete user: " + userId);
        }
    }


    // 6. 회원 영구 삭제 (hard delete)
    @Override
    @Transactional
    public void hardDeleteUser(long userId) {

        int result = userDao.hardDeleteUser(userId);

        if (result != 1) {
            // 관리자에서 없는 회원을 지우려고 한 경우 → 에러
            throw new IllegalArgumentException("User not found or already deleted with id: " + userId);
        }
    }

    // 7. 아이디 중복 확인
    @Override
    public boolean existsUserByLoginId(String userLoginId) {
        return userDao.existsByUserLoginId(userLoginId);
    }

    // 8. 닉네임 중복 확인
    @Override
    public boolean existsUserByNickname(String userNickname) {
        return userDao.existsByUserNickname(userNickname);
    }

    // 9. 이메일 중복 확인
    @Override
    public boolean existsUserByEmail(String userEmail) {
        return userDao.existsByUserEmail(userEmail);
    }


    // 10. Scheduler용
    // 탈퇴 후 14일 지난 회원 일괄 영구 삭제
    @Override
    @Transactional
    public int deleteExpiredInactiveUsers() {

        int deletedCount = userDao.deleteExpiredInactiveUsers();

        if (deletedCount > 0) {
            log.info("Batch hard deleted {} expired inactive users", deletedCount);
        } else {
            log.info("No expired inactive users to hard delete");
        }

        return deletedCount;
    }

    // 11. 로그인한 사용자의 내 정보 조회
    public UserResponseDto getMyInfo(Long userId) {

        UserVo userVo = userDao.selectUserById(userId);

        if (userVo == null) {
            throw new IllegalArgumentException("회원 정보를 찾을 수 없습니다.");
        }

        // inactive인 경우 막기 (나중에 수정해도 됨)
        if (!"active".equalsIgnoreCase(userVo.getUserStatus())) {
            throw new IllegalStateException("비활성화된 계정입니다.");
        }

        return convertToResponseDto(userVo);
    }

    // 12. 비밀번호 찾기 - 본인 확인
    @Override
    public boolean verifyUser(String userLoginId, String userEmail) {
        UserVo userVo = userDao.selectUserByLoginId(userLoginId);

        if (userVo == null) {
            return false;
        }

        // 탈퇴한 회원은 제외
        if (!"active".equals(userVo.getUserStatus())) {
            return false;
        }

        // 이메일 일치 여부 확인
        return userEmail.equalsIgnoreCase(userVo.getUserEmail());
    }

    // 14. 비밀번호 찾기 - 재설정 링크 또는 코드 발송
    @Override
    public String sendPasswordResetCodeOrLink(String userLoginId, String userEmail) {
        // 본인 확인
        if (!verifyUser(userLoginId, userEmail)) {
            throw new IllegalArgumentException("사용자 정보가 일치하지 않습니다.");
        }

        // 가입 시 이메일 인증 완료 여부 확인
        if (emailVerificationService.isEmailPermanentlyVerified(userEmail)) {
            // 재설정 링크 발송
            return emailVerificationService.sendPasswordResetLink(userEmail);
        } else {
            // 인증 코드 발송
            emailVerificationService.sendVerificationCode(userEmail);
            return null; // 링크가 아닌 코드 발송
        }
    }

    // 15. 재설정 링크를 통한 비밀번호 재설정
    @Override
    @Transactional
    public void resetPasswordByToken(String token, String newPassword) {
        // 토큰 검증
        String email = emailVerificationService.verifyResetToken(token);
        if (email == null) {
            throw new IllegalArgumentException("유효하지 않거나 만료된 재설정 링크입니다.");
        }

        // 이메일로 사용자 조회
        UserVo userVo = userDao.selectUserByEmail(email);
        if (userVo == null) {
            throw new IllegalArgumentException("사용자를 찾을 수 없습니다.");
        }

        // 비밀번호 암호화 후 업데이트
        String encodedPassword = passwordEncoder.encode(newPassword);
        int result = userDao.updatePassword(userVo.getUserLoginId(), encodedPassword);

        if (result != 1) {
            throw new IllegalStateException("비밀번호 변경에 실패했습니다.");
        }
    }

    // 13. 비밀번호 재설정
    @Override
    @Transactional
    public void resetPassword(String userLoginId, String userEmail, String newPassword) {
        // 본인 확인 다시 수행 (보안)
        if (!verifyUser(userLoginId, userEmail)) {
            throw new IllegalArgumentException("사용자 정보가 일치하지 않습니다.");
        }

        // 이메일 인증 확인
        if (!emailVerificationService.isEmailVerified(userEmail)) {
            throw new IllegalStateException("이메일 인증이 완료되지 않았습니다. 이메일 인증을 먼저 완료해주세요.");
        }

        // 비밀번호 암호화 후 업데이트
        String encodedPassword = passwordEncoder.encode(newPassword);
        int result = userDao.updatePassword(userLoginId, encodedPassword);

        if (result != 1) {
            throw new IllegalStateException("비밀번호 변경에 실패했습니다.");
        }

        // 비밀번호 재설정 완료 후 이메일 인증 상태 정리
        emailVerificationService.clearVerification(userEmail);
    }

    // DTO 변환
    private UserResponseDto convertToResponseDto(UserVo userVo) {
        UserResponseDto dto = new UserResponseDto();
        dto.setUserId(userVo.getUserId());
        dto.setUserLoginId(userVo.getUserLoginId());
        dto.setUserName(userVo.getUserName());
        dto.setUserNickname(userVo.getUserNickname());
        dto.setUserEmail(userVo.getUserEmail());
        dto.setUserPhone(userVo.getUserPhone());
        dto.setUserBirthDate(userVo.getUserBirthDate());
        dto.setUserRegistDate(userVo.getUserRegistDate());
        dto.setUserStatus(userVo.getUserStatus());
        dto.setUserRole(userVo.getUserRole());
        return dto;
    }



}
