package by.vdavdov.akka

import akka.actor.typed.{ActorRef, Behavior}
import akka.actor.typed.scaladsl.Behaviors

object ChatActor {

  sealed trait Command
  case class SendMessage(user: String, message: String) extends Command
  case class JoinGroup(user: String, replyTo: ActorRef[String]) extends Command
  case class PeerJoined(peerAddress: String) extends Command
  case class DirectMessage(from: String, to: String, message: String) extends Command
  case class LeaveGroup(user: String) extends Command

  // Структура для хранения данных пользователя
  case class User(ref: ActorRef[String], name: String)

  def apply(): Behavior[Command] = Behaviors.setup { context =>
    // Хранение участников группы
    var users = Set.empty[User]

    Behaviors.receiveMessage {
      case SendMessage(user, message) =>
        // Отправка сообщения всем пользователям группы
        context.log.info(s"Group message from $user: $message")
        users.foreach { u =>
          u.ref ! s"$user: $message" // Отправка сообщения на ActorRef участника
        }
        Behaviors.same

      case JoinGroup(user, replyTo) =>
        val newUser = User(replyTo, user) // Создаем нового пользователя с его ActorRef
        users += newUser
        replyTo ! s"$user has joined the chat."
        context.log.info(s"$user has joined the group.")
        Behaviors.same

      case PeerJoined(peerAddress) =>
        context.log.info(s"New peer joined: $peerAddress")
        Behaviors.same

      case DirectMessage(from, to, message) =>
        // Обработка прямых сообщений
        context.log.info(s"Direct message from $from to $to: $message")
        // Здесь может быть логика отправки сообщений конкретному участнику
        Behaviors.same

      case LeaveGroup(user) =>
        // Удаляем пользователя при выходе
        users = users.filterNot(_.name == user)
        context.log.info(s"$user has left the group.")
        Behaviors.same
    }
  }
}
