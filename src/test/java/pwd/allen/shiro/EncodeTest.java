package pwd.allen.shiro;

import org.apache.shiro.codec.Base64;
import org.apache.shiro.codec.Hex;
import org.apache.shiro.crypto.AesCipherService;
import org.apache.shiro.crypto.SecureRandomNumberGenerator;
import org.apache.shiro.crypto.hash.*;
import org.apache.shiro.util.ByteSource;
import org.apache.shiro.util.SimpleByteSource;
import org.junit.Assert;
import org.junit.Test;

import java.security.Key;

/**
 * 编码解码
 * Shiro 提供了 base64 和 16 进制字符串编码/解码的 API 支持，方便一些编码解码操作
 *
 * 散列算法
 * 一般用于生成数据的摘要信息，是一种不可逆的算法，一般适合存储密码之类的数据，常见的散列算法如 MD5、SHA 等
 * 一般进行散列时最好提供一个 salt（盐，干扰数据），这样更难破解
 *
 * @author 门那粒沙
 * @create 2020-07-23 23:17
 **/
public class EncodeTest {

    private String str = "hello";

    /**
     * base64字符串编码/解码
     */
    @Test
    public void base64() {
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
        String encoded = Hex.encodeToString(str.getBytes());
        System.out.println(encoded);
        String str2 = new String(Hex.decode(encoded.getBytes()));
        Assert.assertEquals(str, str2);
    }

    /**
     * 生成散列值，指定salt和散列次数
     * 其他算法：可以看SimpleHash有什么子类
     */
    @Test
    public void md5() {
        // 干扰数据，盐
        String salt = "123";
        // 散列次数
        int hashIterations = 3;

        //<editor-fold desc="方法一">
        System.out.println(new Md5Hash(str, salt, hashIterations).toString());
        //</editor-fold>

        //<editor-fold desc="方法二">
        System.out.println( new SimpleHash("MD5", str, salt, hashIterations).toString());
        //</editor-fold>

        //<editor-fold desc="方法三">
        DefaultHashService hashService = new DefaultHashService(); //默认算法 SHA-512
        hashService.setHashAlgorithmName("MD5");
        // 私盐，其在散列时自动与用户传入的公盐混合产生一个新盐，默认无
//        hashService.setPrivateSalt(new SimpleByteSource(salt));
        hashService.setGeneratePublicSalt(true);//是否生成公盐，默认 false
        hashService.setRandomNumberGenerator(new SecureRandomNumberGenerator());//用于生成公盐。默认就这个
        hashService.setHashIterations(3); //生成 Hash 值的迭代次数
        HashRequest request = new HashRequest.Builder()
                .setAlgorithmName("MD5").setSource(ByteSource.Util.bytes(str))
                .setSalt(ByteSource.Util.bytes(salt)).setIterations(hashIterations).build();
        System.out.println(hashService.computeHash(request).toHex());
        //</editor-fold>
    }

    /**
     * 对称加密解密算法，如 AES、Blowfish 等
     */
    @Test
    public void aes() {
        AesCipherService aesCipherService = new AesCipherService();
        aesCipherService.setKeySize(128); //设置 key 长度
        //生成 key
        Key key = aesCipherService.generateNewKey();
        //加密
        String encrptText = aesCipherService.encrypt(str.getBytes(), key.getEncoded()).toHex();
        //解密
        String text2 = new String(aesCipherService.decrypt(Hex.decode(encrptText), key.getEncoded()).getBytes());
        Assert.assertEquals(str, text2);
    }
}
