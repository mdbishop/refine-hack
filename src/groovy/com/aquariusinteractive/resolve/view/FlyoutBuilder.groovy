package com.aquariusinteractive.resolve.view

import grails.web.JSONBuilder
import groovy.util.logging.Log4j
import groovy.xml.MarkupBuilder

/**
 * TODO / NOT CURRENTLY USED
 * @version
 * @author Michael Bishop
 *
 */
@Log4j
class FlyoutBuilder {

  FlyoutBuilder() {

  }

  /**
   * Accepts text or html encoded markup to be inserted
   * into a Flyout Template for use in a jsonp
   * flyout response
   * @param content
   * @return JSON html element
   */
  static def build(String content) {
    def writer = new StringWriter()
    MarkupBuilder builder = new MarkupBuilder(writer)
    builder.setOmitEmptyAttributes(true)
    builder.setOmitNullAttributes(true)
    builder.div {
      div(class: 'fbs-topic-flyout', id: 'fbs-topic-flyout') {
        h1(id: 'fbs-flyout-title', class: 'fbs-flyout-image-false') {}
        h3(class: 'fbs-topic-properties fbs-flyout-image-false') {
          strong() {}
        }
        p(class: 'fbs-topic-article fbs-flyout-iamge-false') {
          mkp.yieldUnescaped(content)
        }
      }
      div(class: 'fbs-attribution') {
        span(class: 'fbs-flyout-types') {}
      }
    }
    JSONBuilder json = new JSONBuilder()
    def root = json.build {
      html = "${writer}"

    }
    log.error(root.toString())
    return root
  }


}
