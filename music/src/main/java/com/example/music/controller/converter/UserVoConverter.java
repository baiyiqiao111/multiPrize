package com.example.music.controller.converter;

import com.example.music.controller.vo.UserVo;
import com.example.music.entity.User;

import java.util.ArrayList;
import java.util.List;

public class UserVoConverter {
    public static UserVo convertToVo(User user) {
        UserVo userVo = new UserVo();
        userVo.setId(user.getId());
        userVo.setName(user.getName());
        userVo.setPassword(user.getPassword());
        userVo.setGender(user.getGender());
        userVo.setRegisterTime(user.getRegisterTime());
        userVo.setEmail(user.getEmail());
        userVo.setAge(user.getAge());
        userVo.setInterests(user.getInterests());
        return userVo;
    }
    public static List<UserVo> convertToVoList(List<User> userList){
        List<UserVo> userVoList = new ArrayList<>();
        for(int i=0;i<userList.size();i++){
            UserVo userVo = convertToVo(userList.get(i));
            userVoList.add(userVo);
        }
        return userVoList;
    }
}
