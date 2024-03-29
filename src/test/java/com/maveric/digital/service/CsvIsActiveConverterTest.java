package com.maveric.digital.service;

import static org.junit.jupiter.api.Assertions.assertFalse;

import com.opencsv.exceptions.CsvConstraintViolationException;
import com.opencsv.exceptions.CsvDataTypeMismatchException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@SpringBootTest
class CsvIsActiveConverterTest {
  @InjectMocks
  CsvIsActiveConverter converter=new CsvIsActiveConverter();
  @Test
  void shouldReturnFalseWhenInputIsNo() throws  CsvConstraintViolationException, CsvDataTypeMismatchException {
    assertFalse((Boolean) converter.convert("no"));
    assertFalse((Boolean) converter.convert("No"));
    assertFalse((Boolean) converter.convert("NO"));
    assertFalse((Boolean) converter.convert("nO"));
  }
}
