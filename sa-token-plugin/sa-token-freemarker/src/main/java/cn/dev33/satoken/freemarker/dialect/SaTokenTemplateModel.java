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
package cn.dev33.satoken.freemarker.dialect;

import cn.dev33.satoken.stp.StpLogic;
import cn.dev33.satoken.stp.StpUtil;
import cn.dev33.satoken.util.SaFoxUtil;
import freemarker.template.SimpleHash;

import java.util.List;

/**
 * Sa-Token Freemarker 标签模板模型
 *
 * @author click33
 * @since 1.40.0
 */
public class SaTokenTemplateModel extends SimpleHash {

    /**
     * 默认值属性名
     */
    public static final String DEFAULT_ATTR_NAME = "value";

    /**
     * 底层使用的 StpLogic
     */
    public StpLogic stpLogic;

    /**
     * 使用默认参数注册标签模板模型
     */
    public SaTokenTemplateModel() {
        this(DEFAULT_ATTR_NAME, StpUtil.stpLogic);
    }

    /**
     * 构造标签模板模型，使用自定义参数
     *
     * @param stpLogic 使用的 StpLogic 对象
     */
    public SaTokenTemplateModel(StpLogic stpLogic) {
        this(DEFAULT_ATTR_NAME, stpLogic);
    }

    /**
     * 构造标签模板模型，使用自定义参数
     *
     * @param attrName 属性名
     * @param stpLogic 使用的 StpLogic 对象
     */
    public SaTokenTemplateModel(String attrName, StpLogic stpLogic) {
        this.stpLogic = stpLogic;

        // 登录判断
        put("login", new SaTokenTemplateDirectiveModel(attrName, value -> stpLogic.isLogin()));
        put("notLogin", new SaTokenTemplateDirectiveModel(attrName, value -> ! stpLogic.isLogin()));

        // 角色判断
        put("hasRole", new SaTokenTemplateDirectiveModel(attrName, value -> stpLogic.hasRole(value)));
        put("hasRoleAnd", new SaTokenTemplateDirectiveModel(attrName, value -> stpLogic.hasRoleAnd(toArray(value))));
        put("hasRoleOr", new SaTokenTemplateDirectiveModel(attrName, value -> stpLogic.hasRoleOr(toArray(value))));
        put("notRole", new SaTokenTemplateDirectiveModel(attrName, value -> ! stpLogic.hasRole(value)));
        put("lackRole", new SaTokenTemplateDirectiveModel(attrName, value -> ! stpLogic.hasRole(value)));

        // 权限判断
        put("hasPermission", new SaTokenTemplateDirectiveModel(attrName, value -> stpLogic.hasPermission(value)));
        put("hasPermissionAnd", new SaTokenTemplateDirectiveModel(attrName, value -> stpLogic.hasPermissionAnd(toArray(value))));
        put("hasPermissionOr", new SaTokenTemplateDirectiveModel(attrName, value -> stpLogic.hasPermissionOr(toArray(value))));
        put("notPermission", new SaTokenTemplateDirectiveModel(attrName, value -> ! stpLogic.hasPermission(value)));
        put("lackPermission", new SaTokenTemplateDirectiveModel(attrName, value -> ! stpLogic.hasPermission(value)));

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