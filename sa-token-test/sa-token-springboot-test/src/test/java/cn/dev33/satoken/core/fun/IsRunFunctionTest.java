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
package cn.dev33.satoken.core.fun;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import cn.dev33.satoken.fun.IsRunFunction;

/**
 * IsRunFunction 测试 
 * 
 * @author click33
 * @since: 2022-2-9 16:11:10
 */
public class IsRunFunctionTest {

    @Test
    public void test() {
    	
    	class TempClass{
    		int count = 1;
    	}
    	TempClass obj = new TempClass();

    	IsRunFunction fun = new IsRunFunction(true);
    	fun.exe(()->{
    		obj.count = 2;
    	}).noExe(()->{
    		obj.count = 3;
    	});
    	
    	Assertions.assertEquals(obj.count, 2);
    }

    @Test
    public void test2() {
    	
    	class TempClass{
    		int count = 1;
    	}
    	TempClass obj = new TempClass();

    	IsRunFunction fun = new IsRunFunction(false);
    	fun.exe(()->{
    		obj.count = 2;
    	}).noExe(()->{
    		obj.count = 3;
    	});
    	
    	Assertions.assertEquals(obj.count, 3);
    }

}
