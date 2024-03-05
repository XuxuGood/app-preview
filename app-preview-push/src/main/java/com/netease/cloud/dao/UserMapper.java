package com.netease.cloud.dao;

import com.netease.cloud.model.User;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @Author xiaoxuxuy
 * @Date 2024年03月05日
 * @Version: 1.0
 */
@Mapper
@Repository
public interface UserMapper {

    List<User> allUserList();

    User getUserById(int id);

    User getUserById2(int id);

    int addUser(User user);

    int updateUser(User user);

    int deleteUser(int id);

}
