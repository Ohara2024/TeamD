package bean; // あなたのパッケージ名に合わせてください

public class Score {
    // フィールドはtest_regist.jspのフォーム名に合わせてください
    private String studentNo;
    private String subjectCd;
    private String schoolCd;
    private int point;
    // 必要なら、自動採番されるIDなどを追加
    // private int id;

    // コンストラクタ（引数なしのデフォルトコンストラクタは必須）
    public Score() {}

    // 全フィールドを持つコンストラクタ（オプション）
    public Score(String studentNo, String subjectCd, String schoolCd, int point) {
        this.studentNo = studentNo;
        this.subjectCd = subjectCd;
        this.schoolCd = schoolCd;
        this.point = point;
    }

    // GetterとSetter
    public String getStudentNo() { return studentNo; }
    public void setStudentNo(String studentNo) { this.studentNo = studentNo; }
    public String getSubjectCd() { return subjectCd; }
    public void setSubjectCd(String subjectCd) { this.subjectCd = subjectCd; }
    public String getSchoolCd() { return schoolCd; }
    public void setSchoolCd(String schoolCd) { this.schoolCd = schoolCd; }
    public int getPoint() { return point; }
    public void setPoint(int point) { this.point = point; }
    
    // IDのGetter/Setter (もしあれば)
    // public int getId() { return id; }
    // public void setId(int id) { this.id = id; }
}