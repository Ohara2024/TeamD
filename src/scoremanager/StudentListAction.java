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

import bean.Student;
import dao.StudentDao;

@WebServlet(urlPatterns={"/StudentListAction"})
public class StudentListAction extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/html; charset=UTF-8");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();

        String entYearParam = request.getParameter("entYear");
        String classNumParam = request.getParameter("classNum");
        String attendParam = request.getParameter("attend"); // ★ パラメータ名はそのままで取得 ★

        Integer entYear = null;
        if (entYearParam != null && !entYearParam.isEmpty()) {
            try {
                entYear = Integer.parseInt(entYearParam);
            } catch (NumberFormatException e) {
                // 入力された入学年度が数値でなかった場合のエラー処理 (今回は無視します)
            }
        }

        // classNum は String 型のまま DAO に渡します
        Boolean attend = null;
        if (attendParam != null && !attendParam.isEmpty()) {
            attend = Boolean.parseBoolean(attendParam);
        }

        try {
            StudentDao studentDao = new StudentDao();
            List<Student> studentList = studentDao.filter(entYear, classNumParam, attend, null);
            List<Integer> allEntYears = studentDao.getAllEntYears(); // ★ 入学年度一覧を取得 ★
            List<String> allClassNums = studentDao.getAllClassNums(); // ★ クラス一覧を取得 ★

            // ★ ログ出力 ★
            System.out.println("studentList のサイズ: " + (studentList != null ? studentList.size() : "null"));
            if (studentList != null) {
                for (Student student : studentList) {
                    System.out.println("学籍番号: " + student.getNo() + ", 名前: " + student.getName() + ", 学校コード: " + student.getSchool().getCd());
                }
            } else {
                System.out.println("studentList は null です。");
            }
            // ★ ログ出力 ここまで ★

            out.println("<!DOCTYPE html>");
            out.println("<html lang=\"ja\">");
            out.println("<head>");
            out.println("<meta charset=\"UTF-8\">");
            out.println("<title>学生一覧</title>");
            out.println("<style>");
            out.println("body { font-family: Arial, sans-serif; margin: 0; }");
            out.println("header { background: linear-gradient(to right, #dfefff, #eef5ff); padding: 20px; border-bottom: 1px solid #ccc; display: flex; justify-content: space-between; align-items: center; }");
            out.println("header h1 { margin: 0; font-size: 24px; }");
            out.println(".container { display: flex; }");
            out.println(".content { flex-grow: 1; padding: 30px; }");
            out.println(".form-header { background-color: #f0f0f0; padding: 10px; font-size: 18px; font-weight: bold; margin-bottom: 20px; border-left: 5px solid #333; display: flex; justify-content: space-between; align-items: center; }");
            out.println(".form-header h2 { margin: 0; }"); // ★ タイトル用 ★
            out.println(".filter-form { background-color: #f9f9f9; padding: 15px; margin-bottom: 20px; border: 1px solid #ddd; }");
            out.println(".filter-form label { margin-right: 10px; }");
            out.println(".filter-form input[type=text], .filter-form select { padding: 8px; margin-right: 10px; border: 1px solid #ccc; border-radius: 4px; box-sizing: border-box; }");
            out.println(".filter-form input[type=submit] { background-color: #808080; color: white; padding: 10px 15px; border: none; border-radius: 4px; cursor: pointer; font-size: 14px; }"); // ★ 絞り込みボタンをグレーに ★
            out.println(".filter-form input[type=submit]:hover { background-color: #696969; }"); // ★ ホバー時の色も変更 ★
            out.println(".search-result-count { margin-bottom: 10px; font-size: 16px; font-weight: bold; }"); // ★ 件数表示用のスタイル ★
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
            out.println("    background-color: transparent;"); // 背景を透明に
            out.println("    color: #007bff;"); // 文字色を青に
            out.println("    border: none;"); // 枠線をなくす
            out.println("    margin-right: 5px;");
            out.println("}");
            out.println(".edit-button:hover { text-decoration: underline; background-color: transparent; }"); // ホバー時の下線と透明な背景
            out.println(".register-button { color: #007bff; text-decoration: none; } /* 新規登録ボタンの文字色を青に、下線なし */");
            out.println(".register-button:hover { text-decoration: underline; } /* ホバー時に下線 */");
            out.println("</style>");
            out.println("</head>");
            out.println("<body>");
            out.println("<header><h1>得点管理システム</h1></header>");
            out.println("<div class=\"container\">");
            out.println("<div class=\"content\">");
            out.println("<div class=\"form-header\"><h2>学生一覧</h2></div>"); // ★ タイトル表示 ★
            out.println("<div style=\"text-align: right; margin-bottom: 10px;\">"); // ★ 新規登録ボタンの配置 ★
            out.println("<a href=\"studentcreate.jsp\" class=\"register-button\">新規登録</a>"); // ★ リンク先を修正 ★
            out.println("</div>");
            out.println("<div class=\"filter-form\">");
            out.println("<form method=\"get\" action=\"StudentListAction\">");
            out.println("<label for=\"entYear\">入学年度:</label>");
            out.println("<select id=\"entYear\" name=\"entYear\">");
            out.println("<option value=\"\"></option>");
            for (Integer year : allEntYears) { // ★ 取得した入学年度をループで表示 ★
                out.println("<option value=\"" + year + "\">" + year + "</option>");
            }
            out.println("</select>");
            out.println("<label for=\"classNum\">クラス:</label>");
            out.println("<select id=\"classNum\" name=\"classNum\">");
            out.println("<option value=\"\"></option>");
            for (String classNum : allClassNums) { // ★ 取得したクラスをループで表示 ★
                out.println("<option value=\"" + classNum + "\">" + classNum + "組</option>");
            }
            out.println("<input type=\"checkbox\" id=\"attend\" name=\"attend\" value=\"true\">"); // ★ チェックボックスに変更 ★
            out.println("<input type=\"hidden\" name=\"attend\" value=\"false\">"); // ★ チェックがない場合に false を送信 ★
            out.println("<label for=\"attend\">在学中</label>");
            out.println("<input type=\"submit\" value=\"絞り込み\">");
            out.println("</form>");
            out.println("</div>");

            // ★ 検索結果の件数を表示 ★
            out.println("<div class=\"search-result-count\">検索結果: " + (studentList != null ? studentList.size() : 0) + "件</div>");

            out.println("<table>");
            out.println("<thead><tr><th>学籍番号</th><th>氏名</th><th>入学年度</th><th>クラス</th><th>在学中</th><th>操作</th></tr></thead>"); // ★ ヘッダー名を変更 ★
            out.println("<tbody>");
            if (studentList != null) {
                for (Student student : studentList) {
                    String no = student.getNo();
                    out.println("<tr>");
                    out.println("<td>" + no + "</td>");
                    out.println("<td>" + student.getName() + "</td>");
                    out.println("<td>" + student.getEntYear() + "</td>");
                    out.println("<td>" + student.getClassNum() + "</td>");
                    out.println("<td>" + (student.isAttend() ? "〇" : "✕") + "</td>"); // ★ 表示はそのままでOK ★
                    out.println("<td>");
                    out.println("<a href=\"StudentEditFormAction?no=" + no + "\" class=\"edit-button\">変更</a>");
                    out.println("</td>");
                    out.println("</tr>");
                }
            } else {
                out.println("<tr><td colspan=\"6\">学生が見つかりませんでした。</td></tr>");
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
            e.printStackTrace(); // ★ スタックトレースを出力 ★
            out.println("<!DOCTYPE html>");
            out.println("<html lang=\"ja\">");
            out.println("<head><meta charset=\"UTF-8\"><title>エラー</title></head>");
            out.println("<body><h1>エラー</h1><p>学生一覧の取得に失敗しました。</p><pre>");
            e.printStackTrace(out); // ★ HTMLに出力 ★
            out.println("</pre></body></html>");
        }
    }
}