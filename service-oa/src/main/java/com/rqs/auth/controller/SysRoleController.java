package com.rqs.auth.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.rqs.auth.service.SysRoleService;
import com.rqs.common.exception.RqsException;
import com.rqs.common.result.Result;
import com.rqs.model.system.SysRole;
import com.rqs.vo.system.SysRoleQueryVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

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
//        try {
//            int a = 10/0;
//        }catch(Exception e) {
//            throw new RqsException(20001,"出现自定义异常");
//        }
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
        // 2.封装条件前，判断条件是否为空，不为空则进行封装
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

    @ApiOperation("添加角色")
    @PostMapping("save")//get请求没有请求体,只有请求行,请求头。
    public Result addSysRole(@RequestBody SysRole sysRole) {//前端用json对象格式传入数据，例如：{"description": "test","roleCode": "test","roleName": "测试"}。后端用java对象接收。
        boolean is_success = sysRoleService.save(sysRole);
        if (is_success) {
            return Result.ok();
        }
        return Result.fail();
    }

    //修改角色步骤，1.根据id查询角色 2.修改角色
    @ApiOperation("根据id查询角色")
    @GetMapping("get/{id}")
    public Result getId(@PathVariable long id) {
        SysRole sysRole = sysRoleService.getById(id);
        return Result.ok(sysRole);
    }
    @ApiOperation("修改角色")
    @PutMapping("update")
    public Result update(@RequestBody SysRole sysRole) {
        boolean is_success = sysRoleService.updateById(sysRole);
        if (is_success) {
            return Result.ok();
        }
        return Result.fail();
    }

    //根据id删除
    @ApiOperation("根据id删除角色")
    @DeleteMapping("remove/{id}")
    public Result removeSysRoleById(@PathVariable long id) {
        boolean is_success = sysRoleService.removeById(id);
        if (is_success) {
            return Result.ok();
        }
        return Result.fail();
    }

    //批量删除。同样是根据id删除，只不过是传入多个id，例如前端传入json数组格式的id [1,2,3]，后端用List集合的请求体得到前端传入的数组。
    //小知识：json的对象格式会转换为java对象，json的数组格式会转化为java的list集合
    @ApiOperation("批量删除角色")
    @DeleteMapping("batchRemove")
    public Result batchRomoveSysRole(@RequestBody List<Long> idList) {
        boolean is_success = sysRoleService.removeByIds(idList);
        if (is_success) {
            return Result.ok();
        }
        return Result.fail();
    }

}
