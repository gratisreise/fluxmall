package com.fluxmall.controller;

import com.fluxmall.domain.vo.MemberVO;
import com.fluxmall.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;

@Controller
@RequestMapping("/member")
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;

    // 회원가입 폼
    @GetMapping("/register")
    public String registerForm() {
        return "member/register";  // /WEB-INF/views/member/register.jsp
    }

    // 회원가입 처리
    @PostMapping("/register")
    public String register(MemberVO member, Model model) {
        boolean success = memberService.register(member);
        if (success) {
            return "redirect:/member/login";  // 성공 시 로그인 페이지로
        } else {
            model.addAttribute("error", "아이디가 이미 존재합니다.");
            return "member/register";
        }
    }

    // 아이디 중복 체크 (AJAX)
    @GetMapping("/checkUsername")
    @ResponseBody
    public boolean checkUsername(@RequestParam String username) {
        return memberService.isUsernameDuplicate(username);
    }

    // 로그인 폼
    @GetMapping("/login")
    public String loginForm(@RequestParam(required = false) String error, Model model) {
        if (error != null) {
            model.addAttribute("error", "아이디 또는 비밀번호가 올바르지 않습니다.");
        }
        return "member/login";
    }

    // 로그아웃 (Spring Security 설정에 따라 /member/logout POST 처리)
    @PostMapping("/logout")
    public String logout() {
        return "redirect:/";
    }

    // 마이페이지 (내 정보 조회)
    @GetMapping("/myPage")
    public String myPage(HttpSession session, Model model) {
        MemberVO loginMember = memberService.getCurrentMember(session);
        if (loginMember == null) {
            return "redirect:/member/login";
        }
        model.addAttribute("member", loginMember);
        return "member/myPage";
    }

    // 내 정보 수정 처리
    @PostMapping("/update")
    public String updateMember(MemberVO updateMember, HttpSession session, Model model) {
        boolean success = memberService.updateMember(updateMember, session);
        if (success) {
            model.addAttribute("message", "정보가 수정되었습니다.");
        } else {
            model.addAttribute("error", "수정 실패 (본인 확인 필요)");
        }
        return "redirect:/member/myPage";
    }
}