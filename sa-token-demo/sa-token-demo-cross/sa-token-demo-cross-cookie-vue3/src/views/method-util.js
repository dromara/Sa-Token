import axios from 'axios'

// 后端服务地址 (在 Cookie 版跨域模式中，此处应该是一个 https 地址) 
// export const baseUrl = "http://localhost:8081";
export const baseUrl = "https://20e331r221.yicp.fun";


// 封装一下 Ajax 方法
export const ajax = function(path, data, successFn) {
    axios({
        url: baseUrl + path,
        method: 'post',
        data: data,
		// 重点：开启第三方 Cookie 
		withCredentials: true,
        headers: {
            "Content-Type": "application/x-www-form-urlencoded"
        }
    }).
    then(function (response) { // 成功时执行
        const res = response.data;
        successFn(res);
    }).
    catch(function (error) {
        return alert("异常：" + JSON.stringify(error));
    })
}

