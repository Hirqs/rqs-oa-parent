package com.rqs.auth.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.rqs.auth.service.SysRoleService;
import com.rqs.common.result.Result;
import com.rqs.model.system.SysRole;
import com.rqs.vo.system.SysRoleQueryVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
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

    /**
     * page:当前页
     * limit:每一页显示的记录数
     * sysRoleQueryVo:以对象的形式传递条件
     */
    @ApiOperation("条件分页查询")
    @GetMapping("{page}/{limit}")
    public Result pageQueryRole(@PathVariable Long page,
                                @PathVariable long limit,
                                SysRoleQueryVo sysRoleQueryVo) {
        //调用service中的方法实现分页
        // 1.创建Page对象，传递分页相关参数
        Page<SysRole> pageParam = new Page<>(page, limit);
        // 2.封装条件，判断条件是否为空，不为空则进行封装
        LambdaQueryWrapper<SysRole> wrapper = new LambdaQueryWrapper<>();
        String roleName = sysRoleQueryVo.getRoleName();
        if (!StringUtils.isEmpty(roleName)) {
            //封装
            wrapper.like(SysRole::getRoleName, roleName);
        }
        // 3.调用方法进行实现
        Page<SysRole> pageResult = sysRoleService.page(pageParam, wrapper);
        return Result.ok(pageResult);
    }


}
