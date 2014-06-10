package org.denigma.semantic.commons

import play.api.LoggerLike
import org.scalax.semweb.commons.LogLike


case class AppLogger(logger: org.slf4j.Logger) extends LoggerLike with LogLike