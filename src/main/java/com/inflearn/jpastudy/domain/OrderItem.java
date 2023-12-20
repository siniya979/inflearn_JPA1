package com.inflearn.jpastudy.domain;

import com.inflearn.jpastudy.domain.item.Item;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "order_item")
@Getter @Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED) // 기본 생성자 제한
public class OrderItem {

    @Id @GeneratedValue
    @Column(name = "order_item_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id")
    private Order order;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id")
    private Item item;

    private int orderPrice;
    private int count;

    // 기본 생성자 접근 제한 롬복으로 해결
/*    protected OrderItem(){
    }*/

    // 생성 메서드 //
    public static OrderItem createOrderItem(Item item, int orderPrice, int count){
        OrderItem orderItem = new OrderItem();
        orderItem.setItem(item);
        orderItem.setOrderPrice(orderPrice);
        orderItem.setCount(count);

        item.removeStock(count);
        return orderItem;
    }

    // 비즈니스 로직 //
    public void cancel(){
        getItem().addStock(count); // 재고수량 되돌리기
    }

    // 조회 로직 //

    /**
    * 주문상품 전체 가격 조회
    * */
    public int getTotalPrice(){
        return getOrderPrice() * getCount();
    }

}
