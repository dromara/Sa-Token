# SaTokenDao-数据持久接口

SaTokenDao 是数据持久层接口，负责所有会话数据的底层写入和读取。

--- 

### 1、常量 
``` java
SaTokenDao.NEVER_EXPIRE = -1;   // 常量，表示一个key永不过期 (在一个key被标注为永远不过期时返回此值)
SaTokenDao.NOT_VALUE_EXPIRE = -2;   // 常量，表示系统中不存在这个缓存 (在对不存在的key获取剩余存活时间时返回此值)  
```


### 2、字符串读写 
``` java
dao.get(key);   // 获取Value，如无返空
dao.set(key, value, timeout);   // 写入Value，并设定存活时间 (单位: 秒)
dao.update(key, value);   // 更新Value (过期时间不变)
dao.delete(key);   // 删除Value
dao.getTimeout(key);   // 获取Value的剩余存活时间 (单位: 秒) 
dao.updateTimeout(key, timeout);   // 修改Value的剩余存活时间 (单位: 秒) 
```


### 3、对象读写 
``` java
dao.getObject(key);   // 获取Object，如无返空
dao.setObject(key, value, timeout);   // 写入Object，并设定存活时间 (单位: 秒)
dao.setObject(key, value);   // 更新Object (过期时间不变)
dao.deleteObject(key);   // 删除Object
dao.getObjectTimeout(key);   // 获取Object的剩余存活时间 (单位: 秒) 
dao.updateObjectTimeout(key, timeout);   // 修改Object的剩余存活时间 (单位: 秒) 
```


### 4、Session读写 
``` java
dao.getSession(sessionId);   // 获取Session，如无返空
dao.setSession(session, timeout);   // 写入Session，并设定存活时间 (单位: 秒)
dao.setSession(session);   // 更新Session (过期时间不变)
dao.deleteSession(sessionId);   // 删除Session
dao.getSessionTimeout(sessionId);   // 获取Session的剩余存活时间 (单位: 秒) 
dao.updateSessionTimeout(sessionId, timeout);   // 修改Session的剩余存活时间 (单位: 秒) 
```


### 5、会话管理
``` java
dao.searchData(prefix, keyword, start, size, sortType);   // 搜索数据 
```











