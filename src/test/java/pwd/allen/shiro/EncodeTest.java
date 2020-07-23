package pwd.allen.shiro;

import org.apache.shiro.codec.Base64;
import org.apache.shiro.codec.Hex;
import org.junit.Assert;
import org.junit.Test;

/**
 * Shiro 提供了 base64 和 16 进制字符串编码/解码的 API 支持，方便一些编码解码操作
 * @author 门那粒沙
 * @create 2020-07-23 23:17
 **/
public class EncodeTest {

    /**
     * base64
     */
    @Test
    public void base64() {
        String str = "hello";
        String base64Encoded = Base64.encodeToString(str.getBytes());
        System.out.println(base64Encoded);
        String str2 = Base64.decodeToString(base64Encoded);
        Assert.assertEquals(str, str2);
    }

    /**
     * 16进制字符串编码/解码
     */
    @Test
    public void hex() {
        String str = "hello";
        String encoded = Hex.encodeToString(str.getBytes());
        System.out.println(encoded);
        String str2 = new String(Hex.decode(encoded.getBytes()));
        Assert.assertEquals(str, str2);
    }
}
