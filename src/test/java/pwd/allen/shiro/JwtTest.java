package pwd.allen.shiro;

import org.junit.Test;
import pwd.allen.shiro.util.TokenUtil;

/**
 * @author lenovo
 * @create 2020-08-17 9:54
 **/
public class JwtTest {

    @Test
    public void sign() throws InterruptedException {
        // 创建token，过期时间2秒
        String token = TokenUtil.sign("门那粒沙", "pwd", 2 * 1000);
        System.out.println(String.format("token=%s", token));

        // 从token中解析userInfo
        System.out.println(String.format("TokenUtil.getUserName=%s", TokenUtil.getUserName(token, "userInfo")));

        // 验证token有效性
        System.out.println(String.format("TokenUtil.verify=%s", TokenUtil.verify(token)));

        Thread.sleep(2000);

        // 过期后验证失败：TokenExpiredException
        System.out.println(String.format("TokenUtil.verify=%s", TokenUtil.verify(token)));
    }
}
