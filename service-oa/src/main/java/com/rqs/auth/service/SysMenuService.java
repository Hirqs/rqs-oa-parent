package com.rqs.auth.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.rqs.model.system.SysMenu;
import com.rqs.vo.system.AssginMenuVo;
import com.rqs.vo.system.AssginRoleVo;
import com.rqs.vo.system.RouterVo;

import java.util.List;

/**
 * <p>
 * 菜单表 服务类
 * </p>
 *
 * @author rqs
 * @since 2023-04-13
 */
public interface SysMenuService extends IService<SysMenu> {

    List<SysMenu> findNodes();

    void removeMenuById(Long id);

    List<SysMenu> findMenuByRoleId(Long roleId);

    void doAssign(AssginMenuVo assginMenuVo);

    List<RouterVo> findUserMenuListByUserId(Long userId);

    List<String> findUserPermsByUserId(Long userId);
}
