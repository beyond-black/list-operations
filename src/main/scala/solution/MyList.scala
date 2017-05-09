package solution

import java.util.NoSuchElementException

import scala.annotation.tailrec

/** Factory for [[MyList]] instances. */
object MyList {
  /** Creates a [[MyList]] with given items.
    *
    * @param items what to put in the list
    * @tparam T type of items in the list
    * @return new list containing the items in the order given
    */
  def apply[T](items: T*): MyList[T] = items match {
    case Seq() => NilList
    case v +: vs => v :: MyList(vs: _*)
  }
}

/** Simple immutable cons list.
  *
  * Should be constructed using [[MyList]] companion object.
  *
  * Has two implementations - [[NilList]] representing
  * a list with no items in it, and [[ConsList]] representing a list with at least one item and composed of
  * a head and a tail (which may be [[NilList]]).
  */
sealed trait MyList[+T] {
  /** Indicates if there are any items in this list. */
  def isEmpty: Boolean

  /** Returns the first item from this list.
    *
    * For example:
    * {{{
    * scala> MyList(1,2,3).head
    * res1: Int = 1
    * }}}
    *
    * @throws scala.NoSuchElementException if this list is empty.
    */
  def head: T

  /** Returns everything except the first item from this list.
    *
    * For example:
    * {{{
    * scala> MyList(1,2,3).tail
    * res1: solution.MyList[Int] = ConsList(2,ConsList(3,solution.NilList$@111ef33))
    * }}}
    *
    * @return a new [[MyList]] containing the rest of the list, [[NilList]] if this list only has a head.
    * @throws scala.NoSuchElementException if this list is empty.
    */
  def tail: MyList[T]

  /** Applies a function to each item in this list, returning a list of the results.
    *
    * Items are processed in order, from left to right.
    *
    * For example:
    * {{{
    * scala> MyList(1,2,3).map(_ + 1)
    * res1: solution.MyList[Int] = ConsList(2,ConsList(3,ConsList(4,solution.NilList$@111ef33)))
    * }}}
    *
    * @param f Function transforming a list item to the result type
    * @tparam U type of the resulting items
    * @return a new [[MyList]] containing the results in the same order as items processed.
    */
  def map[U](f: (T) => U): MyList[U]

  /** Reverses this list.
    *
    * For example:
    * {{{
    * scala> MyList(1,2,3).reverse
    * res1: solution.MyList[Int] = ConsList(3,ConsList(2,ConsList(1,solution.NilList$@111ef33)))
    * }}}
    *
    * @return a new [[MyList]] containing the reversed items, [[NilList]] if this list is empty.
    */
  def reverse: MyList[T]

  /** Filters this list for items that match the given predicate.
    *
    * For example:
    * {{{
    * scala> MyList(1,2,3).filter(_ % 2 == 0)
    * res1: solution.MyList[Int] = ConsList(2,solution.NilList$@111ef33)
    * }}}
    *
    * @param f predicate against which to test each item
    * @return a new [[MyList]] containing items for which the predicate returns true
    */
  def filter(f: (T) => Boolean): MyList[T]

  /** Reduces this list to a single value given a start value.
    *
    * Items are processed in turn from left to right, given a start value and a function which knows
    * how to combine an accumulated value with the next list item to give a new accumulated value. The
    * accumulator is passed along as each item is processed and can be used to remember a list of results,
    * reduce items to a single value or whatever is required by the caller. The caller supplies an
    * initial value for the accumulator, and it is this value that determines the return type of foldLeft.
    *
    * For example:
    * {{{
    * scala> MyList(1,2,3).foldLeft(1)(_ + _)
    * res1: Int = 7
    *
    * scala> MyList[Int]().foldLeft(1)(_ + _)
    * res2: Int = 1
    * }}}
    *
    * @param accumulator the start value
    * @param f           a function combining the accumulator and the next list item to give a new accumulator
    * @tparam U type of the accumulator and result
    * @return a new [[MyList]] containing the final accumulator value, which is the initial value if this list is empty.
    */
  def foldLeft[U](accumulator: U)(f: (U, T) => U): U

  /** Prepends an item onto this list.
    *
    * Item must be this type or one of its supertypes. Item type of the resulting list will
    * be determined by the type of the item being added.  If it is a supertype of this list's
    * items, the result type will be the supertype.  This is so that we can speak with certainty
    * about the behaviour of a list's items at all times.
    *
    * @param item the item to prepend
    * @tparam U the type of [[MyList]] returned
    * @return a new [[MyList]] with this item prepended onto the items in this list
    */
  def ::[U >: T](item: U): MyList[U]
}

/** An empty [[MyList]]. */
object NilList extends MyList[Nothing] {
  def isEmpty = true

  def head = throw new NoSuchElementException("NilList cannot have a head")

  def tail = throw new NoSuchElementException("NilList cannot have a tail")

  def map[U](f: (Nothing) => U): MyList[U] = NilList

  def reverse = NilList

  def filter(f: (Nothing) => Boolean) = NilList

  def foldLeft[U](accumulator: U)(f: (U, Nothing) => U) = accumulator

  def ::[U >: Nothing](v: U): MyList[U] = ConsList(v)
}

/** A [[MyList]] containing something.
  *
  * Performance:
  *
  * head = O(1) and tail = O(1) as both are simple val accesses.
  *
  * :: = O(1) as it is simple construction of a new ConsList.
  *
  * reverse = O(n) as result is built by repeatedly prepending head and processing tail, all O(1),
  * for each tail in the ConsList.
  *
  * map and filter both = O(n), assuming f is O(1)
  * However, both involve O(n) accumulation of results using O(1) prepend per item,
  * followed by a final O(n) reversal of results to return them to their proper order.
  * This could be improved using a data structure like scala's ListBuffer
  * which keeps a reference to its last item and is backed by a [[ConsList]] with mutable (package
  * private var) tail.  This would give an O(1) append operation, so avoiding the need for a reversal,
  * and also avoid an additional O(n) space as the backing [[ConsList]] could be returned directly.
  * This would come at the cost of complexity and potentially thread safety which needs to be taken
  * into consideration due to mutable state.
  *
  * foldLeft = O(n), assuming f is O(1), as each item is processed once.
  *
  * Implementations avoid potential for stack overflow on very large lists by
  * taking advantage of tail recursion optimization.
  */
final case class ConsList[T](head: T, tail: MyList[T] = NilList) extends MyList[T] {
  def isEmpty = false

  def reverse = reverse(NilList, this)

  @tailrec
  private def reverse(accumulator: MyList[T], list: MyList[T]): MyList[T] =
    if (list.isEmpty) accumulator
    else reverse(list.head :: accumulator, list.tail)

  def map[U](f: (T) => U): MyList[U] = map(NilList, this, f)

  @tailrec
  private def map[U](accumulator: MyList[U], list: MyList[T], f: (T) => U): MyList[U] =
    if (list.isEmpty) accumulator.reverse
    else map(f(list.head) :: accumulator, list.tail, f)

  def filter(f: (T) => Boolean) = filter(NilList, this, f)

  @tailrec
  private def filter(accumulator: MyList[T], list: MyList[T], f: (T) => Boolean): MyList[T] =
    if (list.isEmpty) accumulator.reverse
    else f(list.head) match {
      case true => filter(list.head :: accumulator, list.tail, f)
      case false => filter(accumulator, list.tail, f)
    }

  def foldLeft[U](accumulator: U)(f: (U, T) => U): U = foldLeft(accumulator, f, this)

  @tailrec
  private def foldLeft[U](accumulator: U, f: (U, T) => U, list: MyList[T]): U =
    if (list.isEmpty) accumulator
    else foldLeft(f(accumulator, list.head), f, list.tail)

  def ::[U >: T](v: U): MyList[U] = ConsList(v, this)
}