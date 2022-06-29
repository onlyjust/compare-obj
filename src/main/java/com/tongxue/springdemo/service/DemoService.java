package com.tongxue.springdemo.service;

import com.tongxue.springdemo.entity.Demo;

import java.util.List;

public interface DemoService {


    Demo get(String name);

    String getHobby(String name);

    List<String> getHobby(List<String> name,Integer type);

    String getGenderDesc(Integer gender);
}
