package com.rqs.auth;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.rqs.auth.mapper.SysRoleMapper;
import com.rqs.model.system.SysRole;
import org.junit.jupiter.api.Test;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;


import java.util.List;

@SpringBootTest
@MapperScan("com.rqs.auth.mapper")
public class TestMpDemo1 {
    @Autowired
    private SysRoleMapper mapper;

    //查询所有记录
    @Test
    public void getAll() {
        List<SysRole> list = mapper.selectList(null);
        System.out.println(list);
    }
    //添加操作
    @Test
    public void add() {
        SysRole role=new SysRole();
        role.setRoleName("角色管理员");
        role.setRoleCode("role");
        role.setDescription("角色管理员");
        int rows = mapper.insert(role);
        System.out.println(role);
    }

    //修改操作
    @Test
    public void update() {
        //1.根据id查询
        SysRole role = mapper.selectById(9);
        //2.设置修改值
        role.setRoleName("角色管理员");
        //3.调用方法实现最终修改
        int rows = mapper.updateById(role);
        System.out.println(rows);
    }
    //条件查询
    @Test
    public void testQuery1() {
        QueryWrapper<SysRole> wrapper = new QueryWrapper<>();
        wrapper.eq("role_name", "总经理");
        List<SysRole> list = mapper.selectList(wrapper);
        System.out.println(list);
    }
    @Test
    public void testQuery2() {
        LambdaQueryWrapper<SysRole> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysRole::getRoleName, "总经理");
        List<SysRole> list = mapper.selectList(wrapper);
        System.out.println(list);
    }



}
