package com.rqs.auth.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.rqs.auth.mapper.SysRoleMapper;
import com.rqs.auth.service.SysRoleService;
import com.rqs.auth.service.SysUserRoleService;
import com.rqs.model.system.SysRole;
import com.rqs.model.system.SysUserRole;
import com.rqs.vo.system.AssginRoleVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class SysRoleServiceImpl extends ServiceImpl<SysRoleMapper, SysRole> implements SysRoleService {

    @Autowired
    private SysUserRoleService sysUserRoleService;

    @Autowired
    private SysRoleMapper sysRoleMapper;

    /***
     * 1.在页面中显示所有角色 2.根据用户id在页面中显示当前用户所属的所有角色
     * @param userId
     * @return  roleMap，角色Map中包含两个数据 1.allRoleList 2.assginRoleList
     *          1.allRoleList：系统中含有的所有角色对象列表
     *          2.assginRoleList：当前用户所属的所有角色对象列表
     */
    // 角色分配接口一的实现 进入角色分配页面后 1.在页面中显示所有角色 在页面中显示当前用户所属的所有角色
    @Override
    public Map<String, Object> getRoleByUserId(Long userId) {
        //1.为了在页面中显示所有角色，需要查询角色表，返回【所有角色的list集合】
        List<SysRole> allRoleList = sysRoleMapper.selectList(null);
        //2.为了在页面中显示当前用户所属的所有角色，可以根据当前用户的id，在用户角色关系表中查询用户的所有角色id
        LambdaQueryWrapper<SysUserRole> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysUserRole::getUserId, userId);
        List<SysUserRole> roleList_of_Current_User = sysUserRoleService.list(wrapper);
        //3.从查询出的当前用户的角色列表中，获取所有角色的id
        //写法1【可以理解为遍历当前用户的角色列表，得到角色id】
        List<Long> roleIdList_of_Current_User = roleList_of_Current_User.stream().map(role -> role.getRoleId()).collect(Collectors.toList());
        //写法2 传统方式
//        List<Long> roleIdList_of_Current_User = new ArrayList<>();
//        for (SysUserRole role : roleList_of_Current_User) {
//            Long roleId = role.getRoleId();
//            roleIdList_of_Current_User.add(roleId);
//        }
        //4.根据查询出的所有角色id，找到对应的所有角色对象
        //如果查询到的角色id在【所有角色的list集合】中存在，则取到该角色对象，保存到角色对象list集合中
        List<SysRole> assginRoleList = new ArrayList<>();
        for (SysRole role:allRoleList) {
            //进行比较
            if (roleIdList_of_Current_User.contains(role.getId())) {
                assginRoleList.add(role);
            }
        }
        //4.把得到的两部分数据封装到map集合中
        Map<String, Object> roleMap = new HashMap<>();
        roleMap.put("allRoleList", allRoleList);//第一部分数据，用于在页面中显示所有角色
        roleMap.put("assginRoleList", assginRoleList);//第二部分数据，用于在页面中显示当前用户所属的所有角色
        return roleMap;
    }

    /**
     *依照角色id列表，为用户分配角色
     * @param assginRoleVo：包含两部分数据，用户id，角色id列表
     */
    //角色分配接口二的实现 保存分配的角色 2.选择想要为用户设置的角色，将用户最终选中的所有角色添加到数据库中
    @Override
    public void doAssign(AssginRoleVo assginRoleVo) {
        //根据用户角色关系表中的用户id，把用户之前分配的角色数据删除
        LambdaQueryWrapper<SysUserRole> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysUserRole::getRoleId, assginRoleVo.getUserId());
        boolean remove = sysUserRoleService.remove(wrapper);
        //重新进行分配
        List<Long> roleIdList = assginRoleVo.getRoleIdList();
        for (Long roleId:
             roleIdList) {
            if (StringUtils.isEmpty(roleId)) {
                continue;
            }
            SysUserRole sysUserRole = new SysUserRole();
            sysUserRole.setUserId(assginRoleVo.getUserId());
            sysUserRole.setRoleId(roleId);
            sysUserRoleService.save(sysUserRole);
        }
    }
}
