package tool;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

public class Tool {

    /**
     * ログイン状態の確認。未ログインならログイン画面にリダイレクト。
     * @param request リクエスト
     * @return ログイン済みかどうか
     */
    public static boolean checkLogin(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        return session != null && session.getAttribute("user") != null;
    }

    /**
     * リクエストパラメータの取得（トリム・null対応）
     * @param request リクエスト
     * @param name パラメータ名
     * @return パラメータの値（nullなら空文字）
     */
    public static String getParam(HttpServletRequest request, String name) {
        String param = request.getParameter(name);
        return param == null ? "" : param.trim();
    }

    /**
     * 現在の年度を取得（例：2025年なら "2025" を返す）
     * @return 年度（西暦）
     */
    public static String getCurrentYear() {
        return String.valueOf(java.time.Year.now().getValue());
    }
}
