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

import freemarker.core.Environment;
import freemarker.template.TemplateDirectiveBody;
import freemarker.template.TemplateDirectiveModel;
import freemarker.template.TemplateException;
import freemarker.template.TemplateModel;

import java.io.IOException;
import java.util.Map;
import java.util.function.Function;

/**
 * Sa-Token Freemarker 标签模板指令模型
 *
 * @author click33
 * @since 1.40.0
 */
public class SaTokenTemplateDirectiveModel implements TemplateDirectiveModel {

    /*
     * 参考资料：
     *  - https://blog.csdn.net/m0_64210833/article/details/135994864
     *  - https://blog.csdn.net/qq_35752835/article/details/111321893
     */

    /**
     * 使用标签指令模板时，指定值的属性名
     */
    String attrName;

    /**
     * 断言函数，返回 true 时标签内容显示，返回 false 时标签内容不显示
     */
    Function <String, Boolean> fun;

    public SaTokenTemplateDirectiveModel(String attrName, Function <String, Boolean> fun) {
        this.attrName = attrName;
        this.fun = fun;
    }

    @Override
    public void execute(Environment environment, Map map, TemplateModel[] templateModels, TemplateDirectiveBody templateDirectiveBody)
            throws TemplateException, IOException {

        // 获取 value
        Object obj = map.get(attrName);
        String value = obj == null ? null : obj.toString();

        // 使用断言函数判断是否显示标签内容
        if(this.fun.apply(value)) {
            templateDirectiveBody.render(environment.getOut());
        }

    }

}