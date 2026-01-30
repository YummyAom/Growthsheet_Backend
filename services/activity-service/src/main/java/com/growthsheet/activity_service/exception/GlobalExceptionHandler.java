// // package com.growthsheet.user_service.exception;

// import java.util.HashMap;
// import java.util.Map;

// import jakarta.servlet.http.HttpServletRequest;

// import org.springframework.http.ResponseEntity;
// import org.springframework.web.bind.MethodArgumentNotValidException;
// import org.springframework.web.bind.annotation.ExceptionHandler;
// import org.springframework.web.bind.annotation.RestControllerAdvice;
// import org.springframework.web.server.ResponseStatusException;

// @RestControllerAdvice
// public class GlobalExceptionHandler {

//     // ===== Validation Error =====
//     @ExceptionHandler(MethodArgumentNotValidException.class)
//     public ResponseEntity<Map<String, Object>> handleValidationErrors(
//             MethodArgumentNotValidException ex) {

//         Map<String, String> fieldErrors = new HashMap<>();

//         ex.getBindingResult()
//           .getFieldErrors()
//           .forEach(error ->
//               fieldErrors.put(error.getField(), error.getDefaultMessage())
//           );

//         Map<String, Object> body = new HashMap<>();
//         body.put("status", "error");
//         body.put("code", 400);
//         body.put("errors", fieldErrors);

//         return ResponseEntity
//                 .badRequest()
//                 .body(body);
//     }

//     // ===== Auth / Business Error =====
//     @ExceptionHandler(ResponseStatusException.class)
//     public ResponseEntity<Map<String, Object>> handleResponseStatusException(
//             ResponseStatusException ex,
//             HttpServletRequest request) {

//         Map<String, Object> body = new HashMap<>();
//         body.put("status", "error");
//         body.put("code", ex.getStatusCode().value());
//         body.put("message", ex.getReason());
//         body.put("path", request.getRequestURI());

//         return ResponseEntity
//                 .status(ex.getStatusCode())
//                 .body(body);
//     }
// }

