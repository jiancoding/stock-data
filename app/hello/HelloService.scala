package hello

import javax.ws.rs.Path

import akka.actor.ActorRef
import akka.http.scaladsl.server.Directives
import akka.pattern.ask
import akka.util.Timeout
import hello.HelloActor.{AnonymousHello, Greeting, Hello}
import helper.DefaultJsonFormats
import io.swagger.annotations._

import scala.concurrent.duration._

@Api(value = "/hello", produces = "application/json")
@Path("/hello")
class HelloService(hello: ActorRef)
  extends Directives with DefaultJsonFormats {

  implicit val timeout = Timeout(2.seconds)
  implicit val greetingFormat = jsonFormat1(Greeting)

  val route =
    getHello ~
    getHelloSegment
    
  @ApiOperation(value = "Return Hello greeting", nickname = "anonymousHello", httpMethod = "GET", response = classOf[Greeting])
  @ApiResponses(Array(
    new ApiResponse(code = 200, message = "Return Hello all times"),
    new ApiResponse(code = 400, message = "Bad request"),
    new ApiResponse(code = 500, message = "Internal server error")
  ))
  def getHello =
    path("hello") {
      get {
        complete {
          (hello ? AnonymousHello).mapTo[Greeting]
        }
      }
    } 
    
  @Path("/{name}")
  @ApiOperation(value = "Return Hello greeting with person's name", notes = "", nickname = "hello", httpMethod = "GET")
  @ApiImplicitParams(Array(
    new ApiImplicitParam(name = "name", value = "Name of person to greet", required = false, dataType = "string", paramType = "path")
  ))
  @ApiResponses(Array(
    new ApiResponse(code = 200, message = "Return Hello Greeting", response = classOf[Greeting]),
    new ApiResponse(code = 500, message = "Internal server error")
  ))
  def getHelloSegment =
    path("hello" / Segment) { name =>
      get {
        complete {
          (hello ? Hello(name)).mapTo[Greeting] }
      }
    }
}

