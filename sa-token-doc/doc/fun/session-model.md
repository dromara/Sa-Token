# Session模型详解

--- 

在`sa-token`中, `Session` 分为三种, 分别是：
- `User-Session`: 指的是框架为每个`loginId`分配的`Session` 
- `Token-Session`: 指的是框架为每个`token`分配的`Session` 
- `自定义Session`: 指的是以一个`特定的值`作为SessionId，来分配的`Session` 


**假设三个客户端登录同一账号，且配置了不共享token，那么此时的Session模型是：**

![session-model](https://oss.dev33.cn/sa-token/doc/session-model3.png 's-w')

简而言之：
- `Token-Session` 以token为主，只要token不同，那么对应的Session对象就不同
- `User-Session`  以UserId为主，只要token指向的UserId一致，那么对应的Session对象就一致
- `自定义Session` 以特定的key为主，不同key对应不同的Session对象



