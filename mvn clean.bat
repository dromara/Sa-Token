
:: 整体clean
call mvn clean

:: demo模块clean
cd sa-token-demo

cd sa-token-demo-jwt
call mvn clean
cd ..

cd sa-token-demo-springboot
call mvn clean
cd ..

cd sa-token-demo-webflux
call mvn clean
cd ..

cd sa-token-demo-oauth2-client
call mvn clean
cd ..

cd sa-token-demo-oauth2-server
call mvn clean
cd ..

cd sa-token-demo-quick-login
call mvn clean
cd ..

cd ..

:: 最后打印
echo;
echo;
echo ----------- clean end ----------- 
echo;
pause