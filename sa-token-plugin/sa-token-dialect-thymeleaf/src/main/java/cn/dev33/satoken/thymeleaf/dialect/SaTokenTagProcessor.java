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
 * @author click33 
 *
 */
public class SaTokenTagProcessor extends AbstractAttributeTagProcessor {

    Function <String, Boolean> fun;

    public SaTokenTagProcessor(final String dialectPrefix, String arrtName, Function <String, Boolean> fun) { 
        super(
            TemplateMode.HTML, // This processor will apply only to HTML mode
            dialectPrefix,     // Prefix to be applied to name for matching
            null,              // No tag name: match any tag name
            false,             // No prefix to be applied to tag name
            arrtName,         // Name of the attribute that will be matched
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
    	if(this.fun.apply(attributeValue) == false) {
    		structureHandler.removeElement();
    	};
    }
    
}