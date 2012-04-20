package com.aquariusinteractive.model

import groovy.transform.AutoExternalize
import groovy.transform.Canonical
import groovy.transform.ToString

/**
 * Base DTO class.
 *
 * @version 1.0
 * @author Michael Bishop
 *
 */
@AutoExternalize
@Canonical
@ToString(includeNames = true)
abstract class AbstractDTO implements DTO, Serializable {


}
