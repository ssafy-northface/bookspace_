package com.bookspace.global.security.userdetails;

import com.bookspace.domain.user.vo.UserVo;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Collection;
import java.util.List;

@Getter
public class CustomUserDetails implements UserDetails {

    private final long userId;
    private final String userLoginId;
    private final String userPw;              // 암호화된 비밀번호
    private final String userStatus;          // active / inactive 등
    private final Collection<? extends GrantedAuthority> authorities; // 권한 정보 저장

    public CustomUserDetails(UserVo user) {
        this.userId = user.getUserId();
        this.userLoginId = user.getUserLoginId();
        this.userPw = user.getUserPw();
        this.userStatus = user.getUserStatus();

        // 권한 설정
        String role = user.getUserRole();
        this.authorities = List.of(new SimpleGrantedAuthority("ROLE_" + role));
    }

    // ========= UserDetails 필수 구현 메서드들 =========

    // Security에서 username으로 사용할 값 (로그인 아이디)
    @Override
    public String getUsername() {
        return userLoginId;
    }

    // Security에서 비밀번호로 사용할 값 (암호화된 비밀번호)
    @Override
    public String getPassword() {
        return userPw;
    }

    // 권한 목록
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    // 계정 만료 여부 (true = 만료 안됨)
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    // 계정 잠김 여부 (true = 잠김 아님)
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    // 비밀번호 만료 여부 (true = 만료 안됨)
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    // 계정 사용 가능 여부
    // 여기서 active 상태 체크하면, inactive 유저는 로그인 자체가 안됨.
    @Override
    public boolean isEnabled() {
        return "active".equals(userStatus);
    }
}
