package domain.repository.scalikejdbc

import scalikejdbc._
import scalikejdbc.config._

object DomainDB {

  def setup() = DBs.setupAll()

  def close() = DBs.closeAll()

  val userDdl = sql"""
    create table if not exists `users` (
      `id` bigint not null auto_increment,
      `name` varchar(64) not null
    )
  """

  val messageDdl = sql"""
    create table if not exists `messages` (
      `id` bigint not null auto_increment,
      `message` varchar(256) not null,
      `user_name` varchar(64) not null
    )
  """

  def createTables() = DB localTx { implicit s =>
    userDdl.execute.apply()
    messageDdl.execute.apply()
  }

}
