import scala.concurrent.Future
import com.google.inject.AbstractModule
import com.google.inject.Guice
import play.api.GlobalSettings
import play.api.mvc.RequestHeader
import play.api.mvc.Results.BadRequest
import play.api.mvc.Results.InternalServerError
import play.api.mvc.Results.NotFound
import services.SimpleBusiness
import services.SimpleBusinessListing
import services.SimpleBizDBService
import services.SimpleOHListing
import services.SimpleOHDBService
import services.SimpleDrinkPriceListing
import services.SimpleDrinkPriceDBService

object Global extends GlobalSettings {
  /**
   * Error Handling
   */
  // Bad Request
  override def onBadRequest(request: RequestHeader, error: String) = {
    Future.successful(BadRequest.as("Bad Request: " + error))
  }

  // 500 - internal server error
  override def onError(request: RequestHeader, throwable: Throwable) = {
    Future.successful(InternalServerError(views.html.errors.on500(throwable)))
  }

  // 404 - page not found error
  override def onHandlerNotFound(request: RequestHeader) = {
    Future.successful(NotFound(views.html.errors.on404(request.path)))
  }

  /**
   * Set up Guice injection mechanism.
   */
  val injector = Guice.createInjector(new AbstractModule {
    protected def configure() {
      /**
       * Bind types and build dependency graph.
       */
      bind(classOf[SimpleBusinessListing]).to(classOf[SimpleBizDBService])
      bind(classOf[SimpleOHListing]).to(classOf[SimpleOHDBService])
      bind(classOf[SimpleDrinkPriceListing]).to(classOf[SimpleDrinkPriceDBService])
    }
  })

  /**
   * Controllers must be resolved through the application context. There is a special method of GlobalSettings
   * that we can override to resolve a given controller. This resolution is required by the Play router.
   */
  override def getControllerInstance[A](controllerClass: Class[A]): A = injector.getInstance(controllerClass)
}
