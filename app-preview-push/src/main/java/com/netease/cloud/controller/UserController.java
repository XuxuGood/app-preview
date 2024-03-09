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

    @GetMapping("/getUserById2")
    public User getUserById2(@RequestParam(value = "id") int id) {
        return userMapper.getUserById4(id);
    }

    @GetMapping(value = "/getUserById7")
    public User getUserById7(@RequestParam(value = "id") int id) {
        return userMapper.getUserById7(id);
    }

    @GetMapping(value = "/getUserById8")
    public User getUserById8(@RequestParam(value = "id") int id) {
        return userMapper.getUserById8(id);
    }

   @GetMapping(value = "/getUserById9")
    public User getUserById9(@RequestParam(value = "id") int id) {
        return userMapper.getUserById9(id);
    }

    @GetMapping(value = "/getUserById10")
    public User getUserById10(@RequestParam(value = "id") int id) {
        return userMapper.getUserById10(id);
    }

    @GetMapping( "/getUserById11")
    public User getUserById11(@RequestParam(value = "id") int id) {
        return userMapper.getUserById11(id);
    }

    @GetMapping("/getUserById12")
    public User getUserById12() {
        return userMapper.getUserById12(1);
    }

    @GetMapping("/getUserById14")
    public User getUserById14() {
        return userMapper.getUserById12(1);
    }

    @GetMapping("/getUserById13")
    public User getUserById13() {
        return userMapper.getUserById12(1);
    }

    @GetMapping("/getUserById15")
    public User getUserById15() {
        return userMapper.getUserById12(1);
    }

    @GetMapping("/getUserById16")
    public User getUserById16() {
        return userMapper.getUserById(1);
    }

    @GetMapping("/getUserById19")
    public User getUserById19() {
        return userMapper.getUserById(1);
    }

    @GetMapping("/getUserById20")
    public User getUserById20() {
        return userMapper.getUserById(1);
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
