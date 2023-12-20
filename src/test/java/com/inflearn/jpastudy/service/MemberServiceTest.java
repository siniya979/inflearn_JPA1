package com.inflearn.jpastudy.service;

import com.inflearn.jpastudy.domain.Member;
import com.inflearn.jpastudy.repository.MemberRepository;
import jakarta.persistence.EntityManager;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class) // 스프링이랑 같이 엮어서 실행시키겠다.
@SpringBootTest
@Transactional
public class MemberServiceTest {

    @Autowired MemberService memberService;
    @Autowired MemberRepository memberRepository;
    @Autowired EntityManager em;

    @Test
    @Rollback(false) // 해당 주석을 넣지 않으면 insert 할 이유가 없어서 Insert 안 함.
    public void 회원가입() throws Exception{
        // given
        Member member = new Member();
        member.setName("kim");

        // when
        Long saveId = memberService.join(member);

        // then
        assertEquals(member, memberRepository.findOne(saveId));
     }

     @Test(expected = IllegalArgumentException.class)
     public void 중복_회원_예외() throws Exception{
         // given
         Member member1 = new Member();
         Member member2 = new Member();

         member1.setName("Kim");
         member2.setName("Kim");

         // when
         memberService.join(member1);
         memberService.join(member2);

         // then
         fail("예외가 발생해야 함"); // 이 줄까지 오면 실패, 이 줄은 실행되지 않는게 성공
      }
    
}