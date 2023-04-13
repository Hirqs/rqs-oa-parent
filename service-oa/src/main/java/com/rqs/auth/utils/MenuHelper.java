package com.rqs.auth.utils;

import com.rqs.model.system.SysMenu;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * 根据菜单数据构建菜单数据
 * </p>
 *
 */
public class MenuHelper {

    /**
     * 使用递归方法建菜单
     * @param sysMenuList
     * @return
     */
    public static List<SysMenu> buildTree(List<SysMenu> sysMenuList) {
        List<SysMenu> trees = new ArrayList<>();
        //遍历菜单数据 得到每一个SysMenu菜单对象
        for (SysMenu sysMenu : sysMenuList) {
            //递归入口进入 parentId = 0 是入口
            //判断当前遍历到的菜单对象的parenId是否是0，如果是0，为当前菜单对象加入【下级菜单的列表】
            if (sysMenu.getParentId().longValue() == 0) {
                trees.add(findChildren(sysMenu,sysMenuList));
            }
        }
        return trees;
    }

    /**
     * 递归查找子节点
     * @param sysMenuList
     * @return
     */
    public static SysMenu findChildren(SysMenu sysMenu, List<SysMenu> sysMenuList) {
        //为【参数中传入的当前菜单对象sysMenu】设置一个【下级菜单的列表】，用于存放下级菜单
        sysMenu.setChildren(new ArrayList<SysMenu>());
        // 遍历所有菜单数据
        for (SysMenu menu : sysMenuList) {
            //判断【参数中传入的当前菜单对象sysMenu】的id和【此时遍历到的菜单对象menu】的parentId是否相同
            //如果相同，说明【此时遍历到的菜单对象menu】是【参数中传入的当前菜单对象sysMenu】的下级菜单
            if(sysMenu.getId().longValue() == menu.getParentId().longValue()) {//这段if语句的代码可能存在问题
                if (menu.getChildren() == null) {
                    menu.setChildren(new ArrayList<>());
                }
                // 由于【此时遍历到的菜单对象menu】是【参数中传入的当前菜单对象sysMenu】的下级菜单
                // 且已经通过sysMenu.setChildren设置了一个【下级菜单的列表】
                // 可以在设置的【下级菜单的列表】中通过add方法加入下级菜单对象，并且是递归的方式加入
                sysMenu.getChildren().add(findChildren(menu,sysMenuList));
            }
        }
        return sysMenu;
    }
}