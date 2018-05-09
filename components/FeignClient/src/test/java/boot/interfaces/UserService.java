package boot.interfaces;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(value = "ucenter",fallback = UserServiceError.class)
public interface UserService {


    @GetMapping(value = "/user/login.json", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    Object user();


}
