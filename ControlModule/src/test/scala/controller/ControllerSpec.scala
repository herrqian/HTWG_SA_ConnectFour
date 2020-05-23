package main.scala.controller

import PlayerModul.Player
import main.scala.controller.controllerBaseImpl.Controller
import main.scala.model.gridComponent.gridBaseImpl.Grid
import org.scalatest.{Matchers, WordSpec}
class ControllerSpec extends WordSpec with Matchers {
  "a controller" when {
    "observed by an Observer" should {
      val aGrid = new Grid(6, 7)
      var controller = new Controller(aGrid)

      "test the createEmptyGrid function" in {
        controller.createEmptyGrid("Grid Small")
        controller.grid should be(new Grid(6,7))
        controller.createEmptyGrid("Grid Middle")
        controller.grid should be(new Grid(10,11))
        controller.createEmptyGrid("Grid Large")
        controller.grid should be(new Grid(16,17))
      }
      "test the setValueToBottom function" in {
        controller.setValueToBottom(0)
        controller.grid.col(0).cell(15).value should be(1)
      }
      "test the getTrun function" in {
        controller.getTurn(0) should be(Player("Player2"))
      }
      "test the changeTurn function" in {
        controller.changeTurn()
        controller.getTurn(0) should be(Player("Player1"))
      }
      "test the currentPlayer function" in {
        controller.currentPlayer() should be(Player("Player1"))
      }
      "test the undo and redo functions" in {
        controller.undo
        controller.grid.col(0).cell(15).value should be(0)
        controller.redo
        controller.grid.col(0).cell(15).value should be(1)
      }
    }
  }
}