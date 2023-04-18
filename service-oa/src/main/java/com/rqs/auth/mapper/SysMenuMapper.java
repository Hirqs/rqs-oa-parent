package com.rqs.auth.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.rqs.model.system.SysMenu;

import java.util.List;

/**
 * <p>
 * 菜单表 Mapper 接口
 * </p>
 *
 * @author rqs
 * @since 2023-04-13
 */
public interface SysMenuMapper extends BaseMapper<SysMenu> {

    List<SysMenu> findMenuListByUserId(Long userId);
}
