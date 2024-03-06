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
        List<User> users = userMapper.allUserList();
        for (User user : users) {
            System.out.println(user);
        }
        return users;
    }

    @GetMapping("/getUserById/{id}")
    public User getUserById(@PathVariable int id) {
        return userMapper.getUserById(id);
    }

    @GetMapping("/getUserById3/{id}")
    public User getUserById3(@PathVariable int id) {
        return userMapper.getUserById5(2);
    }

    @GetMapping("/getUserById4/{id}")
    public User getUserById4(@PathVariable int id) {
        return userMapper.getUserById4(id);
    }

    @GetMapping("/getUserById5/{id}")
    public User getUserById5(@PathVariable Integer id) {
        return new User(id, "lili", UUID.randomUUID().toString());
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
