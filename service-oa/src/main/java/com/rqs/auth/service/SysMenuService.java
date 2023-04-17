package com.rqs.auth.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.rqs.model.system.SysMenu;

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
}
