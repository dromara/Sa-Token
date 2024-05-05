
:: 整体clean
call mvn clean

:: demo模块clean
cd sa-token-demo

cd sa-token-demo-alone-redis & call mvn clean & cd ..
cd sa-token-demo-alone-redis-cluster & call mvn clean & cd ..
cd sa-token-demo-case & call mvn clean & cd ..
cd sa-token-demo-grpc & call mvn clean & cd ..
cd sa-token-demo-jwt & call mvn clean & cd ..
cd sa-token-demo-quick-login & call mvn clean & cd ..
cd sa-token-demo-solon & call mvn clean & cd ..
cd sa-token-demo-springboot & call mvn clean & cd ..
cd sa-token-demo-springboot3-redis & call mvn clean & cd ..
cd sa-token-demo-springboot-redis & call mvn clean & cd ..
cd sa-token-demo-springboot-redisson & call mvn clean & cd ..
cd sa-token-demo-test & call mvn clean & cd ..
cd sa-token-demo-thymeleaf & call mvn clean & cd ..
cd sa-token-demo-beetl & call mvn clean & cd ..
cd sa-token-demo-webflux & call mvn clean & cd ..
cd sa-token-demo-webflux-springboot3 & call mvn clean & cd ..
cd sa-token-demo-websocket & call mvn clean & cd ..
cd sa-token-demo-websocket-spring & call mvn clean & cd ..
cd sa-token-demo-bom-import & call mvn clean & cd ..

cd sa-token-demo-hutool-timed-cache & call mvn clean & cd ..
cd sa-token-demo-ssm & call mvn clean & cd ..



cd sa-token-demo-sso
cd sa-token-demo-sso-server & call mvn clean & cd ..
cd sa-token-demo-sso1-client & call mvn clean & cd ..
cd sa-token-demo-sso2-client & call mvn clean & cd ..
cd sa-token-demo-sso3-client & call mvn clean & cd ..
cd sa-token-demo-sso3-client-test2 & call mvn clean & cd ..
cd sa-token-demo-sso3-client-nosdk & call mvn clean & cd ..
cd ..

cd sa-token-demo-sso-for-solon
cd sa-token-demo-sso1-client-solon & call mvn clean & cd ..
cd sa-token-demo-sso2-client-solon & call mvn clean & cd ..
cd sa-token-demo-sso3-client-solon & call mvn clean & cd ..
cd sa-token-demo-sso-server-solon & call mvn clean & cd ..
cd ..

cd sa-token-demo-oauth2
cd sa-token-demo-oauth2-client & call mvn clean & cd ..
cd sa-token-demo-oauth2-server & call mvn clean & cd ..
cd ..

:: cd sa-token-demo-cross
:: cd sa-token-demo-cross-header-server & call mvn clean & cd ..
:: cd sa-token-demo-cross-cookie-server & call mvn clean & cd ..
:: cd ..

cd sa-token-demo-dubbo
cd sa-token-demo-dubbo-consumer & call mvn clean & cd ..
cd sa-token-demo-dubbo-provider & call mvn clean & cd ..
cd sa-token-demo-dubbo3-consumer & call mvn clean & cd ..
cd sa-token-demo-dubbo3-provider & call mvn clean & cd ..
cd ..

cd sa-token-demo-remember-me
cd sa-token-demo-remember-me-server & call mvn clean & cd ..
cd ..







cd ..

:: test clean 

cd sa-token-test
call mvn clean
cd ..




:: 最后打印
echo;
echo;
echo ----------- clean end ----------- 
echo;
pause