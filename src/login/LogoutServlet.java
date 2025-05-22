// src/main/java/login/LogoutServlet.java
package login;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@WebServlet("/login/logout") // ログアウト要求を処理するURL
public class LogoutServlet extends HttpServlet {
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // 1. セッションを取得 (存在する場合のみ)
        HttpSession session = request.getSession(false);

        // 2. セッションが存在すれば無効化する
        if (session != null) {
            session.invalidate(); // セッション内の情報をすべて破棄
        }

        // 3. ログアウト完了画面 (logout_completed.jsp) へリダイレクト
        response.sendRedirect(request.getContextPath() + "/scoremanager/main/logout_completed.jsp");
    }
}