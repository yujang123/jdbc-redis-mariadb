package top.yujiangtao.utils;

import com.alibaba.fastjson.JSONObject;
import io.lettuce.core.RedisClient;
import io.lettuce.core.RedisURI;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.api.sync.RedisCommands;
import org.apache.commons.beanutils.BeanUtils;
import top.yujiangtao.model.User;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.time.Duration;
import java.util.Map;
import java.util.Properties;

/**
 * Created by IntelliJ IDEA.
 *
 * @author yujan
 * Date 2019/4/27/0027
 * Time 22:06
 **/
public class RedisUtils {

    private static final Properties REDIS_CONFIG = new Properties();

    private static final String KEY_PREFIX = "u:info:id:";

    private static RedisClient redisClient;
    private static StatefulRedisConnection<String, String> connect;
    private static RedisCommands<String, String> commands;

    private static Connection mariadbConn;

    static {
        try {
            REDIS_CONFIG.load(RedisUtils.class.getClassLoader().getResourceAsStream("RedisConfig.properties"));

            // Redis配置
            String redisHost = REDIS_CONFIG.getProperty("redis.host");
            int redisPort = Integer.parseInt(REDIS_CONFIG.getProperty("redis.port"));
            long redisTimeout = Long.parseLong(REDIS_CONFIG.getProperty("redis.timeout"));
            int redisDatabase = Integer.parseInt(REDIS_CONFIG.getProperty("redis.database"));
            String redisPassword = REDIS_CONFIG.getProperty("redis.pwd");

            // mariadb配置
            String mariadbDriver = REDIS_CONFIG.getProperty("mariadb.driver");
            String mariadbUrl = REDIS_CONFIG.getProperty("mariadb.url");
            String mariadbUsername = REDIS_CONFIG.getProperty("mariadb.username");
            String mariadbPasswrod = REDIS_CONFIG.getProperty("mariadb.password");

            RedisURI redisURI = RedisURI.builder()
                    .withPassword(redisPassword)
                    .withDatabase(redisDatabase)
                    .withHost(redisHost).withPort(redisPort)
                    .withTimeout(Duration.ofSeconds(redisTimeout)).build();
            // 这里也可以直接给出redis的url,那样就不需要实例化RedisURI对象
            redisClient = RedisClient.create(redisURI);
            connect = redisClient.connect();
            commands = connect.sync();
            try {
                Class.forName(mariadbDriver);
                mariadbConn = DriverManager.getConnection(mariadbUrl, mariadbUsername, mariadbPasswrod);
            } catch (ClassNotFoundException | SQLException e) {
                e.printStackTrace();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static RedisCommands<String, String> getRedisCommands() {
        return commands;
    }

    public static Map<String, String> findRedisById(String id) {
        String uid = "u:info:id:" + id;

        return commands.hgetall(uid);
    }

    public static String redisConnectionClose() {
        String result = "not connection";
        if (null != connect) {
            connect.close();
            result = "connection close";
        }

        return result;
    }

    public static String redisClientShutdown() {
        String result = "not client";
        if (null != redisClient) {
            commands.shutdown(true);
            result = "client shutdown";
        }

        return result;
    }

    public static Connection getMariadbConn() {
        if (null != mariadbConn) {
            return mariadbConn;
        }

        return null;
    }

    public static String addUserToRedis(User userById) {
        String key = KEY_PREFIX + userById.getId();
        // String value = beanToJSON(userById);
        Map<String, String> value = null;
        try {
            value = BeanUtils.describe(userById);
        } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            e.printStackTrace();
        }
        // remove BeanUtils解析过来的map中的class字段
        if (null != value.get("class")) {
            value.remove("class");
        }

        // 此处也能用setex来设置存活时间
        // return commands.set(key, value);
        return commands.hmset(key, value);
    }

    private static String beanToJson(User user) {
        return JSONObject.toJSONString(user);
    }
}
