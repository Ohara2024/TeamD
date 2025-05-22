package scoremanager.main;

import java.io.IOException;
import java.time.Year; // Yearクラスをインポート
import java.util.List;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import bean.School;
import dao.StudentDao; // StudentDaoをインポート

@WebServlet(urlPatterns={"/StudentCreateAction"})
public class StudentCreateAction extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");

        HttpSession session = request.getSession();
        School school = (School) session.getAttribute("loginSchool");

        // ログインチェック
        if (school == null) {
            System.out.println("StudentCreateAction: ログイン情報が見つかりません。ログインページへリダイレクトします。");
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        try {
            StudentDao studentDao = new StudentDao();

            // 入学年度のリストを取得
            // getAllEntYearsメソッドが学校コードを引数に取る場合
            List<Integer> entYears = studentDao.getAllEntYears(school.getCd());
            // または、現在の年度から数年分を生成する場合（JSPのフォールバックロジックと同様）
            if (entYears == null || entYears.isEmpty()) {
                int currentYear = Year.now().getValue();
                for (int i = currentYear - 10; i <= currentYear + 5; i++) { // 例: 過去10年～未来5年
                    entYears.add(i);
                }
            }


            // クラス番号のリストを取得
            // getAllClassNumsメソッドが学校コードを引数に取る場合
            List<String> classNums = studentDao.getAllClassNums(school.getCd());
            // DAOから取得できない場合のフォールバック（JSPのダミーデータと同様）
            if (classNums == null || classNums.isEmpty()) {
                classNums = java.util.Arrays.asList("101", "102", "201", "202", "301", "302");
            }

            // リクエストスコープにデータをセット
            request.setAttribute("entYears", entYears);
            request.setAttribute("classNums", classNums);

            // 成功メッセージ（もしあれば）
            if (request.getParameter("success") != null) {
                request.setAttribute("successMessage", "学生を登録しました。");
            }


            // 学生登録画面（JSP）にフォワード
            RequestDispatcher dispatcher = request.getRequestDispatcher("/scoremanager/main/student_create.jsp");
            dispatcher.forward(request, response);

        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("error", "学生登録ページの初期化に失敗しました。詳細: " + e.getMessage());
            RequestDispatcher dispatcher = request.getRequestDispatcher("/error.jsp"); // WebContent直下のerror.jsp
            dispatcher.forward(request, response);
        }
    }

    // POSTメソッドでフォーム送信を受け取る場合は doPost も実装します。
    // しかし、今回はフォームの送信先が StudentCreateExecuteAction なので、
    // StudentCreateActionのdoPostは通常は不要です。
    // 必要であればバリデーションエラーなどで同じJSPに戻す場合に利用します。
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // 現在はフォーム送信先が StudentCreateExecuteAction なので、ここに直接登録処理は書きません。
        // ただし、入力値エラーなどでこのJSPに戻る場合は、doPost内で同様にentYears, classNumsを設定し、
        // 入力された値を保持する処理（request.setAttribute("oldNo", no); など）も記述する必要があります。
        doGet(request, response); // とりあえずdoGetを呼び出してJSPを再表示
    }
}