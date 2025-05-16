package scoremanager; // SubjectListAction.java と同じパッケージ名にしてください

import java.io.IOException;
import java.io.PrintWriter;
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
        response.setContentType("text/html; charset=UTF-8");
        PrintWriter out = response.getWriter();
        String subjectCode = request.getParameter("cd");
        String message = "";
        boolean isSuccess = false;

        Connection conn = null;
        PreparedStatement pstmt = null;

        try {
            // JNDIコンテキストの取得
            Context initContext = new InitialContext();
            Context envContext = (Context) initContext.lookup("java:/comp/env");

            // データソースのルックアップ (context.xml の設定名に合わせてください)
            DataSource ds = (DataSource) envContext.lookup("jdbc/yajima");

            // データベース接続の取得
            conn = ds.getConnection();

            // SQL文の作成
            String sql = "DELETE FROM SUBJECT WHERE CD = ?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, subjectCode);

            // SQL文の実行
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
            // リソースの解放
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

            // HTMLを直接出力
            out.println("<!DOCTYPE html>");
            out.println("<html lang=\"ja\">");
            out.println("<head>");
            out.println("<meta charset=\"UTF-8\">");
            out.println("<title>科目情報削除</title>");
            out.println("<style>");
            out.println("body { font-family: Arial, sans-serif; margin: 0; }");
            out.println("header { background: linear-gradient(to right, #dfefff, #eef5ff); padding: 20px; border-bottom: 1px solid #ccc; display: flex; justify-content: flex-start; align-items: center; }");
            out.println("header h1 { margin: 0; font-size: 24px; }");
            out.println(".container { display: flex; justify-content: flex-start; padding-top: 30px; padding-left: 20px; }");
            out.println(".content { flex-grow: 0; padding: 20px; background-color: #fff; border-radius: 8px; box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1); }");
            out.println("h2 { font-size: 1.5em; margin-top: 0; margin-bottom: 15px; }");
            out.println(".success-message { background-color: #e0f7de; color: #1b5e20; padding: 10px; border: 1px solid #c8e6c9; margin-bottom: 15px; }");
            out.println(".error-message { background-color: #ffebee; color: #c62828; padding: 10px; border: 1px solid #ef9a9a; margin-bottom: 15px; }");
            out.println(".return-link { display: block; margin-top: 20px; }");
            out.println(".return-link a { text-decoration: none; color: #007bff; }");
            out.println(".return-link a:hover { text-decoration: underline; }");
            out.println("</style>");
            out.println("</head>");
            out.println("<body>");
            out.println("<header><h1>得点管理システム</h1></header>");
            out.println("<div class=\"container\">");
            out.println("<div class=\"content\">");
            out.println("<h2>科目情報削除</h2>");
            if (isSuccess) {
                out.println("<p class=\"success-message\">" + message + "</p>");
            } else {
                out.println("<p class=\"error-message\">" + message + "</p>");
            }
            out.println("<p class=\"return-link\"><a href=\"SubjectListAction\">科目一覧へ戻る</a></p>");
            out.println("</div>");
            out.println("</div>");
            out.println("</body>");
            out.println("</html>");
        }
    }
}