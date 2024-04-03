package dog.rescue.error;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.NoSuchElementException;

import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestControllerAdvice
@Slf4j
public class GlobalControllerErrorHandler {
	
	private enum LogStatus {
		STACK_TRACE, MESSAGE_ONLY
	}
	
	@Data
	@NoArgsConstructor
	private class ExceptionMessage {
		private String message;
		private String statusReason;
		private int statusCode;
		private String timestamp;
		private String uri;
	}
	
	/*
	 * Exception handler for all general Exceptions
	 */
	@ExceptionHandler(Exception.class)
	@ResponseStatus(code = HttpStatus.INTERNAL_SERVER_ERROR)
	public ExceptionMessage handleNoSuchElementException(Exception e, WebRequest webRequest) {
		return buildExceptionMessage(e, HttpStatus.INTERNAL_SERVER_ERROR, webRequest, LogStatus.STACK_TRACE);
	}

	/*
	 * Exception handler for NoSuchElementException
	 */
	@ExceptionHandler(NoSuchElementException.class)
	@ResponseStatus(code = HttpStatus.NOT_FOUND)
	public ExceptionMessage handleNoSuchElementException(NoSuchElementException e, WebRequest webRequest) {
		return buildExceptionMessage(e, HttpStatus.NOT_FOUND, webRequest, LogStatus.MESSAGE_ONLY);
	}

	/*
	 * Exception handler for DuplicateKeyException
	 */
	@ExceptionHandler(DuplicateKeyException.class)
	@ResponseStatus(code = HttpStatus.CONFLICT)
	public ExceptionMessage handleDuplicateKeyException(DuplicateKeyException e, WebRequest webRequest) {
		return buildExceptionMessage(e, HttpStatus.CONFLICT, webRequest, LogStatus.MESSAGE_ONLY);
	}

	/*
	 * Exception handler for UnsupportedOperationException
	 */
	@ExceptionHandler(UnsupportedOperationException.class)
	@ResponseStatus(code = HttpStatus.METHOD_NOT_ALLOWED)
	public ExceptionMessage handleUnsupportedOperationException(UnsupportedOperationException e, WebRequest webRequest) {
		return buildExceptionMessage(e, HttpStatus.METHOD_NOT_ALLOWED, webRequest, LogStatus.MESSAGE_ONLY);
	}

	private ExceptionMessage buildExceptionMessage(Exception e, HttpStatus status, WebRequest webRequest,
			LogStatus logStatus) {
		String message = e.toString();
		String statusReason = status.getReasonPhrase();
		int statusCode = status.value();
		String uri = null;
		String timestamp = ZonedDateTime.now().format(DateTimeFormatter.RFC_1123_DATE_TIME);
		
		if (webRequest instanceof ServletWebRequest swr) {
			uri = swr.getRequest().getRequestURI();
		}
		
		if (logStatus == LogStatus.MESSAGE_ONLY) {
			log.error("Exception: {}", e.toString());
		}
		else {
			log.error("Exception: ", e);
		}
		
		ExceptionMessage em = new ExceptionMessage();
		em.setMessage(message);
		em.setStatusCode(statusCode);
		em.setStatusReason(statusReason);
		em.setTimestamp(timestamp);
		em.setUri(uri);
		
		return em;
	}

}
