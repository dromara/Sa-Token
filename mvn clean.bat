
call mvn clean

cd sa-token-demo-jwt
call mvn clean
cd ..

cd sa-token-demo-springboot
call mvn clean
cd ..

cd sa-token-demo-webflux
call mvn clean
cd ..

cd sa-token-demo-oauth2\sa-token-demo-oauth2-client
call mvn clean
cd ../..

cd sa-token-demo-oauth2\sa-token-demo-oauth2-server
call mvn clean
cd ../..

echo;
echo;
echo ----------- clean end ----------- 
echo;
pause