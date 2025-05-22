<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="bean.Teacher" %>
<!DOCTYPE html>
<html lang="ja">
<head>
    <meta charset="UTF-8">
    <title>学生登録完了</title>
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

        /* メッセージとリンクのスタイル */
        .message-area {
            background-color: #d4edda; /* 薄い緑色 */
            color: #155724; /* 濃い緑色 */
            padding: 15px;
            margin-bottom: 20px;
            border: 1px solid #c3e6cb;
            border-radius: 5px;
            font-weight: bold;
            text-align: center;
        }
        .action-link {
            display: inline-block;
            margin-top: 20px;
            padding: 10px 20px;
            background-color: #007bff;
            color: white;
            text-decoration: none;
            border-radius: 5px;
            transition: background-color 0.2s ease;
        }
        .action-link:hover {
            background-color: #0056b3;
        }
        .logout-link { /* 必要に応じてログアウトリンク独自のスタイルをここに追加 */ }

        .footer { text-align: center; font-size: 12px; color: #555; padding: 20px; border-top: 1px solid #ccc; background-color: #f0f0f0; width: 100%; }
    </style>
</head>
<body>
    <header>
        <h1>得点管理システム</h1>
        <div class="header-info">
            <%
                // セッションからTeacherオブジェクトを取得
                // LoginServletで "teacher" というキー名で保存されている前提
                Teacher teacher = (Teacher) session.getAttribute("teacher"); // ★ここを修正: キー名を "teacher" に変更★
                String teacherName = "ゲスト"; // デフォルトの名前

                // teacherオブジェクトが存在し、名前がnullでなく空文字列でない場合に名前を設定
                if (teacher != null && teacher.getName() != null && !teacher.getName().isEmpty()) {
                    teacherName = teacher.getName();
                }
            %>
            <span><%= teacherName %> さん</span>
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
                <h2>学生登録完了</h2>
            </div>
            <div class="message-area">
                <p>学生情報の登録が正常に完了しました。</p>
                <a href="<%= request.getContextPath() %>/StudentListAction" class="action-link">学生一覧へ</a>
            </div>
        </div>
    </div>

    <div class="footer">© 2025 TIC<br>大原学園</div>
</body>
</html>