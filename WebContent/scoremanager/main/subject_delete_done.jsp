<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="bean.Subject" %>
<%@ page import="bean.Teacher" %>
<!DOCTYPE html>
<html lang="ja">
<head>
    <meta charset="UTF-8">
    <title>科目削除完了</title>
    <style>
        body {
            font-family: Arial, sans-serif;
            margin: 0;
            background-color: #f8f8f8;
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
            flex-grow: 1;
            padding: 30px;
            box-sizing: border-box;
            background-color: #ffffff;
            text-align: center;
        }
        .container {
            width: 500px;
            margin: 50px auto;
            background-color: #fff;
            padding: 30px;
            border-radius: 8px;
            box-shadow: 0 0 15px rgba(0,0,0,0.1);
        }
        h2 {
            font-size: 20px;
            color: #333;
            margin-bottom: 25px;
            text-align: center;
        }
        p {
            font-size: 16px;
            color: #555;
            margin-bottom: 20px;
        }
        .success-message {
            color: #28a745; /* 緑色 */
            font-weight: bold;
            font-size: 18px;
            margin-bottom: 30px;
        }
        .button-group {
            display: flex;
            justify-content: center;
            gap: 20px;
        }
        .button-group a {
            padding: 12px 25px;
            border: none;
            border-radius: 5px;
            font-size: 16px;
            cursor: pointer;
            text-decoration: none;
            display: inline-block;
            text-align: center;
            line-height: normal;
        }
        .back-to-list-button {
            background-color: #007bff;
            color: white;
            transition: background-color 0.3s ease;
        }
        .back-to-list-button:hover {
            background-color: #0056b3;
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
                Teacher teacher = (Teacher) session.getAttribute("teacher");
                String teacherName = "ゲスト";
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
        <div class="container">
            <h2>科目削除完了</h2>
            <%
                Subject deletedSubject = (Subject) request.getAttribute("deletedSubject");

                if (deletedSubject != null) {
            %>
                    <p class="success-message">以下の科目を削除しました。</p>
                    <p><strong>科目コード:</strong> <%= deletedSubject.getCd() %></p>
                    <p><strong>科目名:</strong> <%= deletedSubject.getName() %></p>
            <%
                } else {
            %>
                    <p class="success-message">科目の削除が完了しました。</p>
                    <p>削除された科目情報は表示できません。</p>
            <%
                }
            %>

            <div class="button-group">
                <a href="<%= request.getContextPath() %>/SubjectListAction" class="back-to-list-button">科目一覧に戻る</a>
            </div>
        </div>
    </div>

    <div class="footer">
        © 2025 TIC<br>大原学園
    </div>
</body>
</html>