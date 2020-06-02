package aview

import controller.GridController
import main.scala.model.gridComponent.gridAdvancedImpl.Grid

object GridMain {

  val gridcontroller = new GridController(new Grid(6,7))
  val httpserver = new ModelHttpServer(gridcontroller)
  @volatile var shutdown = false

  def main(args: Array[String]): Unit = {
    println("http://localhost:11111" + " is open")
    while (!shutdown) {
      Thread.sleep(1000)
      ;
    }

    httpserver.unbind
    println("http://localhost:11111" + " is closed")
  }

  def shutdownServer(): Unit = shutdown = true
}