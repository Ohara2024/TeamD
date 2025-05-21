package scoremanager;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javax.naming.InitialContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;

@WebServlet(urlPatterns={"/SubjectList"})
public class SubjectList extends HttpServlet {
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/html; charset=UTF-8");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();

        try {
            // ヘッダーの出力
            out.println("<!DOCTYPE html>");
            out.println("<html lang=\"ja\">");
            out.println("<head>");
            out.println("<meta charset=\"UTF-8\">");
            out.println("<title>科目一覧</title>");
            out.println("<style>");
            out.println("body { font-family: Arial, sans-serif; margin: 0; }");
            out.println("header { background: linear-gradient(to right, #dfefff, #eef5ff); padding: 20px; border-bottom: 1px solid #ccc; display: flex; justify-content: space-between; align-items: center; }");
            out.println("header h1 { margin: 0; font-size: 24px; }");
            out.println("header .userinfo { font-size: 14px; }");
            out.println(".container { display: flex; }");
            out.println(".menu-left { width: 200px; background-color: #f9f9f9; padding: 20px; border-right: 1px solid #ccc; }");
            out.println(".menu-left a { display: block; color: #0033cc; text-decoration: none; margin-bottom: 10px; font-size: 14px; }");
            out.println(".content { flex-grow: 1; padding: 30px; }");
            out.println(".form-header { background-color: #f0f0f0; padding: 10px; font-size: 18px; font-weight: bold; margin-bottom: 20px; border-left: 5px solid #333; }");
            out.println("table { border-collapse: collapse; width: 100%; }");
            out.println("th { padding: 8px; text-align: left; border-bottom: 2px solid #ddd; }");
            out.println("td { padding: 8px 5px; text-align: left; border-bottom: 1px solid #eee; }");
            out.println(".actions { white-space: nowrap; }");
            out.println(".actions a { margin-right: 5px; }");
            out.println(".button { display: inline-block; padding: 5px 10px; border: none; border-radius: 5px; color: #007bff; text-decoration: none; font-size: small; cursor: pointer; background-color: white; }");
            out.println(".footer { text-align: center; font-size: 12px; color: #666; padding: 20px; border-top: 1px solid #ccc; }");
            out.println("</style>");
            out.println("</head>");
            out.println("<body>");

            // ヘッダー
            out.println("<header>");
            out.println("<h1>得点管理システム</h1>");
            out.println("<div class=\"userinfo\">大原 太郎さん　<a href=\"login/logout.jsp\">ログアウト</a></div>");
            out.println("</header>");

            // コンテンツ
            out.println("<div class=\"container\">");
            out.println("<nav class=\"menu-left\">");
            out.println("<a href=\"login/menu.jsp\">メニュー</a>");
            out.println("<a href=\"KensakuServlet\">学生管理</a>");
            out.println("<h4>成績管理</h4>");
            out.println("<a href=\"#\">成績登録</a>");
            out.println("<a href=\"#\">成績参照</a>");
            out.println("<a href=\"#\">科目管理</a>");
            out.println("</nav>");

            out.println("<div class=\"content\">");
            out.println("<div class=\"form-header\">科目管理</div>");
            out.println("<div class=\"button-container\">");
            out.println("<p style=\"text-align: right;\"><a href=\"scoremanager/subject_create.jsp\" class=\"button\">新規登録</a></p>");
            out.println("</div>");

            // DBからデータを取得
            InitialContext ic = new InitialContext();
            DataSource ds = (DataSource) ic.lookup("java:/comp/env/jdbc/yajima");
            try (Connection con = ds.getConnection()) {
                PreparedStatement st = con.prepareStatement("SELECT CD, NAME FROM SUBJECT");
                ResultSet rs = st.executeQuery();

                out.println("<table><thead><tr><th>科目コード</th><th>科目名</th><th style=\"width: 150px;\">操作</th></tr></thead><tbody>");
                while (rs.next()) {
                    String cd = rs.getString("CD");
                    String name = rs.getString("NAME");

                    // HTMLエスケープ
                    cd = cd != null ? cd : "";
                    name = name != null ? name : "";

                    out.println("<tr>");
                    out.println("<td>" + cd + "</td>");
                    out.println("<td>" + name + "</td>");
                    out.println("<td class=\"actions\">");
                    out.println("<a href=\"SubjectDeleteConfirm?cd=" + cd + "\" class=\"button\">削除</a>");
                    out.println("</td>");
                    out.println("</tr>");
                }
                out.println("</tbody></table>");

                rs.close();
                st.close();

            } catch (Exception e) {
                out.println("<p style=\"color:red;\">データの取得に失敗しました。</p>");
                e.printStackTrace(out);
            }

            out.println("</div>"); // content div の閉じタグ
            out.println("</div>"); // container div の閉じタグ

            // フッター
            out.println("<div class=\"footer\">© 2023 TIC<br>大原学園</div>");

            out.println("</body>");
            out.println("</html>");

        } catch (Exception e) {
            e.printStackTrace(out);
            out.println("<p style=\"color:red;\">ページの表示中にエラーが発生しました。</p>");
        }
    }
}