/*
 * Copyright 2020-2099 sa-token.com
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
package cn.dev33.satoken.test.fixture;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;

/**
 * 用户项目里那种 getAnnotation 重写：先取直接注解，没有再看一层元注解。
 * 对应文档 / demo 里用 AnnotatedElementUtils.getMergedAnnotation 的那一步，这里不引 Spring。
 *
 * @author click33
 * @since 1.46.0
 */
public final class MergedAnnotationLookup {

	private MergedAnnotationLookup() {
	}

	/** 从元素上取指定注解，支持一层组合注解 */
	public static Annotation get(AnnotatedElement element, Class<? extends Annotation> annotationClass) {
		Annotation direct = element.getAnnotation(annotationClass);
		if (direct != null) {
			return direct;
		}
		for (Annotation annotation : element.getDeclaredAnnotations()) {
			Annotation found = annotation.annotationType().getAnnotation(annotationClass);
			if (found != null) {
				return found;
			}
		}
		return null;
	}

}
