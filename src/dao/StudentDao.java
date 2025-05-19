package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import bean.School;
import bean.Student;

public class StudentDao extends Dao {

    /**

     * getメソッド 学生番号を指定して学生インスタンスを1件取得する

     *

     * @param no:String

     * 学生番号

     * @return 学生クラスのインスタンス 存在しない場合はnull

     * @throws Exception

     */

    public Student get(String no) throws Exception {

        Student student = new Student();

        Connection connection = getConnection();

        PreparedStatement statement = null;

        try {

            statement = connection.prepareStatement("select * from student where no=?");

            statement.setString(1, no);

            ResultSet rSet = statement.executeQuery();

            SchoolDao schoolDao = new SchoolDao();

            if (rSet.next()) {

                student.setNo(rSet.getString("no"));

                student.setName(rSet.getString("name"));

                student.setEntYear(rSet.getInt("ent_year"));

                student.setClassNum(rSet.getString("class_num"));

                student.setAttend(rSet.getBoolean("is_attend"));

                student.setSchool(schoolDao.get(rSet.getString("school_cd")));

            } else {

                student = null;

            }

        } catch (Exception e) {

            throw e;

        } finally {

            closeStatement(statement);

            closeConnection(connection);

        }

        return student;

    }

    private String baseSql = "select * from student where school_cd=? ";

    private List<Student> postFilter(ResultSet rSet, School school) throws Exception {

        List<Student> list = new ArrayList<>();

        try {

            while (rSet.next()) {

                Student student = new Student();

                student.setNo(rSet.getString("no"));

                student.setName(rSet.getString("name"));

                student.setEntYear(rSet.getInt("ent_year"));

                student.setClassNum(rSet.getString("class_num"));

                student.setAttend(rSet.getBoolean("is_attend"));

                student.setSchool(school);

                list.add(student);

            }

        } catch (SQLException | NullPointerException e) {

            e.printStackTrace();

        }

        return list;

    }

    /**

     * filterメソッド 学校を指定して学生の一覧を取得する (学校コード "oom" と "tky" の両方)

     *

     * @param school:School (ここでは使用しませんが、メソッドのシグネチャを維持するため)

     * @return 学生のリスト:List<Student> 存在しない場合は0件のリスト

     * @throws Exception

     */

    public List<Student> filter(School school) throws Exception {

        List<Student> list = new ArrayList<>();

        Connection connection = getConnection();

        PreparedStatement statement = null;

        ResultSet rSet = null;

        String order = " order by no asc";

        try {

            statement = connection.prepareStatement("SELECT * FROM STUDENT WHERE SCHOOL_CD IN (?, ?) " + order);

            statement.setString(1, "oom");

            statement.setString(2, "tky");

            rSet = statement.executeQuery();

            SchoolDao schoolDao = new SchoolDao();

            while (rSet.next()) {

                Student student = new Student();

                student.setNo(rSet.getString("no"));

                student.setName(rSet.getString("name"));

                student.setEntYear(rSet.getInt("ent_year"));

                student.setClassNum(rSet.getString("class_num"));

                student.setAttend(rSet.getBoolean("is_attend"));

                student.setSchool(schoolDao.get(rSet.getString("school_cd"))); // 学校情報を取得

                list.add(student);

            }

        } catch (Exception e) {

            throw e;

        } finally {

            closeStatement(statement);

            closeResultSet(rSet);

            closeConnection(connection);

        }

        return list;

    }

    // 他の filter メソッドは変更なし



    public List<Student> filter(School school, int entYear, String classNum, boolean isAttend) throws Exception {

        List<Student> list = new ArrayList<>();

        Connection connection = getConnection();

        PreparedStatement statement = null;

        ResultSet rSet = null;

        String condition = "and ent_year=? and class_num=?";

        String order = " order by no asc";

        String conditionIsAttend = "";

        if (isAttend) {

            conditionIsAttend = "and is_attend=true";

        }

        try {

            statement = connection.prepareStatement(baseSql + condition + conditionIsAttend + order);

            statement.setString(1, school.getCd());

            statement.setInt(2, entYear);

            statement.setString(3, classNum);

            rSet = statement.executeQuery();

            list = postFilter(rSet, school);

        } catch (Exception e) {

            throw e;

        } finally {

            closeStatement(statement);

            closeResultSet(rSet);

            closeConnection(connection);

        }

        return list;

    }

    public List<Student> filter(School school, int entYear, boolean isAttend) throws Exception {

        List<Student> list = new ArrayList<>();

        Connection connection = getConnection();

        PreparedStatement statement = null;

        ResultSet rSet = null;

        String condition = "and ent_year=? ";

        String order = " order by no asc";

        String conditionIsAttend = "";

        if (isAttend) {

            conditionIsAttend = "and is_attend=true";

        }

        try {

            statement = connection.prepareStatement(baseSql + condition + conditionIsAttend + order);

            statement.setString(1, school.getCd());

            statement.setInt(2, entYear);

            rSet = statement.executeQuery();

            list = postFilter(rSet, school);

        } catch (Exception e) {

            throw e;

        } finally {

            closeStatement(statement);

            closeResultSet(rSet);

            closeConnection(connection);

        }

        return list;

    }

    public List<Student> filter(School school, boolean isAttend) throws Exception {

        List<Student> list = new ArrayList<>();

        Connection connection = getConnection();

        PreparedStatement statement = null;

        ResultSet rSet = null;

        String order = " order by no asc";

        String conditionIsAttend = "";

        if (isAttend) {

            conditionIsAttend = "and is_attend=true";

        }

        try {

            statement = connection.prepareStatement(baseSql + conditionIsAttend + order);

            statement.setString(1, school.getCd());

            rSet = statement.executeQuery();

            list = postFilter(rSet, school);

        } catch (Exception e) {

            throw e;

        } finally {

            closeStatement(statement);

            closeResultSet(rSet);

            closeConnection(connection);

        }

        return list;

    }

    public boolean save(Student student) throws Exception {

        Connection connection = getConnection();

        PreparedStatement statement = null;

        int count = 0;

        try {

            Student old = get(student.getNo());

            if (old == null) {

                statement = connection.prepareStatement(

                        "insert into student(no, name, ent_year, class_num, is_attend, school_cd) values(?, ?, ?, ?, ?, ?)");

                statement.setString(1, student.getNo());

                statement.setString(2, student.getName());

                statement.setInt(3, student.getEntYear());

                statement.setString(4, student.getClassNum());

                statement.setBoolean(5, student.isAttend());

                statement.setString(6, student.getSchool().getCd());

            } else {

                statement = connection

                        .prepareStatement("update student set name=?, ent_year=?, class_num=?, is_attend=?, school_cd=? where no=?");

                statement.setString(1, student.getName());

                statement.setInt(2, student.getEntYear());

                statement.setString(3, student.getClassNum());

                statement.setBoolean(4, student.isAttend());

                statement.setString(5, student.getSchool().getCd());

                statement.setString(6, student.getNo());

            }

            count = statement.executeUpdate();

        } catch (Exception e) {

            throw e;

        } finally {

            closeStatement(statement);

            closeConnection(connection);

        }

        return count > 0;

    }

    public boolean delete(Student student) throws Exception {

        Connection connection = getConnection();

        PreparedStatement statement = null;

        int count = 0;

        try {

            statement = connection.prepareStatement("delete from student where no=?");

            statement.setString(1, student.getNo());

            count = statement.executeUpdate();

        } catch (Exception e) {

            throw e;

        } finally {

            closeStatement(statement);

            closeConnection(connection);

        }

        return count > 0;

    }

    private void closeResultSet(ResultSet rSet) {

        if (rSet != null) {

            try {

                rSet.close();

            } catch (SQLException sqle) {

                sqle.printStackTrace();

            }

        }

    }

    private void closeStatement(PreparedStatement statement) {

        if (statement != null) {

            try {

                statement.close();

            } catch (SQLException sqle) {

                sqle.printStackTrace();

            }

        }

    }

    private void closeConnection(Connection connection) {

        if (connection != null) {

            try {

                connection.close();

            } catch (SQLException sqle) {

                sqle.printStackTrace();

            }

        }

    }

}

