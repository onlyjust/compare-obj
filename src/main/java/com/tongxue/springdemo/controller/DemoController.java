package com.tongxue.springdemo.controller;

import com.alibaba.fastjson.JSONObject;
import com.tongxue.springdemo.entity.Demo;
import com.tongxue.springdemo.entity.User;
import com.tongxue.springdemo.service.DemoService;
import com.tongxue.springdemo.util.CompareDiffUtil;
import com.tongxue.springdemo.util.CompareObjUtil;
import com.tongxue.springdemo.vo.UserInfoVO;
import com.tongxue.springdemo.vo.UserVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Date;
import java.util.Map;

@RestController
@RequestMapping("/demo")
public class DemoController {

    @Autowired
    private DemoService demoService;

    @GetMapping(value = "/getName")
    public String getName(){
        Demo zhangsan = demoService.get("zhangsan");
        System.out.println(zhangsan);
        return "hello world";
    }


    @GetMapping(value = "/getCompare")
    public Map<String, Object> getCompare(){
        User user = new User("zhangsan",12,1,new ArrayList<>(){{add("2");}},1,1);
        User user1 = new User("lisi",12,2,new ArrayList<>(){{add("1");}},2,2);
        Map<String, Object> differentProperty = CompareObjUtil.getDifferentProperty(user, user1);
        System.out.println(JSONObject.toJSONString(differentProperty));
        return differentProperty;
    }


    @GetMapping(value = "/getCompareObj")
    public Map<String, Object> getCompareObj(){
//        UserVO user = new UserVO("zhangsan", "小张",12,1,new ArrayList<>(){{add("2");}},1,1);
        UserVO user = new UserVO("zhangsan", "小张",12,1,new ArrayList<>(){{add("2");}},1);
        UserInfoVO user1 = new UserInfoVO("lisi",12,2,new ArrayList<>(){{add("1");}},2,2, new Date());
        Map<String, Object> differentProperty = CompareDiffUtil.getDifferentProperty(user, user1);
        System.out.println(JSONObject.toJSONString(differentProperty));
        return differentProperty;
    }
}
