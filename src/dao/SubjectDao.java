package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.naming.NamingException;

import bean.School;
import bean.Subject;

public class SubjectDao extends Dao {
    public SubjectDao() throws NamingException {
        super();
    }

    /**
     * getメソッド 科目コードと学校を指定して科目インスタンスを1件取得する
     *
     * @param cd:String
     * 科目コード
     * @param school:School
     * 学校
     * @return 科目クラスのインスタンス 存在しない場合はnull
     * @throws Exception
     */
    public Subject get(String cd, School school) throws Exception {
        Subject subject = new Subject();
        Connection connection = null;
        PreparedStatement statement = null;

        try {
            connection = getConnection();
            statement = connection.prepareStatement("select * from subject where cd=? and school_cd=?");
            statement.setString(1, cd);
            statement.setString(2, school.getCd());
            ResultSet rSet = statement.executeQuery();

            if (rSet.next()) {
                subject.setCd(rSet.getString("cd"));
                subject.setName(rSet.getString("name"));
                subject.setSchool(school);
            } else {
                subject = null;
            }
        } catch (Exception e) {
            throw new SQLException("科目の取得に失敗しました", e);
        } finally {
            close(statement, connection);
        }

        return subject;
    }

    /**
     * findByCdメソッド 科目コードを指定して科目インスタンスを1件取得する
     *
     * @param cd:String
     * 科目コード
     * @return 科目クラスのインスタンス 存在しない場合はnull
     * @throws Exception
     */
    public Subject findByCd(String cd) throws Exception {
        Subject subject = new Subject();
        Connection connection = null;
        PreparedStatement statement = null;

        try {
            connection = getConnection();
            statement = connection.prepareStatement("select * from subject where cd=?");
            statement.setString(1, cd);
            ResultSet rSet = statement.executeQuery();

            if (rSet.next()) {
                School school = new School();
                school.setCd(rSet.getString("school_cd"));
                subject.setSchool(school);
                subject.setCd(rSet.getString("cd"));
                subject.setName(rSet.getString("name"));
            } else {
                subject = null;
            }
        } catch (Exception e) {
            throw new SQLException("科目の検索に失敗しました", e);
        } finally {
            close(statement, connection);
        }

        return subject;
    }

    /**
     * filterメソッド 学校を指定して科目の一覧を取得する
     *
     * @param school:School
     * 学校 (ここでは使用しません)
     * @return 科目のリスト:List<Subject> 存在しない場合は0件のリスト
     * @throws Exception
     */
    public List<Subject> filter(School school) throws Exception {
        List<Subject> list = new ArrayList<>();
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet rSet = null;

        try {
            connection = getConnection();
            // WHERE句を削除してすべての科目をSELECT
            statement = connection.prepareStatement("select * from subject order by cd");
            rSet = statement.executeQuery();

            while (rSet.next()) {
                Subject subject = new Subject();
                School s = new School();
                s.setCd(rSet.getString("school_cd"));
                subject.setSchool(s);
                subject.setCd(rSet.getString("cd"));
                subject.setName(rSet.getString("name"));
                list.add(subject);
            }
        } catch (Exception e) {
            throw new SQLException("科目一覧の取得に失敗しました", e);
        } finally {
            close(statement, connection);
            if (rSet != null) {
                try {
                    rSet.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }

        return list;
    }

    /**
     * findAllメソッド 全ての科目をリストで取得する
     *
     * @return 科目のリスト:List<Subject> 存在しない場合は0件のリスト
     * @throws Exception
     */
    public List<Subject> findAll() throws SQLException {
        List<Subject> subjectList = new ArrayList<>();
        Connection connection = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            connection = getConnection();
            String sql = "SELECT SCHOOL_CD, CD, NAME FROM SUBJECT";
            pstmt = connection.prepareStatement(sql);
            rs = pstmt.executeQuery();

            while (rs.next()) {
                Subject subject = new Subject();
                School s = new School();
                s.setCd(rs.getString("SCHOOL_CD"));
                subject.setSchool(s);
                subject.setCd(rs.getString("CD"));
                subject.setName(rs.getString("NAME"));
                subjectList.add(subject);
            }
        } catch (Exception e) {
            throw new SQLException("科目一覧の取得に失敗しました", e);
        } finally {
            close(pstmt, connection);
            if (rs != null) {
                try {
                    rs.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
        return subjectList;
    }

    /**
     * saveメソッド 科目インスタンスをデータベースに保存する データが存在する場合は更新、存在しない場合は登録
     *
     * @param subject:Subject
     * 学生
     * @return 成功:true, 失敗:false
     * @throws Exception
     */
    public boolean save(Subject subject) throws Exception {
        Connection connection = null;
        PreparedStatement statement = null;
        int count = 0;

        try {
            connection = getConnection();
            Subject old = get(subject.getCd(), subject.getSchool());
            if (old == null) {
                statement = connection.prepareStatement("insert into subject(name, cd, school_cd) values(?, ?, ?)");
                statement.setString(1, subject.getName());
                statement.setString(2, subject.getCd());
                statement.setString(3, subject.getSchool().getCd());
            } else {
                statement = connection.prepareStatement("update subject set name=? where cd=?");
                statement.setString(1, subject.getName());
                statement.setString(2, subject.getCd());
            }

            count = statement.executeUpdate();

        } catch (Exception e) {
            throw new SQLException("科目の保存に失敗しました", e);
        } finally {
            close(statement, connection);
        }

        return count > 0;
    }

    /**
     * deleteメソッド 科目をデータベースから削除する
     *
     * @param cd:String
     * 科目コード
     * @return 成功:true, 失敗:false
     * @throws Exception
     */
    public int delete(String cd) throws Exception {
        Connection connection = null;
        PreparedStatement statement = null;
        int count = 0;

        try {
            connection = getConnection();
            statement = connection.prepareStatement("delete from subject where cd=?");
            statement.setString(1, cd);
            count = statement.executeUpdate();
        } catch (Exception e) {
            throw new SQLException("科目の削除に失敗しました", e);
        } finally {
            close(statement, connection);
        }

        return count;
    }

    /**
     * closeメソッド PreparedStatementとConnectionをクローズする
     *
     * @param statement:PreparedStatement
     * @param connection:Connection
     */
    private void close(PreparedStatement statement, Connection connection) {
        if (statement != null) {
            try {
                statement.close();
            } catch (SQLException sqle) {
                sqle.printStackTrace();
            }
        }
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException sqle) {
                sqle.printStackTrace();
            }
        }
    }
}