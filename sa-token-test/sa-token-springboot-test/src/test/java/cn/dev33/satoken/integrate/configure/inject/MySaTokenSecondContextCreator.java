package cn.dev33.satoken.integrate.configure.inject;

import org.springframework.stereotype.Component;

import cn.dev33.satoken.context.second.SaTokenSecondContext;
import cn.dev33.satoken.context.second.SaTokenSecondContextCreator;

@Component
public class MySaTokenSecondContextCreator implements SaTokenSecondContextCreator {

	@Override
	public SaTokenSecondContext create() {
		return null;
	}
	
}
