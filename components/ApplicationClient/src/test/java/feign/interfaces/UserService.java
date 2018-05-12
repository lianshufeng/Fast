package feign.interfaces;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;

@FeignClient(value = "ucenter",fallback = UserServiceError.class)
public interface UserService {


    @GetMapping(value = "/user/login.json", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    Object user();


}
