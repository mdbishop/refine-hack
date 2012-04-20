package com.aquariusinteractive.model

import org.codehaus.jackson.annotate.JsonProperty
import org.codehaus.jackson.annotate.JsonUnwrapped
import org.codehaus.jackson.map.ObjectMapper
import org.springframework.http.HttpStatus
import grails.converters.deep.JSON

/**
 * Suggest Response DTO
 * For use with Google Refine clients
 *
 * According to the
 * <code>http://code.google.com/p/google-refine/wiki/SuggestApi</code> spec
 * @version
 * @author Michael Bishop
 *
 */
class SuggestResponseDTO extends AbstractDTO {

  private static final ObjectMapper mapper     = new ObjectMapper()
  static final         String       CODE_OK    = "/api/status/ok"
  static final         String       CODE_ERROR = "/api/status/error"

  /**
   * Set to static field CODE_OK or CODE_ERROR
   */
  String    code    = CODE_OK
  /**
   * Set to HTTP Status Code
   * @see {@link HttpStatus}
   *
   */
  String    status = "200 OK"   // HttpStatus.OK.toString()
  /**
   * The original search term
   */
  String    prefix  = ""
  /**
   *
   */
  @JsonUnwrapped
  @JsonProperty("result")
  List<DTO> results = new ArrayList<DTO>()

  public void setStatus(HttpStatus s) {
    this.status = s.value()
  }

  public String getStatus() {
    return this.status
  }

  /**
   * Render this object as a Json string
   * @return Json String representation
   */
  public String asJsonString() {
    return mapper.writeValueAsString(this)
  }

  public def asJson(){
    return JSON.parse(asJsonString())
  }


}


