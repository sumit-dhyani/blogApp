package com.example.blog.blogapp.exceptions;

import com.example.blog.blogapp.model.ResponseMsg;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.request.WebRequest;

import java.time.LocalDateTime;

import static org.hibernate.tool.schema.SchemaToolingLogging.LOGGER;

@ControllerAdvice
public class CustomExceptionHandling {
    @ExceptionHandler(value = ResourceNotFoundException.class)
    public ResponseEntity<ResponseMsg> handleNotFoundException(ResourceNotFoundException ex, WebRequest request) {
        ResponseMsg response= new ResponseMsg(LocalDateTime.now(),ex.getMessage(),
                request.getDescription(false));
        return new ResponseEntity<>(response,HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(value=UnauthorizedException.class)
    public ResponseEntity<?>  handleUnauthorizedException(UnauthorizedException exception,WebRequest request){
        ResponseMsg response= new ResponseMsg(LocalDateTime.now(),
                exception.getMessage(),request.getDescription(false));
        return new ResponseEntity<>(response,HttpStatus.UNAUTHORIZED);
    }
}
