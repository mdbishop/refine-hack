package com.aquariusinteractive.resolve.factual

import com.aquariusinteractive.model.IReconcileResponse
import com.aquariusinteractive.model.ReconcileResponse
import groovy.transform.AutoExternalize
import groovy.transform.Canonical
import groovy.transform.ToString
import groovy.util.logging.Log4j

/**
 *
 * @version
 * @author Michael Bishop
 * [region:MA, status:1, tel:(508) 992-8503, postcode:02748, country:US,
 * category:Arts, Entertainment & Nightlife > Bars, resolved:true,
 * address:142 Rockdale Ave, name:Marshall's Pub, similarity:1,
 * locality:South Dartmouth, longitude:-70.937252, latitude:41.614948,
 * factual_id:252527ad-6589-4803-9ab3-ab0c9f153458]
 */
@Canonical
@ToString(includeNames = true)
@AutoExternalize
@Log4j
class FactualResolveResponse implements IReconcileResponse, Serializable {

  private static final String SERVICE  = 'FACTUAL'
  private static final List   EXCLUDES = ['class', 'metaClass']

  String  region
  boolean status
  String  tel
  String  postcode
  String  country
  String  category
  boolean resolved
  String  address
  String  name
  Double  similarity
  String  locality
  Double  longitude
  Double  latitude
  String  factual_id

  public Map asMap() {
    return this.properties.findAll { k, v ->
      !EXCLUDES.contains(k)
    }
  }

  @Override
  ReconcileResponse asReconcileResponse() {

    ReconcileResponse response = new ReconcileResponse()
    response.id = factual_id
    response.name = name
    response.type = [category]
    response.score = similarity
    response.match = resolved

    return response
  }
}
