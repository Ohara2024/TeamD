package scoremanager;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/subject/SubjectCreateExecuteAction")
public class SubjectCreateExecuteAction extends HttpServlet {
    private static final long serialVersionUID = 1L;

    // DB接続設定
    private static final String JDBC_URL = "jdbc:h2:~/yajima";
    private static final String DB_USER = "sa";
    private static final String DB_PASS = "";

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");

        // 確認画面から送られてきたデータを取得
        String subjectId = request.getParameter("subjectId");
        String subjectName = request.getParameter("subjectName");

        Connection conn = null;
        PreparedStatement pstmt = null;

        try {
            // JDBCドライバ読み込み
            Class.forName("org.h2.Driver");
            conn = DriverManager.getConnection(JDBC_URL, DB_USER, DB_PASS);

            // SQLを実行
            String sql = "INSERT INTO SUBJECT (SUBJECT_ID, SUBJECT_NAME) VALUES (?, ?)";
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, subjectId);
            pstmt.setString(2, subjectName);

            int result = pstmt.executeUpdate();

            if (result > 0) {
                // 登録成功 → 完了画面へ
                response.sendRedirect(request.getContextPath() + "/subject/subject_success.jsp");
            } else {
                request.setAttribute("errorMessage", "登録に失敗しました。");
                request.getRequestDispatcher("/subject/subject_confirm.jsp").forward(request, response);
            }

        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
            request.setAttribute("errorMessage", "データベースエラー: " + e.getMessage());
            request.getRequestDispatcher("/subject/subject_confirm.jsp").forward(request, response);

        } finally {
            try {
                if (pstmt != null) pstmt.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
