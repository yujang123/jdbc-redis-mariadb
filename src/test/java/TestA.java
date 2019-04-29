import io.lettuce.core.RedisClient;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.api.sync.RedisCommands;
import org.junit.Test;
import top.yujiangtao.dao.RedisDao;
import top.yujiangtao.model.User;
import top.yujiangtao.utils.RedisUtils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * Created by IntelliJ IDEA.
 *
 * @author yujan
 * Date 2019/4/28/0028
 * Time 1:22
 **/
public class TestA {

    @Test
    public void s() {
        RedisClient redisClient = RedisClient.create("redis://192.168.74.128:6379/0");
        StatefulRedisConnection<String, String> connection = redisClient.connect();
        RedisCommands<String, String> syncCommands = connection.sync();
        System.out.println(syncCommands.ping());
    }

    @Test
    public void mariadbConnTest() {
        Connection mariadbConn = RedisUtils.getMariadbConn();
        PreparedStatement statement = null;
        String sql = "INSERT INTO mydb.`user_table` VALUES(?,?,?,?)";
        try {
            assert mariadbConn != null;
            statement = mariadbConn.prepareStatement(sql);
            statement.setString(1, "1");
            statement.setString(2, "Freedom");
            statement.setString(3, "12345678");
            statement.setInt(4, 20);
            int i = statement.executeUpdate();
            System.out.println(i);
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                assert statement != null;
                statement.close();
                mariadbConn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    @Test
    public void testFind() {
        RedisDao rd = new RedisDao();
        User user = rd.findUserById("1");
        System.out.println(user);
    }
}
