import org.apache.camel.builder.RouteBuilder

class ServiceRoute extends RouteBuilder {
  def grailsApplication

  @Override
  void configure() {
    def config = grailsApplication?.config

    from('seda:factual.cache.save')
            .to('bean:responseCacheService?method=saveFactualResponse(${body})')


    from('seda:factual.resolve.multi')
            .throttle(1).timePeriodMillis(1000)
            .beanRef('factualProvider', 'multiResolve')

    //.to('bean:factualProvider?method=multiResolve(${body})')

  }
}
