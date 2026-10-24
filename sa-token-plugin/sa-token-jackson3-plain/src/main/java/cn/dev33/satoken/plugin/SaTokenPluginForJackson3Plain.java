package cn.dev33.satoken.plugin;

import cn.dev33.satoken.SaManager;
import cn.dev33.satoken.json.SaJsonTemplateForJackson3Plain;
import cn.dev33.satoken.session.SaSessionForJackson3PlainCustomized;
import cn.dev33.satoken.strategy.SaStrategy;

/** Installs the Jackson 3 plain JSON implementation. */
public class SaTokenPluginForJackson3Plain implements SaTokenPlugin {
    @Override
    public void install() {
        SaManager.setSaJsonTemplate(new SaJsonTemplateForJackson3Plain());
        SaStrategy.instance.createSession = SaSessionForJackson3PlainCustomized::new;
        SaStrategy.instance.sessionClassType = SaSessionForJackson3PlainCustomized.class;
    }
}
