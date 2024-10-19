package by.vdavdov.akka

import akka.actor.typed.scaladsl.Behaviors
import akka.actor.typed.{ActorRef, ActorSystem}
import akka.cluster.typed.{Cluster, Join}
import by.vdavdov.akka.ChatController
import javafx.application.Application
import javafx.fxml.FXMLLoader
import javafx.scene.Scene
import javafx.stage.Stage

import java.util.UUID

object ChatApp {
  def main(args: Array[String]): Unit = {
    Application.launch(classOf[ChatApplication], args: _*)
  }
}

class ChatApplication extends Application {
  override def start(primaryStage: Stage): Unit = {
    // Инициализация Akka Clusters
    val system: ActorSystem[ChatActor.Command] = ActorSystem(ChatActor(), "ChatCluster")
    val cluster = Cluster(system)

    // Присоединение к кластеру
    cluster.manager ! Join(cluster.selfMember.address)

    // Загружаем FXML
    val loader = new FXMLLoader(getClass.getResource("/fxml/chat.fxml"))
    val scene = new Scene(loader.load())

    // Создаем ActorRef для ChatActor.
    val chatActorRef: ActorRef[ChatActor.Command] = system.systemActorOf(ChatActor(), "ChatActor")

    // Генерируем уникальное имя пользователя (или получаем его другой логикой)
    val username = UUID.randomUUID().toString

    val messageReplyActor: ActorRef[String] = system.systemActorOf(MessageReplyActor(), "MessageReplyActor")
    // Обеспечиваем передачу actorRef в контроллер UI
    val controller = loader.getController[ChatController]
    if (controller != null) {
      controller.setActorRef(chatActorRef, username, messageReplyActor)  // Передаем actorRef и имя пользователя
    } else {
      println("Ошибка: контроллер равен null.")
    }

    primaryStage.setTitle("Chat Application")
    primaryStage.setScene(scene)
    primaryStage.show()
  }
}
