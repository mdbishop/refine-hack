package com.aquariusinteractive.resolve

import com.aquariusinteractive.resolve.api.FlyoutFormattable
import groovy.transform.ToString

/**
 * Base class that holds complete ResolveProvider 
 * responses.  
 *
 * Google refine initial reconcile step will only use
 * a subset of this information but the entire response
 * will be eventually needed and thus should be persisted
 * in an non-ephemeral cache.
 *
 * TODO Eviction strategy -- Currently undefined.
 *
 */
@ToString(includeNames = true)
abstract class ResponseCache implements Serializable, FlyoutFormattable {

  String id
  String providerId

  static mapWith = "mongo"

  static mapping = {
  }

  static constraints = {
    providerId(nullable: false, index: true, unique: true)
  }

  @Override
  String asFlyout(){
    return this.toString()
  }
}
