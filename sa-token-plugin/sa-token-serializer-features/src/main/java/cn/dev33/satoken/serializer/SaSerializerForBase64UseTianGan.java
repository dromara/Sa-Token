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
package cn.dev33.satoken.serializer;

/**
 * 序列化器，base64 算法，采用 十大天干、十二地支 等64个中文字符作为元字符集
 * 
 * @author click33
 * @since 1.41.0
 */
public class SaSerializerForBase64UseTianGan extends SaSerializerForBase64UseCustomCharacters {

	public SaSerializerForBase64UseTianGan() {
		super(
				// 自定义字符集，需确保包含64个不重复的字符
				"甲乙丙丁戊己庚辛" +
				"壬癸子丑寅卯辰巳" +
				"午未申酉戌亥乾坤" +
				"震巽坎离艮兑金木" +
				"水火土天地日月山" +
				"石田风雷电霜雾露" +
				"东南西北中信谷岚" +
				"宇宙羽泰铭安鹤纤"
				,
				// 填充符，确保不在字符集中
				'口'
		);
	}

}
