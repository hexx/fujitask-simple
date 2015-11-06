package fujitask

import java.util.concurrent.{TimeUnit, Executor}

import scala.concurrent.duration.Duration
import scala.concurrent.{Await, ExecutionContext, Future}
import scala.util.{Failure, Success, Try}
import scalaprops._
import scalaz.Equal

object Helper {
  type ATask[A] = Task[Int, A]

  private implicit val dummyExecutor = ExecutionContext.fromExecutor(new BlockingExecutor)

  class BlockingExecutor extends Executor {
    def execute(command: Runnable): Unit = {
      command.run()
    }
  }

  case class ErrorTask(value: Int) extends RuntimeException

  implicit def genErrorTask(implicit I: Gen[Int]): Gen[ErrorTask] =
    I.map(x => ErrorTask(x))

  implicit def equalErrorTask(implicit I: Equal[Int]): Equal[ErrorTask] =
    I.contramap(_.value)

  implicit def equalFuture[A](implicit A: Equal[A], E: Equal[ErrorTask]): Equal[Future[A]] =
    Equal.equal( (a, b) => (
        Try(Await.result(a, Duration(5, TimeUnit.SECONDS))),
        Try(Await.result(b, Duration(5, TimeUnit.SECONDS)))
      ) match {
        case (Success(va), Success(vb)) => A.equal(va, vb)
        case (Failure(ea@(ErrorTask(_))), Failure(eb@ErrorTask(_))) => E.equal(ea, eb)
        case _ => false
      }
    )

  implicit def genTaskRunner[R](implicit R: Gen[R]): Gen[TaskRunner[R]] =
    R.map[TaskRunner[R]] { r => new TaskRunner[R] {
        def run[A](task: Task[R, A]): Future[A] = task.execute(r)
      }
    }

  implicit def equalTask[R, A](implicit F: Equal[Future[A]], R: Gen[TaskRunner[R]]): Equal[Task[R, A]] =
    F.contramap(_.run()(R.sample()))

  implicit def genTask[R, A](implicit A: Gen[R => A], E: Gen[ErrorTask]): Gen[Task[R, A]] =
    Gen.frequency(
      1  -> E.map[Task[R, A]] {e => new Task[R, A] {
              def execute(res: R)(implicit executor: ExecutionContext): Future[A] =
                Future.failed(e)
            }},
      20 -> A.map[Task[R, A]] { f => new Task[R, A] {
              def execute(res: R)(implicit executor: ExecutionContext): Future[A] =
                Future.successful(f(res))
            }}
    )
}
