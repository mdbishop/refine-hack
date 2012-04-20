package com.aquariusinteractive.resolve.api

/**
 * Provider that is able to process entity
 * resolution services.
 *
 * @version 1.0
 * @author Michael Bishop
 * 
 */
public interface ResolveProvider {

  def resolve(Map args)

}
