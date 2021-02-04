package com.fast.dev.pushcenter.core.service.remote;

import com.fast.dev.ucenter.core.service.UserManagerService;
import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(value = "userserver")
public interface RemoteUserCenterService extends UserManagerService {


}