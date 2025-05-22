package scoremanager.main;

import java.io.IOException;

import javax.servlet.RequestDispatcher; // RequestDispatcherをインポート
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import bean.School;
import bean.Student;
import dao.StudentDao;

@WebServlet(urlPatterns={"/StudentUpdateExecuteAction"}) // このURLマッピングであることを確認
public class StudentUpdateExecuteAction extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8"); // リクエストの文字エンコーディング設定

        HttpSession session = request.getSession();
        School school = (School) session.getAttribute("loginSchool");

        // ログインチェック
        if (school == null) {
            System.out.println("StudentUpdateExecuteAction: ログイン情報が見つかりません。ログインページへリダイレクトします。");
            response.sendRedirect(request.getContextPath() + "/login"); // LoginServletのURLマッピングへリダイレクト
            return;
        }

        // リクエストパラメータの取得
        String no = request.getParameter("no"); // 学生番号（hiddenフィールドから）
        String name = request.getParameter("name"); // 氏名
        String classNum = request.getParameter("classNum"); // クラス
        // 入学年度は読み取り専用のテキストフィールドまたはhiddenフィールドから取得
        String entYearParam = request.getParameter("entYear");
        int entYear = 0; // デフォルト値
        if (entYearParam != null && !entYearParam.isEmpty()) {
            try {
                entYear = Integer.parseInt(entYearParam);
            } catch (NumberFormatException e) {
                // 数値変換エラーが発生した場合の処理（例: エラーメッセージをセット）
                e.printStackTrace(); // 開発中のデバッグ用
                // エラー処理を統一するため、catchブロックの外でエラーページにフォワードする
            }
        }
        // 在籍チェックボックスの値（"true"またはnull）。チェックされていればtrue、そうでなければfalse
        boolean attend = request.getParameter("attend") != null;

        StudentDao sDao = new StudentDao();
        Student student = new Student(); // 更新する学生オブジェクト

        // 学生オブジェクトに値をセット
        student.setNo(no);
        student.setName(name);
        student.setEntYear(entYear); // int型の入学年度をセット
        student.setClassNum(classNum);
        student.setAttend(attend);
        student.setSchool(school); // ログイン中の学校情報をセット

        try {
            // データベース更新処理を実行
            // StudentDaoのsaveメソッドがbooleanを返すことを前提
            boolean isUpdated = sDao.save(student);

            if (isUpdated) {
                // ★学生情報の更新が成功した場合★
                System.out.println("StudentUpdateExecuteAction: 学生情報 (" + student.getNo() + ") の更新に成功しました。");
                // 更新完了ページにフォワード
                RequestDispatcher dispatcher = request.getRequestDispatcher("/scoremanager/main/student_update_done.jsp");
                dispatcher.forward(request, response);
            } else {
                // ★学生情報の更新が失敗した場合（DB操作自体は成功したが、レコードが見つからないなど）★
                System.out.println("StudentUpdateExecuteAction: 学生情報 (" + student.getNo() + ") の更新に失敗しました（DB操作は完了）。");
                request.setAttribute("error", "学生情報の更新に失敗しました。対象の学生が見つからないか、データが不正です。");
                // 失敗時は元の更新フォームに戻るため、必要なデータを再度セット
                request.setAttribute("student", student); // ユーザーが入力した情報を保持
                request.setAttribute("entYears", sDao.getAllEntYears(school.getCd())); // 入学年度の選択肢
                request.setAttribute("classNums", sDao.getAllClassNums(school.getCd())); // クラスの選択肢

                RequestDispatcher dispatcher = request.getRequestDispatcher("/scoremanager/main/student_update.jsp");
                dispatcher.forward(request, response);
            }
        } catch (Exception e) {
            // ★データベースアクセスなど、予期せぬエラーが発生した場合★
            e.printStackTrace(); // サーバーログにスタックトレースを出力
            System.err.println("StudentUpdateExecuteAction: 学生情報の更新中に予期せぬエラーが発生しました: " + e.getMessage());
            request.setAttribute("error", "学生情報の更新中にエラーが発生しました。詳細: " + e.getMessage());

            // エラー時も元の更新フォームに戻るため、必要なデータを再度セット
            request.setAttribute("student", student); // ユーザーが入力した情報を保持
            try {
                request.setAttribute("entYears", sDao.getAllEntYears(school.getCd()));
                request.setAttribute("classNums", sDao.getAllClassNums(school.getCd()));
            } catch (Exception e2) {
                e2.printStackTrace(); // 選択肢の取得自体に失敗した場合
                System.err.println("StudentUpdateExecuteAction: 選択肢の取得中にエラーが発生しました: " + e2.getMessage());
                // 最悪の場合、エラーページに直接遷移する
                request.setAttribute("error", "学生情報の更新中に致命的なエラーが発生しました。");
                RequestDispatcher dispatcher = request.getRequestDispatcher("/error.jsp"); // または適切なエラーページ
                dispatcher.forward(request, response);
                return;
            }

            RequestDispatcher dispatcher = request.getRequestDispatcher("/scoremanager/main/student_update.jsp");
            dispatcher.forward(request, response);
        }
    }
}