# SaSession-会话对象

SaSession-会话对象，专业数据缓存组件。

--- 

### 1、常量 
``` java
SaSession.USER= "USER";   // 在 Session 上存储用户对象时建议使用的key 
SaSession.ROLE_LIST = "ROLE_LIST";   // 在 Session 上存储角色时建议使用的key 
SaSession.PERMISSION_LIST = "PERMISSION_LIST";   // 在 Session 上存储权限时建议使用的key 
```


### 2、构建相关 
``` java
session.getId();   // 获取此 Session 的 id 
session.setId(id);   // 写入此 Session 的 id
session.getCreateTime();   // 返回当前会话创建时间（时间戳）
session.setCreateTime(createTime);   // 写入此 Session 的创建时间（时间戳）
```


### 3、SaTerminalInfo 相关 
``` java
session.setTerminalList(terminalList);   // 写入登录终端信息列表
session.getTerminalList();   // 获取登录终端信息列表
session.terminalListCopy();   // 获取 登录终端信息列表 (拷贝副本)
session.getTerminalListByDeviceType(deviceType);   // 获取 登录终端信息列表 (拷贝副本)，根据 deviceType 筛选
session.getTerminal(tokenValue);   // 查找一个终端信息，根据 tokenValue
session.addTerminal(terminal);   // 添加一个终端信息
session.removeTerminal(tokenValue);   // 移除一个终端信息
session.maxTerminalIndex();   // 获取最大的终端索引值，如无返0
session.isTrustDeviceId("xxxxxxxxxxxxxxxxxxxxxxxx");   // 判断指定设备 id 是否为可信任设备
```


### 4、一些操作
``` java
session.update();   // 更新Session（从持久库更新刷新一下）
session.logout();   // 注销Session (从持久库删除)
session.logoutByTerminalCountToZero();   // 当 Session 上的 SaTerminalInfo 数量为零时，注销会话 
session.getTimeout();   // 获取此Session的剩余存活时间 (单位: 秒)
session.updateTimeout(timeout);   // 修改此Session的剩余存活时间
session.updateMinTimeout(minTimeout);   // 修改此Session的最小剩余存活时间 (只有在 Session 的过期时间低于指定的 minTimeout 时才会进行修改)
session.updateMaxTimeout(maxTimeout);   // 修改此Session的最大剩余存活时间 (只有在 Session 的过期时间高于指定的 maxTimeout 时才会进行修改)
session.trans(value);   // value为 -1 时返回 Long.MAX_VALUE，否则原样返回 
```


### 5、存取值 
``` java
session.get(key);   // 取值
session.get(key, defaultValue);   // 取值 (指定默认值)
session.get(key, () -> {});   // 取值 (如果值为 null，则执行 fun 函数获取值，并把函数返回值写入缓存) 
session.getString(key);   // 取值 (转String类型)
session.getInt(key);   // 取值 (转int类型)
session.getLong(key);   // 取值 (转long类型)
session.getDouble(key);   // 取值 (转double类型)
session.getFloat(key);   // 取值 (转float类型)
session.getModel(key, clazz);   // 取值 (指定转换类型)
session.getModel(key, clazz, defaultValue);   // 取值 (指定转换类型, 并指定值为Null时返回的默认值)
session.has(key);   // 是否含有某个key
session.set(key, value);   // 写值
session.setByNull(key, value);   // 写值 (只有在此 key 原本无值的情况下才会写入)
session.delete(key);   // 删值 
session.keys();   // 返回当前Session的所有key 
session.clear();   // 清空所有值 
session.getDataMap();   // 获取数据挂载集合（如果更新map里的值，请调用session.update()方法避免产生脏数据 ） 
session.refreshDataMap(dataMap);   // 写入数据集合 (不改变底层对象，只将此dataMap所有数据进行替换) 
```







