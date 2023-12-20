package com.inflearn.jpastudy.service;

import com.inflearn.jpastudy.domain.Address;
import com.inflearn.jpastudy.domain.Member;
import com.inflearn.jpastudy.domain.Order;
import com.inflearn.jpastudy.domain.OrderStatus;
import com.inflearn.jpastudy.domain.item.Book;
import com.inflearn.jpastudy.domain.item.Item;
import com.inflearn.jpastudy.exception.NotEnoughStockException;
import com.inflearn.jpastudy.repository.OrderRepository;
import jakarta.persistence.EntityManager;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.fail;

@RunWith(SpringRunner.class)
@SpringBootTest
@Transactional
public class OrderServiceTest {

    // 좋은 테스트는 db와 spring 의 의존성 없이 순수하게 메서드를 단위 테스트 하는 것이 좋다.

    @Autowired EntityManager em;
    @Autowired OrderService orderService;
    @Autowired OrderRepository orderRepository;

    @Test
    public void 상품주문() throws Exception{
        // given
        Member member = createMember();

        Book book = createBook("JPA 공부", 10000, 10);

        // when
        int orderCount = 2;
        Long orderId = orderService.order(member.getId(), book.getId(), orderCount);

        // then
        Order getOrder = orderRepository.findOne(orderId);

        Assert.assertEquals("상품 주문시 상태는 ORDER", OrderStatus.ORDER, getOrder.getStatus());
        Assert.assertEquals("주문한 상품 종류 수가 정확해야 한다.", 1, getOrder.getOrderItems().size());
        Assert.assertEquals("주문 가격은 가격 * 수량이다", 10000*orderCount, getOrder.getTotalPrice());
        Assert.assertEquals("주문 수량만큼 재고가 줄어야 한다.", 8, book.getStockQuantity());

    }

    private Book createBook(String name, int price, int stockQuantity) {
        Book book = new Book();
        book.setName(name);
        book.setPrice(price);
        book.setStockQuantity(stockQuantity);
        em.persist(book);
        return book;
    }

    private Member createMember() {
        Member member = new Member();
        member.setName("회원1");
        member.setAddress(new Address("서울", "공항대로", "12345"));
        em.persist(member);
        return member;
    }

    @Test(expected = NotEnoughStockException.class)
     public void  상품주문_재고수량초과() throws Exception{
         // given
        Member member = createMember();
        Item book = createBook("JPA 공부", 10000, 10);

        int orderCount = 11;

        // when
        orderService.order(member.getId(), book.getId(), orderCount);

        // then
        fail("재고 수량 부족 예외가 발생해야 한다.");
      }

      @Test
      public void 주문최소() throws Exception{
          // given
          Member member = createMember();
          Book book = createBook("JPA 공부", 10000, 10);

          int orderCount = 2;
          Long orderId = orderService.order(member.getId(), book.getId(), orderCount);

          // when
          orderService.cancelOrder(orderId);

          // then
          Order getOrder = orderRepository.findOne(orderId);

          Assert.assertEquals("주문이 취소시 상태는 CANCEL 이다. ", OrderStatus.CANCEL, getOrder.getStatus());
          Assert.assertEquals("주문이 취소된 상품은 그만큼 재고가 증가해야한다. ", 10, book.getStockQuantity());
      }

}