package com.aquariusinteractive.resolve

import com.aquariusinteractive.resolve.ResponseCache as RC

/**
 * Persistent store / retrieval of Reconcile
 * Response entities.
 */
class ResponseCacheService {

  static transactional = 'mongo'

  public void saveFactualResponse(Map body) {
    if (!FactualResponseCacheExistsForId(body.factual_id)) {
      def frc = new FactualResponseCache(body)
      frc.updateCoordinates() // Set the lon/lat array according to mongo specs
      if (frc.hasErrors()) {
        log.error(frc.errors)
      } else {
        frc.save()
        log.trace(frc.ident())
      }
    }
  }

  public boolean FactualResponseCacheExistsForId(final String factual_id) {
    return RC.findByProviderId(factual_id) as Boolean
  }

  RC getResponseCacheEntryForProviderId(final String providerId) {
    // TODO BUG --> MongoDB Polymorphic queries not working. <---
    FactualResponseCache frc = FactualResponseCache.findByProviderId(providerId)
    //    RC rc =
    //      RC.findByProviderId(providerId)
    //    return rc
    return frc
  }
}
