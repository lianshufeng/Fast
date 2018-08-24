package com.fast.dev.ucenter.security.service.remote;

import com.fast.dev.ucenter.core.service.UserManagerService;
import org.springframework.cloud.openfeign.FeignClient;

/**
 * 申明远程接口
 */
@FeignClient(value = "userserver", fallback = RemoteUserCenterServiceError.class)
public interface RemoteUserCenterService extends UserManagerService {


}
