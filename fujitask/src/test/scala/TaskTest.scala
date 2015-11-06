package fujitask

import scalaprops._
import scalaz._
import scalaz.std.anyVal._
import Helper._

object TaskTest extends Scalaprops {

  implicit def monadTask[R, A] = new Monad[({type L[B] = Task[R, B]})#L] {
    def point[B](a: => B): Task[R, B] = Task(a)
    def bind[B, C](a: Task[R, B])(f: B => Task[R, C]): Task[R, C] = a.flatMap(f)
  }

  type IntTask[A] = Task[Int, A]
  val intTaskMonadLawsTest = Properties.list(
    scalazlaws.monad.all[IntTask]
  )

  type BooleanTask[A] = Task[Boolean, A]
  val booleanTaskMonadLawsTest = Properties.list(
    scalazlaws.monad.all[BooleanTask]
  )
}
