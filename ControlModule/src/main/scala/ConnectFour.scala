package scala

import com.google.inject.Guice
import main.scala.aview.Tui
import main.scala.aview.gui.SwingGui
import main.scala.controller.controllerBaseImpl.Controller
import main.scala.model.gridComponent.gridAdvancedImpl.Grid

import scala.module.ConnectFourModule

object ConnectFour {
    val injector = Guice.createInjector(new ConnectFourModule)
    val controller = new Controller(new Grid(6,7))
    val tui = new Tui(controller)
    val gui = new SwingGui(controller)

    def main(args: Array[String]): Unit = {
      tui.processInputLineStart()
    }
}