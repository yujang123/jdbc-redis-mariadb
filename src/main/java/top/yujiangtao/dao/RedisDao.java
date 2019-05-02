package top.yujiangtao.dao;

import top.yujiangtao.model.User;
import top.yujiangtao.utils.RedisUtils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by IntelliJ IDEA.
 *
 * @author yujan
 * Date 2019/4/27/0027
 * Time 21:55
 **/
public class RedisDao {

    private static Connection mariadbConn = RedisUtils.getMariadbConn();
    private static PreparedStatement statement;

    public void insertUser(User user) {
        String sql = "INSERT INTO mydb.`user_table` VALUES(?,?,?,?)";

        try {
            assert mariadbConn != null;
            statement = mariadbConn.prepareStatement(sql);
            statement.setString(1, user.getId());
            statement.setString(2, user.getUsername());
            statement.setString(3, user.getPhone());
            statement.setInt(4, user.getAge());
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                assert statement != null;
                statement.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public User findUserById(String id) {
        String sql = "SELECT id,username,phone,age FROM mydb.`user_table` WHERE id = ?";
        ResultSet resultSet = null;
        User user = null;

        try {
            statement = mariadbConn.prepareStatement(sql);
            statement.setString(1, id);
            resultSet = statement.executeQuery();
            while (resultSet.next()) {
                user = new User();
                user.setId(resultSet.getString("id"));
                user.setUsername(resultSet.getString("username"));
                user.setPhone(resultSet.getString("phone"));
                user.setAge(resultSet.getInt("age"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                assert resultSet != null;
                resultSet.close();
                statement.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        return user;
    }
}
