package ru.spacechat.commons;

public class OperationException extends RuntimeException {
    private static final long serialVersionUID = 1L;
    private static final int DEFAULT_ERROR_CODE = -1;
    private final int errorCode;


    public OperationException(int errorCode, String message, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
    }


    public OperationException(int errorCode, String message) {
        this(errorCode, message, null);
    }


    public OperationException(String message, Throwable cause) {
        this(0, message, cause);
    }


    public OperationException(String message) {
        this(0, message, null);
    }


    public OperationException(Throwable cause) {
        this(0, getErrorText(cause), cause);
    }


    /**
     * Получить текст ошибки
     */
    public static String getErrorText(final Throwable error) {
        Throwable current = error;

        while (true) {
            String message = current.getMessage();
            if (!Util.isEmpty(message))
                return message;

            Throwable cause = current.getCause();

            if (cause == null)
                return error.getClass().getName();

            current = cause;
        }

    }


    /**
     * Получить код ошибки
     */
    public static int getErrorCode(final Throwable error) {
        Throwable current = error;

        while (true) {
            if (current instanceof OperationException)
                return ((OperationException) current).getErrorCode();

            Throwable cause = current.getCause();

            if (cause == null)
                return DEFAULT_ERROR_CODE;

            current = cause;
        }

    }


    public int getErrorCode() {
        return errorCode;
    }


}
