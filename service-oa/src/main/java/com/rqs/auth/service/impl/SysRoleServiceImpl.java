package com.rqs.auth.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.rqs.auth.mapper.SysRoleMapper;
import com.rqs.auth.service.SysRoleService;
import com.rqs.model.system.SysRole;
import org.springframework.stereotype.Service;

@Service
public class SysRoleServiceImpl extends ServiceImpl<SysRoleMapper, SysRole> implements SysRoleService {

}
