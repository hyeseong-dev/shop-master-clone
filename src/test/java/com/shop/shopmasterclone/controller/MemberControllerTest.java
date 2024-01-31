package com.shop.shopmasterclone.controller;

import com.shop.shopmasterclone.dto.MemberFormDto;
import com.shop.shopmasterclone.entity.Member;
import com.shop.shopmasterclone.service.MemberService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestBuilders;
import org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;


import static org.junit.jupiter.api.Assertions.*;

// MemberControllerTest 클래스: MemberController의 기능을 테스트하기 위한 클래스
@SpringBootTest // 스프링 부트 통합 테스트 환경 설정
@AutoConfigureMockMvc // MockMvc 자동 설정
@Transactional // 테스트 케이스별로 트랜잭션 관리 및 롤백
@TestPropertySource(locations="classpath:application-test.properties") // 테스트 환경을 위한 별도의 프로퍼티 파일 사용
class MemberControllerTest {

    @Autowired
    private MemberService memberService; // MemberService 주입

    @Autowired
    private MockMvc mockMvc; // MockMvc 주입

    @Autowired
    PasswordEncoder passwordEncoder; // 비밀번호 인코더 주입

    // createMember 메소드: 테스트를 위한 회원 생성 로직
    public Member createMember(String email, String password){
        MemberFormDto memberFormDto = new MemberFormDto(); // DTO 객체 생성
        memberFormDto.setEmail(email); // 이메일 설정
        memberFormDto.setName("홍길동"); // 이름 설정
        memberFormDto.setAddress("서울시 마포구 합정동"); // 주소 설정
        memberFormDto.setPassword(password); // 비밀번호 설정
        Member member = Member.createMember(memberFormDto, passwordEncoder); // Member 객체 생성
        return memberService.saveMember(member); // 생성된 Member 객체 저장
    }

    // 로그인 성공 테스트
    @Test
    @DisplayName("로그인 성공 테스트")
    public void loginSuccessTest() throws Exception{
        String email = "test@gmail.com"; // 테스트 이메일
        String password = "1234"; // 테스트 비밀번호

        this.createMember(email, password); // 테스트용 회원 생성

        // MockMvc를 사용해 로그인 테스트 수행
        mockMvc.perform(SecurityMockMvcRequestBuilders.formLogin()
                        .userParameter("email") // 이메일 파라미터 설정
                        .loginProcessingUrl("/members/login") // 로그인 처리 URL 설정
                        .user(email) // 이메일 설정
                        .password(password)) // 비밀번호 설정
                .andExpect(SecurityMockMvcResultMatchers.authenticated()); // 인증된 상태 확인
    }

    // 로그인 실패 테스트
    @Test
    @DisplayName("로그인 실패 테스트")
    public void loginFailTest() throws Exception{
        String email = "test@gmail.com"; // 테스트 이메일
        String password = "1234"; // 테스트 비밀번호

        this.createMember(email, password); // 테스트용 회원 생성

        // MockMvc를 사용해 잘못된 비밀번호로 로그인 테스트 수행
        mockMvc.perform(SecurityMockMvcRequestBuilders.formLogin()
                        .userParameter("email") // 이메일 파라미터 설정
                        .loginProcessingUrl("/members/login") // 로그인 처리 URL 설정
                        .user(email) // 이메일 설정
                        .password("12345")) // 잘못된 비밀번호 설정
                .andExpect(SecurityMockMvcResultMatchers.unauthenticated()); // 인증되지 않은 상태 확인
    }

    // 회원 가입 입력값 오류 테스트
    @Test
    @DisplayName("회원 가입 - 입력값 오류")
    public void memberFormDtoValidationTest() throws Exception {
        // MockMvc를 사용해 잘못된 값을 가진 회원 가입 요청 테스트 수행
        mockMvc.perform(post("/members/new")
                        .param("name", "") // 잘못된 이름 값 설정
                        .param("email", "invalid-email") // 잘못된 이메일 값 설정
                        .param("password", "123") // 잘못된 비밀번호 값 설정
                        .param("address", "") // 잘못된 주소 값 설정
                        .with(csrf())) // CSRF 토큰 포함
                .andExpect(status().isOk()) // 상태 코드 확인 (OK)
                .andExpect(view().name("member/memberForm")) // 반환 뷰 확인
                .andExpect(model().attributeHasFieldErrors("memberFormDto", "name", "email", "password", "address")); // 모델의 필드 오류 확인
    }
}
