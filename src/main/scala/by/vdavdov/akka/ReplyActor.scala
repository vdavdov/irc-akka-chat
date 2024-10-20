package by.vdavdov.akka

import akka.actor.typed.scaladsl.Behaviors
import akka.actor.typed.{ActorRef, Behavior}

// Актор для обработки сообщений от ChatActor
object ReplyActor {
  def apply(): Behavior[String] = Behaviors.receive { (context, message) =>
    // Здесь вы можете обработать входящие сообщения
    context.log.info(s"Received message from ChatActor: $message")
    Behaviors.same
  }
}
