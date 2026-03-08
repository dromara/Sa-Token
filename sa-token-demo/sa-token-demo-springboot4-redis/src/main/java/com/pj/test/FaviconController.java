package com.pj.test;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class FaviconController {

    @RequestMapping("/favicon.ico")
    public String favicon() {
        return "";
    }

}

