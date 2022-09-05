package cn.dev33.satoken.integrate.configure.inject;

import org.springframework.stereotype.Component;

import cn.dev33.satoken.stp.StpLogic;
import cn.dev33.satoken.stp.StpUtil;

@Component
public class MyStpLogic extends StpLogic {

	public MyStpLogic() {
		super(StpUtil.TYPE);
	}

}
