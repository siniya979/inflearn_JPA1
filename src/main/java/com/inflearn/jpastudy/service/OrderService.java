package com.inflearn.jpastudy.service;

import com.inflearn.jpastudy.domain.*;
import com.inflearn.jpastudy.domain.item.Item;
import com.inflearn.jpastudy.repository.ItemRepository;
import com.inflearn.jpastudy.repository.MemberRepository;
import com.inflearn.jpastudy.repository.OrderRepository;
import com.inflearn.jpastudy.repository.OrderSearch;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final MemberRepository memberRepository;
    private final ItemRepository itemRepository;

    // 주문
    @Transactional
    public Long order(Long memberId, Long itemId, int count){

        //엔티티 조회
        Member member = memberRepository.findOne(memberId);
        Item item = itemRepository.findOne(itemId);

        // 배송정보 생성
        Delivery delivery = new Delivery();
        delivery.setAddress(member.getAddress());
        delivery.setStatus(DeliveryStatus.READY);

        OrderItem orderItem = OrderItem.createOrderItem(item, item.getPrice(), count);
        // Order order = new Order(); < 기본 생성자로 생성해서 로직이 꼬이지 않도록 생성자의 접근제어자 제한하기

        // 주문 생성
        Order order = Order.createOrder(member, delivery, orderItem);

        // 주문 저장
        // delivery 레파지토리를 따로 만들어서 save 하지 않는 이유는 cascade 설정을 ALL 했기 때문.
        // delivery 는 다른 곳에서 참조하지 않음. 이런 프라이빗 오너일 때 캐스캐이드 사용 가능
        // 그러나 다른 곳에서도 참조하는데 캐스캐이드 사용하면 데이터가 날아갈 수도 있음. 조심해야함 따로 레포 파는게 안
        orderRepository.save(order);
        return order.getId();
    }

    // 취소
    @Transactional
    public void cancelOrder(Long orderId){
        Order order = orderRepository.findOne(orderId);
        order.cancel();
    }

    // 검색
    public List<Order> findOrders(OrderSearch orderSearch){
        return orderRepository.findAll(orderSearch);
    }
}
