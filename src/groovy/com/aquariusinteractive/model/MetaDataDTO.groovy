package com.aquariusinteractive.model

import groovy.util.logging.Log4j
import javax.servlet.http.HttpServletRequest
import org.codehaus.jackson.map.ObjectMapper
import org.codehaus.jackson.node.ArrayNode
import org.codehaus.jackson.node.ObjectNode

/**
 * User: mdbishop
 * Date: 2/21/12
 * Time: 5:49 PM
 */
@Log4j
class MetaDataDTO {

  private static String hostName
  private static String path
  private static int    port
  private static String servletPath
  private static String scheme
  private static String prefix
  private static String url

  MetaDataDTO(HttpServletRequest request) {
    hostName = request.getServerName()
    path = request.contextPath
    port = request.getServerPort()
    scheme = request.getScheme()
    prefix = "${scheme}://${hostName}:${port}${path}/reconcile"
  }

  static String content() {
    final String json
    final ObjectMapper om = new ObjectMapper()
    ObjectNode root = om.createObjectNode()

    root.put('name', 'Aquarius Reconciliation Service')
    root.put('identifierSpace', 'http://todo')
    root.put('schemaSpace', 'http://todo')

    ObjectNode view = om.createObjectNode()
    view.put('url', "${prefix}/preview?id={{id}}")
    root.put('view', view)

    ObjectNode preview = om.createObjectNode()
    preview.put('url', "${prefix}/preview?id={{id}}")
    preview.put('width', 500)
    preview.put('height', 400)
    root.put('preview', preview)

    ObjectNode suggest = om.createObjectNode()
    ObjectNode suggestType = om.createObjectNode()

    suggest.put('type', suggestType)
    suggestType.put('service_url', "${prefix}")
    suggestType.put('service_path', "/suggest_type")
    suggestType.put('flyout_service_url', "${prefix}")
    suggestType.put('flyout_service_path', '/flyout_type')
    root.put('suggest', suggest)

    ObjectNode prop = om.createObjectNode()
    prop.put('service_url', "${prefix}")
    prop.put('service_path', '/suggest_property')
    prop.put('flyout_service_url', "${prefix}")
    prop.put('flyout_service_path', '/flyout_property')
    suggest.put('property', prop)

    ObjectNode entity = om.createObjectNode()
    entity.put('service_url', "${prefix}")
    entity.put('service_path', '/suggest')
    entity.put('flyout_service_path', '/flyout')
    suggest.put('entity', entity)

    ArrayNode types = om.createArrayNode()
    root.put('defaultTypes', types)

    json = om.writeValueAsString(root)
    log.trace(json)

    return json
  }


}
