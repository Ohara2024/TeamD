package scoremanager;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;

import javax.naming.InitialContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;

import tool.Action;

public class SubjectDeleteExecuteAction implements Action {
    @Override
    public String execute(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException, Exception {
        // メソッドの処理内容は変更なし
        response.setContentType("text/html; charset=UTF-8");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();

        String cd = request.getParameter("cd");

        try {
            InitialContext ic = new InitialContext();
            DataSource ds = (DataSource) ic.lookup("java:/comp/env/jdbc/yajima");
            Connection con = ds.getConnection();

            PreparedStatement st = con.prepareStatement("DELETE FROM SUBJECT WHERE CD = ?");
            st.setString(1, cd);
            int line = st.executeUpdate();

            st.close();
            con.close();

            out.println("<!DOCTYPE html>");
            out.println("<html>");
            out.println("<head>");
            out.println("<meta charset=\"UTF-8\">");
            out.println("<title>科目削除</title>");
            out.println("<style>");
            out.println("h2 { background-color: #f0f0f0; padding: 10px; }");
            out.println(".success-message { background-color: #33CC66; text-align: center; padding: 15px; border: 1px solid #8fbc8f; }");
            out.println(".return-link { display: block; margin-top: 20px; text-align: center; }");
            out.println("</style>");
            out.println("</head>");
            out.println("<body>");

            out.println("<h2>科目情報削除</h2>");

            if (line > 0) {
                out.println("<p class=\"success-message\">削除が完了しました</p>");
            } else {
                out.println("<p style=\"color:red;\">科目コード「" + cd + "」の削除に失敗しました。</p>");
            }
            out.println("<p class=\"return-link\"><a href=\"SubjectListAction\">戻る</a></p>");

            out.println("</body>");
            out.println("</html>");

            return null;

        } catch (Exception e) {
            e.printStackTrace();
            out.println("<!DOCTYPE html>");
            out.println("<html>");
            out.println("<head>");
            out.println("<meta charset=\"UTF-8\">");
            out.println("<title>エラー</title>");
            out.println("<style>");
            out.println("h2 { background-color: #f0f0f0; padding: 10px; }");
            out.println("</style>");
            out.println("</head>");
            out.println("<body>");
            out.println("<h2>エラー</h2>");
            out.println("<p style=\"color:red;\">データベース処理中にエラーが発生しました。</p>");
            out.println("<p>" + e.getMessage() + "</p>");
            out.println("<p class=\"return-link\"><a href=\"SubjectList.action\">科目一覧へ戻る</a></p>");
            out.println("</body>");
            out.println("</html>");

            return null;
        }
    }
}