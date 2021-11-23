package cn.dev33.satoken.jfinal;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PathAnalyzer {

    private static Map<String, PathAnalyzer> cached = new LinkedHashMap();
    private Pattern pattern;

    public static PathAnalyzer get(String expr) {
        PathAnalyzer pa = (PathAnalyzer)cached.get(expr);
        if (pa == null) {
            synchronized(expr.intern()) {
                pa = (PathAnalyzer)cached.get(expr);
                if (pa == null) {
                    pa = new PathAnalyzer(expr);
                    cached.put(expr, pa);
                }
            }
        }

        return pa;
    }

    private PathAnalyzer(String expr) {
        this.pattern = Pattern.compile(exprCompile(expr), 2);
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
        if (p.indexOf("{") >= 0) {
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
