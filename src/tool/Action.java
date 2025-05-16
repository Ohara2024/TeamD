package tool;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface Action {
    /**
     * 各アクション（例：StudentListAction）が実装すべきメソッド。
     *
     * @param request  HTTPリクエスト
     * @param response HTTPレスポンス
     * @return 遷移先のJSP名（例："student_list" → /WEB-INF/jsp/student_list.jsp）
     * @throws Exception 任意の例外
     */
    String execute(HttpServletRequest request, HttpServletResponse response) throws Exception;
}
