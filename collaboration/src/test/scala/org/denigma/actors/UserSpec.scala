package org.denigma.actors


import akka.actor._


import  scala.language.postfixOps

import org.denigma.actors.mock.MockMaster
import org.denigma.actors.base.MainSpec
import play.api.test.Helpers._
import play.api.db.DB
import anorm._
import org.denigma.actors.models.Message
import play.api.test.FakeApplication
import play.api.Play.current

class UserSpec (_system: ActorSystem) extends MainSpec[MockMaster](_system)//extends BasicSpec(_system)
  //extends TestKit(_system) with WordSpecLike with Matchers with BeforeAndAfterAll with ImplicitSender
{
  def this() = this(ActorSystem("MySpec"))

  "Member actor" should{

    addUser(user1,password1)
    addUser(user2,password2)
    addUser(user3,password3)

    "work with database" in {


        var messages = List.empty[Message]


        running(FakeApplication(additionalConfiguration =
          inMemoryDatabase(options=Map("MODE" -> "MySQL")))) {

          DB.withConnection { implicit c =>

            SQL("""
        CREATE TABLE Messages (
          `id` VARCHAR(255) NOT NULL,
          `user` VARCHAR(128) NOT NULL,
          `text` VARCHAR(255) NOT NULL,
          PRIMARY KEY (`id`)
        )
                """
            ).execute()


            val insert = SQL("insert into Messages(id,user,text) values ({id}, {user}, {text})")

            insert.on("id"->"first_id","user"->"first_user","text"->"first_text").execute()


            insert.on("id"->"second_id","user"->"second_user","text"->"second_text").execute()

            val all = SQL("Select * from Messages")

            all().size.shouldEqual(2)


            val two = SQL("Select * from Messages where text = {txt}").on("txt"->"second_text")
            two().size.shouldEqual(1)

            val messages: Stream[Message] = all().collect {
              case Row(id:String,user:String,text:String) =>  Message(id,user,text)
            }
            messages.length.shouldEqual(2)

            insert.on("id"->"third_id","user"->"third_user","text"->"third_text").execute()


            messages.length.shouldEqual(2)

            val m2 = all().collect {
              case Row(id:String,user:String,text:String) =>  Message(id,user,text)
            }

            m2.head.user.shouldEqual("first_user")
            m2.tail.head.user.shouldEqual("second_user")
            m2.tail.tail.head.user.shouldEqual("third_user")

            m2.length.shouldEqual(3)


          }
        }

      }



  }


}
