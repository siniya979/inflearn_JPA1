package com.inflearn.jpastudy.service;

import com.inflearn.jpastudy.domain.Member;
import com.inflearn.jpastudy.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
// 데이터 변경이 없는 읽기 전용, 영속성 컨텍스트를 플러시 하지 않는다.
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;

    // 회원 가입
    @Transactional
    public Long join(Member member){
        validationDuplicateMembmer(member);
        memberRepository.save(member);
        return member.getId();
    }

    // 검증 로직이 있어도 실무에서 멀티 쓰레드 상황 고려해 회원 테이블의 회원명 컬럼에 유니크 제약 조건 추가하는 것이 안전.
    private void validationDuplicateMembmer(Member member) {
        List<Member> findMembers = memberRepository.findByName(member.getName());
        if(!findMembers.isEmpty()){
            throw new IllegalArgumentException("이미 존재하는 회원입니다.");
        }
    }

    // 전체 회원 조회
    public List<Member> findMembers(){
        return memberRepository.findAll();
    }

    // 특정 회원 조회
    public Member findOne(Long memberId){
        return memberRepository.findOne(memberId);
    }
}
