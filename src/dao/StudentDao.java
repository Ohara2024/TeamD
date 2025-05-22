package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Year; // Yearクラスをインポート
import java.util.ArrayList;
import java.util.Collections; // Collections.sort()のためにインポート
import java.util.List;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

import bean.School;
import bean.Student;

public class StudentDao {

    private DataSource ds;

    public StudentDao() { // NamingExceptionをスローしないように変更、内部でtry-catch
        try {
            InitialContext ic = new InitialContext();
            ds = (DataSource) ic.lookup("java:comp/env/jdbc/yajima"); // あなたのJNDI名に合わせる
        } catch (NamingException e) {
            System.err.println("DataSourceのルックアップに失敗しました: " + e.getMessage());
            e.printStackTrace();
            // RuntimeExceptionをスローして、アプリケーション起動時のエラーとして処理させる
            throw new RuntimeException("データベース接続設定 (DataSource) の取得に失敗しました。JNDI名 'java:comp/env/jdbc/yajima' が正しく設定されているか確認してください。", e);
        }
    }

    // 接続取得のヘルパーメソッドを追加 (冗長なtry-catchをまとめるため)
    private Connection getConnection() throws SQLException {
        return ds.getConnection();
    }

    // 例外処理のヘルパーメソッドを追加
    private void handleSqlException(String message, SQLException e) {
        System.err.println(message + ": " + e.getMessage());
        e.printStackTrace();
        throw new RuntimeException(message, e); // RuntimeExceptionとして再スロー
    }

    /**
     * 学籍番号と学校コードを指定して学生情報を取得します。
     * @param no 学籍番号
     * @param schoolCd 学校コード
     * @return 該当する学生オブジェクト、見つからない場合はnull
     */
    public Student get(String no, String schoolCd) { // throws Exception を削除し、内部でRuntimeExceptionとして処理
        Student student = null;
        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            con = getConnection(); // ヘルパーメソッドを使用

            // SQLクエリ: 学生番号と学校コードで絞り込む
            String sql = "SELECT s.no, s.name, s.ent_year, s.class_num, s.is_attend, sch.cd AS school_cd, sch.name AS school_name " +
                         "FROM student s JOIN school sch ON s.school_cd = sch.cd " +
                         "WHERE s.no = ? AND s.school_cd = ?";

            pstmt = con.prepareStatement(sql);
            pstmt.setString(1, no);
            pstmt.setString(2, schoolCd);

            rs = pstmt.executeQuery();

            if (rs.next()) { // 1件だけ取得
                student = new Student();
                student.setNo(rs.getString("no"));
                student.setName(rs.getString("name"));
                student.setEntYear(rs.getInt("ent_year"));
                student.setClassNum(rs.getString("class_num"));
                student.setAttend(rs.getBoolean("is_attend"));

                // 学校オブジェクトを設定
                School school = new School();
                school.setCd(rs.getString("school_cd"));
                school.setName(rs.getString("school_name"));
                student.setSchool(school);
            }
        } catch (SQLException e) {
            // 例外処理ヘルパーメソッドを使用
            handleSqlException("特定の学生情報の取得に失敗しました (学籍番号: " + no + ", 学校コード: " + schoolCd + ")", e);
        } finally {
            // リソースのクローズ
            if (rs != null) { try { rs.close(); } catch (SQLException ignore) {} }
            if (pstmt != null) { try { pstmt.close(); } catch (SQLException ignore) {} }
            if (con != null) { try { con.close(); } catch (SQLException ignore) {} }
        }
        return student;
    }


    /**
     * 学生情報をフィルタリングして取得する。
     * @param entYear 入学年度 (nullの場合は条件に含めない)
     * @param classNum クラス番号 (nullまたは空の場合は条件に含めない)
     * @param attend 在学中かどうか (nullの場合は条件に含めない)
     * @param schoolCd 学校コード (必須: これがないとログインしている学校のデータが取得できない)
     * @return フィルタリングされた学生のリスト
     */
    public List<Student> filter(Integer entYear, String classNum, Boolean attend, String schoolCd) {
        List<Student> studentList = new ArrayList<>();
        // school_cdは必須条件として最初からWHERE句に含める
        String sql = "SELECT s.no, s.name, s.ent_year, s.class_num, s.is_attend, sch.cd AS school_cd, sch.name AS school_name " +
                     "FROM student s JOIN school sch ON s.school_cd = sch.cd " +
                     "WHERE s.school_cd = ?";

        if (entYear != null) {
            sql += " AND s.ent_year = ?";
        }
        if (classNum != null && !classNum.isEmpty()) {
            sql += " AND s.class_num = ?";
        }
        if (attend != null) { // is_attendはBoolean型なのでnullチェック
            sql += " AND s.is_attend = ?";
        }

        try (Connection con = getConnection();
             PreparedStatement pstmt = con.prepareStatement(sql)) {

            int paramIndex = 1;
            pstmt.setString(paramIndex++, schoolCd);

            if (entYear != null) {
                pstmt.setInt(paramIndex++, entYear);
            }
            if (classNum != null && !classNum.isEmpty()) {
                pstmt.setString(paramIndex++, classNum);
            }
            if (attend != null) {
                pstmt.setBoolean(paramIndex++, attend);
            }

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Student s = new Student();
                    s.setNo(rs.getString("no"));
                    s.setName(rs.getString("name"));
                    s.setEntYear(rs.getInt("ent_year"));
                    s.setClassNum(rs.getString("class_num"));
                    s.setAttend(rs.getBoolean("is_attend"));
                    // School オブジェクトの生成と設定
                    School schoolBean = new School();
                    schoolBean.setCd(rs.getString("school_cd"));
                    schoolBean.setName(rs.getString("school_name"));
                    s.setSchool(schoolBean);
                    studentList.add(s);
                }
            }
        } catch (SQLException e) {
            handleSqlException("学生情報のフィルタリングに失敗しました (学校コード: " + schoolCd + ")", e);
        }
        return studentList;
    }

    /**
     * 学生をデータベースに保存する。学籍番号が既存であれば更新、なければ新規登録。
     * @param student 保存するStudentオブジェクト
     * @return 成功した場合true、失敗した場合false
     */
    public boolean save(Student student) {
        Connection con = null;
        PreparedStatement pstmt = null;
        try {
            con = getConnection();
            con.setAutoCommit(false); // トランザクション開始

            // 同じ学籍番号かつ同じ学校コードの学生が存在するかチェック
            Student existingStudent = get(student.getNo(), student.getSchool().getCd()); // 新しく追加したgetメソッドを使用

            if (existingStudent != null) { // 同じ学籍番号を持つ学生がその学校に存在する場合
                // 更新処理
                String updateSql = "UPDATE student SET name = ?, ent_year = ?, class_num = ?, is_attend = ? WHERE no = ? AND school_cd = ?";
                pstmt = con.prepareStatement(updateSql);
                pstmt.setString(1, student.getName());
                pstmt.setInt(2, student.getEntYear());
                pstmt.setString(3, student.getClassNum());
                pstmt.setBoolean(4, student.isAttend());
                pstmt.setString(5, student.getNo());
                pstmt.setString(6, student.getSchool().getCd());
            } else {
                // 新規登録処理
                String insertSql = "INSERT INTO student (no, name, ent_year, class_num, is_attend, school_cd) VALUES (?, ?, ?, ?, ?, ?)";
                pstmt = con.prepareStatement(insertSql);
                pstmt.setString(1, student.getNo());
                pstmt.setString(2, student.getName());
                pstmt.setInt(3, student.getEntYear());
                pstmt.setString(4, student.getClassNum());
                pstmt.setBoolean(5, student.isAttend());
                pstmt.setString(6, student.getSchool().getCd());
            }
            int count = pstmt.executeUpdate();
            con.commit(); // コミット
            return count > 0; // 1件以上更新/登録されれば成功
        } catch (SQLException e) {
            try {
                if (con != null) {
                    con.rollback(); // ロールバック
                }
            } catch (SQLException rollbackEx) {
                System.err.println("ロールバック中にエラーが発生しました: " + rollbackEx.getMessage());
            }
            handleSqlException("学生情報の保存に失敗しました。", e);
            return false; // 例外が発生したら失敗
        } finally {
            if (pstmt != null) {
                try { pstmt.close(); } catch (SQLException ignore) {}
            }
            if (con != null) {
                try { con.close(); } catch (SQLException ignore) {}
            }
        }
    }


    /**
     * 指定された学校の入学年度のユニークなリストを取得する。
     * @param schoolCd 学校コード
     * @return 入学年度のリスト
     */
    public List<Integer> getAllEntYears(String schoolCd) {
        List<Integer> entYears = new ArrayList<>();
        String sql = "SELECT DISTINCT ent_year FROM student WHERE school_cd = ? ORDER BY ent_year";
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, schoolCd);
            try (ResultSet rSet = statement.executeQuery()) {
                while (rSet.next()) {
                    entYears.add(rSet.getInt("ent_year"));
                }
            }
        } catch (SQLException e) {
            handleSqlException("入学年度の取得に失敗しました (学校コード: " + schoolCd + ")", e);
        }
        // もしDBにデータがない場合や、選択肢を動的に生成したい場合
        if (entYears.isEmpty()) {
            int currentYear = Year.now().getValue();
            // 現在の年から過去10年、未来5年までの範囲を生成
            for (int i = currentYear - 10; i <= currentYear + 5; i++) {
                entYears.add(i);
            }
            Collections.sort(entYears); // ソート
        }
        return entYears;
    }


    /**
     * 指定された学校のクラス番号のユニークなリストを取得する。
     * @param schoolCd 学校コード
     * @return クラス番号のリスト
     */
    public List<String> getAllClassNums(String schoolCd) {
        List<String> classNums = new ArrayList<>();
        String sql = "SELECT DISTINCT class_num FROM student WHERE school_cd = ? ORDER BY class_num";
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, schoolCd);
            try (ResultSet rSet = statement.executeQuery()) {
                while (rSet.next()) {
                    classNums.add(rSet.getString("class_num"));
                }
            }
        } catch (SQLException e) {
            handleSqlException("クラス番号の取得に失敗しました (学校コード: " + schoolCd + ")", e);
        }
        // もしDBにデータがない場合のフォールバック（例: ダミーデータ）
        if (classNums.isEmpty()) {
             classNums = java.util.Arrays.asList("101", "102", "201", "202", "301", "302");
             Collections.sort(classNums); // ソート
        }
        return classNums;
    }

    // getAllSchoolsは今回は直接関係ないですが、参考として残します
    public List<School> getAllSchools() { // throws SQLException を削除
        List<School> schools = new ArrayList<>();
        String sql = "SELECT cd, name FROM school ORDER BY cd";

        try (Connection con = getConnection();
             PreparedStatement pstmt = con.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                School school = new School();
                school.setCd(rs.getString("cd"));
                school.setName(rs.getString("name"));
                schools.add(school);
            }
        } catch (SQLException e) {
            handleSqlException("学校リストの取得に失敗しました。", e);
        }
        return schools;
    }
}