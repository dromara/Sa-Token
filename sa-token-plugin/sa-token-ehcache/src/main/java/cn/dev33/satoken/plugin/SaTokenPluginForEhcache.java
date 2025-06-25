package cn.dev33.satoken.plugin;

import cn.dev33.satoken.SaManager;
import cn.dev33.satoken.dao.SaTokenDaoForEhcache;

/**
 * SaToken 插件安装：DAO 扩展 - Ehcache 版
 *
 * @author sanyang176
 */
public class SaTokenPluginForEhcache implements SaTokenPlugin{
    @Override
    public void install() {
        SaManager.setSaTokenDao(new SaTokenDaoForEhcache());
    }
}
