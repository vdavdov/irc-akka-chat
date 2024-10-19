package by.vdavdov.akka

import akka.actor.TypedActor.context
import akka.actor.typed.{ActorRef, Behavior}
import akka.actor.typed.scaladsl.Behaviors
import by.vdavdov.akka.ChatActor.User
import javafx.fxml.FXML
import javafx.scene.control.{Button, ListView, TextArea, TextField}

class ChatController {

  @FXML
  private var chatArea: TextArea = _
  @FXML
  private var messageField: TextField = _
  @FXML
  private var sendButton: Button = _
  @FXML
  private var userList: ListView[String] = _
  @FXML
  private var directMessageButton: Button = _
  private var actorRef: ActorRef[ChatActor.Command] = _
  private var username: String = _
  private var actorRefString: ActorRef[String] = _

  // Метод для получения сообщения из ChatActor
  def receiveMessage(message: String): Unit = {
    appendToChatArea(message)
  }

  // Устанавливаем ActorRef и имя пользователя
  def setActorRef(ref: ActorRef[ChatActor.Command], user: String, refString : ActorRef[String]): Unit = {
    actorRef = ref
    username = user
    actorRefString = refString

    actorRef ! ChatActor.JoinGroup(username, actorRefString)

    sendButton.setOnAction(_ => {
      val message = messageField.getText
      if (message.nonEmpty) {
        actorRef ! ChatActor.SendMessage(username, message)
        appendToChatArea(s"You: $message")
        messageField.clear()
      }
    })

    directMessageButton.setOnAction(_ => {
      val selectedUser = userList.getSelectionModel.getSelectedItem
      val message = messageField.getText
      if (selectedUser != null && message.nonEmpty) {
        actorRef ! ChatActor.DirectMessage(username, selectedUser, message)
        appendToChatArea(s"Private to $selectedUser: $message")
        messageField.clear()
      }
    })
  }

  private def appendToChatArea(message: String): Unit = {
    chatArea.appendText(message + "\n")
  }

  def leaveGroup(): Unit = {
    actorRef ! ChatActor.LeaveGroup(username)
    appendToChatArea(s"$username has left the chat.")
  }

  def updateUserList(users: List[String]): Unit = {
    userList.getItems.clear()
    users.foreach(userList.getItems.add)
  }
}
