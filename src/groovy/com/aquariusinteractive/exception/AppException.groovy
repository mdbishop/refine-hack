package com.aquariusinteractive.exception

import groovy.transform.Canonical
import groovy.transform.InheritConstructors
import groovy.transform.ToString

/**
 * Base Exception Class for all Application related
 * exceptions.
 *
 * @version
 * @author Michael Bishop
 *
 */
@InheritConstructors
@Canonical
@ToString(includeNames = true)
class AppException extends RuntimeException {}
