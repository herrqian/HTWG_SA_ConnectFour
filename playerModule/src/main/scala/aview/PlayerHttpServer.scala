package aview

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.{ContentTypes, HttpEntity}
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import akka.stream.ActorMaterializer
import controller.PlayerController
import play.api.libs.json.Json

import scala.concurrent.ExecutionContextExecutor

class PlayerHttpServer(val controller:PlayerController) {
  implicit val system:ActorSystem = ActorSystem("my-system")
  implicit val materializer:ActorMaterializer = ActorMaterializer()
  implicit  val executionContext: ExecutionContextExecutor = system.dispatcher

  val route:Route =concat(
    post {
        path("players" / "rename") {
          decodeRequest {
            entity(as[String]) { data =>
              val mydata = Json.parse(data)
              val newname = (mydata \ "player").get.toString()
              controller.rename(newname)
              complete("")
            }
          }
        } ~
      path("players"/"reserving") {
        controller.reserve
        println(controller.playerslist)
        complete("")
      } ~
      path("players"/"resetting") {
        controller.resetting
        complete("")
      }
    },
    get {
      path("players") {
        println("i got a get-request form client")
        complete(HttpEntity(ContentTypes.`application/json`, controller.playersToJson.toString))
      }
    },
  )

  val bindingFuture = Http().bindAndHandle(route, "localhost", 22222)

  def unbind = {
    bindingFuture
      .flatMap(_.unbind()) // trigger unbinding from the port
      .onComplete(_ => system.terminate()) // and shutdown when done
  }

}
