import axios from 'axios'

// sso-client 的后端服务地址
// export const baseUrl = "http://sa-sso-client1.com:9002"; // 模式二后端
export const baseUrl = "http://sa-sso-client1.com:9003";  // 模式三后端

// 封装一下 Ajax 方法
export const ajax = function(path, data, successFn) {
    console.log('发起请求：', baseUrl + path, JSON.stringify(data));
    axios({
        url: baseUrl + path,
        method: 'post',
        data: data,
        headers: {
            "Content-Type": "application/x-www-form-urlencoded",
            "satoken": localStorage.getItem("satoken")
        }
    }).
    then(function (response) { // 成功时执行
        const res = response.data;
        console.log('返回数据：', res);
        if(res.code === 500) {
            return alert(res.msg);
        }
        successFn(res);
    }).
    catch(function (error) {
        console.error('请求失败:', error);
        return alert("异常：" + JSON.stringify(error));
    })
}

// 从url中查询到指定名称的参数值
export const getParam = function(name, defaultValue){
    var query = window.location.search.substring(1);
    var vars = query.split("&");
    for (var i=0;i<vars.length;i++) {
        var pair = vars[i].split("=");
        if(pair[0] == name){return pair[1];}
    }
    return(defaultValue == undefined ? null : defaultValue);
}

