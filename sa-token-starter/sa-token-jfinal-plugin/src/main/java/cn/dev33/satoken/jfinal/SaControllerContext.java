package cn.dev33.satoken.jfinal;

import com.jfinal.core.Controller;

public class SaControllerContext {
    private static ThreadLocal<Controller> controllers = new ThreadLocal<>();


    public static void hold(Controller controller) {
        controllers.set(controller);
    }

    public static Controller get() {
        return controllers.get();
    }

    public static void release() {
        controllers.remove();
    }
}
