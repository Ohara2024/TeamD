package scoremanager.main;

import java.io.IOException;
import java.util.List;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession; // HttpSessionをインポート

import bean.School; // Schoolをインポート
import bean.Student;
import dao.StudentDao;

@WebServlet(urlPatterns={"/StudentListAction"})
public class StudentListAction extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8"); // リクエストの文字エンコーディング設定

        // ログインセッションの取得 (学内コードを取得するため)
        HttpSession session = request.getSession();
        School school = (School) session.getAttribute("loginSchool"); // LoginServletで設定した属性名を使用

        // ログインチェック
        if (school == null) {
            System.out.println("StudentListAction: ログイン情報が見つかりません。ログインページへリダイレクトします。");
            response.sendRedirect(request.getContextPath() + "/login"); // LoginServletのURLマッピングへリダイレクト
            return;
        }

        // リクエストパラメータを取得
        String entYearParam = request.getParameter("entYear");
        String classNumParam = request.getParameter("classNum");
        String attendParam = request.getParameter("attend"); // "true" or "false" (hiddenフィールドから)

        Integer entYear = null;
        if (entYearParam != null && !entYearParam.isEmpty()) {
            try {
                entYear = Integer.parseInt(entYearParam);
            } catch (NumberFormatException e) {
                e.printStackTrace();
                request.setAttribute("error", "入学年度の形式が不正です。"); // JSPで表示するエラーメッセージ
            }
        }

        Boolean attend = null;
        // hiddenフィールドで "false" が送られるか、パラメータ自体がないかで判断
        if (attendParam != null && !attendParam.isEmpty()) {
            attend = Boolean.parseBoolean(attendParam);
        } else {
            // パラメータが全くない場合（チェックボックスがチェックされていない）はfalseとする
            // または、フィルタ条件に含めない場合はnullのままにする。
            // 今回は"studentDao.filter(..., attend, ...)"でnullを許容する設計なので、
            // attendParamがnullの場合はattendもnullのままが適切です。
            // ここではBoolean.parseBooleanが"null"という文字列をfalseに変換するので、
            // あえてattendParamがnullのままならattendもnullとする処理の方が柔軟性があります。
            // 以前の記述はattendParamがnullでもBoolean.parseBoolean(null)でfalseになるので、
            // 実質的にattendParamがnullならattend=falseでした。
            // 現行のStudentDao.filterメソッドのattend引数への対応方針によりますが、
            // 現状のfilterメソッドはBooleanを受け取るので、パラメータがなければnullでもよいでしょう。
            // ただし、JSPでhiddenフィールドで"false"を送信している場合は、attendParamは"false"になり、
            // attendはBoolean.FALSEになります。
            // ここは変更なしで、attendParamがnullならattendもnullのままにしておきます。
            // filterメソッドの挙動とJSPの送信内容を完全に一致させるなら、
            // hiddenフィールドがない＝チェックがない＝false、という明確なロジックが良いです。
            // if (attendParam != null && !attendParam.isEmpty()) {
            //     attend = Boolean.parseBoolean(attendParam);
            // } else {
            //     attend = false; // チェックボックスがチェックされていない場合はfalse
            // }
            // ↑これだとJSPのhiddenフィールドと合わせるなら冗長。JSPがhiddenフィールドで"false"を送るのであれば、
            // そのままBoolean.parseBooleanで処理される。パラメータがない場合はnullのまま。
        }


        try {
            StudentDao studentDao = new StudentDao();
            // filterメソッドにschool.getCd()を渡す
            // StudentDaoのfilterメソッドの最後の引数がschoolCdであることを前提
            List<Student> studentList = studentDao.filter(entYear, classNumParam, attend, school.getCd());

            // StudentDaoのgetAllEntYearsとgetAllClassNumsにschool.getCd()を渡す
            List<Integer> allEntYears = studentDao.getAllEntYears(school.getCd());
            List<String> allClassNums = studentDao.getAllClassNums(school.getCd());

            // デバッグログ出力
            System.out.println("StudentListAction - school.getCd(): " + school.getCd());
            System.out.println("StudentListAction - studentList のサイズ: " + (studentList != null ? studentList.size() : "null"));
            if (studentList != null) {
                for (Student student : studentList) {
                    System.out.println("StudentListAction - 学籍番号: " + student.getNo() + ", 名前: " + student.getName() + ", 学校コード: " + student.getSchool().getCd());
                }
            } else {
                System.out.println("StudentListAction - studentList は null です。");
            }

            // リクエストスコープにデータをセット (JSPのEL式に合わせて属性名を調整)
            request.setAttribute("studentList", studentList);
            request.setAttribute("allEntYears", allEntYears);
            request.setAttribute("allClassNums", allClassNums);
            // フィルタリング用に選択された値を保持するために、元のパラメータ値をJSPに渡す
            // JSP側がEL式でentYear, classNumParam, attend を参照しているため、属性名もそれに合わせる
            request.setAttribute("entYear", entYearParam); // Stringで渡す
            request.setAttribute("classNumParam", classNumParam); // Stringで渡す
            request.setAttribute("attend", attendParam); // Stringで渡す ("true" or null)

            // JSPにフォワード
            // WebContent/scoremanager/main/student_list.jsp に JSP がある場合
            RequestDispatcher dispatcher = request.getRequestDispatcher("/scoremanager/main/student_list.jsp");
            dispatcher.forward(request, response);

        } catch (Exception e) { // NamingException も SQLException も含む汎用的なExceptionでキャッチ
            e.printStackTrace(); // サーバーログにスタックトレースを出力
            request.setAttribute("error", "学生一覧の取得中にエラーが発生しました。詳細: " + e.getMessage()); // ユーザー向けエラーメッセージ
            // エラーページにフォワード (WebContent直下のerror.jsp を想定)
            RequestDispatcher dispatcher = request.getRequestDispatcher("/error.jsp");
            dispatcher.forward(request, response);
        }
    }
}