package org.denigma.data

/**
 * Currently stores only path to the database
 */
trait SesameConfig
{

  def home: String = System.getProperty("user.home")
  def repo: String = ".aduna/openrdf-sesame/repositories"
  def dbName = "genes"
  def db = s"$home/$repo/$dbName"
}
