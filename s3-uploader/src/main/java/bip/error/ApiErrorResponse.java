package bip.error;

/**
 * API Error response
 */
public class ApiErrorResponse {

    private int status;
    private int code;
    private String message;

    public ApiErrorResponse(int status, int code, String message) {
        this.status = status;
        this.code = code;
        this.message = message;
    }

    public int getStatus() {
        return status;
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    @Override
    public String toString() {
        // TODO: Maybe using JSON object instead of string like this here.
        return "ApiErrorResponse {" +
                "status=" + status +
                ", code=" + code +
                ", message=" + message +
                '}';
    }
}