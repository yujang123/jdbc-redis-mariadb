package top.yujiangtao.controller;

import org.apache.commons.beanutils.BeanUtils;
import top.yujiangtao.dao.RedisDao;
import top.yujiangtao.model.User;
import top.yujiangtao.utils.RedisUtils;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 *
 * @author yujan
 * Date 2019/4/27/0027
 * Time 23:58
 **/

public class RedisController extends HttpServlet {

    private static final long serialVersionUID = -2188282810937997781L;

    private static RedisDao redisDao = new RedisDao();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setCharacterEncoding("utf-8");
        resp.setContentType("text/html;charset=utf-8");

        Map parameterMap = req.getParameterMap();
        String method = req.getParameter("method");
        String id = req.getParameter("id");
        User user;

        switch (method) {
            case "add":
                user = new User();
                try {
                    BeanUtils.populate(user, parameterMap);
                } catch (IllegalAccessException | InvocationTargetException e) {
                    e.printStackTrace();
                }
                redisDao.insertUser(user);
                break;
            case "get":
                String redisById = RedisUtils.findRedisById(id);
                if (null != redisById) {
                    resp.getWriter().write(redisById);
                } else {
                    User userById = redisDao.findUserById(id);
                    if (null != userById) {
                        String result = RedisUtils.addUserToRedis(userById);
                        resp.getWriter().write(result);
                    } else {
                        resp.getWriter().write("数据库中没有该用户,可以先添加该用户");
                    }
                }
                System.out.println(redisById);
                break;
            default:
                resp.getWriter().write("没有该方法");
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doPost(req, resp);
    }
}
