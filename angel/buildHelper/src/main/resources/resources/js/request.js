// const axios =  require('./axios.min.js')

//取出服务端地址
const baseUrl = document.getElementById("script_set_baseUrl").value;


// create an axios instance
const service = axios.create({baseURL: baseUrl});

// 合并header
let mergeHeaders = {}

// 请求拦截器
const requestFulfilled = (config) => {

    for (let i in mergeHeaders) {
        if (Object.prototype.hasOwnProperty.call(mergeHeaders, i)) config.headers[i] = mergeHeaders[i]
    }

    if (config.method.toLowerCase() === 'post' && typeof config.data !== 'string') {
        if (config.headers['Content-Type'] && config.headers['Content-Type'].toLowerCase().includes('application/json')) {
            if (typeof config.data !== 'string') {
                config.data = JSON.stringify(config.data)
            }
        } else {
            if (!config.data) {
                config.data = {};
            }

            if (config.headers['Content-Type'] === 'multipart/form-data') {
                let data = new FormData(); //创建form对象
                Object.keys(config.data).forEach(itm => {
                    if (config.data[itm] == null) {
                        data.append(itm, "");
                    } else if (typeof config.data[itm] === "object" && config.data[itm].length) {
                        for (let i = 0; i < config.data[itm].length; i++) {
                            data.append(`${itm}[${i}]`, config.data[itm][i]);
                        }
                    } else {
                        data.append(itm, config.data[itm]);
                    }
                });
                config.data = data;
            }
        }
    }
    return config
};
const requestRejected = (error) => {
    console.log(error); // for debug
    return Promise.reject(error)
};

// 响应拦截器
const responseFulfilled = (resp) => {
    if (resp.config.fullResponse) {
        return Promise.resolve(resp);
    }
    if (resp.config.responseType === 'blob' || resp.config.originResponse) {
        return Promise.resolve(resp.data);
    }

    try {
        const data = resp.data;

        // 异常处理
        if (data.state === 'Exception') {
            switch (data.exception.type) {
                // 令牌失效，踢出系统
                case 'AuthenticationCredentialsNotFoundException':
                    throw new Error('无效的令牌');
                // 没有权限
                case 'AccessDeniedException':
                    throw new Error('不允许访问');
                case 'IllegalArgumentException':
                    throw new Error(data.exception.message);
                default:
                    throw new Error('无法处理的服务器异常');
            }
        }

        // Error
        if (data.state === 'Error') {
            throw new Error('服务器连接超时，请稍后再试')
        }

        // 正常返回
        if (data.state.toLowerCase() === 'success') {
            return Promise.resolve(data.content)

        }

        // 缺省处理
        throw new Error('服务器连接超时，请稍后再试')
    } catch (e) {
        return Promise.reject(e)
    }
};
const responseRejected = (error) => {
    console.log('网络错误:', error) // for debug
};


service.interceptors.request.use(requestFulfilled, requestRejected);

// 返回拦截器
service.interceptors.response.use(responseFulfilled, responseRejected);


/**
 * 封装get方法
 * @param url
 * @param params
 * @param config
 * @param callback 用于流程被打断后重新接续的回调，例登录过程中输入验证码
 * @returns {Promise}
 */
function fetch(url, params = {}, config = {}, callback = null) {
    return service.get(url, {
        params: params,
        ...config,
        callback
    })
}

/**
 * 封装post请求
 * @param url
 * @param data
 * @param config
 * @param callback
 * @returns {Promise}
 */
function post(url, data = {}, config = {}, callback = null) {
    return service.post(url, data, {
        ...config,
        callback
    })
}