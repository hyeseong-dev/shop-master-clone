package com.shop.shopmasterclone.service;

import com.shop.shopmasterclone.entity.Member;
import com.shop.shopmasterclone.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;

    public Member saveMember(Member member){
        validateDuplidateMember(member);
        return memberRepository.save(member);
    }

    private void validateDuplidateMember(Member member){
        Member savedMember = memberRepository.findByEmail(member.getEmail());
        if(savedMember != null){
            throw new IllegalStateException("이미 가입된 회원입니다.");
        }
    }
}
