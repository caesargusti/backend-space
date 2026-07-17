package com.example.wallet.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.Map;

// TODO: Tambahkan anotasi pada class ini agar exception bisa diintercept secara global untuk semua RestController.
public class GlobalExceptionHandler {

    // TODO: Tangani WalletNotFoundException
    // Requirement: Return HTTP 404 (Not Found) dengan ErrorResponse: code="WALLET_NOT_FOUND", message=pesan exception
    public ResponseEntity<ErrorResponse> handleWalletNotFound(WalletNotFoundException ex) { return null; }

    // TODO: Tangani InsufficientFundsException
    // Requirement: Return HTTP 422 (Unprocessable Entity) dengan ErrorResponse: code="INSUFFICIENT_FUNDS", message=pesan exception
    public ResponseEntity<ErrorResponse> handleInsufficientFunds(InsufficientFundsException ex) {
        return null;
    }

    // TODO: Tangani WalletBlockedException
    // Requirement: Return HTTP 422 (Unprocessable Entity) dengan ErrorResponse: code="WALLET_BLOCKED", message=pesan exception
    public ResponseEntity<ErrorResponse> handleWalletBlocked(WalletBlockedException ex) {
        return null;
    }

    // TODO: Tangani IllegalArgumentException
    // Requirement: Return HTTP 400 (Bad Request) dengan ErrorResponse: code="INVALID_ARGUMENT", message=pesan exception
    public ResponseEntity<ErrorResponse> handleIllegalArgument(IllegalArgumentException ex) {
        return null;
    }

    // TODO: Tangani MethodArgumentNotValidException (dilempar saat bean validation gagal)
    // Requirement: Return HTTP 400 (Bad Request).
    // Response body sebaiknya berupa map atau list yang berisi field error hasil validasi.
    // Contoh: Map fieldName -> errorMessage.
    public ResponseEntity<Map<String, String>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        return null;
    }
}
