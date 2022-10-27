import axios from 'axios'

// sso-client 的后端服务地址
export const baseUrl = "http://sa-sso-client1.com:9001";

// 封装一下 Ajax 方法
export const ajax = function(path, data, successFn) {
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
        successFn(res);
    }).
    catch(function (error) {
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

