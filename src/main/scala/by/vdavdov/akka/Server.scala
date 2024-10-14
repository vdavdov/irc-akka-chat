package by.vdavdov.akka

import akka.actor.typed.scaladsl.Behaviors
import akka.actor.typed.{ActorRef, Behavior}
import akka.cluster.typed.{Cluster, Join}

import scala.collection.mutable

object Server {
  sealed trait Command
  private final case class JoinChat(user: String, replyTo: ActorRef[Command]) extends Command
  private final case class SendMessage(user: String, message: String) extends Command
  private final case class ChatHistory(chatHistory: Vector[String]) extends Command

  def apply(): Behavior[Command] = Behaviors.setup { context =>
    Cluster(context.system).manager ! Join(Cluster(context.system).selfMember.address)

    val users = mutable.Map[String, ActorRef[Command]]()
    var chatHistory = Vector[String]()

    Behaviors.receiveMessage {
      case JoinChat(user, replyTo) =>
        users += (user -> replyTo)
        replyTo ! ChatHistory(chatHistory)
        Behaviors.same

      case SendMessage(user, message) =>
        val msg = s"$user: $message"
        chatHistory :+= msg
        users.values.foreach(_ ! SendMessage(user, msg))
        Behaviors.same
    }
  }
}