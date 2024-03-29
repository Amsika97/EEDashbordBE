package com.maveric.digital.service;

import static org.junit.Assert.assertNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.opencsv.exceptions.CsvConstraintViolationException;
import com.opencsv.exceptions.CsvDataTypeMismatchException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@SpringBootTest
class LocalDateConverterTest {
  @InjectMocks
  private LocalDateConverter localDateConverter;

  @Test
  void convertValidDateString() throws CsvConstraintViolationException, CsvDataTypeMismatchException {
    String validDate = "01-11-2016";
    LocalDate expectedDate = LocalDate.parse(validDate, DateTimeFormatter.ofPattern("dd-MM-yyyy", Locale.ENGLISH));
    Object result = localDateConverter.convert(validDate);
    assertNotNull(result);
    assertTrue(result instanceof LocalDate);
    assertEquals(expectedDate, result);
  }
  @Test
  void convertBlankOrNullString() throws CsvConstraintViolationException, CsvDataTypeMismatchException {
    assertNull(localDateConverter.convert(""));
    assertNull(localDateConverter.convert(null));
  }

}
