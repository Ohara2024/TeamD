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

import bean.School;

public class SchoolDao {

    private DataSource ds;

    /**
     * コンストラクタ。JNDIからDataSourceをルックアップする。
     * DataSourceのルックアップに失敗した場合、RuntimeExceptionをスローする。
     */
    public SchoolDao() {
        try {
            // "java:comp/env/jdbc/exam" という名前でDataSourceをルックアップ
            this.ds = (DataSource) (new InitialContext()).lookup("java:comp/env/jdbc/yajima");
        } catch (NamingException e) {
            // ルックアップ失敗時のエラーをログに出力し、RuntimeExceptionをスロー
            System.err.println("DataSourceのルックアップに失敗しました: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("データベース接続設定 (DataSource) の取得に失敗しました。JNDI名 'java:comp/env/jdbc/exam' が正しく設定されているか確認してください。", e);
        }
    }

    /**
     * データソースからデータベース接続を取得する。
     * @return データベース接続オブジェクト
     * @throws SQLException データベースアクセスエラーが発生した場合
     */
    private Connection getConnection() throws SQLException {
        return ds.getConnection();
    }

    /**
     * SQL実行時の例外を処理し、RuntimeExceptionとして再スローするヘルパーメソッド。
     * @param message エラーメッセージ
     * @param e 発生したSQLException
     */
    private void handleSqlException(String message, SQLException e) {
        System.err.println(message + ": " + e.getMessage());
        e.printStackTrace();
        throw new RuntimeException(message, e);
    }

    /**
     * 学校コードに基づいて学校情報を取得する。
     * @param cd 取得する学校のコード
     * @return 取得した学校オブジェクト、見つからない場合はnull
     */
    public School get(String cd) {
        School school = null;
        // try-with-resources を使用して、Connection, PreparedStatement, ResultSetを自動的にクローズ
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement("SELECT * FROM school WHERE cd = ?")) {

            statement.setString(1, cd);
            try (ResultSet rSet = statement.executeQuery()) {
                if (rSet.next()) {
                    school = new School();
                    school.setCd(rSet.getString("cd"));
                    school.setName(rSet.getString("name"));
                }
            }
        } catch (SQLException e) {
            handleSqlException("学校情報の取得に失敗しました (コード: " + cd + ")", e);
        }
        return school;
    }

    /**
     * すべての学校情報を取得する。
     * @return すべての学校オブジェクトのリスト
     */
    public List<School> getAllSchools() {
        List<School> schools = new ArrayList<>();
        // try-with-resources を使用して、Connection, PreparedStatement, ResultSetを自動的にクローズ
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement("SELECT * FROM school ORDER BY cd");
             ResultSet rSet = statement.executeQuery()) { // executeQuery()もここで実行

            while (rSet.next()) {
                School school = new School();
                school.setCd(rSet.getString("cd"));
                school.setName(rSet.getString("name"));
                schools.add(school);
            }
        } catch (SQLException e) {
            handleSqlException("全学校情報の取得に失敗しました。", e);
        }
        return schools;
    }

    // 必要に応じて、他の学校に関するDAOメソッドを追加してください
}