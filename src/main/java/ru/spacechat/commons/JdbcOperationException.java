package ru.spacechat.commons;

public class JdbcOperationException extends OperationException {
    private static final long serialVersionUID = 1L;


    public JdbcOperationException(int errorCode, String message, Throwable cause) {
        super(errorCode, message, cause);
    }


    public JdbcOperationException(int errorCode, String message) {
        super(errorCode, message);
    }


    public JdbcOperationException(String message, Throwable cause) {
        super(message, cause);
    }


    public JdbcOperationException(String message) {
        super(message);
    }


    public JdbcOperationException(Throwable cause) {
        super(cause);
    }

}
