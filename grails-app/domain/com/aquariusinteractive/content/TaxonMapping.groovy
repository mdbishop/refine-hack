package com.aquariusinteractive.content

import groovy.transform.ToString

@ToString(includeNames = true)
class TaxonMapping {

  String   id
  /**
   * Name of the provider
   * eg. Factual
   */
  String   provider
  /**
   * Taxon / Category descriptive name
   */
  String   providerTaxonName
  /**
   * Unique identifier provider uses to
   * identify the taxon / category in a
   * typical api response.  This may be
   * similar / same as the providerTaxonName
   * or a unique uuid, for example.
   */
  String   providerTaxonId
  Set<Map> flags
  Taxon internalTaxon

  static mapWith   = "mongo"

  static constraints = {
    provider(nullable: false)
    providerTaxonName(nullable: false, unique: false, index: true)
    providerTaxonId(nullable: false, unique: true, index: true)
    internalTaxon(nullable: false, index: true)
  }

  static mapping = {
    version(false)
  }
}
