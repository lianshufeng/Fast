package com.fast.dev.pay.server.core.hb.conf;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@NoArgsConstructor
@AllArgsConstructor
@ConfigurationProperties(prefix = "hb")
public class HuaxiaConf {

    //主机地址
    private String host = "http://223.72.175.138:8092/trans/";

    //证书
    private String cert = "LS0tLS1CRUdJTiBDRVJUSUZJQ0FURS0tLS0tDQpNSUlEYXpDQ0FsT2dBd0lCQWdJRVhXZElWakFOQmdrcWhraUc5dzBCQVFzRkFEQndNUXN3Q1FZRFZRUUdFd0pEVGpFUU1BNEdBMVVFQnhNSFFtVnBhbWx1WnpFWE1CVUdBMVVFQ2hNT2QzZDNMbWg0WWk1amIyMHVZMjR4RURBT0JnTlZCQWdUQjBKbGFXcHBibWN4Q3pBSkJnTlZCQXNUQWtsVU1SY3dGUVlEVlFRREV3NTNkM2N1YUhoaUxtTnZiUzVqYmpBZUZ3MHhPVEE0TWprd016TTJOVFJhRncweU9UQTRNall3TXpNMk5UUmFNRnd4Q3pBSkJnTlZCQVlUQWtOT01RMHdDd1lEVlFRS0RBUjZhSHBtTVJBd0RnWURWUVFIREFkQ1pXbHFhVzVuTVJBd0RnWURWUVFJREFkQ1pXbHFhVzVuTVFzd0NRWURWUVFMREFKSlZERU5NQXNHQTFVRUF3d0VlbWg2WmpDQ0FTSXdEUVlKS29aSWh2Y05BUUVCQlFBRGdnRVBBRENDQVFvQ2dnRUJBSnBEb3JPVG9DUnRUbmhwQWw3Yk9IajJjc0ZjeVhTTzVKMTRraXFiZUttbVlRYXVXOHBFNy84SVEwbGFxRGdWelFhMEdxcWJXdWtsTzk0b1ZtSkNzc2NCSjFjaWVHUjU1a1lNK1dUamZudHJ0NkVKV3N5eklxZEx4aitiVXVEOEgrb1lCbEMveUVNakZKS0RuaHBaY1BIc1VVb0owbDdodXUwNE5oWHBYbFUwQVVqNzFERHJScnVNRXBQbE1PQVBhckJxQlBIWkQ3aXFVZ1lTNWVSUXlrQ0VQM25oZnFtQUNpSyswTHBnejJWNUc5cmUzSmpLZ3h4NGlKQVFxQmRBeExUbU0zY2NjcmdKVUl5QUpoeXBOVnlRK29rakptT3hsbUJkcSsvYlNsUmhoVC9pUzBUVDkyRG1kMkloSmJqVHhUSWZYNVZzWkVGZUt0QnExZ3VhcmswQ0F3RUFBYU1oTUI4d0hRWURWUjBPQkJZRUZEMDkwNzB3d0xlM09pUzZSR2hSQWpTUXpWUE9NQTBHQ1NxR1NJYjNEUUVCQ3dVQUE0SUJBUUEweUVxRXBWaXl1WXVGb2p1SmNRUlV4VW5iaC9KYzVMRy9qSWVud2JuSm5lSDlQdmErMStadlBObE9sa3dmSVh3YVBGL2FnUWpMZEhhd1ZRdEdEWldtZlBQK3c5ODEzL0s0SE1VOFZJZ2VqaTZLZ2NoQzJMcTNQdWhBSzRhOGJ6emEyQWcwM29tSVBKQjBVd2VsalBqK0d1eDFmK1VsTXRVY21GZ01PWkhFbEdybEJRbU44bnFjd2YzOTFXNUFqcGdEZk1hdTlqNVhIVVBBWWVDVFk2bWhVaWRoS2sxQmZDdWdnQnFVYmJod1JJcitGdHhLZXhQSUNUUG9YekgycXdMb1BPNFpIa2lxTEZqWkt4RkFlcTczeWhGbkFaM0ZMc1hOSFdIR2F5R2MvUTNxckRkRE16Z1VGMmpYWUdGbDRrUHE3MjNrVzJON2M0QkVEdERxRVdiTg0KLS0tLS1FTkQgQ0VSVElGSUNBVEUtLS0tLQ==";

    //用户订单到期时间,默认7天
    private long userOrderLimitTime = 1000 * 60 * 60 * 24 * 7;

    //每日限制金额(默认1万)
    private long dayLimitAmount = 1000000;

    //冻结次数上限
    private int maxFreezeCount = 9;

    //任务执行的线程数
    private int taskExecuteThreadCount = 80;

    //用户确认扣款时间
    private long userConfirmChargeTime = 1000 * 60 * 60 * 3;

    //调用方服务器
    private String OccurAdd = "192.168.0.1";

    //网络交易平台（或APP）名称
    private String internetPresence = "艾艺荷马";

    //验证码短息模板
    private String templateId = "SMS_198921236";

    //是否使用华夏api
    private boolean useHuaXiaApi = true;


}
