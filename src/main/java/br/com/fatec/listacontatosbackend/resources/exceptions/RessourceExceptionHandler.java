package br.com.fatec.listacontatosbackend.resources.exceptions;

import java.time.Instant;

import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ControllerAdvice
public class RessourceExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(RessourceExceptionHandler.class);

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<StandardError> entityNotFoundException(EntityNotFoundException e,
            HttpServletRequest request) {
        StandardError error = new StandardError();

        HttpStatus status = HttpStatus.NOT_FOUND;

        error.setStatus(status.value());
        error.setError("Resource not found");
        error.setMessage(e.getMessage());
        error.setTimeStamp(Instant.now());
        error.setPath(request.getRequestURI());

        return ResponseEntity.status(status).body(error);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ValidationErrors> validationErrorsException(MethodArgumentNotValidException e,
            HttpServletRequest request) {
        ValidationErrors error = new ValidationErrors();

        HttpStatus status = HttpStatus.UNPROCESSABLE_CONTENT;

        error.setStatus(status.value());
        error.setError("Validation Errors");
        error.setMessage(e.getMessage());
        error.setTimeStamp(Instant.now());
        error.setPath(request.getRequestURI());

        e.getBindingResult()
                .getFieldErrors()
                .forEach(err -> error.addError(err.getDefaultMessage()));

        return ResponseEntity.status(status).body(error);

    }

    @ExceptionHandler(DataAccessException.class)
    public ResponseEntity<StandardError> dataAccessException(
            DataAccessException e,
            HttpServletRequest request) {
        logger.error("Falha de persistência ao processar a requisição", e);

        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
        StandardError error = new StandardError();

        error.setStatus(status.value());
        error.setError("Internal Server Error");
        error.setMessage("Não foi possível concluir a operação.");
        error.setTimeStamp(Instant.now());
        error.setPath(request.getRequestURI());

        return ResponseEntity.status(status).body(error);
    }
}
