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
package cn.dev33.satoken.fun;

/**
 * lambda 表达式辅助封装：根据 Boolean 变量，决定是否执行一个函数
 * 
 * @author click33
 * @since <= 1.34.0
 */
public class IsRunFunction {

	/**
	 * 变量 
	 */
	public final Boolean isRun;

	/**
	 * 设定一个变量，如果为true，则执行exe函数
	 * 
	 * @param isRun 变量
	 */
	public IsRunFunction(boolean isRun) {
		this.isRun = isRun;
	}

	/**
	 * 当 isRun == true 时执行此函数
	 * @param function 函数
	 * @return 对象自身
	 */
	public IsRunFunction exe(SaFunction function) {
		if (isRun) {
			function.run();
		}
		return this;
	}

	/**
	 * 当 isRun == false 时执行此函数
	 * @param function 函数
	 * @return 对象自身
	 */
	public IsRunFunction noExe(SaFunction function) {
		if (!isRun) {
			function.run();
		}
		return this;
	}

}
