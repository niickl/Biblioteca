package com.example.Biblioteca.Exception;

import com.example.Biblioteca.dto.ErroPadraoDTO;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;

@RestControllerAdvice
public class ManipuladorGlobalDeExcecoesGlobal {

    //Tratamento de 404 (Not Found)
    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ErroPadraoDTO> tratarErro404(EntityNotFoundException e, HttpServletRequest request){

        //404 not found
        HttpStatus status = HttpStatus.NOT_FOUND;

        //DTO bonitao
        ErroPadraoDTO erro = new ErroPadraoDTO(
                Instant.now(),
                status.value(),
                "Recurso nao encontrado",
                e.getMessage(),
                request.getRequestURI()
        );

        return ResponseEntity.status(status).body(erro);
    }

    //Tratamento de 400 (Bad Request) - Regras de negócio violadas (ex: enviar campo em branco)
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErroPadraoDTO> tratarErro400(IllegalArgumentException e, HttpServletRequest request){

        //400 bad request
        HttpStatus status = HttpStatus.BAD_REQUEST;

        //DTO bonitao
        ErroPadraoDTO erro = new ErroPadraoDTO(
                Instant.now(),
                status.value(),
                "Requisição inválida",
                e.getMessage(),
                request.getRequestURI()
        );

        return ResponseEntity.status(status).body(erro);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErroPadraoDTO> tratarErro500(Exception e, HttpServletRequest request){

        e.printStackTrace();

        //500 internal server error
        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;

        //DTO bonitao
        ErroPadraoDTO erro = new ErroPadraoDTO(
                Instant.now(),
                status.value(),
                "Erro interno do servidor",
                "Ocorreu um erro inesperado. Tente novamente mais tarde.", // Mensagem genérica para o usuário
                request.getRequestURI()
        );

        return ResponseEntity.status(status).body(erro);
    }


}
