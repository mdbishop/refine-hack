package com.aquariusinteractive.resolve.factual

/**
 * Parameters that are allowable for a Factual Lookup
 * Request
 *
 * @since 3/20/2012
 * @author Michael Bishop
 *
 */
class FactualRequest {


  String  factual_id
  /**
   * Business/POI name
   */
  String  name
  /**
   * PO Box. As they do not represent the
   * physical location of a brick-and-mortar store,
   * PO Boxes are often excluded from mobile use cases.
   * We’ve isolated these for only a limited
   * number of countries, but more will follow
   */
  String  po_box
  /**
   * Street address
   */
  String  address
  /**
   * Additional address incl. suite numbers
   */
  String  address_extended
  /**
   * City, town or equivalent
   */
  String  locality
  /**
   * State, province, territory, or equivalent
   */
  String  region
  /**
   * Additional sub-division, usually but not
   * always a country sub-division
   */
  String  admin_region
  /**
   * Town employed in postal addressing
   */
  String  post_town
  /**
   * Postcode or equivalent (zipcode in US)
   */
  String  postcode
  /**
   * The ISO 3166-1 alpha-2 country code
   */
  String  country
  /**
   * Telephone number with local formatting
   */
  String  tel
  /**
   * Fax number
   */
  String  fax
  /**
   * Authority page (official website)
   */
  String  website
  /**
   * Latitude in decimal degrees (WGS84 datum).
   * Value will not exceed 6 decimal places (0.111m)
   *
   */
  Double  latitude
  Double  longitude
  /*
  * String name of category tree and category branch
  */
  String  category
  /**
   * Boolean representing business as
   * going concern: closed (0) or open (1)
   */
  boolean status
  /**
   * Contact email address of organization
   */
  String  email

}
