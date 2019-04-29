package top.yujiangtao.model;

import lombok.Data;

import java.io.Serializable;

/**
 * Created by IntelliJ IDEA.
 *
 * @author yujan
 * Date 2019/4/27/0027
 * Time 23:50
 **/
@Data
public class User implements Serializable {

    private static final long serialVersionUID = 8207477991219966567L;

    private String id;

    private String username;

    private String phone;

    private int age;
}
