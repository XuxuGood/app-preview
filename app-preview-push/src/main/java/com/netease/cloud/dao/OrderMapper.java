package com.netease.cloud.dao;

import com.netease.cloud.model.Order;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @Author xiaoxuxuy
 * @Date 2024年03月07日
 * @Version: 1.0
 */
@Repository
public interface OrderMapper {

    List<Order> allOrderList();

}
