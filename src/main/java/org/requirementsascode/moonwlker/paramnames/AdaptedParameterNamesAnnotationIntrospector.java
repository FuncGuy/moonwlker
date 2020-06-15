package org.requirementsascode.moonwlker.paramnames;

import java.lang.reflect.MalformedParametersException;
import java.lang.reflect.Parameter;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.databind.AnnotationIntrospector;
import com.fasterxml.jackson.databind.cfg.MapperConfig;
import com.fasterxml.jackson.databind.introspect.*;

/**
 * Introspector that uses parameter name information provided by the Java
 * Reflection API additions in Java 8 to determine the parameter name for
 * methods and constructors.
 * 
 * NOTE from b_muth: This has been adapted from the official ParamNamesModule to
 * be able to deal with all argument constructors without JsonCreator
 * annotation.
 * 
 * @author Lovro Pandzic
 * @author b_muth
 * @see AnnotationIntrospector
 * @see Parameter
 */
public class AdaptedParameterNamesAnnotationIntrospector extends NopAnnotationIntrospector {
  private static final long serialVersionUID = 1L;

  private final ParameterExtractor parameterExtractor;

  public AdaptedParameterNamesAnnotationIntrospector(ParameterExtractor parameterExtractor) {
    this.parameterExtractor = parameterExtractor;
  }

  @Override
  public String findImplicitPropertyName(AnnotatedMember m) {
    if (m instanceof AnnotatedParameter) {
      return findParameterName((AnnotatedParameter) m);
    }
    return null;
  }

  private String findParameterName(AnnotatedParameter annotatedParameter) {

    Parameter[] params;
    try {
      params = getParameters(annotatedParameter.getOwner());
    } catch (MalformedParametersException e) {
      return null;
    }

    Parameter p = params[annotatedParameter.getIndex()];
    return p.isNamePresent() ? p.getName() : null;
  }

  private Parameter[] getParameters(AnnotatedWithParams owner) {
    if (owner instanceof AnnotatedConstructor) {
      return parameterExtractor.getParameters(((AnnotatedConstructor) owner).getAnnotated());
    }
    if (owner instanceof AnnotatedMethod) {
      return parameterExtractor.getParameters(((AnnotatedMethod) owner).getAnnotated());
    }

    return null;
  }

  /*
   * /********************************************************** /* Creator
   * information handling
   * /**********************************************************
   */

  @Override
  public JsonCreator.Mode findCreatorAnnotation(MapperConfig<?> config, Annotated a) {
    JsonCreator ann = _findAnnotation(a, JsonCreator.class);
    if (ann == null) {
      return JsonCreator.Mode.PROPERTIES;
    }
    return null;
  }
  
  @Override
  @Deprecated // remove AFTER 2.9
  public JsonCreator.Mode findCreatorBinding(Annotated a) {
    JsonCreator ann = _findAnnotation(a, JsonCreator.class);
    if (ann == null) {
      return JsonCreator.Mode.PROPERTIES;
    }
    return null;
  }

  @Override
  @Deprecated // since 2.9
  public boolean hasCreatorAnnotation(Annotated a)
  {
      // 02-Mar-2017, tatu: Copied from base AnnotationIntrospector
      JsonCreator ann = _findAnnotation(a, JsonCreator.class);
      if (ann != null) {
          return (ann.mode() != JsonCreator.Mode.DISABLED);
      }
      return false;
  }
}