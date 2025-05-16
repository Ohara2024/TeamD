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

@WebServlet("/subject/SubjectRegisterServlet")
public class SubjectCreateAction extends HttpServlet {
    private static final long serialVersionUID = 1L;

    // DB接続情報（環境に応じて変更してください）
    private static final String JDBC_URL = "jdbc:h2:~/yajima";  // 例: "jdbc:h2:tcp://localhost/~/test"
    private static final String DB_USER = "sa";
    private static final String DB_PASS = "";

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");

        String subjectId = request.getParameter("subjectId");
        String subjectName = request.getParameter("subjectName");

        // 入力チェック
        if (subjectId == null || subjectId.isEmpty() || subjectName == null || subjectName.isEmpty()) {
            request.setAttribute("errorMessage", "すべての項目を入力してください。");
            request.getRequestDispatcher("/subject/subject_register.jsp").forward(request, response);
            return;
        }

        Connection conn = null;
        PreparedStatement pstmt = null;

        try {
            // JDBCドライバのロード
            Class.forName("org.h2.Driver");
            conn = DriverManager.getConnection(JDBC_URL, DB_USER, DB_PASS);

            // SQL文を準備
            String sql = "INSERT INTO SUBJECT (SUBJECT_ID, SUBJECT_NAME) VALUES (?, ?)";
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, subjectId);
            pstmt.setString(2, subjectName);

            int result = pstmt.executeUpdate();

            if (result > 0) {
                // 登録成功 → 成功画面またはリスト画面へリダイレクト
                response.sendRedirect(request.getContextPath() + "/subject/subject_success.jsp");
            } else {
                request.setAttribute("errorMessage", "登録に失敗しました。");
                request.getRequestDispatcher("/subject/subject_register.jsp").forward(request, response);
            }

        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
            request.setAttribute("errorMessage", "データベースエラーが発生しました: " + e.getMessage());
            request.getRequestDispatcher("/subject/subject_register.jsp").forward(request, response);

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
