package com.tongxue.springdemo.entity;

import com.tongxue.springdemo.annotation.CompareProperty;
import com.tongxue.springdemo.enums.StatusEnum;
import com.tongxue.springdemo.service.DemoService;
import lombok.AllArgsConstructor;
import lombok.Data;
import java.util.List;

@Data
@AllArgsConstructor
public class User {

    @CompareProperty(name = "名称")
    public String name;

    @CompareProperty(name = "年纪")
    private Integer age;

    @CompareProperty(name = "状态", executeClass = StatusEnum.class, executeMethod = "getStatusDesc")
    private Integer status;

    @CompareProperty(name = "爱好", executeClass = DemoService.class, executeMethod = "getHobby", methodParamType = {List.class,Integer.class}, methodParamField = {"hobby","type"}, staticMethod = false, ignoreCompare = true)
    private List<String> hobby;

    @CompareProperty(name = "性别", executeClass = DemoService.class, executeMethod = "getGenderDesc", staticMethod = false)
    private Integer gender;

    @CompareProperty(name = "类型")
    private Integer type;
}
