import com.aquariusinteractive.resolve.factual.ResolveQueryNew
import com.factual.driver.ReadResponse

class BootStrap {


  def init = { servletContext ->

    com.factual.driver.Factual.metaClass.resolves = {ResolveQueryNew query ->
      return delegate.fetch("places", query)
    }
    com.factual.driver.Factual.metaClass.resolve = {ResolveQueryNew query ->
      return delegate.resolves(query).first()
    }
    com.factual.driver.Factual.metaClass.fetch = {String tableName, ResolveQueryNew query ->
      return new ReadResponse(request(urlForResolve(tableName, query)))
    }
  }

  def destroy = {
  }
}
