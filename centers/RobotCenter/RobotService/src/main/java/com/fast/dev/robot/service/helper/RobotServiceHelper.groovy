package com.fast.dev.robot.service.helper

import com.fast.dev.robot.service.conf.RobotFirewallConfig
import com.fast.dev.robot.service.type.RobotType
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Component
import org.springframework.util.LinkedMultiValueMap
import org.springframework.util.MultiValueMap
import org.springframework.web.client.RestTemplate

@Component
class RobotServiceHelper {

    @Autowired
    private RobotFirewallConfig robotFirewallConfig

    @Autowired
    private RestTemplate restTemplate;

    /**
     * 校验令牌是否已失效
     * @param token
     * @param parm
     * @return
     */
    boolean validate(String serviceName, RobotType robotType, Map<String, Object> parm) {
        String url = "http://" + robotFirewallConfig.robotServerName + "/validate"
        MultiValueMap<String, Object> map = new LinkedMultiValueMap<String, Object>()
        parm.entrySet().each { it ->
            map[it.getKey()] = it.getValue()
        }
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        HttpEntity<MultiValueMap<String, Object>> request = new HttpEntity<MultiValueMap<String, Object>>(map, headers);

        //进行令牌校验
        ResponseEntity responseEntity = this.restTemplate.postForEntity(url, request, Object.class)
        Object body = responseEntity.getBody()

        if (body['state'] == 'Success') {
            def content = body['content']
            return content['validateType'] == 'Done' && content['serviceName'] == serviceName && content['robotType'] == robotType.name()
        }
        return false
    }



}
