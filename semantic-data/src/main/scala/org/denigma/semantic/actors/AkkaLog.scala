package org.denigma.semantic.actors

import org.denigma.semantic.commons.LogLike

/**
 * A wrapper on top of akka logger for LogLike interface for database-related classes as Akka logger has different methods than play logger
@param logger akka logger
 */
case class AkkaLog(logger: akka.event.LoggingAdapter) extends LogLike{
  override def error(message: => String, error: => Throwable): Unit = logger.error(error,message)

  override def error(message: => String): Unit = logger.error(message)

  override def warn(message: => String, error: => Throwable): Unit = logger.warning(message,error)

  override def warn(message: => String): Unit = logger.warning(message)

  override def info(message: => String, error: => Throwable): Unit = logger.info(message,error)

  override def info(message: => String): Unit = logger.info(message)

  override def debug(message: => String, error: => Throwable): Unit = logger.debug(message,error)

  override def debug(message: => String): Unit = logger.debug(message)


  override def isErrorEnabled: Boolean = logger.isErrorEnabled

  override def isWarnEnabled: Boolean = logger.isWarningEnabled

  override def isInfoEnabled: Boolean = logger.isInfoEnabled

  override def isDebugEnabled: Boolean = logger.isDebugEnabled

}