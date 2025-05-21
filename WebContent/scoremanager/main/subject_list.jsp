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
        .header-right a {
            color: #007bff;
            text-decoration: none;
        }
        .header-right a:hover {
            text-decoration: underline;
        }

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
            margin-bottom: 15px; /* テーブルとの間隔を調整 */
            font-weight: bold;
        }

        /* 新規登録リンクのスタイル */
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
            margin-top: 0; /* h2との間隔はh2のmargin-bottomで制御 */
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
            color: #007bff;
            text-decoration: none;
        }
        .action-links a.delete-link {
            color: #dc3545;
        }
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
                Teacher currentTeacher = (Teacher) session.getAttribute("currentTeacher");
                if (currentTeacher != null) {
            %>
                <span><%= currentTeacher.getName() %>様</span>
            <%
                } else {
            %>
                <span>ゲスト様</span>
            <%
                }
            %>
            <a href="<%= request.getContextPath() %>/LogoutAction">ログアウト</a>
        </div>
    </header>

    <div class="main-wrapper">
        <div class="left-panel">
            <h2>科目管理</h2>
            <ul>
                <li><a href="SubjectListAction" class="active">科目管理</a></li>
          
            </ul>
        </div>

        <div class="content-area">
            <%
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

            <%-- 絞り込みフォームを削除 --%>

            <div class="register-link-container">
                <a href="<%= request.getContextPath() %>/SubjectCreateAction" class="register-text-link">新規登録</a>
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
                        List<Subject> subjects = (List<Subject>) request.getAttribute("subjects");
                        if (subjects != null && !subjects.isEmpty()) {
                            for (Subject subject : subjects) {
                    %>
                                <tr>
                                    <td><%= subject.getCd() %></td>
                                    <td><%= subject.getName() %></td>
                                    <td class="action-links">
                                        <a href="SubjectUpdateAction?cd=<%= subject.getCd() %>">編集</a>
                                        <a href="SubjectDeleteAction?cd=<%= subject.getCd() %>&name=<%= subject.getName() %>" class="delete-link">削除</a>
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