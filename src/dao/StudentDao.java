// StudentDao.java

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

import bean.Student;

public class StudentDao {

    private DataSource ds;

    public StudentDao() throws NamingException {
        InitialContext ic = new InitialContext();
        ds = (DataSource) ic.lookup("java:/comp/env/jdbc/yajima");
    }

    public List<Student> filter(Integer entYear, String classNum, Boolean attend, Object school) throws SQLException {
        List<Student> studentList = new ArrayList<>();
        String sql = "SELECT s.no, s.name, s.ent_year, s.class_num, s.is_attend, sch.cd AS school_cd, sch.name AS school_name " +
                     "FROM student s JOIN school sch ON s.school_cd = sch.cd " +
                     "WHERE 1=1";
        if (entYear != null) {
            sql += " AND s.ent_year = ?";
        }
        if (classNum != null && !classNum.isEmpty()) {
            sql += " AND s.class_num = ?";
        }
        if (attend != null) {
            sql += " AND s.is_attend = ?"; // ★ カラム名を修正 ★
        }
        // school パラメータは現在使用されていません

        try (Connection con = ds.getConnection();
             PreparedStatement pstmt = con.prepareStatement(sql)) {
            int paramIndex = 1;
            if (entYear != null) {
                pstmt.setInt(paramIndex++, entYear);
            }
            if (classNum != null && !classNum.isEmpty()) {
                pstmt.setString(paramIndex++, classNum);
            }
            if (attend != null) {
                pstmt.setBoolean(paramIndex++, attend); // ★ カラム名を修正 ★
            }

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Student s = new Student();
                    s.setNo(rs.getString("no"));
                    s.setName(rs.getString("name"));
                    s.setEntYear(rs.getInt("ent_year"));
                    s.setClassNum(rs.getString("class_num"));
                    s.setAttend(rs.getBoolean("is_attend")); // ★ カラム名を修正 ★
                    // School オブジェクトの生成と設定
                    bean.School schoolBean = new bean.School();
                    schoolBean.setCd(rs.getString("school_cd"));
                    schoolBean.setName(rs.getString("school_name"));
                    s.setSchool(schoolBean);
                    studentList.add(s);
                }
            }
        }
        return studentList;
    }

    // ★ 新しいメソッド: 登録されている入学年度の一覧を取得 ★
    public List<Integer> getAllEntYears() throws SQLException {
        List<Integer> entYears = new ArrayList<>();
        String sql = "SELECT DISTINCT ent_year FROM student ORDER BY ent_year";

        try (Connection con = ds.getConnection();
             PreparedStatement pstmt = con.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                entYears.add(rs.getInt("ent_year"));
            }
        }
        return entYears;
    }

    // ★ 新しいメソッド: 登録されているクラスの一覧を取得 ★
    public List<String> getAllClassNums() throws SQLException {
        List<String> classNums = new ArrayList<>();
        String sql = "SELECT DISTINCT class_num FROM student ORDER BY class_num";

        try (Connection con = ds.getConnection();
             PreparedStatement pstmt = con.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                classNums.add(rs.getString("class_num"));
            }
        }
        return classNums;
    }
}