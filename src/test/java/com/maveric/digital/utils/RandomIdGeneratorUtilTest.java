package com.maveric.digital.utils;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith({SpringExtension.class})
@SpringBootTest
class RandomIdGeneratorUtilTest {

  @Test
  void testGeneratePKId() {
    String text = "test";
    String result = RandomIdGeneratorUtil.generatePKId(text);

    assertNotNull(result, "Generated ID should not be null");
    assertTrue(result.startsWith(text), "Generated ID should start with input text");
    assertTrue(result.length() > text.length(), "Generated ID should be longer than the input text");
    assertTrue(result.substring(text.length()).matches("\\d{13}"), "Generated ID should end with 13 digits representing current time in millis");
  }
}
