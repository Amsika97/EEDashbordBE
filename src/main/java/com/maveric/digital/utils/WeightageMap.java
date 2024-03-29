package com.maveric.digital.utils;

import java.util.HashMap;
import java.util.Map;

import static com.maveric.digital.utils.ServiceConstants.*;
import static com.maveric.digital.utils.ServiceConstants.NA_VALUE;

public class WeightageMap {
  public static final Map<String, Double> WEIGHTAGE_MAP = new HashMap<>();

  static {
    WEIGHTAGE_MAP.put(VERY_GOOD, VERY_GOOD_VALUE);
    WEIGHTAGE_MAP.put(GOOD, GOOD_VALUE);
    WEIGHTAGE_MAP.put(ACCEPTABLE,ACCEPTABLE_VALUE);
    WEIGHTAGE_MAP.put(POOR, POOR_VALUE);
    WEIGHTAGE_MAP.put(VERY_POOR,VERY_POOR_VALUE);
    WEIGHTAGE_MAP.put(NA, NA_VALUE);

  }
}
