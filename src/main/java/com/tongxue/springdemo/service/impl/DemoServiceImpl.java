package com.tongxue.springdemo.service.impl;

import com.tongxue.springdemo.entity.Demo;
import com.tongxue.springdemo.service.DemoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

@Slf4j
@Service
public class DemoServiceImpl implements DemoService {


    @Override
    public Demo get(String name) {
        return new Demo(name, 12, new Date());
    }

    @Override
    public String getHobby(String name) {
        if ("1".equals(name)) {
            return "足球";
        }
        return "篮球";
    }

    @Override
    public List<String> getHobby(List<String> name, Integer type) {
        log.info("name:{} type:{}", name, type);
        if (type == 1){
            return new ArrayList<>(){{
                add("足球");
                add("篮球");
            }};
        } else if (type ==2){
            return new ArrayList<>(){{
                add("乒乓球");
                add("羽毛球");
            }};
        }
        return Collections.emptyList();
    }

    @Override
    public String getGenderDesc(Integer gender) {
        if (1 == gender) {
            return "男";
        } else if (2 == gender) {
            return "女";
        }
        return null;
    }

}
