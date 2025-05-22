<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.List" %>
<%@ page import="bean.Student" %>
<%@ page import="bean.Teacher" %> <%-- ★追加: Teacher Beanをインポート★ --%>
<%
    // リクエストスコープから属性を取得
    List<Student> studentList = (List<Student>) request.getAttribute("studentList");
    List<Integer> allEntYears = (List<Integer>) request.getAttribute("allEntYears");
    List<String> allClassNums = (List<String>) request.getAttribute("allClassNums");

    // フィルタリング用に選択された値を保持
    String selectedEntYear = (String) request.getAttribute("entYear");
    String selectedClassNum = (String) request.getAttribute("classNumParam"); // StudentListActionの属性名と合わせる
    String selectedAttend = (String) request.getAttribute("attend");

    // ★追加: セッションからログインユーザー情報を取得★
    // LoginServletで "teacher" というキー名で保存されている前提
    Teacher loginTeacher = (Teacher) session.getAttribute("teacher");
    String teacherName = "ゲスト"; // デフォルト値または未ログイン時の表示名

    if (loginTeacher != null && loginTeacher.getName() != null && !loginTeacher.getName().isEmpty()) {
        teacherName = loginTeacher.getName(); // Teacherオブジェクトから名前を取得
    }
%>
<!DOCTYPE html>
<html lang="ja">
<head>
<meta charset="UTF-8">
<title>学生一覧</title>
<style>
    body { font-family: Arial, sans-serif; margin: 0; }
    header { background: linear-gradient(to right, #dfefff, #eef5ff); padding: 20px; border-bottom: 1px solid #ccc; display: flex; justify-content: space-between; align-items: center; }
    header h1 { margin: 0; font-size: 24px; }
    .header-info { display: flex; align-items: center; }
    .header-info span { margin-right: 15px; }
    .header-info a { text-decoration: none; color: #007bff; }
    .container { display: flex; }
    /* サイドバーがないJSPなので、コンテンツ幅を広げるために修正 */
    .content { flex-grow: 1; padding: 30px; margin-left: auto; margin-right: auto; max-width: 1200px; } /* センター寄せと最大幅の追加 */
    .form-header { background-color: #f0f0f0; padding: 10px; font-size: 18px; font-weight: bold; margin-bottom: 20px; border-left: 5px solid #333; display: flex; justify-content: space-between; align-items: center; }
    .form-header h2 { margin: 0; }
    .filter-form { background-color: #f9f9f9; padding: 15px; margin-bottom: 20px; border: 1px solid #ddd; }
    .filter-form label { margin-right: 10px; }
    .filter-form input[type=text], .filter-form select { padding: 8px; margin-right: 10px; border: 1px solid #ccc; border-radius: 4px; box-sizing: border-box; }
    .filter-form input[type=submit] { background-color: #808080; color: white; padding: 10px 15px; border: none; border-radius: 4px; cursor: pointer; font-size: 14px; }
    .filter-form input[type=submit]:hover { background-color: #696969; }
    .search-result-count { margin-bottom: 10px; font-size: 16px; font-weight: bold; }
    table { border-collapse: collapse; width: 100%; }
    th { padding: 8px; text-align: left; border-bottom: 2px solid #ddd; }
    td { padding: 8px 5px; text-align: left; border-bottom: 1px solid #eee; }
    .footer { text-align: center; font-size: 12px; color: #333; padding: 20px; border-top: 1px solid #ccc; background-color: #f0f0f0; }

    /* ボタンとリンクのスタイル */
    .edit-button {
        display: inline-block;
        padding: 5px 10px;
        font-size: 14px;
        text-align: center;
        text-decoration: none;
        border-radius: 5px;
        cursor: pointer;
        background-color: transparent;
        color: #007bff;
        border: none;
    }
    .edit-button:hover { text-decoration: underline; background-color: transparent; }
    .register-button { color: #007bff; text-decoration: none; }
    .register-button:hover { text-decoration: underline; }
</style>
</head>
<body>
<header>
    <h1>得点管理システム</h1>
    <div class="header-info">
        <span><%= teacherName %>様</span> <%-- ★ここを修正: teacherName変数を使用★ --%>
        <a href="<%= request.getContextPath() %>/login/logout" class="logout-link">ログアウト</a>
    </div>
</header>
<div class="container">
    <div class="content">
        <div class="form-header">
            <h2>学生一覧</h2>
            <a href="<%= request.getContextPath() %>/StudentCreateAction" class="register-button">新規登録</a>
        </div>

        <% String error = (String) request.getAttribute("error"); %>
        <% if (error != null && !error.isEmpty()) { %>
            <div style="color: red; margin-bottom: 15px; font-weight: bold;"><%= error %></div>
        <% } %>

        <div class="filter-form">
            <form method="get" action="<%= request.getContextPath() %>/StudentListAction">
                <label for="entYear">入学年度:</label>
                <select id="entYear" name="entYear">
                    <option value=""></option>
                    <% if (allEntYears != null) { %>
                    <% for (Integer year : allEntYears) { %>
                        <option value="<%= year %>" <%= (selectedEntYear != null && selectedEntYear.equals(String.valueOf(year))) ? "selected" : "" %>>
                            <%= year %>
                        </option>
                    <% } %>
                    <% } %>
                </select>
                <label for="classNum">クラス:</label>
                <select id="classNum" name="classNum">
                    <option value=""></option>
                    <% if (allClassNums != null) { %>
                    <% for (String classNum : allClassNums) { %>
                        <option value="<%= classNum %>" <%= (selectedClassNum != null && selectedClassNum.equals(classNum)) ? "selected" : "" %>>
                            <%= classNum %>組
                        </option>
                    <% } %>
                    <% } %>
                </select>
                <input type="checkbox" id="attend" name="attend" value="true" <%= ("true".equals(selectedAttend)) ? "checked" : "" %>>
                <label for="attend">在学中</label>
                <input type="submit" value="絞り込み">
                <%-- クリアリンクは削除しました --%>
            </form>
        </div>

        <div class="search-result-count">
            検索結果: <%= (studentList != null ? studentList.size() : 0) %>件
        </div>

        <table>
            <thead>
                <tr>
                    <th>学籍番号</th>
                    <th>氏名</th>
                    <th>入学年度</th>
                    <th>クラス</th>
                    <th>在学中</th>
                    <th>操作</th>
                </tr>
            </thead>
            <tbody>
                <% if (studentList != null && !studentList.isEmpty()) { %>
                <% for (Student student : studentList) { %>
                    <tr>
                        <td><%= student.getNo() %></td>
                        <td><%= student.getName() %></td>
                        <td><%= student.getEntYear() %></td>
                        <td><%= student.getClassNum() %></td>
                        <td><%= (student.isAttend() ? "〇" : "✕") %></td>
                        <td>
                            <a href="<%= request.getContextPath() %>/StudentUpdateAction?no=<%= student.getNo() %>" class="edit-button">変更</a>
                        </td>
                    </tr>
                <% } %>
                <% } else { %>
                    <tr><td colspan="6" class="text-center">該当する学生はいません。</td></tr>
                <% } %>
            </tbody>
        </table>
    </div>
</div>
<div class="footer">© 2025 TIC<br>大原学園</div>
</body>
</html>