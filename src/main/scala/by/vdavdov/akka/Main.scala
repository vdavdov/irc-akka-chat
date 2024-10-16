package by.vdavdov.akka

import akka.actor.typed.ActorSystem
import akka.cluster.typed.Cluster
import akka.cluster.typed.Join
import javafx.application.Application
import javafx.fxml.FXMLLoader
import javafx.scene.Scene
import javafx.stage.Stage

object ChatApp {
  def main(args: Array[String]): Unit = {
    Application.launch(classOf[ChatApplication], args: _*)
  }
}

class ChatApplication extends Application {
  override def start(primaryStage: Stage): Unit = {
    val loader = new FXMLLoader(getClass.getResource("/fxml/chat.fxml"))
    val scene = new Scene(loader.load())

    primaryStage.setTitle("Chat Application")
    primaryStage.setScene(scene)
    primaryStage.show()

    // Initialize Akka Clusters
    val system: ActorSystem[ChatActor.Command] = ActorSystem(ChatActor(), "ChatCluster")
    val cluster = Cluster(system)

    // Join the cluster
    cluster.manager ! Join(cluster.selfMember.address)

    // Provide the actor reference to the UI Controller
    loader.getController[ChatController].setActorRef(system)
  }
}
