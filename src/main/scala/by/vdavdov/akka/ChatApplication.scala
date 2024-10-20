package by.vdavdov.akka

import akka.actor.Address
import akka.actor.typed.{ActorRef, ActorSystem}
import akka.cluster.typed.{Cluster, Join}
import javafx.application.Application
import javafx.fxml.FXMLLoader
import javafx.scene.Scene
import javafx.stage.Stage

import java.util.UUID

object ChatApp {
  // Храним ActorSystem и ActorRef для одного экземпляра
  private var system: ActorSystem[ChatActor.Command] = _
  private var chatActorRef: ActorRef[ChatActor.Command] = _


  def main(args: Array[String]): Unit = {
    // Инициализация ActorSystem и ChatActor
    system = ActorSystem(ChatActor(), "ChatCluster")
    chatActorRef = system.systemActorOf(ChatActor(), "ChatActor")

    // Присоединение к кластеру
    val cluster = Cluster(system)


    cluster.manager ! Join(Address("akka", "ChatCluster", "127.0.0.1", 2553)) // и тут меняю каждый раз

    Application.launch(classOf[ChatApplication], args: _*)
  }

  // Метод для доступа к ActorRef
  def getChatActor: ActorRef[ChatActor.Command] = chatActorRef
  def getReplyActor: ActorRef[String] = system.systemActorOf(ReplyActor(), "ReplyActor")
}

class ChatApplication extends Application {

  override def start(primaryStage: Stage): Unit = {
    // Загружаем FXML
    val loader = new FXMLLoader(getClass.getResource("/fxml/chat.fxml"))
    val scene = new Scene(loader.load())

    // Генерируем уникальное имя пользователя
    val username = UUID.randomUUID().toString

    // Получаем ActorRef для ChatActor
    val chatActorRef = ChatApp.getChatActor

    // Передаем actorRef и имя пользователя в контроллер UI
    val replyActor : ActorRef[String] = ChatApp.getReplyActor
    val controller = loader.getController[ChatController]
    if (controller != null) {
      controller.setActorRef(chatActorRef, username, replyActor) // Передаем ActorRef и имя пользователя
    } else {
      println("Ошибка: контроллер равен null.")
    }

    // Настраиваем и показываем основное окно
    primaryStage.setTitle("Chat Application")
    primaryStage.setScene(scene)
    primaryStage.show()
  }
}
