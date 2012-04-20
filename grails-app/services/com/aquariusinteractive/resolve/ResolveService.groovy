package com.aquariusinteractive.resolve

import com.aquariusinteractive.FlyoutCommand
import com.aquariusinteractive.PreviewCommand
import com.aquariusinteractive.ReconcileCommand
import com.aquariusinteractive.SuggestCommand
import com.aquariusinteractive.content.Taxon
import com.aquariusinteractive.exception.ReconcileException
import com.aquariusinteractive.resolve.api.ResolveProvider
import grails.converters.JSON
import org.codehaus.groovy.grails.web.converters.exceptions.ConverterException
import org.codehaus.groovy.grails.web.json.JSONObject
import com.aquariusinteractive.model.*

class ResolveService  {

  static transactional = false

  ResolveProvider resolveProvider
  def             taxonService
  def             responseCacheService
  Integer         CALLS = 0

  // List of valid properties (currently tied to Factual's API.)
  private static final LOOKUP_PROPERTIES = [ "factual_id", "name", "po_box", "address",
          "address_extended", "locality", "region",
          "admin_region", "post_town", "postcode",
          "country", "tel", "fax", "website",
          "latitude", "longitude", "category",
          "status", "email" ]

  def doReconcile(final ReconcileCommand cmd) throws ReconcileException {
    if (cmd.query) {
      try {
        def jsonQuery = JSON.parse(cmd.query)
        log.debug("Query is JSON.")
        return doQuery(cmd, jsonQuery)
      }
      catch (ConverterException c) {
        log.debug("Query is not json.")
        return doQuery(cmd)
      }
    } else if (cmd.queries) {
      return doQueries(cmd)

    } else {
      throw new ReconcileException("Query and Queries are blank.")
    }
  }

  private Map toResolveParams(final JSONObject cmd) {
    def query = [ : ]
    query[ 'name' ] = cmd.query
    cmd.properties.each() { prop ->
      /**
       * properties array from Google Refine comes in as:
       *{ "pid": "propName", "v", "value"}*/
      query[ (prop.pid) ] = prop.v
    }
    if (cmd.limit && cmd.limit != 0) {
      query[ 'limit' ] = cmd.limit
    } else {
      query[ 'limit' ] = 3
    }
    if (cmd.type) query[ 'category' ] = cmd.type

    return query
  }


  private def doQueries(final ReconcileCommand cmd) {
    final JSONObject json = JSON.parse(cmd.queries)
    final Set keys = json.keySet().sort()
    Map results = [ : ]
    for (String key in keys) {
      ReconcileResultList rl = new ReconcileResultList()
      final def query = toResolveParams(json.get(key))
      List queryResults = requestMessage('seda:factual.resolve.multi',
                                         query)
      queryResults.each() {
        rl.addResult(it.asReconcileResponse())
        sendMessage('seda:factual.cache.save', it.asMap())
      }
      results[ key ] = rl
    }
    return results as JSON
  }


  private def doQuery(final ReconcileCommand cmd) {
    return doResolve(toResolveParams(cmd))
  }

  ReconcileResponse doResolve(final Map args) {
    def resp = resolveProvider.resolve(args)
    if (resp) {
      sendMessage('seda:factual.cache.save', resp.asMap())
    }
    return resp.asReconcileResponse()
  }

  /**
   * Manual Entity Search
   * @param SuggestCommand
   * @return
   */
  SuggestResponseDTO doSuggestEntity(SuggestCommand cmd) {
    SuggestResponseDTO dto = new SuggestResponseDTO()
    dto.prefix = cmd.prefix
    //    List<FactualResolveResponse> results =
    //      resolveProvider.multiResolve([ name: cmd.prefix,
    //                                           category: cmd.type, limit: cmd.limit ])
    List results = requestMessage('seda:factual.resolve.multi',
                                  [ name: cmd.prefix,
                                          category: cmd.type, limit: cmd.limit ])
    results.each() {
      sendMessage('seda:factual.cache.save', it.asMap())
      SuggestResponse rec = new SuggestResponse(id: it.factual_id, name: "${it.name} ${it.locality}",
                                                guid: it.factual_id, rscore: it.similarity)
      rec.notableType.id = it.category
      rec.notableType.name = it.category
      dto.results.add(rec)
    }
    return dto
  }

  /**
   * Suggest a type / category to Google Refine
   * @param suggest command request
   * @return List of suggestions
   */
  SuggestResponseDTO doSuggestType(SuggestCommand cmd) {
    List<Taxon> suggestions
    SuggestResponseDTO dto = new SuggestResponseDTO()
    dto.prefix = cmd.prefix
    suggestions = taxonService.searchTaxonsByPrefix(cmd.prefix)
    suggestions.eachWithIndex { Taxon taxon, int i ->
      if (cmd.limit == 0 || i <= cmd.limit) {
        DTO d = new SuggestResponse()
        d.guid = taxon.id
        d.id = taxon.id
        d.name = "${taxon.name.intern()}"
        dto.results.add(d)
      }
    }
    return dto
  }

  String doRenderTypeFlyout(FlyoutCommand cmd) {
    Taxon t = Taxon.findById(cmd.id, [ readOnly: true ])
    StringBuilder flyout = new StringBuilder()
    flyout.append(t.name)
    flyout.append("\n").append(t.description).append("\n")
    flyout.append(t.fullPath).append("\n")
    return flyout.toString()
  }

  String doRenderPropertyFlyout(FlyoutCommand cmd) {
    StringBuilder flyout = new StringBuilder()
    flyout.append('DESCRIPTION NOT AVAILABLE AT THIS TIME'.intern())
    return flyout.toString()
  }

  SuggestResponseDTO doSuggestProperty(SuggestCommand cmd) {
    // TODO support the type of entity we're trying to get a property for.
    // TODO will be used to determine which resolveProvider properties
    // TODO are applicable.
    SuggestResponseDTO dto = new SuggestResponseDTO()
    dto.prefix = cmd.prefix

    def results = LOOKUP_PROPERTIES.findAll() { String it ->
      it.startsWith(cmd.prefix)
    }

    results.each() { String result ->
      dto.results.add(new SuggestResponse(id: result.intern(),
                                          name: result.intern(),
                                          guid: result.intern()))
    }
    return dto
  }

  String doRenderEntityFlyout(FlyoutCommand cmd) {
    StringBuilder flyout = new StringBuilder()
    // Look for id in externally-resolved entity lookup cache
    def external = responseCacheService.getResponseCacheEntryForProviderId(cmd.id)
    //ResponseCache external = FactualResponseCache.findByProviderId(cmd.id)
    // TODO internal lookup logic
    if (external) {
      return external.asFlyout()
    } else {
      return null
    }
  }

  // TODO Instead of a map, should this be an object response?
  Map<String, Object> getPreviewModel(PreviewCommand cmd) {
    Map response = [ : ]
    def responseCacheEntry
    def internalEntry
    responseCacheEntry = responseCacheService.getResponseCacheEntryForProviderId(cmd.id)
    if (responseCacheEntry) {
      response[ 'external' ] = responseCacheEntry?.asFlyout()
    }
    return response
  }
}         