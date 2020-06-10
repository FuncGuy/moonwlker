package org.requirementsascode.moonwlker;

/**
 * Main entry point for the Moonwlker API.
 * Use this class to create builders for Jackson's ObjectMapper instances.
 * 
 * @author b_muth
 *
 */
public class Moonwlker {
  /**
   * Starts building Jackson's ObjectMapper instances.
   * 
   * @return a builder.
   */
  public static Json json() {
    return ObjectMapperBuilder.json();
  }
}
