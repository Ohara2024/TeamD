<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="bean.Student" %>
<%@ page import="bean.School" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="java.util.Arrays" %>
<%
    // サーブレットから渡される学生情報Bean
    Student student = (Student) request.getAttribute("student");
    // nullチェック: もしサーブレットからstudentが渡されなかった場合やエラーの場合に備える
    if (student == null) {
        student = new Student(); // NullPointerExceptionを避けるため空のインスタンスを作成
        // デフォルト値やエラーメッセージを設定することも検討
    }

    // サーブレットから渡される入学年度のリスト
    List<Integer> entYears = (List<Integer>) request.getAttribute("entYears");
    if (entYears == null) {
        // デバッグ用または未設定時のダミーデータ
        int currentYear = java.time.Year.now().getValue();
        entYears = new ArrayList<>();
        for (int i = currentYear - 10; i <= currentYear + 5; i++) {
            entYears.add(i);
        }
    }

    // サーブレットから渡されるクラスのリスト
    List<String> classNums = (List<String>) request.getAttribute("classNums");
    if (classNums == null) {
        // デバッグ用または未設定時のダミーデータ
        classNums = Arrays.asList("101", "102", "201", "202", "301", "302");
    }

    // サーブレットから渡される学校のリスト（もし必要なら）
    List<School> schools = (List<School>) request.getAttribute("schools");
%>
<!DOCTYPE html>
<html lang="ja">
<head>
    <meta charset="UTF-8">
    <title>学生情報変更</title>
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
            background-color: #007bff;
            color: white;
            margin-right: 10px;
        }
        .form-group button[type="submit"]:hover {
            background-color: #0056b3;
        }
        .back-button {
            background-color: #6c757d;
            color: white;
        }
        .back-button:hover {
            background-color: #5a6268;
        }
        .readonly-field {
            background-color: #e9ecef; /* 読み取り専用フィールドの背景色 */
            opacity: 1; /* iOSでの透明度リセット */
        }
        .logout-link { /* 必要に応じてログアウトリンク独自のスタイルをここに追加 */ }

        .footer { text-align: center; font-size: 12px; color: #555; padding: 20px; border-top: 1px solid #ccc; background-color: #f0f0f0; width: 100%; }
    </style>
</head>
<body>
    <header>
        <h1>得点管理システム</h1>
        <div class="header-info">
            <span>テスト様</span>
            <a href="<%= request.getContextPath() %>/login/logout" class="logout-link">ログアウト</a>
        </div>
    </header>

    <div class="container">
        <div class="sidebar">
            <h2>メニュー</h2>
            <ul>
                <li><a href="<%= request.getContextPath() %>/StudentListAction">学生管理</a></li>
                <li><a href="#">成績管理</a></li>
                <li><a href="#">得点登録</a></li>
                <li><a href="#">履修登録</a></li>
                <li><a href="#">科目管理</a></li>
            </ul>
        </div>

        <div class="content">
            <div class="form-header">
                <h2>学生情報変更</h2>
            </div>

            <div class="form-area">
                <form action="<%= request.getContextPath() %>/StudentUpdateExecuteAction" method="post">
                    <%-- hiddenフィールドで学生番号と学校コードを送信 --%>
                    <input type="hidden" name="no" value="<%= (student.getNo() != null) ? student.getNo() : "" %>">
                    <%-- schoolCdのアクセス方法を修正 --%>
                    <input type="hidden" name="schoolCd" value="<%= (student.getSchool() != null) ? student.getSchool().getCd() : "" %>">

                    <div class="form-group">
                        <label for="entYear">入学年度:</label>
                        <%-- 入学年度は読み取り専用とする --%>
                        <input type="text" id="entYear" name="entYear"
                            value="<%= (student.getEntYear() != 0) ? student.getEntYear() : "" %>"
                            readonly class="readonly-field">
                    </div>

                    <div class="form-group">
                        <label for="displayNo">学生番号:</label>
                        <%-- 学生番号は読み取り専用とする --%>
                        <input type="text" id="displayNo" name="displayNo"
                            value="<%= (student.getNo() != null) ? student.getNo() : "" %>"
                            readonly class="readonly-field">
                    </div>

                    <div class="form-group">
                        <label for="name">氏名:</label>
                        <input type="text" id="name" name="name"
                            value="<%= (student.getName() != null) ? student.getName() : "" %>"
                            placeholder="氏名を入力してください" required>
                    </div>

                    <div class="form-group">
                        <label for="classNum">クラス:</label>
                        <select id="classNum" name="classNum">
                            <%-- ドロップダウンの初期値を設定 --%>
                            <option value="">------</option>
                            <%
                                if (classNums != null) {
                                    for (String cNum : classNums) {
                                        String selected = "";
                                        if (student.getClassNum() != null && student.getClassNum().equals(cNum)) {
                                            selected = " selected";
                                        }
                                        out.println("<option value=\"" + cNum + "\"" + selected + ">" + cNum + "組</option>");
                                    }
                                }
                            %>
                        </select>
                    </div>

                    <div class="form-group">
                        <label for="attend">在籍:</label>
                        <%-- 在籍チェックボックスの初期値を設定 --%>
                        <input type="checkbox" id="attend" name="attend" value="true"
                            <%= student.isAttend() ? "checked" : "" %>>
                    </div>

                    <div class="form-group">
                        <button type="submit">変更</button>
                        <button type="button" class="back-button" onclick="location.href='<%= request.getContextPath() %>/StudentListAction'">戻る</button>
                    </div>
                </form>
            </div>
        </div>
    </div>

    <div class="footer">© 2025 TIC<br>大原学園</div>
</body>
</html>