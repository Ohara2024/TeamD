<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="java.sql.Connection" %>
<%@ page import="java.sql.PreparedStatement" %>
<%@ page import="java.sql.ResultSet" %>
<%@ page import="java.sql.SQLException" %>
<%@ page import="javax.naming.InitialContext" %>
<%@ page import="javax.sql.DataSource" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="java.util.List" %>
<%@ page import="java.time.LocalDate" %>
<%@ page import="java.time.format.DateTimeFormatter" %>

<%
    Connection con = null;
    PreparedStatement st = null;
    ResultSet rs = null;
    List<String> studentNos = new ArrayList<>();
    List<String> subjectCds = new ArrayList<>();
    List<String> schoolCds = new ArrayList<>();
    List<String> testTypes = new ArrayList<>();
    testTypes.add("中間テスト");
    testTypes.add("期末テスト");
    testTypes.add("小テスト");
    testTypes.add("その他");
    List<String> schoolTypeOptions = new ArrayList<>();
    schoolTypeOptions.add("小学校");
    schoolTypeOptions.add("中学校");
    schoolTypeOptions.add("高校");
    schoolTypeOptions.add("その他");

    try {
        InitialContext ic = new InitialContext();
        DataSource ds = (DataSource) ic.lookup("java:/comp/env/jdbc/TeamD");
        con = ds.getConnection();

        // 学生番号の取得
        String sqlStudentNos = "SELECT DISTINCT NO, NAME FROM STUDENT ORDER BY NO";
        st = con.prepareStatement(sqlStudentNos);
        rs = st.executeQuery();
        while (rs.next()) {
            studentNos.add(rs.getString("NO") + " - " + rs.getString("NAME"));
        }
        if (st != null) { try { st.close(); } catch (SQLException e) {} }
        if (rs != null) { try { rs.close(); } catch (SQLException e) {} }
        st = null;
        rs = null;

        // 科目コードの取得
        String sqlSubjectCds = "SELECT DISTINCT CD, NAME FROM SUBJECT ORDER BY CD";
        st = con.prepareStatement(sqlSubjectCds);
        rs = st.executeQuery();
        while (rs.next()) {
            subjectCds.add(rs.getString("CD") + " - " + rs.getString("NAME"));
        }
        if (st != null) { try { st.close(); } catch (SQLException e) {} }
        if (rs != null) { try { rs.close(); } catch (SQLException e) {} }
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
<head>
    <meta charset="UTF-8">
    <title>成績登録</title>
    <style>
        body { font-family: Arial, sans-serif; margin: 0; padding: 0; background-color: #f4f4f4; color: #333; }
        header { background-color: #ddd; color: #333; padding: 20px; text-align: left; display: flex; justify-content: space-between; align-items: center; }
        header h1 { margin: 0; color: #333; }
        .container { max-width: 600px; margin: 20px auto; background-color: white; box-shadow: 0 0 10px rgba(0, 0, 0, 0.1); border-radius: 5px; padding: 20px; }
        h1 { text-align: center; color: #333; margin-bottom: 20px; }
        form label { display: block; margin-bottom: 5px; font-weight: bold; }
        form select, form input[type="number"], form input[type="date"], form input[type="text"] { width: 100%; padding: 8px; margin-bottom: 15px; border: 1px solid #ccc; border-radius: 4px; box-sizing: border-box; }
        form button { background-color: #007bff; color: white; padding: 10px 15px; border: none; border-radius: 4px; cursor: pointer; font-size: 16px; }
        form button:hover { background-color: #0056b3; }
        .error { color: red; margin-top: 10px; }
    </style>
</head>
<body>
    <header>
        <h1>得点管理システム - 成績登録</h1>
    </header>
    <div class="container">
        <h1>成績登録</h1>
        <% if (request.getAttribute("errorMessage") != null) { %>
            <p class="error"><%= request.getAttribute("errorMessage") %></p>
        <% } %>
        <form action="<%= request.getContextPath() %>/ScoreRegisterServlet" method="post">
            <div>
                <label for="studentNo">学生番号 - 氏名:</label>
                <select id="studentNo" name="studentNo">
                    <option value="">-- 学生を選択 --</option>
                    <% for (String student : studentNos) {
                        String[] parts = student.split(" - ");
                        String no = parts[0];
                        String name = parts.length > 1 ? parts[1] : "";
                    %>
                        <option value="<%= no %>"><%= student %></option>
                    <% } %>
                </select>
            </div>
            <div>
                <label for="subjectCd">科目コード - 科目名:</label>
                <select id="subjectCd" name="subjectCd">
                    <option value="">-- 科目を選択 --</option>
                    <% for (String subject : subjectCds) { %>
                        <option value="<%= subject.split(" - ")[0] %>"><%= subject %></option>
                    <% } %>
                </select>
            </div>
            <div>
                <label for="schoolCd">学校コード:</label>
                <select id="schoolCd" name="schoolCd">
                    <option value="">-- 学校を選択 --</option>
                    <% for (String cd : schoolCds) { %>
                        <option value="<%= cd %>"><%= cd %></option>
                    <% } %>
                </select>
            </div>

            <div>
                <label for="point">点数:</label>
                <input type="number" id="point" name="point" min="0" max="100">
            </div>
            <button type="submit">登録</button>
        </form>
        <div style="margin-top: 20px;">
            <a href="<%= request.getContextPath() %>/seiseki/score_list.jsp">成績一覧に戻る</a>
        </div>
    </div>
</body>
</html>