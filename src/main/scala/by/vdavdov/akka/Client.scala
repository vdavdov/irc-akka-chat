package by.vdavdov.akka

import akka.actor.typed.ActorSystem
import javafx.application.Application
import javafx.fxml.FXMLLoader
import javafx.scene.Scene
import javafx.scene.layout.VBox
import javafx.stage.Stage

object Client extends App {
  Application.launch(classOf[ChatClient], args: _*)
}

class ChatClient extends Application {
  private var system: ActorSystem[Server.Command] = _

  override def start(primaryStage: Stage): Unit = {
    system = ActorSystem(Server(), "ChatSystem")

    val loader = new FXMLLoader(getClass.getResource("/chat.fxml"))
    val root = loader.load().asInstanceOf[VBox]

    primaryStage.setTitle("Chat Client")
    primaryStage.setScene(new Scene(root))
    primaryStage.show()
  }

  override def stop(): Unit = {
    system.terminate()
  }
}