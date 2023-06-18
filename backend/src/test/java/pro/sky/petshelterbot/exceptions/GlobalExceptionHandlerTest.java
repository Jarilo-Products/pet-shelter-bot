package pro.sky.petshelterbot.exceptions;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GlobalExceptionHandlerTest {

  @Mock
  private BindingResult bindingResult;

  private final GlobalExceptionHandler out = new GlobalExceptionHandler();

  @Test
  void handleValidationExceptions() {
    when(bindingResult.getAllErrors()).thenReturn(List.of(
        new FieldError("objectName", "fieldName1", "errorMessage1"),
        new FieldError("objectName", "fieldName2", "errorMessage2")));

    MethodArgumentNotValidException ex = new MethodArgumentNotValidException(null, bindingResult);

    Map<String, String> result = out.handleValidationExceptions(ex);

    Map<String, String> expectedErrors = new HashMap<>();
    expectedErrors.put("fieldName1", "errorMessage1");
    expectedErrors.put("fieldName2", "errorMessage2");

    assertEquals(expectedErrors, result);
  }
}