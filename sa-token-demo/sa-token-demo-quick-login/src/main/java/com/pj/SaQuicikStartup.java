package com.pj;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import cn.dev33.satoken.quick.SaQuickManager;

/**
 * springboot启动之后 
 * @author kong
 *
 */
@Component
public class SaQuicikStartup implements CommandLineRunner {

	@Value("${spring.application.name:sa-quick}")
    private String applicationName;
	
	@Value("${server.port:8080}")
    private String port;

    @Value("${server.servlet.context-path:}")
    private String path;

//    @Value("${spring.profiles.active:}")
//    private String active;
   
    @Override
    public void run(String... args) throws Exception {
         String str = "\n------------- " + applicationName + " 启动成功 (" + getNow() + ") -------------\n" + 
                 "    - home: " + "http://localhost:" + port + path + "\n" +
                 "    - name: " + SaQuickManager.getConfig().getName() + "\n"+
                 "    - pwd : " + SaQuickManager.getConfig().getPwd() + "\n";
         System.out.println(str);
    }

    
	
	/**
	 * 返回系统当前时间的YYYY-MM-dd hh:mm:ss 字符串格式
	 */
	private static String getNow(){
		return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
	}
    

}

