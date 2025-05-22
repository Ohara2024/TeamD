package scoremanager.main;

import java.io.IOException;
import java.util.List; // java.util.List をインポート

import javax.servlet.RequestDispatcher; // javax.servlet.RequestDispatcher をインポート
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession; // javax.servlet.http.HttpSession をインポート

import bean.School; // bean.School をインポート
import bean.Student; // bean.Student をインポート
import dao.StudentDao; // dao.StudentDao をインポート

// このサーブレットは、学生情報変更フォームを表示するためのものとする
@WebServlet(urlPatterns={"/StudentUpdateAction"}) // student_list.jsp のリンク先と合わせる
public class StudentUpdateAction extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        // response.setContentType("text/html; charset=UTF-8"); // JSP側で設定されるため通常不要

        HttpSession session = request.getSession();
        School school = (School) session.getAttribute("loginSchool"); // ログイン中の学校情報を取得

        // ログインチェック
        if (school == null) {
            System.out.println("StudentUpdateAction: ログイン情報が見つかりません。ログインページへリダイレクトします。");
            response.sendRedirect(request.getContextPath() + "/login"); // LoginServletのURLマッピングへリダイレクト
            return;
        }

        String no = request.getParameter("no"); // 学生番号を取得

        StudentDao studentDao = null;
        Student student = null;
        String error_message = null; // エラーメッセージ用

        try {
            // StudentDaoのコンストラクタ内でNamingExceptionがRuntimeExceptionとして処理されるため、
            // ここでは直接NamingExceptionをthrows/catchする必要がなくなりました。
            studentDao = new StudentDao();

            // StudentDao.get(String no, String schoolCd) を使用する
            // StudentDaoの実装で、このメソッドが適切に定義されていることを前提とします。
            student = studentDao.get(no, school.getCd());

            if (student == null) {
                // 指定された学生が見つからない場合
                error_message = "指定された学生が見つかりませんでした。学籍番号: " + no;
                request.setAttribute("error", error_message); // エラーメッセージをセット
                // エラー時は学生一覧に戻る
                request.getRequestDispatcher("/scoremanager/main/student_list.jsp").forward(request, response);
                return; // ここで処理を終了
            }

            request.setAttribute("student", student);

            // ドロップダウンリスト用のデータをDAOから取得
            // getAllEntYears() と getAllClassNums() も学校コードを引数にとるように実装していることを前提とします。
            List<Integer> entYears = studentDao.getAllEntYears(school.getCd());
            List<String> classNums = studentDao.getAllClassNums(school.getCd());

            request.setAttribute("entYears", entYears);
            request.setAttribute("classNums", classNums);

            // 学生変更画面（JSP）にフォワード
            RequestDispatcher dispatcher = request.getRequestDispatcher("/scoremanager/main/student_update.jsp");
            dispatcher.forward(request, response);

        } catch (RuntimeException e) {
            // StudentDaoのコンストラクタやDAO内のメソッドがスローするRuntimeExceptionを捕捉します。
            // 特にDataSourceのルックアップ失敗によるRuntimeExceptionをここで扱います。
            e.printStackTrace(); // 開発者向けにスタックトレースを出力

            // NamingExceptionが原因のRuntimeExceptionであるかを確認し、適切なメッセージを設定
            if (e.getCause() instanceof javax.naming.NamingException) {
                error_message = "データベース接続設定 (DataSource) の初期化に失敗しました。アプリケーション管理者に連絡してください。<br>原因: " + e.getCause().getMessage();
            } else {
                error_message = "学生情報の取得中に予期せぬエラーが発生しました。<br>" + e.getMessage();
            }
            request.setAttribute("error", error_message); // エラーメッセージをセット
            RequestDispatcher dispatcher = request.getRequestDispatcher("/error.jsp"); // エラーページにフォワード
            dispatcher.forward(request, response);
        } catch (Exception e) {
            // その他の予期せぬ例外（例: StudentDao内のSQLExceptionなど）を捕捉
            e.printStackTrace(); // 開発者向けにスタックトレースを出力
            error_message = "学生情報の取得中に予期せぬエラーが発生しました。<br>" + e.getMessage();
            request.setAttribute("error", error_message); // エラーメッセージをセット
            RequestDispatcher dispatcher = request.getRequestDispatcher("/error.jsp"); // エラーページにフォワード
            dispatcher.forward(request, response);
        }
    }

    // このサーブレットはフォーム表示に特化するため、POSTメソッドは StudentUpdateExecuteAction に任せる
    // もし誤ってPOSTリクエストが来たら、学生一覧にリダイレクトするなどの処理を行う
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // POSTリクエストをGETリクエストにリダイレクトして、学生一覧表示に戻す
        response.sendRedirect(request.getContextPath() + "/StudentListAction");
    }
}