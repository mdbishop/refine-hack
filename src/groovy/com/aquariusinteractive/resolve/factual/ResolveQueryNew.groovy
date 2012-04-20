package com.aquariusinteractive.resolve.factual

import com.google.common.collect.Maps
import groovy.transform.InheritConstructors
import org.codehaus.jackson.JsonGenerationException
import org.codehaus.jackson.map.JsonMappingException
import org.codehaus.jackson.map.ObjectMapper
import org.codehaus.jackson.map.annotate.JsonSerialize
import groovy.util.logging.Log4j
import groovy.transform.ToString
import com.factual.driver.ResolveQuery

/**
 * Overridden toJsonStr
 *
 * @version 1.0
 * @author Michael Bishop
 *
 */
@InheritConstructors
@Log4j
@ToString
class ResolveQueryNew extends ResolveQuery {

  Map<String, Object> values = Maps.newHashMap();

  ResolveQueryNew() {
    super()
  }

  public ResolveQuery add(String key, Object val) {
    values.put(key, val);
    return this;
  }

  @Override
  protected String toUrlQuery() {
    return urlPair("values", toJsonStr(values));
  }

  private String toJsonStr(Object obj) {
    
    try {
      ObjectMapper om = new ObjectMapper()
      om.setSerializationInclusion(JsonSerialize.Inclusion.NON_NULL)
      return om.writeValueAsString(obj)
    }
    catch (JsonGenerationException e) {
      log.error("**** ${obj} ${e} ****")
      throw new RuntimeException(e);
    }
    catch (JsonMappingException e) {
      throw new RuntimeException(e);
    }
    catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  private String urlPair(String name, Object val) {
    if (val != null) {
      try {
        return name + "=" + (val instanceof String ? URLEncoder.encode(val.toString(), "UTF-8") : val);
      }
      catch (UnsupportedEncodingException e) {
        throw new RuntimeException(e);
      }
    } else {
      return null;
    }
  }
}
