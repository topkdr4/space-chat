package ru.spacechat.config;


import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.WebRequest;
import ru.spacechat.commons.OperationException;





@Slf4j
@ControllerAdvice(annotations = RestController.class)
public class RestApiErrorHandler {


    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> handleConflict(Exception ex, WebRequest request) {
        if (ex.getClass().getName().contains("ClientAbortException"))
            return null;


        ErrorResp resp = new ErrorResp(
                OperationException.getErrorCode(ex),
                OperationException.getErrorText(ex));


        log.error("request error", ex);

        return new ResponseEntity<>(resp, HttpStatus.CONFLICT);
    }


    @Getter
    protected static class ErrorResp {
        protected final int errorCode;
        protected final String errorText;


        public ErrorResp(int errorCode, String errorText) {
            this.errorCode = errorCode;
            this.errorText = errorText;
        }
    }


}
