package com.aquariusinteractive.resolve.factual

import com.aquariusinteractive.resolve.api.ResolveProvider
import com.factual.driver.Factual
import com.factual.driver.FactualApiException
import groovy.util.logging.Log4j
import org.springframework.beans.factory.DisposableBean
import org.springframework.beans.factory.InitializingBean

/**
 *
 * @version 1.0
 * @author Michael Bishop
 *
 */
@Log4j
class FactualProvider implements ResolveProvider, InitializingBean, DisposableBean {

  static                       String  API_KEY
  static                       String  API_SECRET
  static volatile synchronized Integer API_CALLS
  static volatile synchronized Integer API_ERRORS
  /**
   * Delegate gives direct access to the api if needed
   */
  @Delegate                    Factual api

  FactualProvider() { }

  FactualProvider(String KEY, String SECRET) {
    API_KEY = KEY
    API_SECRET = SECRET
  }

  /**
   * Runs an "All or nothing resolve match"
   * @param args
   * @return Map of the response
   *
   * Example: <code>
   * [region:MA, status:1, tel:(508) 992-8503, postcode:02748, country:US,
   * category:Arts, Entertainment & Nightlife > Bars, resolved:true,
   * address:142 Rockdale Ave, name:Marshall's Pub, similarity:1,
   * locality:South Dartmouth, longitude:-70.937252, latitude:41.614948,
   * factual_id:252527ad-6589-4803-9ab3-ab0c9f153458]
   * </code>
   */
  @Override
  public FactualResolveResponse resolve(Map args) {
    API_CALLS++
    ResolveQueryNew q = new ResolveQueryNew()
    def response = null
    FactualResolveResponse factualResolveResponse

    args.each() { arg ->
      q.add((String) arg.key, arg.value)
    }

    try {
      response = api.resolve(q)
      factualResolveResponse = new FactualResolveResponse(response)
    }
    catch (FactualApiException e) {
      log.error(e)
      API_ERRORS++
      throw e
    }
    return factualResolveResponse
  }

  /**
   * Run a query for multiple potential matches.
   * @param args
   * @return
   */
  public List<FactualResolveResponse> multiResolve(Map args) {
    API_CALLS++
    List<FactualResolveResponse> factualResponses = new ArrayList<FactualResolveResponse>()
    ResolveQueryNew q = new ResolveQueryNew()
    args.each() { String k, v ->
      q.add(k, v)
    }
    try {
      def response = api.resolves(q)
      response.data.each {
        def frr = new FactualResolveResponse(it)
        factualResponses << frr
      }
    }
    catch (FactualApiException e) {
      log.error(e)
      API_ERRORS++
      throw e
    }
    return factualResponses
  }

  @Override
  void afterPropertiesSet() {
    API_CALLS = 0
    API_ERRORS = 0
    try {
      api = new Factual(API_KEY, API_SECRET)
    }
    // TODO This should not prevent other services
    // TODO from starting up
    catch (FactualApiException e) {
      log.error(e)
      throw new RuntimeException(e)
    }
    log.info("Factual Provider has started.")
  }

  @Override
  void destroy(){
    log.error("API_CALLS: ${API_CALLS}")
    log.error("API_ERRORS: ${API_ERRORS}")
  }
}
