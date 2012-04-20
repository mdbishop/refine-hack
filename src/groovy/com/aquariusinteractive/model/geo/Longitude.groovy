package com.aquariusinteractive.model.geo

import groovy.transform.AutoExternalize
import groovy.transform.Canonical
import groovy.transform.ToString

/**
 *
 * @version
 * @author Michael Bishop
 *
 */
@AutoExternalize
@Canonical
@ToString(includeNames = true)
class Longitude implements Serializable, Comparable {

  private static final long serialVersionUID = -8614900608052762636L;
  public static final double MIN_VALUE = -180.0;
  public static final double MAX_VALUE = 180.0;

  public Double value

  public Longitude(Double theta) {
    setValue(theta)
  }

  public Longitude(String theta) throws NumberFormatException {
    setValue(Double.valueOf(theta))
  }

  public setValue(Double theta) {
    if (theta >= MIN_VALUE && theta <= MAX_VALUE) {
      this.value = theta
    } else {
      throw new IllegalArgumentException("${theta} is out of range.")
    }
  }

  public Double getValue() {
    return value
  }


  public Double asType(Class<Double> d) {
    return value
  }

  @Override
  int compareTo(Object t) {
    return value <=> t
  }
}
