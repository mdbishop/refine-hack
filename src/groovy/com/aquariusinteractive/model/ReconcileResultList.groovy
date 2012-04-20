package com.aquariusinteractive.model

import groovy.util.logging.Log4j
import org.codehaus.jackson.map.ObjectMapper

/**
 * DTO for a list of ReconcileResults
 * For one entity query.
 * @version
 * @author Michael Bishop
 *
 */
@Log4j
class ReconcileResultList {

  static ObjectMapper mapper = new ObjectMapper()
  def                 result = []

  ReconcileResultList() {

  }
  
  public void addResult(def r){
    result << r
  }

  public String toJsonString() {
    final def result = mapper.writeValueAsString(this)
    return result
  }

  /**
   * Normalize scores to a value between 1 - 100
   * Scores returned from Virtuoso have an undefined and exponential
   * range.  Needs to be reduced to a value that Google Refine
   * can work with.
   */
  @Deprecated
  public void normalizeScores() {
    if (this.result) {
      final Collection<Number> scores = this.result.collect { it.score }
      final Integer range = (scores.min().abs() + scores.max().abs()) - 1

      this.result.each() { ReconcileResponse score ->
        final oldScore = score.score
        try {
          /**/
          score.score = (score.score / range * 100).round(4)
          /**/
        }
        catch (IllegalArgumentException ie) {
          log.warn(ie.message)
          score.score = oldScore
        }
      }
    }

  }

  @Override
  public String toString() {
    StringBuffer sb = new StringBuffer()
    result.each() {
      sb.append(it.toString())
      sb.append("\n")
    }
    return sb.toString()
  }
}
