# SaTokenInfo 参数详解

token信息Model: 用来描述一个token的常用参数

``` js
{
	"code": 200,
	"msg": "ok",
	"data": {
		"tokenName": "satoken",           // token名称
		"tokenValue": "e67b99f1-3d7a-4a8d-bb2f-e888a0805633",      // token值
		"isLogin": true,                  // 此token是否已经登录
		"loginId": "10001",               // 此token对应的LoginId，未登录时为null
		"loginType": "login",              // 账号类型标识
		"tokenTimeout": 2591977,          // token剩余有效期 (单位: 秒)
		"sessionTimeout": 2591977,        // User-Session剩余有效时间 (单位: 秒)
		"tokenSessionTimeout": -2,        // Token-Session剩余有效时间 (单位: 秒)
		"tokenActivityTimeout": -1,       // token剩余无操作有效时间 (单位: 秒)
		"loginDevice": "default-device"   // 登录设备标识
	},
}
```