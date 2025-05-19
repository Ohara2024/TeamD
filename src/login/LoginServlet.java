// src/login/LoginServlet.java
package login;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.sql.DataSource;

@WebServlet("/login")
public class LoginServlet extends HttpServlet {
    private static final int MAX_ATTEMPTS = 5;                  // 初期入力可能回数
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

        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            // JNDI を使用して DataSource をルックアップ
            Context initContext = new InitialContext();
            Context envContext = (Context) initContext.lookup("java:/comp/env");
            DataSource ds = (DataSource) envContext.lookup("jdbc/TeamD");
            conn = ds.getConnection();

            // ユーザー名とパスワードに基づいてユーザーを検索する SQL クエリ
            String sql = "SELECT * FROM TEACHER WHERE ID = ? AND PASSWORD = ?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, username);
            pstmt.setString(2, password);
            rs = pstmt.executeQuery();

            if (rs.next()) {
                // 認証成功
                session.setAttribute("username", username);
                session.removeAttribute("remainingAttempts");
                session.removeAttribute("loginLocked");
                session.removeAttribute("lockTime");
                response.sendRedirect(request.getContextPath() + "/login/menu.jsp");
            } else {
                // 認証失敗
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

        } catch (NamingException e) {
            e.printStackTrace();
            request.setAttribute("errorMessage", "データベースへの接続に失敗しました (JNDIエラー)。");
            request.getRequestDispatcher("/login/login.jsp").forward(request, response);
        } catch (SQLException e) {
            e.printStackTrace();
            request.setAttribute("errorMessage", "データベースの操作中にエラーが発生しました。");
            request.getRequestDispatcher("/login/login.jsp").forward(request, response);
        } finally {
            // 接続、ステートメント、結果セットをクローズする
            if (rs != null) {
                try {
                    rs.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if (pstmt != null) {
                try {
                    pstmt.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}