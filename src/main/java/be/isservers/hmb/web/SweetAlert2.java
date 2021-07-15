package be.isservers.hmb.web;

public class SweetAlert2 {
    public final static String SUCCESS = "success";
    public final static String ERROR = "error";
    public final static String WARNING = "warning";
    public final static String INFO = "info";
    public final static String QUESTION = "question";
    private final String type;
    private final String message;

    public SweetAlert2(String type, String message) {
        this.type = type;
        this.message = message;
    }

    public static String generate(String type, String message) {
        return "Swal.fire({ icon: '" + type + "', title: '" + message + "' })";
    }

    public String generate() {
        return SweetAlert2.generate(this.type, this.message);
    }
}
