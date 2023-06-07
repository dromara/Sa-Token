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
package cn.dev33.satoken.strategy;

import cn.dev33.satoken.SaManager;
import cn.dev33.satoken.annotation.*;
import cn.dev33.satoken.basic.SaBasicUtil;
import cn.dev33.satoken.exception.SaTokenException;
import cn.dev33.satoken.fun.strategy.SaGenerateUniqueTokenFunction;
import cn.dev33.satoken.fun.strategy.*;
import cn.dev33.satoken.session.SaSession;
import cn.dev33.satoken.stp.StpLogic;
import cn.dev33.satoken.util.SaFoxUtil;
import cn.dev33.satoken.util.SaTokenConsts;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.function.Function;

/**
 * Sa-Token 策略对象 
 * <p>
 * 此类统一定义框架内的一些关键性逻辑算法，方便开发者进行按需重写，例：
 * </p>
 * <pre>
	// SaStrategy全局单例，所有方法都用以下形式重写 
	SaStrategy.instance.setCreateToken((loginId, loginType) -》 {
		// 自定义Token生成的算法 
		return "xxxx";
	});
 * </pre>
 * 
 * @author click33
 * @since 1.27.0
 */
public final class SaStrategy {

	private SaStrategy() {
	}

	/**
	 * 获取 SaStrategy 对象的单例引用 
	 */
	public static final SaStrategy instance = new SaStrategy();


	// ----------------------- 所有策略
	
	/**
	 * 创建 Token 的策略
	 */
	public SaCreateTokenFunction createToken = (loginId, loginType) -> {
		// 根据配置的tokenStyle生成不同风格的token 
		String tokenStyle = SaManager.getConfig().getTokenStyle();

		switch (tokenStyle) {
			// uuid
			case SaTokenConsts.TOKEN_STYLE_UUID:
				return UUID.randomUUID().toString();

			// 简单uuid (不带下划线)
			case SaTokenConsts.TOKEN_STYLE_SIMPLE_UUID:
				return UUID.randomUUID().toString().replaceAll("-", "");

			// 32位随机字符串
			case SaTokenConsts.TOKEN_STYLE_RANDOM_32:
				return SaFoxUtil.getRandomString(32);

			// 64位随机字符串
			case SaTokenConsts.TOKEN_STYLE_RANDOM_64:
				return SaFoxUtil.getRandomString(64);

			// 128位随机字符串
			case SaTokenConsts.TOKEN_STYLE_RANDOM_128:
				return SaFoxUtil.getRandomString(128);

			// tik风格 (2_14_16)
			case SaTokenConsts.TOKEN_STYLE_TIK:
				return SaFoxUtil.getRandomString(2) + "_" + SaFoxUtil.getRandomString(14) + "_" + SaFoxUtil.getRandomString(16) + "__";

			// 默认，还是uuid
			default:
				SaManager.getLog().warn("配置的 tokenStyle 值无效：{}，仅允许以下取值: " +
						"uuid、simple-uuid、random-32、random-64、random-128、tik", tokenStyle);
				return UUID.randomUUID().toString();
		}
	};
	
	/**
	 * 创建 Session 的策略
	 */
	public SaCreateSessionFunction createSession = (sessionId) -> {
		return new SaSession(sessionId);
	};

	/**
	 * 判断：集合中是否包含指定元素（模糊匹配）
	 */
	public SaHasElementFunction hasElement = (list, element) -> {

		// 空集合直接返回false
		if(list == null || list.size() == 0) {
			return false;
		}

		// 先尝试一下简单匹配，如果可以匹配成功则无需继续模糊匹配 
		if (list.contains(element)) {
			return true;
		}
		
		// 开始模糊匹配 
		for (String patt : list) {
			if(SaFoxUtil.vagueMatch(patt, element)) {
				return true;
			}
		}
		
		// 走出for循环说明没有一个元素可以匹配成功 
		return false;
	};

	/**
	 * 对一个 [Method] 对象进行注解校验 （注解鉴权内部实现）
	 */
	public SaCheckMethodAnnotationFunction checkMethodAnnotation = (method) -> {

		// 先校验 Method 所属 Class 上的注解 
		instance.checkElementAnnotation.accept(method.getDeclaringClass());

		// 再校验 Method 上的注解  
		instance.checkElementAnnotation.accept(method);
	};

	/**
	 * 对一个 [元素] 对象进行注解校验 （注解鉴权内部实现）
	 */
	public SaCheckElementAnnotationFunction checkElementAnnotation = (element) -> {

		// 校验 @SaCheckLogin 注解 
		SaCheckLogin checkLogin = (SaCheckLogin) SaStrategy.instance.getAnnotation.apply(element, SaCheckLogin.class);
		if(checkLogin != null) {
			SaManager.getStpLogic(checkLogin.type(), false).checkByAnnotation(checkLogin);
		}
		
		// 校验 @SaCheckRole 注解 
		SaCheckRole checkRole = (SaCheckRole) SaStrategy.instance.getAnnotation.apply(element, SaCheckRole.class);
		if(checkRole != null) {
			SaManager.getStpLogic(checkRole.type(), false).checkByAnnotation(checkRole);
		}
		
		// 校验 @SaCheckPermission 注解
		SaCheckPermission checkPermission = (SaCheckPermission) SaStrategy.instance.getAnnotation.apply(element, SaCheckPermission.class);
		if(checkPermission != null) {
			SaManager.getStpLogic(checkPermission.type(), false).checkByAnnotation(checkPermission);
		}

		// 校验 @SaCheckSafe 注解
		SaCheckSafe checkSafe = (SaCheckSafe) SaStrategy.instance.getAnnotation.apply(element, SaCheckSafe.class);
		if(checkSafe != null) {
			SaManager.getStpLogic(checkSafe.type(), false).checkByAnnotation(checkSafe);
		}

		// 校验 @SaCheckDisable 注解
		SaCheckDisable checkDisable = (SaCheckDisable) SaStrategy.instance.getAnnotation.apply(element, SaCheckDisable.class);
		if(checkDisable != null) {
			SaManager.getStpLogic(checkDisable.type(), false).checkByAnnotation(checkDisable);
		}
		
		// 校验 @SaCheckBasic 注解
		SaCheckBasic checkBasic = (SaCheckBasic) SaStrategy.instance.getAnnotation.apply(element, SaCheckBasic.class);
		if(checkBasic != null) {
			SaBasicUtil.check(checkBasic.realm(), checkBasic.account());
		}

		// 校验 @SaCheckOr 注解
		SaCheckOr checkOr = (SaCheckOr) SaStrategy.instance.getAnnotation.apply(element, SaCheckOr.class);
		if(checkOr != null) {
			SaStrategy.instance.checkOrAnnotation.accept(checkOr);
		}
	};

	/**
	 * 对一个 @SaCheckOr 进行注解校验
	 */
	public SaCheckOrAnnotationFunction checkOrAnnotation = (at) -> {

		// 记录校验过程中所有的异常
		List<SaTokenException> errorList = new ArrayList<>();

		// 逐个开始校验 >>>

		// 1、校验注解：@SaCheckLogin
		SaCheckLogin[] checkLoginArray = at.login();
		for (SaCheckLogin item : checkLoginArray) {
			try {
				SaManager.getStpLogic(item.type(), false).checkByAnnotation(item);
				return;
			} catch (SaTokenException e) {
				errorList.add(e);
			}
		}

		// 2、校验注解：@SaCheckRole
		SaCheckRole[] checkRoleArray = at.role();
		for (SaCheckRole item : checkRoleArray) {
			try {
				SaManager.getStpLogic(item.type(), false).checkByAnnotation(item);
				return;
			} catch (SaTokenException e) {
				errorList.add(e);
			}
		}

		// 3、校验注解：@SaCheckPermission
		SaCheckPermission[] checkPermissionArray = at.permission();
		for (SaCheckPermission item : checkPermissionArray) {
			try {
				SaManager.getStpLogic(item.type(), false).checkByAnnotation(item);
				return;
			} catch (SaTokenException e) {
				errorList.add(e);
			}
		}

		// 4、校验注解：@SaCheckSafe
		SaCheckSafe[] checkSafeArray = at.safe();
		for (SaCheckSafe item : checkSafeArray) {
			try {
				SaManager.getStpLogic(item.type(), false).checkByAnnotation(item);
				return;
			} catch (SaTokenException e) {
				errorList.add(e);
			}
		}

		// 5、校验注解：@SaCheckDisable
		SaCheckDisable[] checkDisableArray = at.disable();
		for (SaCheckDisable item : checkDisableArray) {
			try {
				SaManager.getStpLogic(item.type(), false).checkByAnnotation(item);
				return;
			} catch (SaTokenException e) {
				errorList.add(e);
			}
		}

		// 6、校验注解：@SaCheckBasic
		SaCheckBasic[] checkBasicArray = at.basic();
		for (SaCheckBasic item : checkBasicArray) {
			try {
				SaBasicUtil.check(item.realm(), item.account());
				return;
			} catch (SaTokenException e) {
				errorList.add(e);
			}
		}

		// 如果执行到这里，有两种可能：
		//		可能 1. SaCheckOr 注解上不包含任何注解校验，此时 errorList 里面一个异常都没有，我们直接跳过即可
		//		可能 2. 所有注解校验都通过不了，此时 errorList 里面会有多个异常，我们随便抛出一个即可
		if(errorList.size() == 0) {
			// return;
		} else {
			throw errorList.get(0);
		}
	};

	/**
	 * 从元素上获取注解
	 */
	public SaGetAnnotationFunction getAnnotation = (element, annotationClass)->{
		// 默认使用jdk的注解处理器 
		return element.getAnnotation(annotationClass);
	};

	/**
	 * 判断一个 Method 或其所属 Class 是否包含指定注解
	 */
	public SaIsAnnotationPresentFunction isAnnotationPresent = (method, annotationClass) -> {
		return instance.getAnnotation.apply(method, annotationClass) != null ||
				instance.getAnnotation.apply(method.getDeclaringClass(), annotationClass) != null;
	};

	/**
	 * 生成唯一式 token 的算法
	 */
	public SaGenerateUniqueTokenFunction generateUniqueToken = (elementName, maxTryTimes, createTokenFunction, checkTokenFunction) -> {

		// 为方便叙述，以下代码注释均假设在处理生成 token 的场景，但实际上本方法也可能被用于生成 code、ticket 等

		// 循环生成
		for (int i = 1; ; i++) {
			// 生成 token
			String token = createTokenFunction.get();

			// 如果 maxTryTimes == -1，表示不做唯一性验证，直接返回
			if (maxTryTimes == -1) {
				return token;
			}

			// 如果 token 在DB库查询不到数据，说明是个可用的全新 token，直接返回
			if (checkTokenFunction.apply(token)) {
				return token;
			}

			// 如果已经循环了 maxTryTimes 次，仍然没有创建出可用的 token，那么抛出异常
			if (i >= maxTryTimes) {
				throw new SaTokenException(elementName + " 生成失败，已尝试" + i + "次，生成算法过于简单或资源池已耗尽");
			}
		}
	};

	/**
	 * 创建 StpLogic 的算法
	 */
	public SaCreateStpLogicFunction createStpLogic = (loginType) -> {
		return new StpLogic(loginType);
	};


	// ----------------------- 重写策略 set连缀风格

	/**
	 * 重写创建 Token 的策略
	 *
	 * @param createToken / 
	 * @return /
	 */
	public SaStrategy setCreateToken(SaCreateTokenFunction createToken) {
		this.createToken = createToken;
		return this;
	}

	/**
	 * 重写创建 Session 的策略
	 *
	 * @param createSession / 
	 * @return /
	 */
	public SaStrategy setCreateSession(SaCreateSessionFunction createSession) {
		this.createSession = createSession;
		return this;
	}

	/**
	 * 判断：集合中是否包含指定元素（模糊匹配）
	 *
	 * @param hasElement /
	 * @return /
	 */
	public SaStrategy setHasElement(SaHasElementFunction hasElement) {
		this.hasElement = hasElement;
		return this;
	}

	/**
	 * 对一个 [Method] 对象进行注解校验 （注解鉴权内部实现）
	 *
	 * @param checkMethodAnnotation /
	 * @return /
	 */
	public SaStrategy setCheckMethodAnnotation(SaCheckMethodAnnotationFunction checkMethodAnnotation) {
		this.checkMethodAnnotation = checkMethodAnnotation;
		return this;
	}

	/**
	 * 对一个 [元素] 对象进行注解校验 （注解鉴权内部实现）
	 *
	 * @param checkElementAnnotation / 
	 * @return /
	 */
	public SaStrategy setCheckElementAnnotation(SaCheckElementAnnotationFunction checkElementAnnotation) {
		this.checkElementAnnotation = checkElementAnnotation;
		return this;
	}

	/**
	 * 对一个 @SaCheckOr 进行注解校验
	 * <p> 参数 [SaCheckOr 注解的实例]
	 *
	 * @param checkOrAnnotation /
	 * @return /
	 */
	public SaStrategy setCheckOrAnnotation(SaCheckOrAnnotationFunction checkOrAnnotation) {
		this.checkOrAnnotation = checkOrAnnotation;
		return this;
	}

	/**
	 * 从元素上获取注解
	 *
	 * @param getAnnotation / 
	 * @return /
	 */
	public SaStrategy setGetAnnotation(SaGetAnnotationFunction getAnnotation) {
		this.getAnnotation = getAnnotation;
		return this;
	}

	/**
	 * 判断一个 Method 或其所属 Class 是否包含指定注解
	 *
	 * @param isAnnotationPresent / 
	 * @return /
	 */
	public SaStrategy setIsAnnotationPresent(SaIsAnnotationPresentFunction isAnnotationPresent) {
		this.isAnnotationPresent = isAnnotationPresent;
		return this;
	}

	/**
	 * 生成唯一式 token 的算法
	 *
	 * @param generateUniqueToken /
	 * @return /
	 */
	public SaStrategy setGenerateUniqueToken(SaGenerateUniqueTokenFunction generateUniqueToken) {
		this.generateUniqueToken = generateUniqueToken;
		return this;
	}

	/**
	 * 创建 StpLogic 的算法
	 *
	 * @param createStpLogic /
	 * @return /
	 */
	public SaStrategy setCreateStpLogic(SaCreateStpLogicFunction createStpLogic) {
		this.createStpLogic = createStpLogic;
		return this;
	}

	//

	/**
	 * 请更换为 instance
	 */
	@Deprecated
	public static final SaStrategy me = instance;

}
