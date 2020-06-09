package org.requirementsascode.moonwlker;

import org.requirementsascode.moonwlker.ObjectMapperBuilder.UntypedJson;

public class Moonwlker {
  public static UntypedJson json() {
    return ObjectMapperBuilder.untypedJson();
  }

  public static ObjectMapperBuilder json(String typePropertyName) {
    return ObjectMapperBuilder.typedJson(typePropertyName);
  }
}
