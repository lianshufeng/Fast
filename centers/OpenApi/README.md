# 开发应用平台接口说明


### 应用密钥 与 私钥
1. 在企业管理中获取 ak 与 sk 
2. ak为应用接口id , sk为 通信密钥
3. 请妥善保管 sk , 可通过企业管理页面进行重置 sk


### 功能

- 通信流程
```shell script
参数数据(加密) -> openapi -> (解密) -> 转发到业务 -> 响应数据(加密)
```

- 加密方式 (字符编码均为 UTF-8)
````shell script
# 密码为 sk 
密码 = "openapi_key"
# 数据  , time为当前系统时间，数据为接口的参数,注意：body 的类型为字符串，如果参数为json则需要先转成json字符串
数据 = {
  "time" : 1601002002434
  "body" : "{'name':  'Hello World' }"
}

密钥 = md5(密码)
密文 = Base64Encrypt ( AesEncrypt( 数据,密钥 ) ) 
````


- 解密方式 (字符编码均为 UTF-8)
````shell script
# 密码为 sk 
密码 = "openapi_key"


密钥 = md5(密码)
数据 =  ( AesDecrypt( Base64Decrypt(密文) ,密钥 ) ) 
````


- 接口参数要求 , data密文
````json
{
    "ak":"5870e1b34c7440d4ac826ea1b06f6d6f",
    "data":"SivwD+M3+l+lVPXcmgKLsL1zg0pA6TnfSgePLJmozywMAVGz0aYtAjhHnWwFvhY69CU47hFdduP/mfQZW7PM8YSIlMdySed3q4hBSoDv3dU="
}
````

- 请求的URL
````shell script
openapi/v1/{url}
````


