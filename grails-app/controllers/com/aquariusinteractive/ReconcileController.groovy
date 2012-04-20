package com.aquariusinteractive

import com.aquariusinteractive.model.MetaDataDTO
import com.aquariusinteractive.model.SuggestResponseDTO
import groovy.transform.AutoExternalize
import groovy.transform.Canonical
import groovy.transform.ToString
import org.springframework.http.HttpStatus

class ReconcileController {

  def reconciliationService
  def queryService
  def resolveService

  // Default
  def index(final ReconcileCommand cmd) {
    cmd.validate()
    log.error(cmd)
    try {
      if (cmd.callback) {
        final def meta = new MetaDataDTO(request).content()
        render(contentType: 'application/javascript', text: "${cmd.callback}(${meta})")
      } else if (cmd.query || cmd.queries) {
        log.error("Query.")
        final def resp = resolveService.doReconcile(cmd)
        render(contentType: 'application/json', text: resp)
      } else {
        final String json = new MetaDataDTO(request).content()
        render(contentType: 'text/plain', text: json)
      }
    }
    finally {
    }
  }

  /**
   * @param cmd
   */
  def preview(PreviewCommand cmd) {
    if (cmd.id) {
      try {
        final Map response = resolveService.getPreviewModel(cmd)
        if (response.size()) {
          render(view: 'preview', model: [ result: response, cmd: cmd ], status: HttpStatus.OK)

        } else {
          final String msg = "No additional information about this id was found.".intern()
          log.error("${msg} ${cmd}")
          render(view: 'error', model: [ cmd: cmd, msg: msg ], status: HttpStatus.NOT_FOUND)
        }
      }
      catch (Exception e) {
        log.error(e)
        final String msg = "Server was unable to handle your request at this time.".intern()
        render(view: 'error', model: [ cmd: cmd, msg: msg ], status: HttpStatus.INTERNAL_SERVER_ERROR)

      }
    } else {
      final String msg = 'Missing / unrecognized parameters'.intern()
      render(view: 'error', model: [ cmd: cmd, msg: msg ], status: HttpStatus.BAD_REQUEST)
    }
  }

  /**
   *
   * @param cmd
   */
  def suggest(final SuggestCommand cmd) {
    validate(cmd)
    if (cmd.prefix.length() < 5) return
    final SuggestResponseDTO resp = resolveService.doSuggestEntity(cmd)
    final String json = resp.asJsonString()
    log.trace(json)
    render(contentType: "application/javascript", text: "${cmd.callback}(${json})")
  }

  /**
   * Property represents a query param
   * (eg latitude, longitude)
   * @param cmd
   */
  def suggest_property(final SuggestCommand cmd) {
    log.trace(cmd)
    validate(cmd)
    final SuggestResponseDTO resp = resolveService.doSuggestProperty(cmd)
    final String json = resp.asJsonString()

    render(contentType: "application/javascript", text: "${cmd.callback}(${json})")
  }

  /**
   * Type identifies a "category"
   * eg "Bar" "Arch" "Lake"
   * @param cmd
   */
  def suggest_type(final SuggestCommand cmd) {
    validate(cmd)
    final SuggestResponseDTO resp = resolveService.doSuggestType(cmd)
    final String json = resp.asJsonString()
    render(contentType: "application/javascript", text: "${cmd.callback}(${json})")
  }

  def flyout_property(FlyoutCommand cmd) {
    validate(cmd)
    final String flyout = resolveService.doRenderPropertyFlyout(cmd).encodeAsJavaScript()
    render(template: 'flyout', model: [ comment: flyout, cmd: cmd ],
           contentType: 'application/javascript')
  }

  def flyout_type(FlyoutCommand cmd) {
    validate(cmd)
    final String flyout = resolveService.doRenderTypeFlyout(cmd).encodeAsJavaScript()
    render(template: 'flyout', model: [ comment: flyout, cmd: cmd ],
           contentType: 'application/javascript')
  }

  def flyout(FlyoutCommand cmd) {
    validate(cmd)
    response.contentType = 'text/javascript'
    def content = resolveService.doRenderEntityFlyout(cmd)
    def flyoutHtml = g.render(template: 'flyout', model: [ comment: content ])
    render "${cmd.callback}({\"id\":\"${cmd.id}\",\"html\":\"${flyoutHtml}\"})"
  }

  /**
   * Validate a command object
   * @param commandObject
   */
  private validate(def cmd) {
    cmd.validate()
    if (cmd.hasErrors()) {
      response.sendError(response.SC_BAD_REQUEST)
    }
  }
}

/**
 * Command Object for suggest parameters
 */
@AutoExternalize
@Canonical
@ToString(includeNames = true)
class SuggestCommand {

  String callback
  String prefix
  def    type
  String type_strict
  def    limit = 0
  def    start = 0

  static constraints = {
    callback(nullable: false)
    prefix(nullable: false, blank: false, minSize: 3)
    type(nullable: true, blank: true)
    type_strict(inList: [ "any", "all", "should" ])
    limit nullable: true
    start nullable: true
  }
}

/**
 * Command object for Perview parameters
 */
@AutoExternalize
@Canonical
@ToString(includeNames = true)
class PreviewCommand {

  // TODO Fix this
  private static String virtuoso =
    'http://v.aquariusinteractive.com:8890/about/html/'

  String id

  /**
   * Return a fully formed service url
   * @return
   */
  String getUrl() {
    if (id) {
      final def String url = "${virtuoso}${id}".trim()
      return url
    } else {
      throw new Exception("id is not initalized.")
    }
  }
}

/**
 * Command object for Main Reconcile Action
 */
@AutoExternalize
@Canonical
@ToString(includeNames = true)
class ReconcileCommand {

  String callback
  def    query
  def    queries

  static constraints = {
    callback(nullable: true)
    query(nullable: true)
    queries(nullable: true)
  }
}

/**
 * Flyout Command Object
 */
@AutoExternalize
@Canonical
@ToString(includeNames = true)
class FlyoutCommand {

  String id
  String callback

  static constraints = {
    id(nullable: false)
    callback(nullable: true)
  }
}