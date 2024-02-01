package com.shop.shopmasterclone.entity;

import com.shop.shopmasterclone.repository.MemberRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
@TestPropertySource(locations = "classpath:application-test.properties")
class MemberTest {

    @Autowired
    MemberRepository memberRepository;

    @PersistenceContext
    EntityManager entityManager;

    @Test
    @DisplayName("멤버 생성 시 Auditing 정보가 정확히 기록되어야 함")
    public void givenNewMember_whenSaved_thenAuditingInformationShouldBeRecordedCorrectly() {
        // Given: 새로운 멤버 생성
        Member newMember = new Member();

        // When: 멤버 저장
        memberRepository.save(newMember);
        entityManager.flush();
        entityManager.clear();

        // Then: Auditing 정보 검증
        Member member = memberRepository.findById(newMember.getId())
                .orElseThrow(EntityNotFoundException::new);
        assertNotNull(member.getRegTime());
        assertNotNull(member.getUpdateTime());
        assertNotNull(member.getCreatedBy());
        assertNotNull(member.getModifiedBy());
    }
}