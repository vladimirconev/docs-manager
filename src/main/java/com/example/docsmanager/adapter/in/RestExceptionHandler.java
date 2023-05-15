package com.example.docsmanager.adapter.in;

import static java.time.ZoneOffset.UTC;
import static java.util.Objects.requireNonNull;
import static org.springframework.http.MediaType.APPLICATION_JSON;

import com.example.docsmanager.adapter.in.dto.ErrorResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import java.text.SimpleDateFormat;
import java.time.Clock;
import java.time.Instant;
import java.util.*;
import org.springframework.boot.web.error.ErrorAttributeOptions;
import org.springframework.boot.web.servlet.error.DefaultErrorAttributes;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.HttpMediaTypeNotAcceptableException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;

@RestControllerAdvice(basePackageClasses = DocumentRestController.class)
public class RestExceptionHandler {

  private static final String DATE_TIME_FORMAT = "yyyy-MM-dd'T'HH:mm:SS.ssZ";
  private static final String PATH_KEY = "path";
  private static final String MESSAGE_KEY = "message";

  private final DefaultErrorAttributes errorAttributes;
  private final ObjectMapper objectMapper;

  public RestExceptionHandler(
      final DefaultErrorAttributes errorAttributes, final ObjectMapper objectMapper) {
    this.errorAttributes = errorAttributes;
    this.objectMapper = objectMapper;
  }

  protected ResponseEntity<String> handleException(
      final Exception exception,
      final HttpStatus status,
      final WebRequest request,
      final String message) {
    HttpHeaders httpHeaders = new HttpHeaders();
    httpHeaders.setContentType(APPLICATION_JSON);
    ErrorResponse errorResponse = buildErrorResponseDto(status, request, message);
    try {
      return new ResponseEntity<>(
          objectMapper.writeValueAsString(errorResponse), httpHeaders, status);
    } catch (JsonProcessingException jpe) {
      return new ResponseEntity<>(exception.getMessage(), httpHeaders, status);
    }
  }

  protected ErrorResponse buildErrorResponseDto(
      final HttpStatus httpStatus, final WebRequest webRequest, final String messageDetails) {
    ServletWebRequest servletRequest = (ServletWebRequest) webRequest;
    HttpServletRequest request = servletRequest.getNativeRequest(HttpServletRequest.class);

    requireNonNull(request, "Http servlet request should not be null.");

    Map<String, Object> errors =
        errorAttributes.getErrorAttributes(webRequest, ErrorAttributeOptions.defaults());
    final Throwable webRequestThrowable = errorAttributes.getError(webRequest);

    String exception =
        Optional.ofNullable(webRequestThrowable.getCause())
            .map(cause -> cause.getClass().getSimpleName())
            .orElse(webRequestThrowable.getClass().getSimpleName());
    String status = httpStatus.getReasonPhrase();
    String code = String.valueOf(httpStatus.value());
    if (httpStatus == HttpStatus.INTERNAL_SERVER_ERROR) {
      status = HttpStatus.SERVICE_UNAVAILABLE.getReasonPhrase();
      code = String.valueOf(HttpStatus.SERVICE_UNAVAILABLE);
    }

    String message =
        Optional.ofNullable(messageDetails)
            .orElse(Optional.ofNullable(errors.get(MESSAGE_KEY)).map(Object::toString).orElse(""));
    String path =
        Optional.ofNullable(errors.get(PATH_KEY))
            .map(Object::toString)
            .orElse(request.getRequestURI());
    Clock fixed = Clock.fixed(Instant.EPOCH, UTC);
    return new ErrorResponse(
        status,
        code,
        message,
        path,
        request.getMethod(),
        exception,
        new SimpleDateFormat(DATE_TIME_FORMAT).format(Date.from(fixed.instant())));
  }

  @ResponseBody
  @ExceptionHandler(IllegalStateException.class)
  protected ResponseEntity<String> handleIllegalStateException(
      final IllegalStateException ex, final WebRequest webRequest) {
    return handleException(ex, HttpStatus.CONFLICT, webRequest, null);
  }

  @ResponseBody
  @ExceptionHandler(IllegalArgumentException.class)
  protected ResponseEntity<String> handleIllegalArgumentException(
      final IllegalArgumentException ex, final WebRequest webRequest) {
    return handleException(ex, HttpStatus.BAD_REQUEST, webRequest, null);
  }

  @ResponseBody
  @ExceptionHandler(HttpMediaTypeNotAcceptableException.class)
  protected ResponseEntity<String> handleHttpMediaTypeNotAcceptable(
      final HttpMediaTypeNotAcceptableException ex, final WebRequest request) {
    return handleException(
        ex,
        HttpStatus.NOT_ACCEPTABLE,
        request,
        "Use acceptable media types %s.".formatted(ex.getSupportedMediaTypes()));
  }

  @ResponseBody
  @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
  protected ResponseEntity<String> handleHttpRequestMethodNotSupported(
      final HttpRequestMethodNotSupportedException ex, final WebRequest request) {
    return handleException(
        ex,
        HttpStatus.METHOD_NOT_ALLOWED,
        request,
        "Use supported HTTP methods %s.".formatted(ex.getSupportedHttpMethods()));
  }

  @ResponseBody
  @ExceptionHandler(NoSuchElementException.class)
  protected ResponseEntity<String> handleNoSuchElementException(
      final NoSuchElementException ex, final WebRequest webRequest) {
    return handleException(ex, HttpStatus.NOT_FOUND, webRequest, ex.getMessage());
  }

  @ResponseBody
  @ExceptionHandler(MethodArgumentNotValidException.class)
  protected ResponseEntity<String> handleNMethodArgumentNotValidException(
      final MethodArgumentNotValidException ex, final WebRequest webRequest) {
    return handleException(ex, HttpStatus.BAD_REQUEST, webRequest, ex.getMessage());
  }

  @ResponseBody
  @ExceptionHandler(Exception.class)
  protected ResponseEntity<String> handleGenericException(
      final Exception ex, final WebRequest webRequest) {
    return handleException(ex, HttpStatus.SERVICE_UNAVAILABLE, webRequest, ex.getMessage());
  }
}
