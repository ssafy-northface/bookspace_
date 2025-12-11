package com.bookspace.domain.user.service;

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
