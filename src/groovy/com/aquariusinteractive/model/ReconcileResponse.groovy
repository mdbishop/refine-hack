package com.aquariusinteractive.model

import groovy.transform.ToString

/**
 *
 * @version
 * @author Michael Bishop
 * 
 */
@ToString(includeNames=true)
class ReconcileResponse {

  String       id
  String       name
  String[]     type = []
  Double       score
  Boolean      match

}
