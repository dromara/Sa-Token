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
package cn.dev33.satoken.annotation.handler;

import cn.dev33.satoken.annotation.*;
import cn.dev33.satoken.exception.SaTokenException;
import cn.dev33.satoken.strategy.SaAnnotationStrategy;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 注解 SaCheckOr 的处理器
 *
 * @author click33
 * @since 2024/8/2
 */
public class SaCheckOrHandler implements SaAnnotationHandlerInterface<SaCheckOr> {

    @Override
    public Class<SaCheckOr> getHandlerAnnotationClass() {
        return SaCheckOr.class;
    }

    @Override
    public void checkMethod(SaCheckOr at, AnnotatedElement element) {
        _checkMethod(at.login(), at.role(), at.permission(), at.safe(), at.httpBasic(), at.httpDigest(), at.disable(), at.append(), element);
    }

    public static void _checkMethod(
            SaCheckLogin[] login,
            SaCheckRole[] role,
            SaCheckPermission[] permission,
            SaCheckSafe[] safe,
            SaCheckHttpBasic[] httpBasic,
            SaCheckHttpDigest[] httpDigest,
            SaCheckDisable[] disable,
            Class<? extends Annotation>[] append,
            AnnotatedElement element
    ) {
        // 先把所有注解塞到一个 list 里
        List<Annotation> annotationList = new ArrayList<>();
        annotationList.addAll(Arrays.asList(login));
        annotationList.addAll(Arrays.asList(role));
        annotationList.addAll(Arrays.asList(permission));
        annotationList.addAll(Arrays.asList(safe));
        annotationList.addAll(Arrays.asList(disable));
        annotationList.addAll(Arrays.asList(httpBasic));
        annotationList.addAll(Arrays.asList(httpDigest));
        for (Class<? extends Annotation> annotationClass : append) {
            Annotation annotation = SaAnnotationStrategy.instance.getAnnotation.apply(element, annotationClass);
            if(annotation != null) {
                annotationList.add(annotation);
            }
        }

        // 如果 atList 为空，说明 SaCheckOr 上不包含任何注解校验，我们直接跳过即可
        if(annotationList.isEmpty()) {
            return;
        }

        // 逐个开始校验 >>>
        List<SaTokenException> errorList = new ArrayList<>();
        for (Annotation item : annotationList) {
            try {
                SaAnnotationStrategy.instance.annotationHandlerMap.get(item.annotationType()).check(item, element);
                // 只要有一个校验通过，就可以直接返回了
                return;
            } catch (SaTokenException e) {
                errorList.add(e);
            }
        }

        // 执行至此，说明所有注解校验都通过不了，此时 errorList 里面会有多个异常，我们随便抛出一个即可
        throw errorList.get(0);
    }

}