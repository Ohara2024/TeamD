<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.List" %>
<%@ page import="bean.Subject" %>
<%@ page import="bean.Teacher" %>
<!DOCTYPE html>
<html lang="ja">
<head>
    <meta charset="UTF-8">
    <title>科目一覧</title>
    <style>
        body {
            font-family: Arial, sans-serif;
            margin: 0;
            background-color: #ffffff;
            display: flex;
            flex-direction: column;
            min-height: 100vh;
        }
        header {
            background: linear-gradient(to right, #dfefff, #eef5ff);
            padding: 20px;
            border-bottom: 1px solid #ccc;
            display: flex;
            justify-content: space-between;
            align-items: center;
        }
        header h1 {
            margin: 0;
            font-size: 24px;
            text-align: left;
            flex-grow: 1;
            padding-left: 20px;
        }
        .header-right {
            display: flex;
            align-items: center;
            gap: 15px;
            padding-right: 20px;
            font-size: 14px;
        }
        .user-info {
            margin-right: auto;
            font-weight: bold;
            color: #333;
        }
        .header-right a {
            color: #007bff;
            text-decoration: none;
        }
        .header-right a:hover {
            text-decoration: underline;
        }
        /* ★ここを修正：.logout-link のスタイルを削除しました */
        /*
        .logout-link {
            text-decoration: none;
            color: #d32f2f;
            padding: 8px 15px;
            border: 1px solid #d32f2f;
            border-radius: 3px;
            background-color: #ffebee;
            transition: background-color 0.3s ease, color 0.3s ease;
            margin-left: 20px;
        }
        .logout-link:hover {
            background-color: #d32f2f;
            color: #fff;
        }
        */
        /* ★ここまで修正 */

        .main-wrapper {
            display: flex;
            flex-grow: 1;
        }

        .left-panel {
            width: 200px;
            padding: 20px 0 20px 20px;
            box-sizing: border-box;
            background-color: #ffffff;
            border-right: 1px solid #e0e0e0;
            box-shadow: 2px 0 5px rgba(0,0,0,0.05);
        }
        .left-panel h2 {
            margin-top: 0;
            margin-bottom: 20px;
            font-size: 18px;
            color: #333;
            padding-left: 0;
        }
        .left-panel ul {
            list-style: none;
            padding: 0;
            margin: 0;
        }
        .left-panel li {
            margin-bottom: 10px;
        }
        .left-panel a {
            display: block;
            text-decoration: none;
            color: #007bff;
            padding: 5px 0;
            font-size: 14px;
        }
        .left-panel a:hover {
            text-decoration: underline;
        }
        .left-panel a.active {
            font-weight: bold;
            color: #0056b3;
            background-color: #f0f8ff;
        }

        .content-area {
            flex-grow: 1;
            padding: 30px;
            box-sizing: border-box;
            background-color: #ffffff;
        }

        .status-message-box {
            padding: 10px 15px;
            margin-bottom: 20px;
            font-size: 16px;
            color: #333;
            text-align: left;
            border: 1px solid;
        }
        .status-message-box.success {
            background-color: #e6ffe6;
            border-color: #a3e0a3;
        }
        .status-message-box.error {
            background-color: #ffe6e6;
            border-color: #e0a3a3;
        }

        .content-area h2 {
            font-size: 16px;
            color: #333;
            margin-top: 0;
            margin-bottom: 15px;
            font-weight: bold;
        }

        .register-link-container {
            text-align: right;
            margin-bottom: 10px;
        }
        .register-text-link {
            display: inline-block;
            color: #007bff;
            text-decoration: none;
            font-size: 14px;
            padding: 0;
        }
        .register-text-link:hover {
            text-decoration: underline;
        }

        table {
            width: 100%;
            border-collapse: collapse;
            margin-top: 0;
        }
        th, td {
            border: 1px solid #ddd;
            padding: 8px;
            text-align: left;
        }
        th {
            background-color: #f2f2f2;
            font-weight: bold;
        }
        .action-links a {
            margin-right: 10px;
            color: #007bff; /* 他のリンクと同じデフォルト色に戻す */
            text-decoration: none;
        }
        /* ↓ このCSSルールを削除しました ↓ */
        /*
        .action-links a.delete-link {
            color: #dc3545;
        }
        */
        .action-links a:hover {
            text-decoration: underline;
        }

        .footer {
            text-align: center;
            font-size: 12px;
            color: #333;
            padding: 20px;
            border-top: 1px solid #ccc;
            background-color: #f0f0f0;
            width: 100%;
            box-sizing: border-box;
            margin-top: auto;
        }
    </style>
</head>
<body>
    <header>
        <h1>得点管理システム</h1>
        <div class="header-right">
            <%
                // セッションからTeacherオブジェクトを取得
                // LoginServletで "teacher" というキー名で保存されている前提
                Teacher teacher = (Teacher) session.getAttribute("teacher");

                String teacherName = "ゲスト"; // デフォルトの名前
                // teacherオブジェクトが存在し、名前がnullでなく空文字列でない場合に名前を設定
                if (teacher != null && teacher.getName() != null && !teacher.getName().isEmpty()) {
                    teacherName = teacher.getName();
                }
            %>
            <div class="user-info"><%= teacherName %> さん</div>
            <%-- ★ここを修正：class="logout-link" を削除しました --%>
            <a href="<%= request.getContextPath() %>/login/logout">ログアウト</a>
        </div>
    </header>

    <div class="main-wrapper">
        <div class="left-panel">
            <h2>科目管理</h2>
            <ul>
                <li><a href="<%= request.getContextPath() %>/main/SubjectListAction" class="active">科目管理</a></li>
                <li><a href="<%= request.getContextPath() %>/StudentListAction">学生管理</a></li>
                <li><a href="<%= request.getContextPath() %>/main/TestList.action">成績参照</a></li>
            </ul>
        </div>

        <div class="content-area">
            <%
                // リクエストスコープからメッセージと成功/失敗フラグを取得
                String statusMessage = (String) request.getAttribute("message");
                Boolean isSuccessObj = (Boolean) request.getAttribute("isSuccess");
                boolean isSuccess = (isSuccessObj != null) ? isSuccessObj.booleanValue() : false;

                if (statusMessage != null && !statusMessage.isEmpty()) {
            %>
                <div class="status-message-box <%= isSuccess ? "success" : "error" %>">
                    <%= statusMessage %>
                </div>
            <%
                }
            %>

            <h2>科目情報一覧</h2>

            <div class="register-link-container">
                <a href="<%= request.getContextPath() %>/scoremanager/main/subject_create.jsp">登録・変更</a>
            </div>

            <table>
                <thead>
                    <tr>
                        <th>科目コード</th>
                        <th>科目名</th>
                        <th>操作</th>
                    </tr>
                </thead>
                <tbody>
                    <%
                        // SubjectListActionから渡された科目リストを取得
                        List<Subject> subjects = (List<Subject>) request.getAttribute("subjectList"); // SubjectListActionで設定したキー名

                        if (subjects != null && !subjects.isEmpty()) {
                            for (Subject subject : subjects) {
                    %>
                                <tr>
                                    <td><%= subject.getCd() %></td>
                                    <td><%= subject.getName() %></td>
                                    <td class="action-links">
                                        <%-- 削除リンクのクラスは残しますが、CSSは削除しました --%>
                                        <a href="<%= request.getContextPath() %>/SubjectDeleteAction?cd=<%= subject.getCd() %>&name=<%= subject.getName() %>">削除</a>
                                    </td>
                                </tr>
                    <%
                            }
                        } else {
                    %>
                            <tr>
                                <td colspan="3">登録されている科目はありません。</td>
                            </tr>
                    <%
                        }
                    %>
                </tbody>
            </table>
        </div>
    </div>

    <div class="footer">
        © 2025 TIC<br>大原学園
    </div>
</body>
</html>