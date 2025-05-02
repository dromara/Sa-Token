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
package cn.dev33.satoken.util;

import cn.dev33.satoken.fun.SaFunction;

import java.util.function.Supplier;

/**
 * 代码语法糖封装
 *
 * @author click33
 * @since 1.43.0
 */
public class SaSugar {

	/**
	 * 执行一个 Lambda 表达式，返回这个 Lambda 表达式的结果值，
	 * <br> 方便组织代码，例如: 
	 * <pre> 
	 	int value = Sugar.get(() -> {
			int a = 1;
			int b = 2;
			return a + b;
		});
		</pre> 
	 * @param <R> 返回值类型 
	 * @param lambda lambda 表达式
	 * @return lambda 的执行结果
	 */
	public static <R> R get(Supplier<R> lambda) {
		return lambda.get();
	}

	/**
	 * 执行一个 Lambda 表达式 
	 * <br> 方便组织代码，例如: 
	 * <pre> 
	 	Sugar.exe(() -> {
			int a = 1;
			int b = 2;
			return a + b;
		});
		</pre> 
	 * @param lambda lambda 表达式
	 */
	public static void exe(SaFunction lambda) {
		lambda.run();
	}

}
