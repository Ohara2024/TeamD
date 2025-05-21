<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="java.sql.*" %>
<%@ page import="javax.naming.InitialContext" %>
<%@ page import="javax.sql.DataSource" %>
<%@ page import="java.util.*" %>
<%@ page import="java.time.LocalDate" %>
<%@ page import="java.time.format.DateTimeFormatter" %>

<%
    Connection con = null;
    PreparedStatement st = null;
    ResultSet rs = null;

    List<String> studentNos = new ArrayList<>();
    List<String> subjectCds = new ArrayList<>();
    List<String> schoolCds = new ArrayList<>();
    List<String> testTypes = Arrays.asList("中間テスト", "期末テスト", "小テスト", "その他");
    List<String> schoolTypeOptions = Arrays.asList("小学校", "中学校", "高校", "その他");

    try {
        InitialContext ic = new InitialContext();
        DataSource ds = (DataSource) ic.lookup("java:/comp/env/jdbc/tokuten");
        con = ds.getConnection();

        // 学生番号の取得
        String sqlStudentNos = "SELECT DISTINCT NO, NAME FROM STUDENT ORDER BY NO";
        st = con.prepareStatement(sqlStudentNos);
        rs = st.executeQuery();
        while (rs.next()) {
            studentNos.add(rs.getString("NO") + " - " + rs.getString("NAME"));
        }
        if (st != null) try { st.close(); } catch (SQLException e) {}
        if (rs != null) try { rs.close(); } catch (SQLException e) {}
        st = null;
        rs = null;

        // 科目コードの取得
        String sqlSubjectCds = "SELECT DISTINCT CD, NAME FROM SUBJECT ORDER BY CD";
        st = con.prepareStatement(sqlSubjectCds);
        rs = st.executeQuery();
        while (rs.next()) {
            subjectCds.add(rs.getString("CD") + " - " + rs.getString("NAME"));
        }
        if (st != null) try { st.close(); } catch (SQLException e) {}
        if (rs != null) try { rs.close(); } catch (SQLException e) {}
        st = null;
        rs = null;

        // 学校コードの取得
        String sqlSchoolCds = "SELECT DISTINCT SCHOOL_CD FROM SUBJECT ORDER BY SCHOOL_CD";
        st = con.prepareStatement(sqlSchoolCds);
        rs = st.executeQuery();
        while (rs.next()) {
            schoolCds.add(rs.getString("SCHOOL_CD"));
        }

    } catch (Exception e) {
        e.printStackTrace();
    } finally {
        if (rs != null) try { rs.close(); } catch (SQLException e) {}
        if (st != null) try { st.close(); } catch (SQLException e) {}
        if (con != null) try { con.close(); } catch (SQLException e) {}
    }

    LocalDate today = LocalDate.now();
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    String formattedDate = today.format(formatter);
%>

<!DOCTYPE html>
<html lang="ja">
