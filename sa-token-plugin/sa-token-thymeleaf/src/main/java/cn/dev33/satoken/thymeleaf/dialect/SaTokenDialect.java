/*
 * Copyright 2020-2099 sa-token.cc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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
import org.thymeleaf.standard.processor.StandardXmlNsTagProcessor;
import org.thymeleaf.templatemode.TemplateMode;

/**
 * Sa-Token 集成 Thymeleaf 标签方言 
 * 
 * @author click33
 * @since 1.27.0
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
     * 构造方言对象，使用自定义参数
	 *
     * @param name 方言名称
     * @param precedence 优先级
     * @param stpLogic 使用的 StpLogic 对象 
     */
    public SaTokenDialect(String name, int precedence, StpLogic stpLogic) {
    	// 名称、前缀、优先级 
        super(name, name, precedence);
        this.stpLogic = stpLogic;
    }
    
    /**
     * 返回所有方言处理器 
     */
    @Override
    public Set<IProcessor> getProcessors(String prefix) {
    	return new HashSet<>(Arrays.asList(
				// 登录判断
				new SaTokenTagProcessor(prefix, "login", value -> stpLogic.isLogin()),
				new SaTokenTagProcessor(prefix, "notLogin", value -> ! stpLogic.isLogin()),

				// 角色判断
				new SaTokenTagProcessor(prefix, "hasRole", value -> stpLogic.hasRole(value)),
				new SaTokenTagProcessor(prefix, "hasRoleAnd", value -> stpLogic.hasRoleAnd(toArray(value))),
				new SaTokenTagProcessor(prefix, "hasRoleOr", value -> stpLogic.hasRoleOr(toArray(value))),
				new SaTokenTagProcessor(prefix, "notRole", value -> ! stpLogic.hasRole(value)),
				new SaTokenTagProcessor(prefix, "lackRole", value -> ! stpLogic.hasRole(value)),

				// 权限判断
				new SaTokenTagProcessor(prefix, "hasPermission", value -> stpLogic.hasPermission(value)),
				new SaTokenTagProcessor(prefix, "hasPermissionAnd", value -> stpLogic.hasPermissionAnd(toArray(value))),
				new SaTokenTagProcessor(prefix, "hasPermissionOr", value -> stpLogic.hasPermissionOr(toArray(value))),
				new SaTokenTagProcessor(prefix, "notPermission", value -> ! stpLogic.hasPermission(value)),
				new SaTokenTagProcessor(prefix, "lackPermission", value -> ! stpLogic.hasPermission(value)),

				// 移除<html>标签命名空间
				new StandardXmlNsTagProcessor(TemplateMode.HTML,prefix)

		));
    }

    /**
     * String 转 Array 
     * @param str 字符串 
     * @return 数组 
     */
    public String[] toArray(String str) {
    	List<String> list = SaFoxUtil.convertStringToList(str);
    	return list.toArray(new String[0]);
    }
    
}
