package ru.spacechat.commons;

import java.sql.SQLException;





public class JdbcErrors {


    private static final String SQLERROR_PREFIX = "ERROR: ";
    private static final String SQLSTATE_PREFIX = "ER";
    private JdbcErrors() {
    }


    /**
     * Обработать системную ошибку
     */
    public static RuntimeException rethrow(Throwable e) {
        if (e instanceof RuntimeException)
            return (RuntimeException) e;

        if (e instanceof SQLException)
            return fromSQLException((SQLException) e);

        return new JdbcOperationException(e);
    }


    private static JdbcOperationException fromSQLException(SQLException e) {
        String state = e.getSQLState();

        if (state == null)
            return new JdbcOperationException(e);

        String errorText = firstLine(e.getMessage());

        if (errorText.startsWith(SQLERROR_PREFIX)) {
            errorText = errorText.substring(SQLERROR_PREFIX.length());
        }

        int errorCode = -1;

        if (state.startsWith(SQLSTATE_PREFIX)) {
            String temp = state.substring(SQLSTATE_PREFIX.length());
            try {
                errorCode = Integer.parseInt(temp);
            } catch (Exception numberFormat) {
                errorCode = -1;
            }
        }

        return new JdbcOperationException(errorCode, errorText);
    }


    private static String firstLine(String text) {
        int eol = text.indexOf('\n');

        if (eol < 0)
            return text;

        if (eol > 0 && text.charAt(eol - 1) == '\r')
            eol--;

        return text.substring(0, eol);
    }
}
