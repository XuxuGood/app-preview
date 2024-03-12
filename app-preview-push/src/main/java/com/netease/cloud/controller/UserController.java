package com.netease.cloud.controller;

import com.netease.cloud.dao.UserMapper;
import com.netease.cloud.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

/**
 * @Author xiaoxuxuy
 * @Date 2024年03月05日
 * @Version: 1.0
 */
@RestController
public class UserController {

    @Autowired
    private UserMapper userMapper;

    @GetMapping("/allUserList")
    public List<User> allUserList() {
        return userMapper.allUserList();
    }

    @GetMapping("/getUserById/{id}")
    public User getUserById(@PathVariable int id) {
        return userMapper.getUserById(id);
    }

    @PostMapping("/addUser")
    public String addUser() {
        userMapper.addUser(new User("Lucy" + UUID.randomUUID(), UUID.randomUUID().toString()));
        return "用户添加成功...";
    }

    @PostMapping("/updateUser/{id}")
    public String updateUser(@PathVariable int id) {
        userMapper.updateUser(new User(id, "lili", UUID.randomUUID().toString()));
        return "用户修改成功";
    }

    @DeleteMapping("/deleteUser/{id}")
    public String deleteUser(@PathVariable int id) {
        userMapper.deleteUser(id);
        return "用户删除成功...";
    }

}
