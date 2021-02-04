function formatDate(timestamp, fmt = 'yyyy-MM-dd') {
    if (!timestamp) {
        return '?'
    }
    const date = new Date(Number(timestamp))

    if (/(y+)/.test(fmt)) {
        fmt = fmt.replace(RegExp.$1, (date.getFullYear() + '').substr(4 - RegExp.$1.length));
    }
    let o = {
        'M+': date.getMonth() + 1,
        'd+': date.getDate(),
        'h+': date.getHours(),
        'm+': date.getMinutes(),
        's+': date.getSeconds()
    };
    for (let k in o) {
        let str = o[k] + '';
        if (new RegExp(`(${k})`).test(fmt)) {
            fmt = fmt.replace(RegExp.$1, (RegExp.$1.length === 1) ? str : padLeftZero(str));
        }
    }
    return fmt;
}
function formatTime(timestamp, fmt = 'yyyy-MM-dd hh:mm') {
    if (!timestamp) {
        return '?'
    }
    return formatDate(timestamp, fmt)
}
function padLeftZero(str) {
    return ('00' + str).substr(str.length);
}