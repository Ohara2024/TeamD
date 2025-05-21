<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.List" %>
<%@ page import="bean.Subject" %>
<!DOCTYPE html>
<html lang="ja">
<head>
    <meta charset="UTF-8">
    <title>科目一覧</title>
    <style>
        /* (前回の回答と同じスタイルシート) */
        body { font-family: Arial, sans-serif; margin: 0; }
        header { background: linear-gradient(to right, #dfefff, #eef5ff); padding: 20px; border-bottom: 1px solid #ccc; display: flex; justify-content: space-between; align-items: center; }
        header h1 { margin: 0; font-size: 24px; }
        .container { /* display: flex; */ }
        .content { flex-grow: 1; padding: 30px; }
        .form-header { background-color: #f0f0f0; padding: 10px; font-size: 18px; font-weight: bold; margin-bottom: 10px; border-left: 5px solid #333; display: flex; justify-content: space-between; align-items: center; }
        .form-header h2 { margin: 0; }
        .form-header p { margin: 0; }
        table { border-collapse: collapse; width: 100%; margin-top: 20px; }
        th { padding: 8px; text-align: left; border-bottom: 2px solid #ddd; }
        td { padding: 8px 5px; text-align: left; border-bottom: 1px solid #eee; }
        .button {
            display: inline-block;
            padding: 0;
            margin-right: 5px;
            font-size: 14px;
            text-decoration: none;
            border: none;
            background-color: transparent;
            cursor: pointer;
        }
        .button:hover {
            text-decoration: underline;
        }
        .delete-button { color: #dc3545; }
        .register-button { color: #007bff; }
        .footer { text-align: center; font-size: 12px; color: #333; padding: 20px; border-top: 1px solid #ccc; background-color: #f0f0f0; }
    </style>
</head>
<body>
    <header><h1>得点管理システム</h1></header>
    <div class="container">
        <div class="content">
            <h2 style="background-color: lightgray; padding: 5px; margin-bottom: 10px;">科目管理</h2>
            <div style="text-align: right; margin-bottom: 10px;">
                <%-- ★ここから subject_create.jsp へリンクします★ --%>
                <a href="<%= request.getContextPath() %>/scoremanager/main/subject_create.jsp" class="button register-button">新規登録</a>
            </div>
            <table>
                <thead><tr><th>科目コード</th><th>科目名</th><th>操作</th></tr></thead>
                <tbody>
                    <%
                    // SubjectListAction から渡された subjectList を取得して表示
                    List<Subject> subjectList = (List<Subject>) request.getAttribute("subjectList");
                    if (subjectList != null && !subjectList.isEmpty()) {
                        for (Subject subject : subjectList) {
                            String cd = subject.getCd();
                    %>
                            <tr>
                                <td><%= cd %></td>
                                <td><%= subject.getName() %></td>
                                <td style="white-space: nowrap;">
                                    <%-- 削除ボタンから SubjectDeleteAction サーブレットへリンク --%>
                                    <a href="<%= request.getContextPath() %>/SubjectDeleteAction?cd=<%= cd %>&name=<%= subject.getName() %>" class="button delete-button">削除</a>
                                </td>
                            </tr>
                    <%
                        }
                    } else {
                    %>
                        <tr><td colspan="3">科目が見つかりませんでした。</td></tr>
                    <%
                    }
                    %>
                </tbody>
            </table>
        </div>
    </div>
    <div class="footer">© 2025 TIC<br>大原学園</div>
</body>
</html>