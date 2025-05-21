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
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>登録/変更完了</title>
    <style>
        body { font-family: sans-serif; text-align: center; margin-top: 50px; }
        .message { font-size: 1.5em; margin-bottom: 20px; }
        .success { color: green; } /* 成功時のメッセージ色 */
        .error { color: red; }    /* 失敗時のメッセージ色（念のため） */
        .details { border: 1px solid #ccc; padding: 15px; display: inline-block; text-align: left; margin-bottom: 20px;}
        .details p { margin: 5px 0; }
        a { text-decoration: none; color: #007bff; }
        a:hover { text-decoration: underline; }
        div.links { margin-top: 30px; }
        div.links a { display: block; margin-bottom: 10px; }
    </style>
</head>
<body>
    <h1>処理結果</h1>

    <%-- 処理された成績データの詳細を表示（'processedGrade'という名前でデータが渡される想定） --%>
    <c:if test="${not empty processedGrade}">
        <div class="details">
            <p><strong>学生番号:</strong> <c:out value="${processedGrade.studentId}"/></p>
            <p><strong>氏名:</strong> <c:out value="${processedGrade.studentName}"/></p>
            <p><strong>科目名:</strong> <c:out value="${processedGrade.subjectName}"/></p>
            <p><strong>点数:</strong> <c:out value="${processedGrade.score}"/></p>
            <%-- 必要に応じて、その他の科目コード、学校コードなども追加してください --%>
            <%-- <p><strong>科目コード:</strong> <c:out value="${processedGrade.subjectCode}"/></p> --%>
            <%-- <p><strong>学校コード:</strong> <c:out value="${processedGrade.schoolCode}"/></p> --%>
        </div>
    </c:if>

    <div class="links">
        <%-- この成績を変更するリンク（processedGradeにIDがあれば表示） --%>
        <c:if test="${not empty processedGrade && not empty processedGrade.id && processedGrade.id ne 0}">
            <%-- フォームのJSPファイル名に合わせてリンクを調整してください --%>
            <a href="gradeFormAndProcess.jsp?id=<c:out value='${processedGrade.id}'/>">この成績を変更する</a>
        </c:if>
        
        <%-- 続けて新しい成績を登録するリンク（元の登録フォームへ） --%>
        <%-- フォームのJSPファイル名に合わせてリンクを調整してください --%>
        <a href="gradeFormAndProcess.jsp">続けて成績を登録する</a>

        <%-- 成績一覧に戻るリンク（仮のリストページへのリンク） --%>
        <a href="gradeList.jsp">成績一覧に戻る</a>
    </div>
</body>
</html>