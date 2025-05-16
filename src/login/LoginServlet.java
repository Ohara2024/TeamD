// src/login/LoginServlet.java
package login;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public class LoginServlet extends HttpServlet {
    private static final int MAX_ATTEMPTS = 5;             // 初期入力可能回数
    private static final long LOCKOUT_DURATION = 3 * 60 * 1000; // ロックアウト時間 (3分)

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String username = request.getParameter("username");
        String password = request.getParameter("password");
        HttpSession session = request.getSession();

        Long lockTime = (Long) session.getAttribute("lockTime");
        Boolean loginLocked = (Boolean) session.getAttribute("loginLocked");

        // ロックされているか確認し、ロック時間が過ぎていれば解除
        if (loginLocked != null && loginLocked) {
            if (lockTime != null && (System.currentTimeMillis() - lockTime > LOCKOUT_DURATION)) {
                session.removeAttribute("loginLocked");
                session.removeAttribute("lockTime");
                request.setAttribute("errorMessage", "アカウントのロックが解除されました。");
            } else {
                request.setAttribute("errorMessage", "アカウントがロックされています。しばらく経ってから再度お試しください。");
                request.setAttribute("loginLocked", true);
                request.getRequestDispatcher("/login/login.jsp").forward(request, response);
                return;
            }
        }

        Integer remainingAttempts = (Integer) session.getAttribute("remainingAttempts");

        if ("admin".equals(username) && "1234".equals(password)) {
        	session.setAttribute("username", username);
            session.removeAttribute("remainingAttempts");
            session.removeAttribute("loginLocked");
            session.removeAttribute("lockTime");
            response.sendRedirect(request.getContextPath() + "/login/menu.jsp");
        } else {
            if (remainingAttempts == null) {
                remainingAttempts = MAX_ATTEMPTS - 1;
            } else {
                remainingAttempts--;
            }
            session.setAttribute("remainingAttempts", remainingAttempts);
            request.setAttribute("remainingAttempts", remainingAttempts);
            request.setAttribute("errorMessage", "ユーザー名またはパスワードが間違っています。");

            if (remainingAttempts <= 0) {
                session.setAttribute("loginLocked", true);
                session.setAttribute("lockTime", System.currentTimeMillis()); // ロック時間を記録
                request.setAttribute("loginLocked", true);
                request.setAttribute("errorMessage", "入力可能回数が上限に達しました。アカウントをロックしました。");
            }

            request.getRequestDispatcher("/login/login.jsp").forward(request, response);
        }
    }
}
