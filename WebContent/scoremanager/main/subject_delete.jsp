<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="bean.Teacher" %>
<!DOCTYPE html>
<html lang="ja">
<head>
    <meta charset="UTF-8">
    <title>科目情報削除</title>
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

        .content-area {
            flex-grow: 1;
            padding: 30px;
            box-sizing: border-box;
            background-color: #ffffff;
        }

        .content-area h2 {
             font-size: 16px;
             color: #333;
             margin-top: 0;
             margin-bottom: 15px;
             font-weight: bold;
        }
        .content-area p {
            font-size: 16px;
            color: #555;
            line-height: 1.5;
            margin-bottom: 20px;
        }
        .action-area { /* ボタンとリンクをまとめる新しいコンテナ */
            display: flex;
            flex-direction: column; /* 要素を縦に並べる */
            gap: 10px; /* 要素間のスペース */
            margin-top: 20px;
            align-items: flex-start; /* 左寄せ */
        }
        .delete-button {
            display: inline-block;
            padding: 5px 10px;
            font-size: 14px;
            text-align: center;
            text-decoration: none;
            border-radius: 5px;
            cursor: pointer;
            border: 1px solid;
            background-color: #dc3545; /* 赤色 */
            color: white;
            border-color: #dc3545;
            transition: background-color 0.3s ease;
        }
        .delete-button:hover {
            background-color: #c82333;
            border-color: #c82333;
        }

        /* 「戻る」リンクのスタイル */
        .return-text-link { /* 純粋なテキストリンクとして定義 */
            display: inline-block; /* 必要に応じて調整 */
            color: #007bff; /* 青色 */
            text-decoration: none; /* 下線なし */
            font-size: 14px; /* 適切なフォントサイズ */
            padding: 0; /* パディングなし */
            margin-top: 0; /* 上のマージンはflexboxのgapで制御 */
        }
        .return-text-link:hover {
            text-decoration: underline; /* ホバーで下線 */
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
            <h2>科目情報削除</h2>
            <a href="SubjectListAction">科目管理</a>
        </div>

        <div class="content-area">
            <h2>科目情報削除</h2>
            <p>「<%= request.getParameter("name") %>」（<%= request.getParameter("cd") %>）を削除してもよろしいですか？</p>
            <div class="action-area"> <%-- ボタンとリンクを縦に配置するための新しいコンテナ --%>
                <form action="<%= request.getContextPath() %>/SubjectDeleteExecuteAction" method="post">
                    <input type="hidden" name="cd" value="<%= request.getParameter("cd") %>">
                    <button type="submit" class="delete-button">削除</button>
                </form>
                <%-- 「戻る」リンクを削除ボタンの下に配置し、ボタンデザインをなくす --%>
                <a href="SubjectListAction" class="return-text-link">戻る</a>
            </div>
        </div>
    </div>

    <div class="footer">
        © 2025 TIC<br>大原学園
    </div>
</body>
</html>