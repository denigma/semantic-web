package controllers.endpoints

import controllers.PJaxPlatformWith
import org.openrdf.model.Value
import scala.concurrent.duration._
import spray.caching.{LruCache, Cache}

import scala.util.Try

/**
 * Tools like sparql and paper viewer
 */
object MainEndpoint extends PJaxPlatformWith("datagrid")
  with ExploreEndpoint
  with ModelEndpoint //with ShapeEndpoint

