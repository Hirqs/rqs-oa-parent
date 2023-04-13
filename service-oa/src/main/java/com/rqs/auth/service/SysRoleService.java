package com.rqs.auth.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.rqs.model.system.SysRole;
import com.rqs.vo.system.AssginRoleVo;

import java.util.Map;


public interface SysRoleService extends IService<SysRole> {

    // 角色分配接口一  进入角色分配页面 1.在页面中显示所有角色 在页面中显示当前用户所属的所有角色
    Map<String, Object> getRoleByUserId(Long userId);
    //角色分配接口二  保存分配的角色 2.选择想要为用户设置的角色，将用户最终选中的所有角色添加到数据库中
    void doAssign(AssginRoleVo assginRoleVo);
}
