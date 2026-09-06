package com.example.music.mapper;

import com.example.music.entity.User;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Set;

@Mapper
public interface UserMapper {

    /** 新增用户（注册） */
    void insert(User user);

    /** 根据 id 删除 */
    void deleteById(@Param("id") int id);

    /** 修改用户信息 */
    void update(User user);

    /** 查询所有用户 */
    List<User> selectAll();

    /** 根据用户名模糊查询 */
    List<User> selectByName(@Param("name") String name);

    /** 根据 id 查询 */
    User selectById(@Param("id") int id);

    /** 根据用户名精确查询（用于注册查重 / 登录） */
    User selectByExactName(@Param("name") String name);

    List<User> queryByIds(Set<Integer> ids);
}
