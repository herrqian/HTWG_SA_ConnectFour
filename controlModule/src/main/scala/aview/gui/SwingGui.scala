//package main.scala.aview.gui
//
//
//import controller._
//
//import scala.swing._
//import scala.swing.event._
//
//class CellClicked(val row: Int, val column: Int) extends Event
//
//class SwingGui(controller: ControllerInterface) extends Frame {
//  listenTo(controller)
//  var cells = Array.ofDim[CellPanel](controller.getGridRow, controller.getGridCol)
//  title = "Connect Four"
//
//  def gridPanel = new GridPanel(controller.getGridRow, controller.getGridCol) {
//    for {
//      row <- 0 until controller.getGridRow
//      column <- 0 until controller.getGridCol
//    } {
//      val cellpanel = new CellPanel(row,column,controller)
//      cells(row)(column) = cellpanel
//      contents += cellpanel
//      listenTo(cellpanel)
//    }
//  }
//
//  val statusline = new Label(){text = "Player " + controller.currentPlayer().toString + " it's your Turn!"}
//
//  contents = new BorderPanel {
//    add(gridPanel, BorderPanel.Position.Center)
//    add(statusline, BorderPanel.Position.North)
//  }
//
//  menuBar = new MenuBar {
//    contents += new Menu("File") {
//      mnemonic = Key.F
//      contents += new MenuItem(Action("New") { controller.createEmptyGrid("Grid Small") })
//      contents += new MenuItem(Action("Save") {controller.save})
//      contents += new MenuItem(Action("Load"){controller.load})
//      contents += new MenuItem(Action("Quit") { System.exit(0) })
//    }
//    contents += new Menu("Edit") {
//      mnemonic = Key.E
//      contents += new MenuItem(Action("Undo") { controller.undo })
//      contents += new MenuItem(Action("Redo") { controller.redo })
//    }
//    contents += new Menu("Options") {
//      mnemonic = Key.O
//      contents += new MenuItem(Action("Small") { controller.createEmptyGrid("Grid Small") })
//      contents += new MenuItem(Action("Middle") { controller.createEmptyGrid("Grid Middle") })
//      contents += new MenuItem(Action("Large") { controller.createEmptyGrid("Grid Large") })
//    }
//  }
//
//  visible =true
//  redraw
//
//  reactions += {
//    case event: GridSizeChanged => {
//      val row = controller.getGridRow
//      val col = controller.getGridCol
//      resize(row,col)
//    }
//    case event: GridChanged => redraw
//    case event: CellChanged     => redraw
//    case event: WinEvent        => printWinner()
//    case event: LoadError => Dialog.showMessage(contents.head,event.e, "LoadError")
//    case event: SaveError => Dialog.showMessage(contents.head, event.e, "SaveError")
//    case event: SetError => Dialog.showMessage(contents.head, event.e,"SetError")
//  }
//
//  def resize(gridrow: Int, gridcol:Int) = {
//    cells = Array.ofDim[CellPanel](controller.getGridRow, controller.getGridCol)
//    statusline.text = controller.currentPlayer() + " it's your Turn!"
//    contents = new BorderPanel {
//      add(gridPanel, BorderPanel.Position.Center)
//      add(statusline, BorderPanel.Position.North)
//    }
//  }
//
//  def redraw = {
//    for {
//      row <- 0 until controller.getGridRow
//      column <- 0 until controller.getGridCol
//    } cells(row)(column).redraw
//    statusline.text = controller.currentPlayer() + " it's your Turn!"
//    repaint
//  }
//
//  def printWinner() = {
//    statusline.text = controller.currentPlayer() + " won!"
//    repaint
//  }
//}