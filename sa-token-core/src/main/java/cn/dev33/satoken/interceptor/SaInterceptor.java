package cn.dev33.satoken.interceptor;

import cn.dev33.satoken.exception.BackResultException;
import cn.dev33.satoken.exception.StopMatchException;
import cn.dev33.satoken.fun.SaParamFunction;
import cn.dev33.satoken.fun.SaParamRetFunction;
import cn.dev33.satoken.router.SaRouter;
import cn.dev33.satoken.strategy.SaAnnotationStrategy;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;

public class SaInterceptor implements HandlerInterceptor {

	public SaParamFunction<Object> beforeAuth = handler -> {};

	public SaParamFunction<Object> auth = handler -> {};

	public SaParamRetFunction<Object, Boolean> isAnnotation = handler -> true;

	public SaInterceptor() {
	}

	public SaInterceptor(SaParamFunction<Object> auth) {
		this.auth = auth;
	}

	public SaInterceptor setBeforeAuth(SaParamFunction<Object> beforeAuth) {
		this.beforeAuth = beforeAuth;
		return this;
	}

	public SaInterceptor setAuth(SaParamFunction<Object> auth) {
		this.auth = auth;
		return this;
	}

	public SaInterceptor setIsAnnotation(SaParamRetFunction<Object, Boolean> isAnnotation) {
		this.isAnnotation = isAnnotation;
		return this;
	}

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
		try {
			SaRouter.match("/**").check(r -> {
				beforeAuth.run(handler);
				if (handler instanceof HandlerMethod) {
					Method method = ((HandlerMethod) handler).getMethod();
					if (isAnnotation.apply(method)) {
						SaAnnotationStrategy.instance.checkMethodAnnotation.accept(method);
					}
				}
				auth.run(handler);
			});
		} catch (StopMatchException e) {

		} catch (BackResultException e) {
			if (response.getContentType() == null) {
				response.setContentType("text/plain; charset=utf-8");
			}
			response.getWriter().print(e.getMessage());
		}
		return true;
	}

}
