package demo;

import com.fast.dev.openapi.client.model.v1.ApiParamContent;
import com.fast.dev.openapi.client.util.OpenApiV1Util;
import lombok.SneakyThrows;

import java.net.URLEncoder;
import java.util.Map;

public class DataEncryptTest {
    @SneakyThrows
    public static void main(String[] args) {


        ApiParamContent apiParamContent = new ApiParamContent();
        apiParamContent.setTime(System.currentTimeMillis());
        apiParamContent.setBody(Map.of("code",11,"enterpriseName","asd哈哈%%@@++="));
        String data = OpenApiV1Util.encrypt("253e05b2d2e94027b1609720263624ba", apiParamContent);
        System.out.println(data);


    }
}
