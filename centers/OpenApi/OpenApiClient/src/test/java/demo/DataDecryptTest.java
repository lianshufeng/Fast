package demo;

import com.fast.dev.openapi.client.model.v1.ApiParamContent;
import com.fast.dev.openapi.client.util.OpenApiV1Util;
import lombok.SneakyThrows;

public class DataDecryptTest {
    @SneakyThrows
    public static void main(String[] args) {

        Object data = OpenApiV1Util.decrypt("253e05b2d2e94027b1609720263624ba", "ZFwUJ6h5yoFNIt1fpptaSyIQs6KGf44GkF1+5ypp95JabmWJqkfvo/Cvfrr+z+oGQCL7ijvOXLqKzwHs3BKpaOd1NDo3yd/7K9XoME6cTGPrGPSjZ53tcxn6bmDztZ8nPZVgsbhrb225bWQkxyEoYXGQoxucMrE+sWKpPO/BayIIzlKTLRpzwIXl0+TIn7Dc9lyMlRc4cFmXeRgzl/jwnzuld5L04Ry5LbIip6pTFvT8Hi0QMbv5PUVNMZIqoAZdDl3iGapVwgCHcyB6d5hl7gpnumwuvDgRyQosAWMKbEmxLQTLBNrG2VWo56h10/1KhhxysQOeYe/IHRgviPjLEK27Y9dZ9z2U0Bf+8QgRZ+Y2TbTYDAO+RbOF2EInuEgXnzF95QrFOv09webt2El9SUiy90cmaZuQfxRyeZGlJ8yRvk7fnbwGZNqgkEAs+AN6niHLrg2KQEYZwpBYbrbXGs1HQE3Qe9dAI6qkbX1zyEIoAN2Y3lKc2leLl5cqOVjXHPedEnQ84gLsoMseBrvfqVsjobqmWGDinmbGWYRfASm37JYr1Fpwz441a90P2hVzrXXdDOtoFy9hjRSOtGQRl5+8ZKnoncH72DfyDxQYeJw=", Object.class);
        System.out.println(data);


    }
}
