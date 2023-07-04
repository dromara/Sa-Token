package com.pj.test;

import com.pj.util.AjaxJson;

import cn.dev33.satoken.exception.*;

import org.noear.solon.annotation.Component;
import org.noear.solon.core.handle.Context;
import org.noear.solon.core.handle.Filter;
import org.noear.solon.core.handle.FilterChain;


/**
 * 全局异常处理
 *
 * @author noear
 */
@Component
public class GlobalExceptionFilter implements Filter {
	@Override
	public void doFilter(Context ctx, FilterChain chain) throws Throwable {
		try {
			chain.doFilter(ctx);
		} catch (SaTokenException e) {
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
				aj = AjaxJson.getNotJur("无此权限：" + ee.getPermission());
			} else if (e instanceof DisableServiceException) {    // 如果是被封禁异常
				DisableServiceException ee = (DisableServiceException) e;
				aj = AjaxJson.getNotJur("账号被封禁：" + ee.getDisableTime() + "秒后解封");
			} else {    // 普通异常, 输出：500 + 异常信息
				aj = AjaxJson.getError(e.getMessage());
			}

			ctx.render(aj);
		}
	}
}
