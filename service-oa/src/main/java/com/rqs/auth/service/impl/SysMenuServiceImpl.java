package com.rqs.auth.service.impl;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.rqs.auth.mapper.SysMenuMapper;
import com.rqs.auth.service.SysMenuService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.rqs.auth.service.SysRoleMenuService;
import com.rqs.auth.utils.MenuHelper;
import com.rqs.common.exception.RqsException;
import com.rqs.common.result.Result;
import com.rqs.model.system.SysMenu;
import com.rqs.model.system.SysRoleMenu;
import com.rqs.vo.system.AssginMenuVo;
import com.rqs.vo.system.AssginRoleVo;
import com.rqs.vo.system.MetaVo;
import com.rqs.vo.system.RouterVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 * 菜单表 服务实现类
 * </p>
 *
 * @author rqs
 * @since 2023-04-13
 */
@Service
public class SysMenuServiceImpl extends ServiceImpl<SysMenuMapper, SysMenu> implements SysMenuService {


    @Autowired
    private SysRoleMenuService sysRoleMenuService;

    @Override
    public List<SysMenu> findNodes() {
        //1.查询所有菜单数据
        List<SysMenu> sysMenuList = baseMapper.selectList(null);
        //2.将菜单数据构建为树形结构
        /**
         * {
         *     第一层
         *     children:[
         *          {
         *              第二层
         *                      ...
         *          }
         *     ]
         * }
         */
        List<SysMenu> resultList = MenuHelper.buildTree(sysMenuList);
        return resultList;
    }

    @Override
    public void removeMenuById(Long id) {
        //判断当前菜单是否有下一层菜单
        LambdaQueryWrapper<SysMenu> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysMenu::getParentId, id);
        Integer count = baseMapper.selectCount(wrapper);
        if (count > 0) {
            throw new RqsException(201, "菜单不能删除");
        }
        baseMapper.deleteById(id);
    }

    @Override
    public List<SysMenu> findMenuByRoleId(Long roleId) {
        //查询所有菜单 status =1 表示菜单可用
        LambdaQueryWrapper<SysMenu> wrapperSysMenu = new LambdaQueryWrapper<>();
        wrapperSysMenu.eq(SysMenu::getStatus, 1);
        List<SysMenu> allSysMenuList = baseMapper.selectList(wrapperSysMenu);

        //根据角色id 查询【角色菜单关系表里的】中角色id对应的【所有菜单id】
        LambdaQueryWrapper<SysRoleMenu> wrapperSysRoleMenu = new LambdaQueryWrapper<>();
        wrapperSysRoleMenu.eq(SysRoleMenu::getRoleId, roleId);
        List<SysRoleMenu> sysRoleMenuList = sysRoleMenuService.list(wrapperSysRoleMenu);
        List<Long> menuIdList = sysRoleMenuList.stream().map(c -> c.getMenuId()).collect(Collectors.toList());

        //用菜单id 和所有菜单集合里的id进行比较 ，如果相同则进行封装
        allSysMenuList.stream().forEach(item -> {
            if (menuIdList.contains(item.getId())) {
                item.setSelect(true);
            } else {
                item.setSelect(false);
            }
        });

        //返回树形格式的菜单列表
        List<SysMenu> sysMenuList = MenuHelper.buildTree(allSysMenuList);
        return sysMenuList;

    }

    @Override
    public void doAssign(AssginMenuVo assginMenuVo) {
        //根据角色id 删除菜单角色表里已经为该角色分配的菜单id
        LambdaQueryWrapper<SysRoleMenu> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysRoleMenu::getRoleId,assginMenuVo.getRoleId());
        sysRoleMenuService.remove(wrapper);
        //从参数中获取角色新分配的菜单id列表，进行遍历，把每个菜单id添加到菜单角色表中
        List<Long> menuIdList = assginMenuVo.getMenuIdList();
        for (Long menuId : menuIdList) {
            if (StringUtils.isEmpty(menuId)) {
                continue;
            }
            SysRoleMenu sysRoleMenu = new SysRoleMenu();
            sysRoleMenu.setMenuId(menuId);
            sysRoleMenu.setRoleId(assginMenuVo.getRoleId());
            sysRoleMenuService.save(sysRoleMenu);
        }
    }

    @Override
    public List<RouterVo> findUserMenuListByUserId(Long userId) {
        List<SysMenu> sysMenuList = null;
        // 1.判断当前用户是否是管理员
        if (userId.longValue() == 1) {
            // 1.1 如果是管理员，查询所有菜单列表
            LambdaQueryWrapper<SysMenu> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(SysMenu::getStatus, 1);
            wrapper.orderByAsc(SysMenu::getSortValue);
            sysMenuList = baseMapper.selectList(wrapper);
        } else {
            // 1.2 如果不是管理员，根据userId查询可以操作的菜单列表
            sysMenuList = baseMapper.findMenuListByUserId(userId);
        }
        // 多表关联查询 用户角色关系表【userId -> roleId】 角色菜单关系表【roleId -> menuId】 菜单表【memuId -> 菜单信息】

        // 2.把查询出来的数据列表构建成框架要求的路由数据结构
        //使用菜单操作工具类构建树形结构
        List<SysMenu> sysMenuTreeList = MenuHelper.buildTree(sysMenuList);
        //构建成前端需要的路由结构
        List<RouterVo> routerVoList = this.buildRouter(sysMenuTreeList);
        return routerVoList;
    }

    //构建成前端需要的路由结构
    private List<RouterVo> buildRouter(List<SysMenu> menus) {
        //创建list集合，用于存储最终数据
        ArrayList<RouterVo> routers = new ArrayList<>();
        //menus遍历
        for (SysMenu menu : menus) {
            RouterVo router = new RouterVo();
            router.setHidden(false);
            router.setAlwaysShow(false);
            router.setPath(getRouterPath(menu));
            router.setComponent(menu.getComponent());
            router.setMeta(new MetaVo(menu.getName(), menu.getIcon()));

            List<SysMenu> children = menu.getChildren();//得到菜单的所有子菜单

            if (menu.getType().intValue() == 1) {// 如果遍历到的数据type == 1，代表当前不是按钮，也是菜单
                //菜单的下一层如果有隐藏路由，则加载隐藏路由（具体表现为type==2，以下遍历了type==2的所有item，如果item的component不为空 则加载路由）
                List<SysMenu> hiddenMenuList = children.stream().filter(item -> !StringUtils.isEmpty(item.getComponent())).collect(Collectors.toList());
                for (SysMenu hiddenMenu : hiddenMenuList) {
                    RouterVo hiddenRouter = new RouterVo();
                    //hidden 为 true 表示为隐藏路由
                    hiddenRouter.setHidden(true);
                    hiddenRouter.setAlwaysShow(false);
                    hiddenRouter.setPath(getRouterPath(hiddenMenu));
                    hiddenRouter.setComponent(hiddenMenu.getComponent());
                    hiddenRouter.setMeta(new MetaVo(hiddenMenu.getName(), hiddenMenu.getIcon()));
                    routers.add(hiddenRouter);
                }
            } else { // 如果遍历到的数据是type == 0 或者 type==2
                if (!CollectionUtils.isEmpty(children)) {
                    if(children.size() > 0) {//说明此时type == 0 ，是一级菜单，也就是系统管理，
                        router.setAlwaysShow(true);//系统管理的alwaysShow为true，代表该层级栏直接打开，否则进入折叠状态
                    }
                    //递归
                    router.setChildren(buildRouter(children));
                }
            }
            routers.add(router);
        }
        return routers;
    }

    @Override
    public List<String> findUserPermsByUserId(Long userId) {
        //1.判断当前用户是否是管理员
        List<SysMenu> sysMenuList = null;
        if (userId.longValue() == 1) {
            // 1.1 如果是管理员，查询所有按钮列表
            LambdaQueryWrapper<SysMenu> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(SysMenu::getStatus, 1);
            sysMenuList = baseMapper.selectList(wrapper);
        } else {
            // 1.2 如果不是管理员，根据userId查询可以操作的按钮列表
            sysMenuList  = baseMapper.findMenuListByUserId(userId);
        }
        // 多表关联查询 用户角色关系表【userId -> roleId】 角色菜单关系表【roleId -> menuId】 菜单表【memuId -> 菜单信息】

        // 2.从查询出来的数据中获取可以操作的按钮值的list集合，返回
        List<String> permsList = sysMenuList.stream().filter(item -> item.getType() == 2).map(item -> item.getPerms()).collect(Collectors.toList());
        return permsList;
    }

    /**
     * 获取路由地址
     *
     * @param menu 菜单信息
     * @return 路由地址
     */
    public String getRouterPath(SysMenu menu) {
        String routerPath = "/" + menu.getPath();
        if(menu.getParentId().intValue() != 0) {
            routerPath = menu.getPath();
        }
        return routerPath;
    }

}
