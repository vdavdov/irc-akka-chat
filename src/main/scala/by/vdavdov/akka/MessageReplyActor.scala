package by.vdavdov.akka

import akka.actor.typed.{ActorRef, Behavior}
import akka.actor.typed.scaladsl.Behaviors

object MessageReplyActor {
  def apply(): Behavior[String] = Behaviors.receiveMessage { message =>
    println(message) // Или отправьте сообщение в ваше текстовое поле, если это возможно
    Behaviors.same
  }

  def start(): Unit = {

  }
}
