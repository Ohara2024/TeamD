<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="ja">
<head>
    <meta charset="UTF-8">
    <title>科目情報削除確認</title>
    <style>
        body { font-family: Arial, sans-serif; margin: 0; }
        header { background: linear-gradient(to right, #dfefff, #eef5ff); padding: 20px; border-bottom: 1px solid #ccc; display: flex; justify-content: flex-start; align-items: center; }
        header h1 { margin: 0; font-size: 24px; }
        .container { display: flex; justify-content: flex-start; padding-top: 30px; padding-left: 20px; }
        .content { flex-grow: 0; padding: 20px; background-color: #fff; border-radius: 8px; box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1); }
        h2 { font-size: 1.5em; margin-top: 0; margin-bottom: 15px; }
        p { font-size: 16px; margin-bottom: 15px; }
        .button-group { display: flex; gap: 10px; margin-top: 20px; }
        .delete-button, .return-button { /* 複数のクラスに同じスタイルを適用 */
            display: inline-block;
            padding: 5px 10px; /* サイズを一覧のボタンに合わせる */
            margin-right: 5px; /* 右側のマージンを追加 */
            font-size: 14px; /* フォントサイズを一覧のボタンに合わせる */
            text-align: center;
            text-decoration: none;
            border-radius: 5px;
            cursor: pointer;
        }
        .delete-button {
            background-color: #dc3545; /* 赤色 */
            color: white;
            border: 1px solid #dc3545;
        }
        .return-button {
            background-color: #6c757d; /* グレー */
            color: white;
            border: 1px solid #6c757d;
        }
        .delete-button:hover { background-color: #c82333; }
        .return-button:hover { background-color: #545b62; }
    </style>
</head>
<body>
    <header><h1>得点管理システム</h1></header>
    <div class="container">
        <div class="content">
            <h2>科目情報削除確認</h2>
            <p>「<%= request.getParameter("name") %>」（<%= request.getParameter("cd") %>）を削除してもよろしいですか？</p>
            <div class="button-group">
                <form action="SubjectDeleteExecuteAction" method="post">
                    <input type="hidden" name="cd" value="<%= request.getParameter("cd") %>">
                    <button type="submit" class="delete-button">削除</button>
                </form>
                <a href="SubjectListAction" class="return-button">戻る</a>
            </div>
        </div>
    </div>
</body>
</html>