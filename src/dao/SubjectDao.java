package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.naming.NamingException; // NamingExceptionのインポートを追加

import bean.School;
import bean.Subject;

public class SubjectDao extends Dao { // Daoクラスを継承

    // DataSourceは親クラスのDaoで初期化されるため、ここでは不要（もしDaoで初期化されていない場合は必要）
    // private DataSource dataSource; // <- もしDaoで初期化済ならコメントアウト

    public SubjectDao() throws NamingException {
        super(); // 親クラスのコンストラクタを呼び出す
        // もし親クラスのDaoでDataSourceが初期化されていない場合は、ここで初期化
        // InitialContext ic = new InitialContext();
        // this.dataSource = (DataSource) ic.lookup("java:comp/env/jdbc/yajima");
    }

    /**
     * 科目コードと学校コードで科目情報を取得します。
     * @param cd 科目コード
     * @param schoolCd 学校コード
     * @return 取得したSubjectオブジェクト。見つからない場合はnull。
     * @throws Exception データベースアクセスエラーが発生した場合
     */
    public Subject get(String cd, String schoolCd) throws Exception {
        Subject subject = null;
        Connection connection = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            connection = getConnection(); // 親クラスのDaoからConnectionを取得
            String sql = "SELECT CD, NAME, SCHOOL_CD FROM SUBJECT WHERE CD = ? AND SCHOOL_CD = ?";
            ps = connection.prepareStatement(sql);
            ps.setString(1, cd);
            ps.setString(2, schoolCd);
            rs = ps.executeQuery();

            if (rs.next()) {
                subject = new Subject();
                subject.setCd(rs.getString("CD"));
                subject.setName(rs.getString("NAME"));
                // Schoolオブジェクトをセットする場合（SubjectクラスにSchoolフィールドがある前提）
                School school = new School();
                school.setCd(rs.getString("SCHOOL_CD"));
                subject.setSchool(school);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new Exception("科目情報の取得に失敗しました。", e);
        } finally {
            if (rs != null) {
                try { rs.close(); } catch (SQLException sqle) { sqle.printStackTrace(); }
            }
            if (ps != null) {
                try { ps.close(); } catch (SQLException sqle) { sqle.printStackTrace(); }
            }
            if (connection != null) {
                try { connection.close(); } catch (SQLException sqle) { sqle.printStackTrace(); }
            }
        }
        return subject;
    }

    /**
     * 指定された学校の科目情報をすべて取得します。
     * @param school 対象の学校オブジェクト
     * @return 取得したSubjectオブジェクトのリスト
     * @throws Exception データベースアクセスエラーが発生した場合
     */
    public List<Subject> filter(School school) throws Exception {
        List<Subject> subjects = new ArrayList<>();
        Connection connection = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            connection = getConnection();
            String sql = "SELECT CD, NAME, SCHOOL_CD FROM SUBJECT WHERE SCHOOL_CD = ? ORDER BY CD";
            ps = connection.prepareStatement(sql);
            ps.setString(1, school.getCd());
            rs = ps.executeQuery();

            while (rs.next()) {
                Subject subject = new Subject();
                subject.setCd(rs.getString("CD"));
                subject.setName(rs.getString("NAME"));
                School associatedSchool = new School();
                associatedSchool.setCd(rs.getString("SCHOOL_CD"));
                subject.setSchool(associatedSchool);
                subjects.add(subject);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new Exception("科目リストの取得に失敗しました。", e);
        } finally {
            if (rs != null) {
                try { rs.close(); } catch (SQLException sqle) { sqle.printStackTrace(); }
            }
            if (ps != null) {
                try { ps.close(); } catch (SQLException sqle) { sqle.printStackTrace(); }
            }
            if (connection != null) {
                try { connection.close(); } catch (SQLException sqle) { sqle.printStackTrace(); }
            }
        }
        return subjects;
    }

    /**
     * 科目情報をデータベースに保存（追加または更新）します。
     * @param subject 保存するSubjectオブジェクト
     * @return 成功した場合はtrue、失敗した場合はfalse
     * @throws Exception データベースアクセスエラーが発生した場合
     */
    public boolean save(Subject subject) throws Exception {
        Connection connection = null;
        PreparedStatement ps = null;
        boolean isSuccess = false;

        try {
            connection = getConnection();
            // まず既存のレコードがあるか確認（科目コードと学校コードでユニーク）
            Subject existingSubject = get(subject.getCd(), subject.getSchool().getCd());

            if (existingSubject == null) {
                // レコードが存在しない場合、新規追加
                String sql = "INSERT INTO SUBJECT (CD, NAME, SCHOOL_CD) VALUES (?, ?, ?)";
                ps = connection.prepareStatement(sql);
                ps.setString(1, subject.getCd());
                ps.setString(2, subject.getName());
                ps.setString(3, subject.getSchool().getCd());
            } else {
                // レコードが存在する場合、更新
                String sql = "UPDATE SUBJECT SET NAME = ? WHERE CD = ? AND SCHOOL_CD = ?";
                ps = connection.prepareStatement(sql);
                ps.setString(1, subject.getName());
                ps.setString(2, subject.getCd());
                ps.setString(3, subject.getSchool().getCd());
            }

            int count = ps.executeUpdate();
            isSuccess = (count > 0);

        } catch (SQLException e) {
            e.printStackTrace();
            throw new Exception("科目の保存に失敗しました。", e);
        } finally {
            if (ps != null) {
                try { ps.close(); } catch (SQLException sqle) { sqle.printStackTrace(); }
            }
            if (connection != null) {
                try { connection.close(); } catch (SQLException sqle) { sqle.printStackTrace(); }
            }
        }
        return isSuccess;
    }

    /**
     * 科目情報をデータベースから削除します。
     * @param subject 削除するSubjectオブジェクト（科目コードと学校コードが必須）
     * @return 成功した場合はtrue、失敗した場合はfalse
     * @throws Exception データベースアクセスエラーが発生した場合
     */
    public boolean delete(Subject subject) throws Exception {
        Connection connection = null;
        PreparedStatement ps = null;
        boolean isSuccess = false;

        try {
            connection = getConnection();
            String sql = "DELETE FROM SUBJECT WHERE CD = ? AND SCHOOL_CD = ?";
            ps = connection.prepareStatement(sql);
            ps.setString(1, subject.getCd());
            ps.setString(2, subject.getSchool().getCd()); // SubjectオブジェクトにSchoolがセットされている前提

            int count = ps.executeUpdate();
            isSuccess = (count > 0);

        } catch (SQLException e) {
            e.printStackTrace();
            throw new Exception("科目の削除に失敗しました。", e);
        } finally {
            if (ps != null) {
                try { ps.close(); } catch (SQLException sqle) { sqle.printStackTrace(); }
            }
            if (connection != null) {
                try { connection.close(); } catch (SQLException sqle) { sqle.printStackTrace(); }
            }
        }
        return isSuccess;
    }
}