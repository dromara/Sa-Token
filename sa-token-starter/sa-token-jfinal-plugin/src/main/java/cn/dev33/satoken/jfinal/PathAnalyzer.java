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
package cn.dev33.satoken.jfinal;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PathAnalyzer {

    private static final Map<String, PathAnalyzer> cached = new LinkedHashMap<>();
    private final Pattern pattern;

    public static PathAnalyzer get(String expr) {
        PathAnalyzer pa = cached.get(expr);
        if (pa == null) {
            synchronized(expr.intern()) {
                pa = cached.get(expr);
                if (pa == null) {
                    pa = new PathAnalyzer(expr);
                    cached.put(expr, pa);
                }
            }
        }

        return pa;
    }

    private PathAnalyzer(String expr) {
        this.pattern = Pattern.compile(exprCompile(expr), Pattern.CASE_INSENSITIVE);
    }

    public Matcher matcher(String uri) {
        return this.pattern.matcher(uri);
    }

    public boolean matches(String uri) {
        return this.pattern.matcher(uri).find();
    }

    private static String exprCompile(String expr) {
        String p = expr.replace(".", "\\.");
        p = p.replace("$", "\\$");
        p = p.replace("**", ".[]");
        p = p.replace("*", "[^/]*");
        if (p.contains("{")) {
            if (p.indexOf("_}") > 0) {
                p = p.replaceAll("\\{[^\\}]+?\\_\\}", "(.+?)");
            }

            p = p.replaceAll("\\{[^\\}]+?\\}", "([^/]+?)");
        }

        if (!p.startsWith("/")) {
            p = "/" + p;
        }

        p = p.replace(".[]", ".*");
        return "^" + p + "$";
    }
}
