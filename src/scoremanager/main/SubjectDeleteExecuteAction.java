package scoremanager.main;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;

@WebServlet("/SubjectDeleteExecuteAction")
public class SubjectDeleteExecuteAction extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        // response.setContentType("text/html; charset=UTF-8"); // JSPへフォワードするため不要になります
        // PrintWriter out = response.getWriter(); // JSPへフォワードするため不要になります

        String subjectCode = request.getParameter("cd");
        String message = "";
        boolean isSuccess = false;

        Connection conn = null;
        PreparedStatement pstmt = null;

        try {
            Context initContext = new InitialContext();
            Context envContext = (Context) initContext.lookup("java:/comp/env");
            DataSource ds = (DataSource) envContext.lookup("jdbc/yajima");
            conn = ds.getConnection();

            String sql = "DELETE FROM SUBJECT WHERE CD = ?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, subjectCode);

            int rowsAffected = pstmt.executeUpdate();

            if (rowsAffected > 0) {
                message = "削除が完了しました。";
                isSuccess = true;
            } else {
                message = "削除に失敗しました。科目コード「" + subjectCode + "」が存在しない可能性があります。";
            }

        } catch (NamingException e) {
            e.printStackTrace();
            message = "JNDI エラーが発生しました: " + e.getMessage();
        } catch (SQLException e) {
            e.printStackTrace();
            message = "データベースエラーが発生しました: " + e.getMessage();
        } finally {
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

            // ★★★ ここを修正します ★★★
            // 処理結果をJSPに渡す
            request.setAttribute("message", message);
            request.setAttribute("isSuccess", isSuccess);

            // 削除完了画面（subject_delete_done.jsp）へフォワード
            // JSPが webcontent/scoremanager/main/ にあるため、絶対パスで指定します
            request.getRequestDispatcher("/scoremanager/main/subject_delete_done.jsp").forward(request, response);
        }
    }
}