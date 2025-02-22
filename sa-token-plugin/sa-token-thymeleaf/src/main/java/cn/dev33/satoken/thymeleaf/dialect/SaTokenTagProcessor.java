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

import java.util.function.Function;

import org.thymeleaf.context.ITemplateContext;
import org.thymeleaf.engine.AttributeName;
import org.thymeleaf.model.IProcessableElementTag;
import org.thymeleaf.processor.element.AbstractAttributeTagProcessor;
import org.thymeleaf.processor.element.IElementTagStructureHandler;
import org.thymeleaf.templatemode.TemplateMode;

/**
 * 封装 Sa-Token 标签方言处理器
 * 
 * @author click33
 * @since 1.27.0
 */
public class SaTokenTagProcessor extends AbstractAttributeTagProcessor {

    Function <String, Boolean> fun;

    public SaTokenTagProcessor(final String dialectPrefix, String attrName, Function <String, Boolean> fun) {
        super(
            TemplateMode.HTML, // This processor will apply only to HTML mode
            dialectPrefix,     // Prefix to be applied to name for matching
            null,              // No tag name: match any tag name
            false,             // No prefix to be applied to tag name
            attrName,         // Name of the attribute that will be matched
            true,              // Apply dialect prefix to attribute name
            10000,        		// Precedence (inside dialect's own precedence)
            true);             // Remove the matched attribute afterwards
        this.fun = fun;
    }

    @Override
    protected void doProcess( 
            final ITemplateContext context, final IProcessableElementTag tag,
            final AttributeName attributeName, final String attributeValue,
            final IElementTagStructureHandler structureHandler) {
    	// 执行表达式返回值为false，则删除这个标签 
    	if( ! this.fun.apply(attributeValue)) {
    		structureHandler.removeElement();
    	}
    }
    
}