package com.aquariusinteractive.resolve

import com.aquariusinteractive.model.geo.Latitude
import com.aquariusinteractive.model.geo.Longitude
import groovy.transform.ToString
import groovy.util.logging.Log4j

@ToString(includeNames = true, includeSuper = true)
@Log4j
class FactualResponseCache extends ResponseCache {

  private static final String PROVIDER = "FACTUAL"

  String  name
  String  category
  String  address
  String  locality
  String  region
  String  postcode
  String  country
  String  tel
  Double  longitude
  Double  latitude
  boolean status
  List    coordinates

  static mapWith = "mongo"

  static constraints = {
    providerId(nullable: false, index: true, unique: true)
    region(nullable: true)
    status(nullable: false)
    tel(nullable: true)
    postcode(nullable: true)
    country(nullable: true)
    category(nullable: true)
    address(nullable: true)
    name(nullable: false)
    locality(nullable: true)
    longitude(nullable: false, min: Longitude.MIN_VALUE, max: Longitude.MAX_VALUE)
    latitude(nullable: false, min: Latitude.MIN_VALUE, max: Longitude.MAX_VALUE)
    coordinates(validator: { val, obj ->
      if (obj.longitude && obj.latitude) {
        if (!val) return false
        if (val.size == 2) return true
        return false
      }
    })
  }

  static mapping = {
    providerId index:true,unique: true
    name index: true
    coordinates geoIndex: true
  }

  /**
   * Mongo requires specific positional
   * parameters for a geo index.
   */
  void updateCoordinates() {
    coordinates = [longitude, latitude]
  }

  public setFactual_id(String id) {
    this.providerId = id
  }

  @Override
  public String asFlyout() {
    if (!this.id) return ''
    StringBuilder flyout = new StringBuilder()
            .append('<p>').append(PROVIDER).append('</p>')
            .append('<p>').append(this.name).append('<br/>')
            .append(this.category).append('</p>')
            .append('<p>').append(this.address).append('</br>')
            .append(this.locality).append(' ').append(this.region)
            .append(this.postcode).append('</br>')
            .append(this.tel).append('</br>')
            .append('Lon/Lat: ').append(this.coordinates).append('<br/>')
            .append('In Business: ').append(this.status).append('</p>')

    return flyout.toString()
  }
}
