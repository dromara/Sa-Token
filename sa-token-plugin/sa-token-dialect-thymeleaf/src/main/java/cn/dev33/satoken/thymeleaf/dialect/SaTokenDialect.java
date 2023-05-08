package cn.dev33.satoken.thymeleaf.dialect;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.thymeleaf.dialect.AbstractProcessorDialect;
import org.thymeleaf.processor.IProcessor;

import cn.dev33.satoken.stp.StpLogic;
import cn.dev33.satoken.stp.StpUtil;
import cn.dev33.satoken.util.SaFoxUtil;

/**
 * Sa-Token 集成 Thymeleaf 标签方言 
 * 
 * @author click33
 *
 */
public class SaTokenDialect extends AbstractProcessorDialect {
	
	/**
	 * 底层使用的 StpLogic 
	 */
	public StpLogic stpLogic;
	
	/**
	 * 使用默认参数注册方言 
	 */
    public SaTokenDialect() {
    	this("sa", 1000, StpUtil.stpLogic);
    }
    
    /**
     * 构造方言对象，使用
     * @param name 方言名称
     * @param recedence 优先级 
     * @param stpLogic 使用的 StpLogic 对象 
     */
    public SaTokenDialect(String name, int recedence, StpLogic stpLogic) {
    	// 名称、前缀、优先级 
        super(name, name, recedence);
        this.stpLogic = stpLogic;
    }
    
    /**
     * 返回所有方言处理器 
     */
    @Override
    public Set<IProcessor> getProcessors(String prefix) {
    	return new HashSet<IProcessor>(Arrays.asList(
    			// 登录判断 
    			new SaTokenTagProcessor(prefix, "login", value -> stpLogic.isLogin()),
    			new SaTokenTagProcessor(prefix, "notLogin", value -> stpLogic.isLogin() == false),
    			
    			// 角色判断 
    			new SaTokenTagProcessor(prefix, "hasRole", value -> stpLogic.hasRole(value)),
    			new SaTokenTagProcessor(prefix, "hasRoleOr", value -> stpLogic.hasRoleOr(toArray(value))),
    			new SaTokenTagProcessor(prefix, "hasRoleAnd", value -> stpLogic.hasRoleAnd(toArray(value))),
    			new SaTokenTagProcessor(prefix, "lackRole", value -> stpLogic.hasRole(value) == false),
    			
    			// 权限判断 
    			new SaTokenTagProcessor(prefix, "hasPermission", value -> stpLogic.hasPermission(value)),
    			new SaTokenTagProcessor(prefix, "hasPermissionOr", value -> stpLogic.hasPermissionOr(toArray(value))),
    			new SaTokenTagProcessor(prefix, "hasPermissionAnd", value -> stpLogic.hasPermissionAnd(toArray(value))),
    			new SaTokenTagProcessor(prefix, "lackPermission", value -> stpLogic.hasPermission(value) == false)
    			
    		));
    }

    /**
     * String 转 Array 
     * @param str 字符串 
     * @return 数组 
     */
    public String[] toArray(String str) {
    	List<String> list = SaFoxUtil.convertStringToList(str);
    	return list.toArray(new String[list.size()]);
    }
    
}
