package com.aquariusinteractive.resolve

import com.aquariusinteractive.model.AbstractDTO
import org.codehaus.jackson.annotate.JsonProperty
import groovy.transform.ToString
import groovy.transform.Canonical

/**
 * Individual Suggest Record. Formatted
 * according to the rules of the Google Refine
 * reconciliation api requirements.
 *
 * @since 2/21/2012
 * @author Michael Bishop
 *
 */
@Canonical
@ToString(includeNames=true, includeSuper=true)
class SuggestResponse extends AbstractDTO {

  String guid
  String id
  String name

  @JsonProperty("r:score")
  Double rscore

  @JsonProperty("n:type")
  Type notableType = new Type()

  @JsonProperty("type")
  List<Type> types = new ArrayList<SuggestResponse.Type>()


  class Type extends AbstractDTO {
  
    String id
    String name
    
  }
}

