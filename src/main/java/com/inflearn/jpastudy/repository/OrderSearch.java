package com.inflearn.jpastudy.repository;

import com.inflearn.jpastudy.domain.OrderStatus;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class OrderSearch {

    private String memberName;
    private OrderStatus orderStatus;

}
