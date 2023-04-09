package com.rqs.auth.controller;

import com.rqs.auth.service.SysRoleService;
import com.rqs.common.result.Result;
import com.rqs.model.system.SysRole;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Api(tags = "角色管理接口")
@RestController
@RequestMapping("/admin/system/sysRole")
public class SysRoleController {


    //注入service
    @Autowired
    private SysRoleService sysRoleService;

    //查询所有角色
    //http://localhost:8800/admin/system/sysRole/findAll
//    @GetMapping("/findAll")
//    public List<SysRole> findAll() {
//        List<SysRole> list = sysRoleService.list();
//        return list;
//    }

    //http://localhost:8800/admin/system/sysRole/findAll
    @ApiOperation("查询所有角色")
    @GetMapping("/findAll")
    public Result findAll() {
        List<SysRole> list = sysRoleService.list();
        return Result.ok(list);
    }


}
