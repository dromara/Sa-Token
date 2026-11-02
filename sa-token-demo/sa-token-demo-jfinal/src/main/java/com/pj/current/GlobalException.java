package com.pj.current;

import com.jfinal.aop.Interceptor;
import com.jfinal.aop.Invocation;

import cn.dev33.satoken.exception.DisableServiceException;
import cn.dev33.satoken.exception.NotLoginException;
import cn.dev33.satoken.exception.NotPermissionException;
import cn.dev33.satoken.exception.NotRoleException;
import cn.dev33.satoken.util.SaResult;

/**
 * 全局异常处理
 */
public class GlobalException implements Interceptor {

	@Override
	public void intercept(Invocation inv) {
		try {
			inv.invoke();
		} catch (Exception e) {
			System.out.println("全局异常---------------");
			e.printStackTrace();

			SaResult aj;
			if (e instanceof NotLoginException) {	// 如果是未登录异常
				NotLoginException ee = (NotLoginException) e;
				aj = SaResult.get(401, ee.getMessage(), null);
			}
			else if(e instanceof NotRoleException) {		// 如果是角色异常
				NotRoleException ee = (NotRoleException) e;
				aj = SaResult.get(403, "无此角色：" + ee.getRole(), null);
			}
			else if(e instanceof NotPermissionException) {	// 如果是权限异常
				NotPermissionException ee = (NotPermissionException) e;
				aj = SaResult.get(403, "无此权限：" + ee.getPermission(), null);
			}
			else if(e instanceof DisableServiceException) {	// 如果是被封禁异常
				DisableServiceException ee = (DisableServiceException) e;
				aj = SaResult.get(403, "账号被封禁：" + ee.getDisableTime() + "秒后解封", null);
			}
			else {	// 普通异常, 输出：500 + 异常信息
				aj = SaResult.error(e.getMessage());
			}
			inv.getController().renderJson(aj);
		}
	}

}
