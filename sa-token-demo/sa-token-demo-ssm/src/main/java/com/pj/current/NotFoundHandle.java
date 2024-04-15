package com.pj.current;

import cn.dev33.satoken.util.SaResult;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 处理 404
 * @author click33
 */
@RestController
public class NotFoundHandle implements ErrorController {

	@RequestMapping("/error")
    public Object error(HttpServletRequest request, HttpServletResponse response) {
		response.setStatus(200);
        return SaResult.get(404, "not found", null);
    }

}
