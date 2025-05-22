package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

public class ClassNumDao {

    private DataSource ds;

    public ClassNumDao() {
        try {
            this.ds = (DataSource) (new InitialContext()).lookup("java:comp/env/jdbc/Gakuseiseiseki");
        } catch (NamingException e) {
            e.printStackTrace();
            throw new RuntimeException("DataSourceのルックアップに失敗しました。", e);
        }
    }

    private Connection getConnection() throws SQLException {
        return ds.getConnection();
    }

    // ★ このメソッドのシグネチャが重要です ★
    public List<String> filter(String schoolCd) throws Exception { // 引数と throws Exception を確認
        List<String> classNums = new ArrayList<>();
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet rSet = null;

        try {
            connection = getConnection();
            statement = connection.prepareStatement("SELECT DISTINCT class_num FROM student WHERE school_cd = ? ORDER BY class_num");
            statement.setString(1, schoolCd);
            rSet = statement.executeQuery();
            while (rSet.next()) {
                classNums.add(rSet.getString("class_num"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("クラス番号の取得に失敗しました。", e);
        } finally {
            try {
                if (rSet != null) rSet.close();
                if (statement != null) statement.close();
                if (connection != null) connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return classNums;
    }
}