package dao; // あなたのパッケージ名に合わせてください

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import javax.naming.InitialContext;
import javax.sql.DataSource;

import bean.Score;

public class ScoreDAO {
    // データソースのルックアップ名 (test_regist.jspと同じ)
    private static final String DATASOURCE_NAME = "java:/comp/env/jdbc/exam";

    /**
     * 新しい成績をデータベースに登録します。
     * @param score 登録する成績情報
     * @return 登録が成功した場合はtrue、失敗した場合はfalse
     * @throws SQLException データベースアクセスエラーが発生した場合
     */
    public boolean insertScore(Score score) throws SQLException {
        Connection con = null;
        PreparedStatement st = null;
        boolean success = false;

        try {
            InitialContext ic = new InitialContext();
            DataSource ds = (DataSource) ic.lookup(DATASOURCE_NAME);
            con = ds.getConnection();

            // SQL INSERT文
            // テーブル名やカラム名が不明なため仮定しています。実際のDBスキーマに合わせてください。
            String sql = "INSERT INTO SCORE (STUDENT_NO, SUBJECT_CD, SCHOOL_CD, POINT) VALUES (?, ?, ?, ?)";
            st = con.prepareStatement(sql);

            st.setString(1, score.getStudentNo());
            st.setString(2, score.getSubjectCd());
            st.setString(3, score.getSchoolCd());
            st.setInt(4, score.getPoint());

            int rowsAffected = st.executeUpdate(); // INSERT文を実行
            if (rowsAffected > 0) {
                success = true;
            }

        } catch (Exception e) { // NamingExceptionなどもここでキャッチ
            e.printStackTrace();
            throw new SQLException("データベース接続またはSQL実行エラー: " + e.getMessage(), e);
        } finally {
            // リソースの解放
            if (st != null) { try { st.close(); } catch (SQLException e) { e.printStackTrace(); } }
            if (con != null) { try { con.close(); } catch (SQLException e) { e.printStackTrace(); } }
        }
        return success;
    }

    // もし変更処理も行うなら、ここに updateScore メソッドを追加
    // public boolean updateScore(Score score) throws SQLException { ... }

    // もしIDで成績情報を取得するなら、ここに findById メソッドを追加
    // public Score findById(String id) throws SQLException { ... }
}