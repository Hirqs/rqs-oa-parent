package com.rqs.auth.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.rqs.model.system.SysUser;
import com.rqs.vo.system.AssginRoleVo;

import java.util.Map;

/**
 * <p>
 * 用户表 服务类
 * </p>
 *
 * @author rqs
 * @since 2023-04-11
 */
public interface SysUserService extends IService<SysUser> {

    void updateStatus(Long id, Integer status);
}
