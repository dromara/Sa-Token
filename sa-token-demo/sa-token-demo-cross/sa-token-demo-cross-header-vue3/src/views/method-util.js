import axios from 'axios'

// 后端服务地址
export const baseUrl = "http://localhost:8081";

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

