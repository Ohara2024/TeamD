package scoremanager;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import javax.naming.NamingException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import bean.School;
import bean.Subject;
import dao.SubjectDao;

@WebServlet(urlPatterns={"/SubjectListAction"})
public class SubjectListAction extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/html; charset=UTF-8");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();

        School school = new School();
        school.setCd(null); // ★ 必要に応じて学校コードを設定してください (例: school.setCd("oom");)

        try {
            SubjectDao subjectDao = new SubjectDao();
            List<Subject> subjectList = subjectDao.filter(school);

            // ★ ログ出力 ★
            System.out.println("subjectList のサイズ: " + (subjectList != null ? subjectList.size() : "null"));
            if (subjectList != null) {
                for (Subject subject : subjectList) {
                    System.out.println("科目CD: " + subject.getCd() + ", 科目名: " + subject.getName());
                }
            } else {
                System.out.println("subjectList は null です。");
            }
            // ★ ログ出力 ここまで ★

            out.println("<!DOCTYPE html>");
            out.println("<html lang=\"ja\">");
            out.println("<head>");
            out.println("<meta charset=\"UTF-8\">");
            out.println("<title>科目一覧</title>");
            out.println("<style>");
            out.println("body { font-family: Arial, sans-serif; margin: 0; }");
            out.println("header { background: linear-gradient(to right, #dfefff, #eef5ff); padding: 20px; border-bottom: 1px solid #ccc; display: flex; justify-content: space-between; align-items: center; }");
            out.println("header h1 { margin: 0; font-size: 24px; }");
            out.println(".container { display: flex; }");
            out.println(".content { flex-grow: 1; padding: 30px; }");
            out.println(".form-header { background-color: #f0f0f0; padding: 10px; font-size: 18px; font-weight: bold; margin-bottom: 20px; border-left: 5px solid #333; }");
            out.println("table { border-collapse: collapse; width: 100%; }");
            out.println("th { padding: 8px; text-align: left; border-bottom: 2px solid #ddd; }");
            out.println("td { padding: 8px 5px; text-align: left; border-bottom: 1px solid #eee; }");
            out.println(".footer { text-align: center; font-size: 12px; color: #333; padding: 20px; border-top: 1px solid #ccc; background-color: #f0f0f0; }");
            out.println(".edit-button {");
            out.println("    display: inline-block;");
            out.println("    padding: 5px 10px;");
            out.println("    font-size: 14px;");
            out.println("    text-align: center;");
            out.println("    text-decoration: none;");
            out.println("    border-radius: 5px;");
            out.println("    cursor: pointer;");
            out.println("    background-color: #007bff;");
            out.println("    color: white;");
            out.println("    border: 1px solid #007bff;");
            out.println("    margin-right: 5px;");
            out.println("}");
            out.println(".edit-button:hover { background-color: #0056b3; }");
            out.println(".delete-button {");
            out.println("    display: inline-block;");
            out.println("    padding: 5px 10px;");
            out.println("    font-size: 14px;");
            out.println("    text-align: center;");
            out.println("    text-decoration: none;");
            out.println("    border-radius: 5px;");
            out.println("    cursor: pointer;");
            out.println("    background-color: #dc3545;");
            out.println("    color: white;");
            out.println("    border: 1px solid #dc3545;");
            out.println("}");
            out.println(".delete-button:hover { background-color: #c82333; }");
            out.println("</style>");
            out.println("</head>");
            out.println("<body>");
            out.println("<header><h1>得点管理システム</h1></header>");
            out.println("<div class=\"container\">");
            out.println("<div class=\"content\">");
            out.println("<div class=\"form-header\">科目一覧</div>");
            out.println("<table>");
            out.println("<thead><tr><th>科目コード</th><th>科目名</th><th>操作</th></tr></thead>");
            out.println("<tbody>");
            if (subjectList != null) {
                for (Subject subject : subjectList) {
                    String cd = subject.getCd();
                    out.println("<tr>");
                    out.println("<td>" + cd + "</td>");
                    out.println("<td>" + subject.getName() + "</td>");
                    out.println("<td>");
                    out.println("<a href=\"?cd=" + cd + "\" class=\"edit-button\">変更</a>");
                    out.println("<a href=\"subjectdeleteaction.jsp?cd=" + cd + "&name=" + subject.getName() + "\" class=\"delete-button\">削除</a>");
                    out.println("</td>");
                    out.println("</tr>");
                }
            } else {
                out.println("<tr><td colspan=\"3\">科目が見つかりませんでした。</td></tr>");
            }
            out.println("</tbody>");
            out.println("</table>");
            out.println("</div>");
            out.println("</div>");
            out.println("<div class=\"footer\">© 2025 TIC<br>大原学園</div>");
            out.println("</body>");
            out.println("</html>");

        } catch (NamingException e) {
            e.printStackTrace();
            out.println("<!DOCTYPE html>");
            out.println("<html lang=\"ja\">");
            out.println("<head><meta charset=\"UTF-8\"><title>エラー</title></head>");
            out.println("<body><h1>エラー</h1><p>初期化エラーが発生しました。</p></body></html>");
        } catch (Exception e) {
            e.printStackTrace();
            out.println("<!DOCTYPE html>");
            out.println("<html lang=\"ja\">");
            out.println("<head><meta charset=\"UTF-8\"><title>エラー</title></head>");
            out.println("<body><h1>エラー</h1><p>科目一覧の取得に失敗しました。</p></body></html>");
        }
    }
}