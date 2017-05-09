# list-operations

My solution to a coding / API design coding challenge.

## The Challenge

Implement a generic List data structure supporting at least the following operations: reverse, filter[1], map[2], foldLeft[3].

We are interested in your data structure design choices, so create your list from your language primitives, do not wrap an  already existing data structure or container such as ArrayList.

The expected behavior:
```
MyList(1,2,3,4).reverse
> MyList(4,3,2,1)

MyList(1,2,3,4).filter((x) => x % 2 == 0)
> MyList(2,4)

MyList("foo","bar","baz","boom").map((x) => x.length)
> MyList(3,3,3,4)

MyList("foo","bar","baz").map((x) => x.toUpperCase)
> MyList("FOO","BAR","BAZ")

MyList("foo","bar","baz","boom").foldLeft(0)((a,x) => a + x.length)
> 13

List("foo","bar","baz").foldLeft("")((a,x) => a + x)
> "foobarbaz"
```
BONUS QUESTIONS:
1. What are the computational complexities for the operations that you have implemented
2. What guarantees your implementation provide regarding concurrent access and modification
3. For a very large list how could you decompose the problem over many nodes

## Answers to Bonus Questions

_1. What are the computational complexities for the operations that you have implemented_

I've documented this in the scaladoc of MyList, as follows:

```
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
```
 
_2. What guarantees your implementation provide regarding concurrent access and modification_

MyList is immutable - there is no variable state and each operation returns a new MyList instance, making it safe for concurrent access and mofication.

_3. For a very large list how could you decompose the problem over many nodes_

MyList is in effect a singly linked list, and could be extended / modified such that different portions of the list could be store on different nodes. However, in processing the list, it must be walked so it does not lend itself to processing different chunks in parallel. For very large problems and to allow for distribution and parallelization, a different data structure akin to Scala's Vector would be more suitable, such that the MyList implementation is an n-ary tree where each node stores n references to child nodes or n peices of data.  Operations could then be writen so that processing is farmed out in parallel on different chunks of the tree and recombined at the end.
