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
    var users = Map.empty[String, User]

    Behaviors.receiveMessage {
      case SendMessage(user, message) =>
        context.log.info(s"Group message from $user: $message")
        // Отправляем сообщения всем пользователям
        context.log.info(s"Now in group is ${users.keys}")
        users.values.foreach { u =>
          context.log.info(s"Sending message to ${u.name}: $message")
          u.ref ! s"$user: $message"
        }
        Behaviors.same

      case JoinGroup(user, replyTo) =>
        context.log.info(s"$user is trying to join the group")
        if (!users.contains(user)) {
          val newUser = User(replyTo, user)
          users += (user -> newUser)
          // Уведомляем пользователя о том, что он успешно присоединился
          replyTo ! s"$user has joined the chat."
          context.log.info(s"$user has joined the group.")
        } else {
          // Уведомляем пользователя о том, что он уже в чате
          replyTo ! s"$user is already in the chat."
          context.log.warn(s"$user is already in the chat.")
        }
        Behaviors.same

      case PeerJoined(peerAddress) =>
        context.log.info(s"New peer joined: $peerAddress")
        Behaviors.same

      case DirectMessage(from, to, message) =>
        users.get(to) match {
          case Some(user) =>
            user.ref ! s"Direct message from $from: $message"
            context.log.info(s"Direct message from $from to $to: $message")
          case None =>
            context.log.warn(s"$to is not in the chat, unable to send direct message from $from")
        }
        Behaviors.same

      case LeaveGroup(user) =>
        if (users.contains(user)) {
          users = users - user
          context.log.info(s"$user has left the group.")
        } else {
          context.log.warn(s"$user attempted to leave the group but was not found.")
        }
        Behaviors.same
    }
  }
}
