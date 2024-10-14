package by.vdavdov.akka.room

import javafx.fxml.FXML
import javafx.scene.control.{Button, ListView, TextArea, TextField}

class ChatController {
  @FXML private var chatArea: TextArea = _
  @FXML private var messageField: TextField = _
  @FXML private var userList: ListView[String] = _

  def sendMessage(): Unit = {
    val message = messageField.getText
    messageField.clear()
  }
}