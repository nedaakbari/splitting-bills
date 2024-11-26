//package ir.splitwise.splitbills.controller;
//
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.ControllerAdvice;
//import org.springframework.web.bind.annotation.ExceptionHandler;
//
//@ControllerAdvice
//public class GlobalExceptionHandler {
//    @ExceptionHandler(Exception.class)
//    public ResponseEntity<String> hamdleExcpetion(Exception exception) {
//        return new ResponseEntity<>("an error happened: " + exception.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
//    }
//}
