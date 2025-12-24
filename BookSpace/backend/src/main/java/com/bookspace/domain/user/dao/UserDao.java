package com.bookspace.domain.user.dao;

import com.bookspace.domain.user.vo.UserVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface UserDao {

    // 1. 회원 등록 (회원가입)
    int insertUser(UserVo userVo);

    // 2. 회원 정보 수정
    int updateUser(UserVo userVo);

    // 3. 회원 정보 조회 (by userId)
    UserVo selectUserById(@Param("userId") long userId);

    // 4. 회원 정보 조회 (by userLoginId)
    UserVo selectUserByLoginId(@Param("userLoginId") String userLoginId);

    // 4-1. 회원 정보 조회 (by userEmail)
    UserVo selectUserByEmail(@Param("userEmail") String userEmail);

    // 5. 회원 탈퇴 (soft delete)
    int softDeleteUser(@Param("userId") long userId);

    // 6. 회원 영구 삭제 (hard delete) => 단일 회원 삭제
    int hardDeleteUser(@Param("userId") long userId);

    // 7. 로그인 아이디 중복 체크 (true -> 이미 존재)
    boolean existsByUserLoginId(@Param("userLoginId") String userLoginId);

    // 8. 닉네임 중복 체크
    boolean existsByUserNickname(@Param("userNickname") String userNickname);

    // 9. 이메일 중복 체크
    boolean existsByUserEmail(@Param("userEmail") String userEmail);

    // 10. 14일 지난 만료회원 자동 삭제
    int deleteExpiredInactiveUsers();

    // 11. 비밀번호 업데이트
    int updatePassword(@Param("userLoginId") String userLoginId, @Param("newPassword") String newPassword);

}
