package com.netease.cloud.dao;

import com.netease.cloud.model.Order;
import com.netease.cloud.model.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @Author xiaoxuxuy
 * @Date 2024年03月07日
 * @Version: 1.0
 */
@Mapper
@Repository
public interface TestMapper {

    List<User> allUserList();

    List<User> allUserList1();

    @Select(value = "select * from user")
    List<User> allUserList2();

}
