package com.bookspace.global.security.userdetails;

import com.bookspace.domain.user.dao.UserDao;
import com.bookspace.domain.user.vo.UserVo;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {


    private final UserDao userDao;


    // Spring Security가 username(userLoginid)로 유저를 찾을 때 호출하는 메서드
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        // DB에서 로그인 아이디로 사용자 조회
        UserVo user = userDao.selectUserByLoginId(username);

        // 유저를 찾지 못하면 Spring Security 로그인 실패 처리
        if (user == null) {
            throw new UsernameNotFoundException("User not found with loginId: " + username);
        }

        // DB에서 가져온 UserVo를 Security 전용 UserDetails로 변환
        return new CustomUserDetails(user);
    }




}
