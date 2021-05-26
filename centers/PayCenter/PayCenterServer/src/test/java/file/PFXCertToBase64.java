package file;

import lombok.SneakyThrows;
import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.Base64;

public class PFXCertToBase64 {

    @Test
    @SneakyThrows
    public void ali_merchantPrivateKey() {
        File file = new File("C:\\Users\\zZ\\Desktop\\中金支付\\zjyh_new.pfx");
        System.out.println(Base64.getEncoder().encodeToString(FileUtils.readFileToByteArray(file)));

    }


}
