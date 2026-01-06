package com.fluxmall.service;

import com.fluxmall.dao.MemberDao;
import com.fluxmall.domain.vo.MemberVO;
import javax.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MemberService {
    private final MemberDao memberDao;
    private final PasswordEncoder encoder;

    /**
     * 회원가입
     */
    @Transactional
    public boolean register(MemberVO member) {
        // 1. username 중복 체크
        if (memberDao.findByUsername(member.getUsername()) != null) {
            return false;  // 중복 있음
        }

        // 2. 비밀번호 암호화
        member.setPassword(encoder.encode(member.getPassword()));

        // 3. DB 저장
        return memberDao.insertMember(member) > 0;
    }

    /**
     * 로그인
     * @return 로그인 성공 시 MemberVO, 실패 시 null
     */
    public MemberVO login(String username, String rawPassword, HttpSession session) {
        MemberVO member = memberDao.findByUsername(username);

        // 회원 존재 + 비밀번호 일치 확인
        if (member != null && encoder.matches(rawPassword, member.getPassword())) {
            // 세션에 로그인 정보 저장 (password는 노출시키지 말고 null 처리)
            MemberVO loginMember = MemberVO.builder()
                .id(member.getId())
                .username(member.getUsername())
                .nickname(member.getNickname())
                .createdAt(member.getCreatedAt())
                .updatedAt(member.getUpdatedAt())
                .build();

            session.setAttribute("loginMember", loginMember);
            return loginMember;
        }

        return null;  // 로그인 실패
    }

    /**
     * 로그아웃
     */
    public void logout(HttpSession session) {
        session.removeAttribute("loginMember");
        session.invalidate();
    }

    /**
     * 현재 로그인한 회원 조회
     */
    public MemberVO getCurrentMember(HttpSession session) {
        return (MemberVO) session.getAttribute("loginMember");
    }

    /**
     * 내 정보 수정 (닉네임, 비밀번호 변경 등)
     */
    @Transactional
    public boolean updateMember(MemberVO updateMember, HttpSession session) {
        MemberVO currentMember = getCurrentMember(session);
        if (currentMember == null || !currentMember.getId().equals(updateMember.getId())) {
            return false;  // 본인만 수정 가능
        }

        // 비밀번호 변경 시 암호화
        if (updateMember.getPassword() != null && !updateMember.getPassword().isEmpty()) {
            updateMember.setPassword(encoder.encode(updateMember.getPassword()));
        } else {
            // 비밀번호 변경 안 하면 기존 값 유지
            updateMember.setPassword(currentMember.getPassword());
        }

        return memberDao.updateMember(updateMember) > 0;
    }

    /**
     * username 중복 체크 (회원가입 시 AJAX용)
     */
    public boolean isUsernameDuplicate(String username) {
        return memberDao.findByUsername(username) != null;
    }

}
