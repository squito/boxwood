import org.scalatest.FunSuite
import org.scalatest.matchers.ShouldMatchers

/**
 *
 */

class StackableTraits extends FunSuite with ShouldMatchers {
  test("vars in stackable traits") {
    val a = new FooImpl2
    a.mName should be (null)
    a.foo()
    a.mName should be ("M")


    val b = new FooImpl3
    b.mName should be (null)
    b.qName should be (null)
    b.foo()
    b.mName should be ("M")
    b.qName should be ("Q")
  }

  test("stackable traits with vars and params") {
    val a = new BarImpl2
    a.x should be (0)
    a.bar(3) should be (8)
    a.x should be (3)
    a.bar(0) should be (5)
    a.x should be (0)

    val b = new BarImpl3
    b.x should be (0)
    b.y should be (0)
    b.bar(35) should be (57)
    b.x should be (52)
    b.y should be (35)
  }
}

trait Foo {
  def foo()
}

trait M extends Foo {
  var mName : String = _
  abstract override def foo() {
    println("M")
    mName = "M"
    super.foo()
  }
}

trait Q extends Foo {
  var qName : String = _
  abstract override def foo() {
    println("Q")
    qName = "Q"
    super.foo()
  }
}

class FooImpl1 extends Foo {
  override def foo() {println("Impl")}
}

class FooImpl2 extends FooImpl1 with M

class FooImpl3 extends FooImpl1 with M with Q


trait Bar {
  def bar(i: Int): Int
}

trait X extends Bar {
  var x = 0
  abstract override def bar(i:Int) = {
    x = i
    super.bar(i + 5)
  }
}


trait Y extends Bar {
  var y = 0
  abstract override def bar(i: Int) = {
    y = i
    super.bar(i + 17)
  }
}

class BarImpl extends Bar {
  override def bar(i: Int) = {println("i = " + i); i}
}

class BarImpl2 extends BarImpl with X
class BarImpl3 extends BarImpl with X with Y