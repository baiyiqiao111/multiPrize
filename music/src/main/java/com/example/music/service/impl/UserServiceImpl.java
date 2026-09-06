package com.example.music.service.impl;

import com.example.music.controller.cmd.ModifyUserCmd;
import com.example.music.controller.cmd.RegisterCmd;
import com.example.music.controller.vo.BaseVo;
import com.example.music.entity.User;
import com.example.music.entity.UserToken;
import com.example.music.exception.PasswordWrongException;
import com.example.music.exception.UserNotExistException;
import com.example.music.mapper.UserMapper;
import com.example.music.producer.AddUserProducer;
import com.example.music.repository.TagRepository;
import com.example.music.repository.UserTokenRepository;
import com.example.music.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
@Slf4j
@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;
    @Autowired
    private UserTokenRepository userTokenRepository;
    @Autowired
    private TagRepository tagRepository;
    @Autowired
    private AddUserProducer addUserProducer;

    //todo:添加用户入参是一个cmd
    @Override
    public void register(RegisterCmd registerCmd) {
        // 用户名查重
        User exist = userMapper.selectByExactName(registerCmd.getName());
        if (exist != null) {
            throw new IllegalArgumentException("用户名已存在：" + registerCmd.getName());
        }
        User user = new User();
        user.setName(registerCmd.getName());
        user.setPassword(registerCmd.getPassword());
        user.setGender(registerCmd.getGender());
        user.setEmail(registerCmd.getEmail());
        user.setAge(registerCmd.getAge());
        user.setInterests(registerCmd.getInterests());

        if (user.getRegisterTime() == null) {
            user.setRegisterTime(new Date());
        }
        userMapper.insert(user);
        User userInDb = userMapper.selectByExactName(user.getName());
        String[] newTagArray = registerCmd.getInterests().split(",");
        Set<String> removed = shouldRemoved(new String[0], newTagArray);
        Set<String> add = shouldAdd(new String[0], newTagArray);
        log.info("需要删除的标签有：{}",removed);
        for (String o : removed) {
            tagRepository.delete(o,userInDb.getId());
        }
        log.info("需要新增的标签有：{}",add);
        for (String o : add) {
            tagRepository.add(o,userInDb.getId());
        }
    }

    @Override
    public void login(String name, String password) {
        User user = userMapper.selectByExactName(name);
        if (user == null) {
           throw new UserNotExistException("用户不存在");
        }
        if (!user.getPassword().equals(password)) {
            throw new PasswordWrongException("用户密码错误");
        }
        UserToken userToken = new UserToken();
        userToken.setUserId(user.getId());
        userToken.setName(name);
        userTokenRepository.add(user.getId(),userToken);
    }

    @Override
    public void delete(int id) {
        userMapper.deleteById(id);
    }

    @Override
    public void modify(ModifyUserCmd modifyUserCmd) {
        User user = userMapper.selectById(modifyUserCmd.getId());
        user.setName(modifyUserCmd.getName());
        user.setPassword(modifyUserCmd.getPassword());
        user.setEmail(modifyUserCmd.getEmail());
        user.setId(modifyUserCmd.getId());
        user.setGender(modifyUserCmd.getGender());
        user.setInterests(modifyUserCmd.getInterests());
        user.setAge(modifyUserCmd.getAge());
        userMapper.update(user);
    }

    @Override
    public List<User> queryAll() {
        return userMapper.selectAll();
    }

    @Override
    public List<User> queryByName(String name) {
        return userMapper.selectByName(name);
    }

    @Override
    public User queryById(int id) {
        return userMapper.selectById(id);
    }

    @Override
    @Transactional
    public void selectInterests(int userId, String interests) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new UserNotExistException("用户不存在");
        }
        String[] oldTagArray = user.getInterests().split(",");
        user.setInterests(interests);
        userMapper.update(user);
        String[] newTagArray = interests.split(",");
        Set<String> removed = shouldRemoved(oldTagArray, newTagArray);
        Set<String> add = shouldAdd(oldTagArray, newTagArray);
        log.info("需要删除的标签有：{}",removed);
        for (String o : removed) {
            tagRepository.delete(o,userId);
        }
        log.info("需要新增的标签有：{}",add);
        for (String o : add) {
            tagRepository.add(o,userId);
        }
    }
    private Set<String> shouldRemoved(String[] oldTagArray,String[] newTagArray){
        Set<String> newSet = new HashSet<>();
        Set<String> oldSet = new HashSet<>();
        for(int i=0;i<oldTagArray.length;i++){
            oldSet.add(oldTagArray[i]);
        }
        for(int i=0;i<newTagArray.length;i++){
            newSet.add(newTagArray[i]);
        }
        oldSet.removeAll(newSet);
        return oldSet;
    }

    private Set<String> shouldAdd(String[] oldTagArray,String[] newTagArray){
        Set<String> newSet = new HashSet<>();
        Set<String> oldSet = new HashSet<>();
        for(int i=0;i<oldTagArray.length;i++){
            oldSet.add(oldTagArray[i]);
        }
        for(int i=0;i<newTagArray.length;i++){
            newSet.add(newTagArray[i]);
        }
        newSet.removeAll(oldSet);
        return newSet;
    }
}
