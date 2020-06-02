package main.scala.controller.controllerBaseImpl

import java.util.concurrent.TimeUnit

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.client.RequestBuilding._
import akka.http.scaladsl.model.{ContentTypes, HttpEntity, HttpMethods, HttpRequest, HttpResponse}
import akka.http.scaladsl.unmarshalling._
import akka.stream.ActorMaterializer
import com.google.inject.Inject
import controller._
import javax.annotation.Nullable
import main.scala.controller.GameStatus.{IDLE, WIN}
import play.api.libs.json.{JsValue, Json}

import scala.concurrent.duration.Duration
import scala.concurrent.{Await, ExecutionContextExecutor, Future}


class Controller @Inject() extends ControllerInterface {

  var gameStatus = IDLE
  implicit val system: ActorSystem = ActorSystem("my-system")
  implicit val materializer: ActorMaterializer = ActorMaterializer()
  implicit val executionContext: ExecutionContextExecutor = system.dispatcher

  def createEmptyGrid(s: String): Unit = {
    val url = "http://localhost:11111/grid/createEmptyGrid/" + s
    Controller.postResponseFromUrl(url, null)
    this.resetPlayerList()
    gameStatus = IDLE
    publish(new GridSizeChanged(s))
  }

  def setValueToBottom(col:Int):Unit = {
    val url = "http://localhost:11111/valueSetting"
    val data = Json.obj("col" -> col,
    "value"->getValueOfPlayer)
    val response = Controller.postResponseFromUrl(url, data)
    val jsonStringFuture = response.flatMap(r => Unmarshal(r.entity).to[String])
    print("setValueToBottom" + response.toString)
    val jsonString = Await.result(jsonStringFuture, Duration(1, TimeUnit.SECONDS))
    val json = Json.parse(jsonString)
    (json \ "event").get.toString().replace("\"","") match {
      case "CellChanged" => {
        val row = (json \ "row").get.toString().toInt
        val col = (json \ "col").get.toString().toInt
        val value = (json \ "value").get.toString().toInt
        changeTurn()
        publish(new CellChanged(row,col,value))
      }
      case "WIN" => publish(new WinEvent)
      case "SetError" => publish(new SaveError((json \ "error").get.toString()))
    }
  }

  def getValueOfPlayer:Int = {
    val url = "http://localhost:22222/values"
    val jsonString = Controller.getValueResponseFromUrl(url)
    jsonString.toInt
  }

  def getGrid:JsValue = {
    val url = "http://localhost:11111/grids"
    val jsonString = Controller.getValueResponseFromUrl(url)
    Json.parse(jsonString)
    //printToHTML(json)
  }

  def printToHTML(jsValue: JsValue):String = {
    val rows = (jsValue \ "rows").get.toString.toInt
    val cols = (jsValue \ "cols").get.toString.toInt
    val cellsarray = Array.ofDim[Int](rows, cols)
    for (index <- 0 until rows * cols) {
      val row = (jsValue \\ "row") (index).as[Int]
      val col = (jsValue \\ "col") (index).as[Int]
      val cell = (jsValue \\ "cell") (index)
      val value = (cell \ "value").as[Int]
      cellsarray(row)(col) = value
    }
    var htmltext = ""

    cellsarray.foreach(
      row => {
        htmltext += row.fold("<p style=\"font-family:'Lucida Console', monospace\">")(_ + " " + _)  + "</p>"
      }
    )
    htmltext
  }

  def printToString(jsValue: JsValue):String = {
    val rows = (jsValue \ "rows").get.toString.toInt
    val cols = (jsValue \ "cols").get.toString.toInt
    val cellsarray = Array.ofDim[Int](rows, cols)
    for (index <- 0 until rows * cols) {
      val row = (jsValue \\ "row") (index).as[Int]
      val col = (jsValue \\ "col") (index).as[Int]
      val cell = (jsValue \\ "cell") (index)
      val value = (cell \ "value").as[Int]
      cellsarray(row)(col) = value
    }

    var text = ""
    cellsarray.foreach(
      row => {
        text += row.fold("")(_ + " " + _) + System.lineSeparator()
      }
    )
    text
  }

  def changeTurn(): Unit = {
    val url = "http://localhost:22222/players/reserving"
    Controller.postResponseFromUrl(url, null)
  }

  def currentPlayer():String = {
    val url = "http://localhost:22222/players"
    val jsonString = Controller.getValueResponseFromUrl(url)
    val json = Json.parse(jsonString)
    (json \ "player1").get.toString().replace("\"","")
  }

  def resetPlayerList(): Unit = {
    val url = "http://localhost:22222/players/resetting"
    Controller.postResponseFromUrl(url, null)
  }

  def gridToString: String = printToString(getGrid)

  def gridToHTML: String = printToHTML(getGrid)

  def undo: Unit = {
    Controller.postResponseFromUrl("http://localhost:11111/undo", null)
    this.changeTurn()
    publish(new GridChanged)
  }

  def redo: Unit = {
    Controller.postResponseFromUrl("http://localhost:11111/redo", null)
    this.changeTurn()
    publish(new GridChanged)
  }

  override def renamePlayer(newname: String): Unit = {
    val url = "http://localhost:22222/players/rename"
    val data = Json.obj({"name" -> newname})
    Controller.postResponseFromUrl(url, data)
  }

  override def getGameStatus() = gameStatus

  override def getGridRow: Int = {
    val url = "http://localhost:22222/grids/rows"
    val jsonString = Controller.getValueResponseFromUrl(url)
    jsonString.toInt
  }

  override def getGridCol: Int = {
    val url = "http://localhost:22222/gird/cols"
    val jsonString = Controller.getValueResponseFromUrl(url)
    jsonString.toInt
  }

  override def save: Unit = {}

  override def load: Unit = {}

  object Controller {

    def getValueResponseFromUrl(url: String) : String = {
      val response = Http().singleRequest(Get(url))
      val jsonStringFuture = response.flatMap(r => Unmarshal(r.entity).to[String])
      Await.result(jsonStringFuture, Duration(1, TimeUnit.SECONDS))
    }


    def postResponseFromUrl(url: String, @Nullable data: JsValue) : Future[HttpResponse] = {
      if (data != null) {
        Http().singleRequest(HttpRequest(
          method = HttpMethods.POST,
          uri = url,
          entity = HttpEntity(ContentTypes.`application/json`, data.toString())
        ))

      } else {
        Http().singleRequest(HttpRequest(
          method = HttpMethods.POST,
          uri = url
        ))
      }
    }
  }
}
