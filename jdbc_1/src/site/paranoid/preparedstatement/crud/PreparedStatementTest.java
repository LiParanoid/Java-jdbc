package site.paranoid.preparedstatement.crud;

import org.junit.Test;
import site.paranoid.preparedstatement.util.JDBCUtil;

import java.io.InputStream;
import java.sql.*;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Properties;

public class PreparedStatementTest {

    @Test
    public void testInsert() {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        // 1 读取配置文件信息
        try {
            InputStream resourceAsStream = PreparedStatementTest.class.getClassLoader().getResourceAsStream("jdbc.properties");
            Properties properties = new Properties();
            properties.load(resourceAsStream);
            String url = properties.getProperty("url");
            String user = properties.getProperty("user");
            String password = properties.getProperty("password");
            // 2.加载数据库链接驱动
            Class.forName("com.mysql.jdbc.Driver");
            // 3.(注册Driver已在DriverManager静态方法中调用,直接获得Connection实例对象)
            connection = DriverManager.getConnection(url, user, password);

            // 4.编写SQL
            String sql = "INSERT INTO USER(NAME,AGE,BRITH) VALUES (?,?,?)";
            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, "小明");
            preparedStatement.setInt(2, 18);
            LocalDateTime afterDate = LocalDateTime.of(2019, 12, 31, 23, 59, 59,1);
            long afterDateLong =  afterDate.toInstant(ZoneOffset.of("+8")).toEpochMilli();
            preparedStatement.setTimestamp(3, new Timestamp(afterDateLong));

            preparedStatement.execute();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (preparedStatement != null)
                    preparedStatement.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }

            try {
                if (connection != null)
                    connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

    }


    @Test
    public void testAlter() {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        try {
            connection = JDBCUtil.getConnections();
            String sql = "UPDATE USER SET NAME=? WHERE ID = ? ";

            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1,"小李");
            preparedStatement.setInt(2,1);

            preparedStatement.execute();
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            JDBCUtil.closeResource(connection,preparedStatement);
        }

    }

    /***
     * 实现一个通用的增删改方法
     */
    public void update(String sql,Object ...args){
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        try {
            connection = JDBCUtil.getConnections();
            preparedStatement = connection.prepareStatement(sql);
            for (int i = 0; i < args.length; i++) {
                preparedStatement.setObject(i+1,args[i]);
            }
            preparedStatement.execute();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            JDBCUtil.closeResource(connection,preparedStatement);
        }
    }

    @Test
    public void testCommonUpdate(){}{
//        String sql = "DELETE FROM USER WHERE ID = ?";
//        String sql = "UPDATE USER SET AGE = ? WHERE ID = ?";
        String sql = "INSERT INTO USER(NAME,AGE,BRITH)VALUES(?,?,?)";
        LocalDateTime localDateTime = LocalDateTime.of(2018, 10, 15, 22, 23, 24, 5678);
        long milli = localDateTime.toInstant(ZoneOffset.of("+8")).toEpochMilli();

        update(sql,"小孙",22,new Timestamp(milli));
    }
}
