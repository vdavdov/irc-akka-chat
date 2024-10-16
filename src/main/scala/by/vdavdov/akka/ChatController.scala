package by.vdavdov.akka

import javafx.fxml.FXML
import javafx.scene.control.{Button, ListView, TextArea, TextField}
import akka.actor.typed.ActorRef
import akka.actor.typed.scaladsl.AskPattern._
import akka.util.Timeout
import scala.concurrent.duration._
import scala.concurrent.ExecutionContext.Implicits.global

class ChatController {

  @FXML
  private var chatArea: TextArea = _
  @FXML
  private var messageField: TextField = _
  @FXML
  private var sendButton: Button = _
  @FXML
  private var userList: ListView[String] = _
  private var actorRef: ActorRef[ChatActor.Command] = _

  implicit val timeout: Timeout = 3.seconds

  // Устанавливаем ActorRef
  def setActorRef(ref: ActorRef[ChatActor.Command]): Unit = {
    actorRef = ref

    // Действие для отправки сообщения
    sendButton.setOnAction(_ => {
      val message = messageField.getText
      if (message.nonEmpty) {
        actorRef ! ChatActor.SendMessage("User", message)
        appendToChatArea(s"You: $message")
        messageField.clear()
      }
    })

    // При добавлении тестовой логики: присоединение к группе
//    joinGroup("User")

    // Например, также можно добавить действие при выходе из группы
    // реализовав это в другом месте UI
  }

  // Метод для добавления сообщения в окно чата
  private def appendToChatArea(message: String): Unit = {
    chatArea.appendText(message + "\n")
  }

  // Присоединение к группе
//  def joinGroup(username: String): Unit = {
//    // Отправка сообщения о присоединении в чат
//    actorRef ! ChatActor.JoinGroup(username, actorRef)
//    appendToChatArea(s"$username has joined the chat.")
//  }

  // Можно создать метод для обработки выхода из группы
  def leaveGroup(username: String): Unit = {
    actorRef ! ChatActor.LeaveGroup(username)
    appendToChatArea(s"$username has left the chat.")
  }

  // Метод для отправки прямого сообщения
  def sendDirectMessage(to: String, message: String): Unit = {
    if (message.nonEmpty) {
      actorRef ! ChatActor.DirectMessage("User", to, message)
      appendToChatArea(s"Private to $to: $message")
    }
  }
}
