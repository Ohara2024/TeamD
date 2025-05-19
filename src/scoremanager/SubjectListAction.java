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

        // 学校コードを "oom" で固定
        String schoolCd = "oom";

        School school = new School();
        school.setCd(schoolCd);

        try {
            SubjectDao subjectDao = new SubjectDao();
            List<Subject> subjectList = subjectDao.filter(school);

            out.println("<!DOCTYPE html>");
            out.println("<html lang=\"ja\">");
            out.println("<head>");
            out.println("<meta charset=\"UTF-8\">");
            out.println("<title>科目一</title>");
            out.println("<style>");
            out.println("body { font-family: Arial, sans-serif; margin: 0; }");
            out.println("header { background: linear-gradient(to right, #dfefff, #eef5ff); padding: 20px; border-bottom: 1px solid #ccc; display: flex; justify-content: space-between; align-items: center; }");
            out.println("header h1 { margin: 0; font-size: 24px; }");
            out.println(".container { /* display: flex; */ }");
            out.println(".content { flex-grow: 1; padding: 30px; }");
            out.println(".form-header { background-color: #f0f0f0; padding: 10px; font-size: 18px; font-weight: bold; margin-bottom: 10px; border-left: 5px solid #333; display: flex; justify-content: space-between; align-items: center; }");
            out.println(".form-header h2 { margin: 0; }");
            out.println(".form-header p { margin: 0; }");
            out.println("table { border-collapse: collapse; width: 100%; margin-top: 20px; }");
            out.println("th { padding: 8px; text-align: left; border-bottom: 2px solid #ddd; }");
            out.println("td { padding: 8px 5px; text-align: left; border-bottom: 1px solid #eee; }");
            out.println(".button {");
            out.println("    display: inline-block;");
            out.println("    padding: 0; /* パディングをなくす */");
            out.println("    margin-right: 5px;");
            out.println("    font-size: 14pxl; /* 少し小さめのフォント */");
            out.println("    text-decoration: none;");
            out.println("    border: none; /* 枠線をなくす */");
            out.println("    background-color: transparent; /* 背景を透明にする */");
            out.println("    cursor: pointer;");
            out.println("}");
            out.println(".button:hover {");
            out.println("    text-decoration: underline; /* ホバー時に下線を表示するなど */");
            out.println("}");
            out.println(".delete-button { color: #dc3545; } /* 削除ボタンの文字色を赤に */");
            out.println(".register-button { color: #007bff; } /* 新規登録ボタンの文字色を青に */");
            out.println(".footer { text-align: center; font-size: 12px; color: #333; padding: 20px; border-top: 1px solid #ccc; background-color: #f0f0f0; }");
            out.println("</style>");
            out.println("</head>");
            out.println("<body>");
            out.println("<header><h1>得点管理システム</h1></header>");
            out.println("<div class=\"container\">");
            out.println("<div class=\"content\">");
            out.println("<h2 style=\"background-color: lightgray; padding: 5px; margin-bottom: 10px;\">科目管理</h2>");
            out.println("<div style=\"text-align: right; margin-bottom: 10px;\">");
            out.println("<a href=\"r\"class=\"button register-button\">新規登録</a>");
            out.println("</div>");
            out.println("<table>");
            out.println("<thead><tr><th>科目コード</th><th>科目名</th><th>操作</th></tr></thead>"); // "操作"列を追加
            out.println("<tbody>");
            for (Subject subject : subjectList) {
                String cd = subject.getCd();
                out.println("<tr>");
                out.println("<td>" + cd + "</td>");
                out.println("<td>" + subject.getName() + "</td>");
                out.println("<td style=\"white-space: nowrap;\">"); // ボタンが改行しないように
                out.println("<a href=\"?cd=" + cd + "\" class=\"button\">変更</a>");
                out.println("<a href=\"subjectdeleteaction.jsp?cd=" + cd + "&name=" + subject.getName() + "\" class=\"button delete-button\">削除</a>");
                out.println("</td>");
                out.println("</tr>");
            }
            out.println("</tbody>");
            out.println("</table>");
            out.println("</div>");
            out.println("</div>");
            out.println("<div class=\"footer\">© 2025 TIC<br>大原学園</div>");
            out.println("</body>");
            out.println("</html>");

        } catch (NamingException e) {
            e.printStackTrace(out);
            out.println("<!DOCTYPE html>");
            out.println("<html lang=\"ja\">");
            out.println("<head><meta charset=\"UTF-8\"><title>エラー</title></head>");
            out.println("<body><h1>エラー</h1><p>初期化エラーが発生しました。</p></body></html>");
        } catch (Exception e) {
            e.printStackTrace(out);
            out.println("<!DOCTYPE html>");
            out.println("<html lang=\"ja\">");
            out.println("<head><meta charset=\"UTF-8\"><title>エラー</title></head>");
            out.println("<body><h1>エラー</h1><p>データの取得に失敗しました。</p></body></html>");
        }
    }
}