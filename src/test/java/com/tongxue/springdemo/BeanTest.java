package com.tongxue.springdemo;

import com.alibaba.fastjson.JSONObject;
import com.tongxue.springdemo.entity.User;
import com.tongxue.springdemo.util.CompareDiffUtil;
import com.tongxue.springdemo.vo.UserInfoVO;
import com.tongxue.springdemo.vo.UserVO;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.BeanUtils;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Map;

@Slf4j
public class BeanTest {

    @Test
    public void copyTest() {
        User user = new User("zhangsan", 20, 1, new ArrayList<>() {{
            add("足球");
        }}, 1, 1);
        User user1 = new User("lisi", 22, 2, new ArrayList<>() {{
            add("篮球");
        }}, 2, 2);

        BeanUtils.copyProperties(user, user1);

        System.out.println(JSONObject.toJSONString(user));
        System.out.println(JSONObject.toJSONString(user1));
    }


    @Test
    public void copyTest1() throws InvocationTargetException, IllegalAccessException {
//        UserVO user = new UserVO("zhangsan", "xiaozhang", 20, 1, new ArrayList<>() {{
//            add("足球");
//        }}, 1, 1);
        UserVO user = new UserVO("zhangsan", "xiaozhang", 22, 1, new ArrayList<>() {{
            add("篮球1");
        }},1);
        UserInfoVO user1 = new UserInfoVO("lisi", 22, 2, new ArrayList<>() {{
            add("篮球");
        }}, 2, 2, new Date());

        Map<String, Object> differentProperty = CompareDiffUtil.getDifferentProperty(user, user1);
        System.out.println(differentProperty);
    }

}
