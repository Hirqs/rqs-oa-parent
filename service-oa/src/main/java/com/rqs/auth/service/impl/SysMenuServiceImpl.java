package com.rqs.auth.service.impl;


import com.rqs.auth.mapper.SysMenuMapper;
import com.rqs.auth.service.SysMenuService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.rqs.auth.utils.MenuHelper;
import com.rqs.common.result.Result;
import com.rqs.model.system.SysMenu;
import org.springframework.stereotype.Service;

import java.util.List;

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

}
