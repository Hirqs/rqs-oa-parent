package com.rqs.auth.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.rqs.auth.service.SysMenuService;
import com.rqs.auth.service.SysUserService;
import com.rqs.common.exception.RqsException;
import com.rqs.common.jwt.JwtHelper;
import com.rqs.common.result.Result;
import com.rqs.common.utils.MD5;
import com.rqs.model.system.SysUser;
import com.rqs.vo.system.LoginVo;
import com.rqs.vo.system.RouterVo;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Api(tags = "后台登录管理")
@RestController
@RequestMapping("/admin/system/index")
public class IndexController {

    @Autowired
    private SysUserService sysUserService;
    @Autowired
    private SysMenuService sysMenuService;
    //login
    @PostMapping("login")
    public Result login(@RequestBody  LoginVo loginVo) {
        //1.获取输入的用户名和密码
        //2.根据用户名查询数据库
        String username = loginVo.getUsername();
        LambdaQueryWrapper<SysUser> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysUser::getUsername, username);
        SysUser sysUser = sysUserService.getOne(wrapper);
        //3.用户信息是否存在
        if (sysUser == null) {
            throw new RqsException(201, "用户不存在");
        }
        //4.判断密码
        //4.1 获取数据库中经过MD5加密的密码
        String password_db = sysUser.getPassword();
        //4.2 获取输入的密码
        String password_input = loginVo.getPassword();
        String password_input_MD5 = MD5.encrypt(password_input);
        if (password_db.equals(password_input_MD5)) {
            throw new RqsException(201, "密码错误");
        }
        //5.判断用户是否被禁用  1:可用 0:禁用
        Integer status = sysUser.getStatus();
        if (status.intValue() == 0) {
            throw new RqsException(201, "用户已被禁止使用");
        }
        //6.使用jwt根据用户id和用户名称生成token字符串
        String token = JwtHelper.createToken(sysUser.getId(), sysUser.getPassword());
        //7.返回
        Map<String ,Object> map =new HashMap<>();
        map.put("token", token);
        return Result.ok(token);
    }

    //info
    @GetMapping("info")
    public Result info(HttpServletRequest request) {
        //1.从请求头获取用户信息（获取请求头token字符串）
        String token = request.getHeader("header");
        //2.从token字符串中获取用户id或者用户名称
        Long userId = JwtHelper.getUserId(token);
        //3.根据用户id查询数据库，获取用户信息
        SysUser sysUser = sysUserService.getById(userId);
        //4.根据用户id获取用户可以操作的菜单列表，查询数据库动态构建路由结构，进行最终的显示
        List<RouterVo> routerList = sysMenuService.finUserMenuListByUserId(userId);
        //5.根据用户id获取用户可以操作的按钮列表
        List<String> permsList = sysMenuService.findUserPermsByUserId(userId);
        //6.返回数据
        //{"code":20000,"data":{"roles":["admin"],"introduction":"I am a super administrator","avatar":"https://wpimg.wallstcn.com/f778738c-e4f8-4870-b634-56703b4acafe.gif","name":"Super Admin"}}
        Map<String, Object> map = new HashMap<>();
        map.put("roles", "[admin]");
        map.put("introduction", "I am a super administrator");
        map.put("avatar", "https://wpimg.wallstcn.com/f778738c-e4f8-4870-b634-56703b4acafe.gif");
        map.put("name", sysUser.getName());
        //返回用户可以操作的菜单
        map.put("routers", routerList);
        //返回用户可以操作的按钮
        map.put("buttons", permsList);
        return Result.ok(map);
    }

    //logout
    @PostMapping("logout")
    public Result logout() {

        return Result.ok();
    }

}
