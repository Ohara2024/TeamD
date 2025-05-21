package scoremanager.main; // あなたのパッケージ名に合わせてください

import java.io.IOException;
import java.sql.SQLException; // SQL関連の例外をハンドリング

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import bean.Score;
import dao.ScoreDAO;

@WebServlet("/ScoreRegisterServlet") // test_regist.jspのform actionと一致させる
public class TestRegistAction extends HttpServlet {

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // 文字化け対策
        request.setCharacterEncoding("UTF-8");

        String message = ""; // 結果画面に渡すメッセージ
        Score processedScore = null; // 処理した成績情報（結果画面で表示するため）

        try {
            // フォームからパラメータを取得
            String studentNo = request.getParameter("studentNo");
            String subjectCd = request.getParameter("subjectCd");
            String schoolCd = request.getParameter("schoolCd");
            String pointStr = request.getParameter("point");

            // 点数を数値に変換
            int point = Integer.parseInt(pointStr);

            // ここでは登録処理（INSERT）を想定
            // test_regist.jspには更新用のIDがないため、現在のところ新規登録のみ
            Score newScore = new Score(); // Score Beanをインスタンス化
            newScore.setStudentNo(studentNo);
            newScore.setSubjectCd(subjectCd);
            newScore.setSchoolCd(schoolCd);
            newScore.setPoint(point);
            // 他にも必要なフィールドがあればセット

            ScoreDAO scoreDAO = new ScoreDAO(); // ScoreDAOのインスタンス化
            boolean success = scoreDAO.insertScore(newScore); // DAOでデータベースに登録

            if (success) {
                message = "成績情報を登録しました。";
                processedScore = newScore; // 登録したデータを結果画面に渡す
            } else {
                message = "成績情報の登録に失敗しました。";
            }

        } catch (NumberFormatException e) {
            message = "点数の入力形式が不正です。";
            e.printStackTrace();
        } catch (SQLException e) { // DAO内でSQLExceptionがスローされる想定
            message = "データベース処理中にエラーが発生しました：" + e.getMessage();
            e.printStackTrace();
        } catch (Exception e) { // その他の予期せぬエラー
            message = "処理中に予期せぬエラーが発生しました：" + e.getMessage();
            e.printStackTrace();
        }

        // 処理結果をJSPに渡すための属性をセット
        request.setAttribute("message", message);
        request.setAttribute("processedGrade", processedScore); // test_regist_done.jspが'processedGrade'を期待するため、この名前でセット

        // test_regist_done.jsp へフォワード
        // test_regist_done.jsp が /WEB-INF/views/ の中にある場合を想定
        request.getRequestDispatcher("/scoremanager/main/test_regist_done.jsp").forward(request, response);
    }
}