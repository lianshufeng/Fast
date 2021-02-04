#### 测试环境：http://api.dev.aiyilearning.com/openapi/v1/

- 接口状态表

接口返回状态码 | 描述
---|---
Success | 成功
Fail | 失败

- 订单状态表

订单状态码 | 描述
---|---
WaitFreeze | 等待冻结
WaitCharge | 等待扣款
Finish | 完成合同
Cancel | 终止合同

- 冻结状态表

订单冻结状态码 | 描述
---|---
Wait | 等待冻结
Fail | 冻结失败
Finish | 冻结成功

- 扣款状态表

订单扣款状态码 | 描述
---|---
Success | 扣款成功
Fail | 扣款失败
WaitCheck | 等待验证

- 证件类型表

证件类型状态码 | 描述
---|---
IdCard | 身份证




- 立即执行调度任务(hb/ea/doJob)
 
````
// 请求
{
    
}

// 应答
{
    "state":Success,                                                                        //业务状态（见状态表）
    "msg":"成功",                                                                           //描述
    "content":true                                                                          //返回结果
}
````

- 获取当前企业的信息(hb/ea/doJob)
 
````
// 请求
{
    
}

// 应答
{
    "state":Success,                                                                        //业务状态（见状态表）
    "msg":"成功",                                                                           //描述
    "content":{                                                 
        "epId":"5f39fa786e3acd2002eca138",                                                  //企业id
        "id":"5f39fa786e3acd2002eca138",                                                    //华夏企业账户id
        "code":"c0000037",                                                                  //企业编码
        "enterpriseName":"重庆艾玛",                                                        //企业名
        "enterprisePhone":"1***3",                                                          //企业负责人手机
        "enterprisePersonName":"锋锋",                                                      //企业负责人
        "mchtId":"HX0200000000440",                                                         //商户id
        "mchtNo":"HX0200000000440",                                                         //商户号
        "appid":"537A5A4155304167784C576A496B5171784A734E526466473657513D",                 //应用id
        "workTime":600,                                                                   //工作时间，单位分钟，如早上8点为 : 8 * 60， 意见时间为早上8点到晚上20点之前
        "salePhone":"15123241363",                                                          //销售手机号码
        "customerPhone":"400-444-4444",                                                     //收款客户电话

    }
}
````

- 更新预开通的企业(hb/ea/update)
 
````
// 请求
{
        "enterpriseName":"重庆艾玛",                                                        //企业名
        "enterprisePhone":"1***3",                                                          //企业负责人手机
        "enterprisePersonName":"小锋锋",                                                    //企业负责人
        "mchtId":"HX0200000000440",                                                         //商户id
        "mchtNo":"HX0200000000440",                                                         //商户号
        "appid":"537A5A4155304167784C576A496B5171784A734E526466473657513D",                 //应用id
        "workTime":600,                                                                   //工作时间，单位分钟，如早上8点为 : 8 * 60， 意见时间为早上8点到晚上20点之前
        "salePhone":"15123241363",                                                          //销售手机号码
        "customerPhone":"400-444-4444",                                                     //收款客户电话
}

// 应答
{
    "state":Success,                                                                        //业务状态（见状态表）
    "msg":"成功",                                                                           //描述
    "content":{                                                 
        "epId":"5f39fa786e3acd2002eca138",                                                  //企业id
        "id":"5f39fa786e3acd2002eca138",                                                    //华夏企业账户id
        "code":"c0000037",                                                                  //企业编码
        "enterpriseName":"重庆艾玛",                                                        //企业名
        "enterprisePhone":"1***3",                                                          //企业负责人手机
        "enterprisePersonName":"小锋锋",                                                    //企业负责人
        "mchtId":"HX0200000000440",                                                         //商户id
        "mchtNo":"HX0200000000440",                                                         //商户号
        "appid":"537A5A4155304167784C576A496B5171784A734E526466473657513D",                 //应用id
        "workTime":600,                                                                   //工作时间，单位分钟，如早上8点为 : 8 * 60， 意见时间为早上8点到晚上20点之前
        "salePhone":"15123241363",                                                          //销售手机号码
        "customerPhone":"400-444-4444",                                                     //收款客户电话
    }
}
````

- 企业合同列表(hb/ea/contract/list)
 
````
// 请求
{
        "userName":"asd",                                                       //扣款人姓名
        "userMobile":"15123241363",
        "consumePhone":"15123241363"                                                //消费者电话
        "startFreezeTime":11111,                                                    //开始冻结时间          
        "endFreezeTime":111111,                                                     //结束冻结时间
        "startChargeTime":11111,                                                    //开始扣款时间
        "endChargeTime":111111,                                                     //结束扣款的时间
        "orderState":"Process",                                                     //订单状态
        "chargeState":"Success"                                                     //扣款状态
        "page":0,                                                                   //页码（从0开始）
        "size":20,                                                                  //每页数量
        "sort":creatTime,Desc                                                       //排序
    
}

// 应答
{
    "state":Success,                                                                        //业务状态（见业务状态表）
    "msg":"成功",                                                                           //描述
    "content":{                                                 
        "content": [{                                                                       //分页返回实体
				"epId": "5f39fa786e3acd2002eca138",                                         //企业id
				"id": "5f6821c6a7a0257da1c811e6",                                           //合同id
				"consumePhone": "13368172379",                                              //消费者手机号码
				"contractState": "WaitFreeze",                                              //扣款的订单状态（见订单状态表）
				"contractFreeze": {                                                         //订单的冻结状态
					"time": 1601008415770,                                                  //冻结时间
					"state": "Fail"                                                         //冻结状态（见冻结状态表）
				},
				"lastChargeContract":  {                                                    //最近的一次扣款信息
					"time": 1601008415770,                                                  //扣款时间
					"state": "Fail"                                                          //扣款状态（见扣款状态表）
				},                                                 
				"userAccount": {                                                            //用户账户信息
					"bindCardNo": null,                                                     //绑定账户
					"idNo": null,                                                           //身份证号码
					"idType": "IdCard",                                                     //证件类型（见证件类型表）
					"userName": "张亚军",                                                   //真实姓名      
					"userMobile": "1336833333",                                             //手机号码
				},
				"autoCharge": {                                                             //扣款信息
					"autoChargeInfos": [{                                                   //代扣信息
						"time": 2,                                                          //扣款时间,单位是天,如：第1天，第2天
						"amount": 100,                                                      //金额,单位精确到分,如：500 = 5块
						"taskId": null                                                      //扣款任务的id,可为null
					}],
					"remark": ""                                                             //备注,用于描述商品信息
				},
				"userOpenAccountInfo": null,                                                //用户的开户信息
				"orderName": "测试套餐4元",                                                 //订单名称
				"chargeAmount": 0,                                                          //已扣款金额
				"totalAmount": 400,                                                         //总金额 
				"startChargeTime": 0,                                                       //开始扣款时间
				"endChargeTime": 0,                                                         //结束扣款时间
				"chargeState": "Success",                                                   //扣款状态
				"orderState": "Process",                                                    //合同状态
				"freezeState": "Fail"                                                       //冻结状态
			},{...},{...}],
		"totalElements": 12,                                                                //总条数
		"totalPages": 2                                                                     //总页数
    }
}
````

- 查询合同详情(hb/ea/contract/details)
 
````
// 请求
{
    "id":"5f39fa786e3acd2002eca138"                                                         //合同ID
}

// 应答
{
    "state":Success,                                                                        //业务状态（见状态表）
    "msg":"成功",                                                                           //描述
    "content":{                                                 
        "userName":"张三",                                                                  //扣款人姓名
        "userPhone":"154646565656565656565656",                                             //扣款人电话
        "consumePhone":"12556565",                                                          //消费者电话
        "orderName":"测试套餐",                                                             //套餐名称，如果为空则为自定义套餐
        "totalAmount":"1***3",                                                              //总金额
        "freezeTask":{                                                                      //冻结任务
                "time": 1600659910476,                                                      //冻结时间
                "state": "Cancel",                                                          //冻结状态
                },                                                                 
        "chargeTasks": [{                                                                   //扣款信息
        			"amount": 100,                                                          //扣款金额
        			"paymentAmount": 0                                                      //已经支付金额
        		}, {
        			"amount": 100,
        			"paymentAmount": 0
        		}, {
        			"amount": 100,
        			"paymentAmount": 0
        		}, {
        			"amount": 100,
        			"paymentAmount": 0
        		}],
        "remark":"aa",                                                                      //备注
    }
}
````

- 终止合同(hb/ea/contract/details)
 
````
// 请求
{
    "id":"5f39fa786e3acd2002eca138"                                                         //合同ID
}

// 应答
{
    "state":Success,                                                                        //业务状态（见状态表）
    "msg":"成功",                                                                           //描述
    "content": true
}
````
- 获取所有的套餐列表(hb/ea/template/list）
 
````
// 请求
{                                            
        "page":0,                                                                   //页码（从0开始）
        "size":20,                                                                  //每页数量
        "sort":creatTime,Desc                                                       //排序
}

// 应答
{
    "state":Success,                                                                        //业务状态（见业务状态表）
    "msg":"成功",                                                                           //描述
    "content":{                                                 
        "content": [{                                                                       //代扣信息
				"autoChargeInfos": [{
					"time": 2,                                                              //扣款时间,单位是天,如：第1天，第2天
					"amount": 100,                                                          //金额,单位精确到分,如：500 = 5块
					"taskId": null
				}, {
					"time": 4,
					"amount": 100,
					"taskId": null
				},],
				"remark": "",                                                           //备注,用于描述商品信息
				"id": "5f3c943138cc6d4614f8129f",                                       //模板id
				"epId": "5f39fa786e3acd2002eca138",                                     //企业id                
				"hbId": "5f39fa20952b2c6c7af45ddc",                                     //华夏企业账户id
				"hbCode": "11",                                                         //华夏企业账户编码
				"code": "004",                                                          //模板编码
				"name": "测试套餐4元",                                                  //模板名称(套餐名称)
				"startTime": 1597766400000,                                             //套餐开始时间
				"endTime": 1601395200000,                                               //套餐结束时间
				"disable": false,                                                       //是否停用
				"epName": "asd哈哈%%@@++=",                                             //企业名
				"customerPhone": "10086"                                                //客服电话
			},{...},{...}],
		"totalElements": 12,                                                                //总条数
		"totalPages": 2                                                                     //总页数
    }
}
````

- 新增或者更新套餐模板(hb/ea/template/update）
 
````
// 请求

{
        "id": "5f3c943138cc6d4614f8129f",                                       //模板id(新增不填)
		"name": "测试套餐4元",                                                  //模板名称(套餐名称)
		"startTime": 1597766400000,                                             //套餐开始时间
		"endTime": 1601395200000,                                               //套餐结束时间
		"disable": false,                                                       //是否停用
		"autoChargeInfos": [{                                                   //代扣信息
					"time": 2,                                                 //扣款时间,单位是天,如：第1天，第2天
					"amount": 100,                                             //金额,单位精确到分,如：500 = 5块
					"taskId": null
				}, {
					"time": 4,
					"amount": 100,
					"taskId": null
		},]
}

// 应答
{
    "state":Success,                                                            //业务状态（见业务状态表）
    "msg":"成功",                                                              //描述
    "content":{                                                 
        "id": "5f3c943138cc6d4614f8129f",                                       //模板id
        "remark": "",                                                           //备注,用于描述商品信息
		"hbId": "5f39fa20952b2c6c7af45ddc",                                     //华夏企业账户id
		"hbCode": "11",                                                         //华夏企业账户编码
		"code": "004",                                                          //模板编码
		"name": "测试套餐4元",                                                  //模板名称(套餐名称)
		"startTime": 1597766400000,                                             //套餐开始时间
		"endTime": 1601395200000,                                               //套餐结束时间
		"disable": false,                                                       //是否停用
		"epName": "asd哈哈%%@@++=",                                             //企业名
		"customerPhone": "10086"                                                //客服电话
		"autoChargeInfos": [{                                                   //代扣信息
					"time": 2,                                                 //扣款时间,单位是天,如：第1天，第2天
					"amount": 100,                                             //金额,单位精确到分,如：500 = 5块
					"taskId": null
				}, {
					"time": 4,
					"amount": 100,
					"taskId": null
		},]
    }
}
````

- 删除套餐模板(hb/ea/template/remove)
 
````
// 请求
{
    "id":"5f39fa786e3acd2002eca138"                                                         //模板id
}

// 应答
{
    "state":Success,                                                                        //业务状态（见状态表）
    "msg":"成功",                                                                           //描述
    "content": Success
}
````

- 添加一个用户订单(hb/ea/userorder/add)
 
````
// 请求
{
	"consumePhone": "10086"                                                     //消费者手机号
	"name": "测试套餐4元",                                                      //模板名称(套餐名称)
	"autoChargeInfos": [{                                                       //代扣信息
					"time": 2,                                                 //扣款时间,单位是天,如：第1天，第2天
					"amount": 100,                                             //金额,单位精确到分,如：500 = 5块
					"taskId": null
				}, {
					"time": 4,
					"amount": 100,
					"taskId": null
		},]
	
}

// 应答
{
    "state":Success,                                                                        //业务状态（见状态表）
    "msg":"成功",                                                                           //描述
    "content": "sdasdasda"
}
````

- 订单列表(hb/ea/userorder/list)
 
````
// 请求
{
	"consumePhone": "10086"                                                     //消费者手机号
	"used": true,                                                               //是否使用
    "page":0,                                                                   //页码（从0开始）
    "size":20,                                                                  //每页数量
    "sort":creatTime,Desc                                                       //排序
}

// 应答
{
    "state":Success,                                                                        //业务状态（见业务状态表）
    "msg":"成功",                                                                           //描述
    "content":{                                                 
        "content": [{                                                                       //代扣信息
				"autoChargeInfos": [{
					"time": 2,                                                              //扣款时间,单位是天,如：第1天，第2天
					"amount": 100,                                                          //金额,单位精确到分,如：500 = 5块
					"taskId": null
				}, {
					"time": 4,
					"amount": 100,
					"taskId": null
				},],
				"remark": "",                                                           //备注,用于描述商品信息
				"id": "5f3c943138cc6d4614f8129f",                                       //订单ID
				"epId": "5f39fa786e3acd2002eca138",                                     //企业id                
				"hbId": "5f39fa20952b2c6c7af45ddc",                                     //华夏企业账户id
				"hbCode": "11",                                                         //华夏企业账户编码
				"code": "004",                                                          //模板编码
				"name": "测试套餐4元",                                                  //用户订单模型
				"endTime": 1601395200000,                                               //到期时间
				"used": false,                                                          //是否已使用
				"timeOut": false,                                                       //是否已过期
				"epName": "asd哈哈%%@@++=",                                             //企业名
				"customerPhone": "10086"                                                //客服电话
			},{...},{...}],
		"totalElements": 12,                                                                //总条数
		"totalPages": 2                                                                     //总页数
    }
}
````