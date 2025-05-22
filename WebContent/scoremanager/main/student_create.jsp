<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.List" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="java.time.Year" %>
<%@ page import="bean.Teacher" %> <%-- ★追加: Teacher Beanをインポート★ --%>
<%
    // サーブレットから渡される入学年度のリスト (StudentCreateActionでセットされることを想定)
    List<Integer> entYears = (List<Integer>) request.getAttribute("entYears");
    if (entYears == null) {
        entYears = new ArrayList<>();
        int currentYear = Year.now().getValue();
        for (int i = currentYear - 10; i <= currentYear + 5; i++) {
            entYears.add(i);
        }
    }

    // サーブレットから渡されるクラスのリスト (StudentCreateActionでセットされることを想定)
    List<String> classNums = (List<String>) request.getAttribute("classNums");
    if (classNums == null) {
        // ダミーデータまたはエラー時のデフォルト
        classNums = java.util.Arrays.asList("101", "102", "201", "202", "301", "302");
    }

    // エラーメッセージがあれば取得
    String errorMessage = (String) request.getAttribute("error");

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
    <title>学生登録</title>
    <style>
        /* 共通のCSSスタイル */
        body { font-family: Arial, sans-serif; margin: 0; display: flex; flex-direction: column; min-height: 100vh; }
        header { background: linear-gradient(to right, #dfefff, #eef5ff); padding: 20px; border-bottom: 1px solid #ccc; display: flex; justify-content: space-between; align-items: center; }
        header h1 { margin: 0; font-size: 24px; }
        .header-info { display: flex; align-items: center; }
        .header-info span { margin-right: 15px; }
        .header-info a { text-decoration: none; color: #007bff; }

        .container { display: flex; flex-grow: 1; }
        .sidebar { width: 200px; background-color: #f0f0f0; padding: 20px; border-right: 1px solid #ccc; }
        .sidebar h2 { font-size: 18px; margin-top: 0; margin-bottom: 15px; color: #333; }
        .sidebar ul { list-style: none; padding: 0; margin: 0; }
        .sidebar li { margin-bottom: 5px; }
        .sidebar li a { display: block; padding: 8px 10px; text-decoration: none; color: #333; border-radius: 4px; transition: background-color 0.2s ease; }
        .sidebar li a:hover { background-color: #e0e0e0; }

        .content { flex-grow: 1; padding: 30px; background-color: #fff; }
        .form-header { background-color: #f0f0f0; padding: 10px; font-size: 18px; font-weight: bold; margin-bottom: 20px; border-left: 5px solid #333; color: #333; }

        /* フォーム固有のスタイル */
        .form-area { background-color: #f9f9f9; padding: 20px; border: 1px solid #ddd; margin-bottom: 20px; border-radius: 5px;}
        .form-group { margin-bottom: 15px; }
        .form-group label { display: block; margin-bottom: 5px; font-weight: bold; color: #555; }
        .form-group input[type="text"],
        .form-group input[type="number"],
        .form-group select {
            width: calc(100% - 22px);
            padding: 10px;
            border: 1px solid #ccc;
            border-radius: 4px;
            box-sizing: border-box;
            font-size: 16px;
        }
        .form-group input[type="checkbox"] {
            margin-right: 5px;
            transform: scale(1.2);
            vertical-align: middle;
        }
        .form-group button {
            padding: 10px 20px;
            border: none;
            border-radius: 5px;
            cursor: pointer;
            font-size: 16px;
            transition: background-color 0.2s ease;
        }
        .form-group button[type="submit"] {
            background-color: #28a745; /* 登録ボタンを緑に */
            color: white;
            margin-right: 10px;
        }
        .form-group button[type="submit"]:hover {
            background-color: #218838;
        }
        .back-button {
            background-color: #6c757d;
            color: white;
        }
        .back-button:hover {
            background-color: #5a6268;
        }
        .error-message {
            color: red;
            margin-bottom: 15px;
            font-weight: bold;
        }
        .logout-link { /* 必要に応じてログアウトリンク独自のスタイルをここに追加 */ }

        .footer { text-align: center; font-size: 12px; color: #555; padding: 20px; border-top: 1px solid #ccc; background-color: #f0f0f0; width: 100%; }
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
        <div class="sidebar">
            <h2>メニュー</h2>
            <ul>
                <li><a href="${pageContext.request.contextPath}/StudentListAction" class="active">学生管理</a></li>
                <li><a href="${pageContext.request.contextPath}/main/TestRegist.action" class="active">成績登録</a></li>
                <li><a href="${pageContext.request.contextPath}/main/TestList.action" class="active">成績参照</a></li>
                <li><a href="${pageContext.request.contextPath}/main/SubjectListAction" class="active">科目管理</a></li>
            </ul>
        </div>

        <div class="content">
            <div class="form-header">
                <h2>学生登録</h2>
            </div>

            <div class="form-area">
                <% if (errorMessage != null) { %>
                    <p class="error-message"><%= errorMessage %></p>
                <% } %>
                <form action="<%= request.getContextPath() %>/StudentCreateExecuteAction" method="post">
                    <div class="form-group">
                        <label for="entYear">入学年度:</label>
                        <select id="entYear" name="entYear" required>
                            <option value="">------</option>
                            <% if (entYears != null) { %>
                                <% for (Integer year : entYears) { %>
                                    <option value="<%= year %>"><%= year %></option>
                                <% } %>
                            <% } %>
                        </select>
                    </div>

                    <div class="form-group">
                        <label for="no">学生番号:</label>
                        <input type="text" id="no" name="no"
                            value="<%= request.getParameter("no") != null ? request.getParameter("no") : "" %>"
                            placeholder="学生番号を入力してください" required maxlength="10">
                    </div>

                    <div class="form-group">
                        <label for="name">氏名:</label>
                        <input type="text" id="name" name="name"
                            value="<%= request.getParameter("name") != null ? request.getParameter("name") : "" %>"
                            placeholder="氏名を入力してください" required>
                    </div>

                    <div class="form-group">
                        <label for="classNum">クラス:</label>
                        <select id="classNum" name="classNum" required>
                            <option value="">------</option>
                            <% if (classNums != null) { %>
                                <% for (String cNum : classNums) { %>
                                    <option value="<%= cNum %>"><%= cNum %>組</option>
                                <% } %>
                            <% } %>
                        </select>
                    </div>

                    <div class="form-group">
                        <label for="attend">在籍:</label>
                        <input type="checkbox" id="attend" name="attend" value="true" checked>
                    </div>

                    <div class="form-group">
                        <button type="submit">登録</button>
                        <button type="button" class="back-button" onclick="location.href='<%= request.getContextPath() %>/StudentListAction'">戻る</button>
                    </div>
                </form>
            </div>
        </div>
    </div>

    <div class="footer">© 2025 TIC<br>大原学園</div>
</body>
</html>