package com.tongxue.springdemo.vo;

import com.tongxue.springdemo.annotation.CompareProperty;
import com.tongxue.springdemo.enums.StatusEnum;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

/**
 * @Author zhangtong
 * @Description:
 * @Date 2022/7/5 15:11
 */
@Data
@AllArgsConstructor
public class UserVO {

    @CompareProperty(name = "名称")
    public String name;

    @CompareProperty(name = "昵称")
    private String nickname;

    @CompareProperty(name = "年纪")
    private Integer age;

    @CompareProperty(name = "状态", executeClass = StatusEnum.class, executeMethod = "getStatusDesc")
    private Integer status;

    @CompareProperty
//    @CompareProperty(name = "爱好", executeClass = DemoService.class, executeMethod = "getHobby", methodParamType = {List.class,Integer.class}, methodParamField = {"hobby","type"}, staticMethod = false, ignoreCompare = true)
    private List<String> hobby;

//    @CompareProperty(name = "性别", executeClass = DemoService.class, executeMethod = "getGenderDesc", staticMethod = false)
//    private Integer gender;

    @CompareProperty(name = "类型")
    private Integer type;



}
