package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

import bean.School;
import bean.Subject;

public class SubjectDao {

    private DataSource ds;

    public SubjectDao() throws NamingException {
        Context initCtx = new InitialContext();
        Context envCtx = (Context) initCtx.lookup("java:comp/env");
        ds = (DataSource) envCtx.lookup("jdbc/MySQLDB");
    }

    public List<Subject> filter(School school) throws SQLException {
        List<Subject> subjectList = new ArrayList<>();
        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            con = ds.getConnection();
            // ★ 学校コードで絞り込むSQLクエリ ★
            String sql = "SELECT cd, name FROM subject WHERE school_cd = ?";
            pstmt = con.prepareStatement(sql);
            pstmt.setString(1, school.getCd());
            rs = pstmt.executeQuery();

            while (rs.next()) {
                Subject subject = new Subject();
                subject.setCd(rs.getString("cd"));
                subject.setName(rs.getString("name"));
                subjectList.add(subject);
            }

        } finally {
            closeResources(con, pstmt, rs);
        }
        return subjectList;
    }

    public Subject findByCd(String cd) throws SQLException {
        Subject subject = null;
        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            con = ds.getConnection();
            String sql = "SELECT s.cd, s.name, sc.cd AS school_cd, sc.name AS school_name FROM subject s JOIN school sc ON s.school_cd = sc.cd WHERE s.cd = ?";
            pstmt = con.prepareStatement(sql);
            pstmt.setString(1, cd);
            rs = pstmt.executeQuery();

            if (rs.next()) {
                subject = new Subject();
                subject.setCd(rs.getString("cd"));
                subject.setName(rs.getString("name"));
                School school = new School();
                school.setCd(rs.getString("school_cd"));
                school.setName(rs.getString("school_name"));
                subject.setSchool(school);
            }

        } finally {
            closeResources(con, pstmt, rs);
        }
        return subject;
    }

    private void closeResources(Connection con, PreparedStatement pstmt, ResultSet rs) {
        try {
            if (rs != null) {
                rs.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        try {
            if (pstmt != null) {
                pstmt.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        try {
            if (con != null) {
                con.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // 他のSubjectDaoのメソッド（登録、更新など）も必要に応じて学校コードを考慮するように修正してください。
    // 例えば、登録処理ではどの学校に科目を登録するのかといった情報が必要になります。
}