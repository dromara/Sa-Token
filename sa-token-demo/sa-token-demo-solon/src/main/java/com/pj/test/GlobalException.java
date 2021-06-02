package com.pj.test;

import cn.dev33.satoken.exception.*;
import com.pj.util.AjaxJson;

import org.noear.solon.annotation.Component;
import org.noear.solon.core.event.EventListener;
import org.noear.solon.core.handle.Context;


/**
 * 全局异常处理
 *
 * @author noear
 */
@Component
public class GlobalException implements EventListener<Throwable> {

	@Override
	public void onEvent(Throwable e) {
		Context c = Context.current();

		if (c != null) {
			// 不同异常返回不同状态码
			AjaxJson aj = null;
			if (e instanceof NotLoginException) {    // 如果是未登录异常
				NotLoginException ee = (NotLoginException) e;
				aj = AjaxJson.getNotLogin().setMsg(ee.getMessage());
			} else if (e instanceof NotRoleException) {        // 如果是角色异常
				NotRoleException ee = (NotRoleException) e;
				aj = AjaxJson.getNotJur("无此角色：" + ee.getRole());
			} else if (e instanceof NotPermissionException) {    // 如果是权限异常
				NotPermissionException ee = (NotPermissionException) e;
				aj = AjaxJson.getNotJur("无此权限：" + ee.getCode());
			} else if (e instanceof DisableLoginException) {    // 如果是被封禁异常
				DisableLoginException ee = (DisableLoginException) e;
				aj = AjaxJson.getNotJur("账号被封禁：" + ee.getDisableTime() + "秒后解封");
			} else {    // 普通异常, 输出：500 + 异常信息
				aj = AjaxJson.getError(e.getMessage());
			}

			c.result = aj;
		}
	}
}
