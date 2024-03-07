package com.netease.cloud.controller;

import com.netease.cloud.dao.OrderMapper;
import com.netease.cloud.model.Order;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @Author xiaoxuxuy
 * @Date 2024年03月07日
 * @Version: 1.0
 */
@RestController
public class OrderController {

    @Autowired
    private OrderMapper orderMapper;

    @GetMapping("/allOrderList")
    public List<Order> allOrderList() {
        return orderMapper.allOrderList();
    }

}
