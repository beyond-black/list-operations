package solution

import org.scalatest.{FreeSpec, Matchers}

class MyListTest extends FreeSpec with Matchers {
  "apply should" - {
    "be NilList if there are no values" in {
      MyList() shouldBe NilList
    }

    "be a one element list if there is only one value" in {
      MyList(0) shouldBe ConsList(0)
    }

    "be an ordered list if there is more than one value" in {
      MyList(0, 1, 2) shouldBe ConsList(0, ConsList(1, ConsList(2)))
    }
  }

  "isEmpty should" - {
    "be false if there are no elements" in {
      MyList().isEmpty shouldBe true
    }

    "be true if there are elements" in {
      MyList(0).isEmpty shouldBe false
    }
  }

  ":: should" - {
    "prepend a value onto empty list" in {
      0 :: NilList shouldBe MyList(0)
    }

    "prepend a value onto a list" in {
      0 :: MyList(1) shouldBe MyList(0, 1)
    }
  }

  "head should" - {
    "be rejected if there are no elements" in {
      val thrown = intercept[NoSuchElementException] {
        NilList.head
      }
      thrown.getMessage shouldBe "NilList cannot have a head"
    }

    "return its first element" in {
      MyList(0, 1, 2).head shouldBe 0
    }
  }

  "tail should" - {
    "be rejected if there are no elements" in {
      val thrown = intercept[NoSuchElementException] {
        NilList.tail
      }
      thrown.getMessage shouldBe "NilList cannot have a tail"
    }

    "give back all elements other than the fist one" in {
      MyList(0, 1, 2).tail shouldBe MyList(1, 2)
    }
  }

  "map should" - {
    "be empty for empty list" in {
      MyList().map(_ => 0) shouldBe MyList()
    }

    "apply f to a single value" in {
      MyList(0).map(v => s"applied-to-$v") shouldBe MyList("applied-to-0")
    }

    "apply f to every value" in {
      MyList(0, 1, 2).map(v => s"applied-to-$v") shouldBe MyList("applied-to-0", "applied-to-1", "applied-to-2")
    }

    "demonstrate challenge expectations" - {
      "it should give back different type" in {
        MyList("foo","bar","baz","boom").map((x) => x.length) shouldBe MyList(3,3,3,4)
      }

      "it should give back same type" in {
        MyList("foo","bar","baz").map((x) => x.toUpperCase) shouldBe MyList("FOO","BAR","BAZ")
      }
    }
  }

  "reverse should" - {
    "be empty for empty list" in {
      MyList().reverse shouldBe MyList()
    }

    "give back a single value" in {
      MyList(0).reverse shouldBe MyList(0)
    }

    "reverse all values" in {
      MyList(0, 1, 2).reverse shouldBe MyList(2, 1, 0)
    }

    "demonstrate challenge expectations" - {
      "it should reverse values" in {
        MyList(1,2,3,4).reverse shouldBe MyList(4,3,2,1)
      }
    }
  }

  "filter should" - {
    "be empty for empty list" in {
      MyList().filter(_ => true) shouldBe MyList()
    }

    "give back a passing value" in {
      MyList(0).filter(_ => true) shouldBe MyList(0)
    }

    "filter out a failing value" in {
      MyList(0).filter(_ => false) shouldBe MyList()
    }

    "filter out values" in {
      MyList(0, 1, 2, 3).filter(Seq(1, 2).contains) shouldBe MyList(1, 2)
    }

    "demonstrate challenge expectations" - {
      "it should filter out odd values" in {
        MyList(1,2,3,4).filter((x) => x % 2 == 0) shouldBe MyList(2,4)
      }
    }
  }

  "foldLeft should" - {
    "give back starting accumulator value if empty" in {
      MyList().foldLeft(Seq())(_ :+ _) shouldBe Seq()
    }

    "accumulate one value" in {
      MyList(0).foldLeft(Seq[Int]())(_ :+ _) shouldBe Seq(0)
    }

    "accumulate values" in {
      MyList(0, 1, 2).foldLeft(Seq[Int]())(_ :+ _) shouldBe Seq(0, 1, 2)
    }

    "demonstrate challenge expectations" - {
      "it should accumulate values to give a different type" in {
        MyList("foo","bar","baz","boom").foldLeft(0)((a,x) => a + x.length) shouldBe 13
      }

      "it should accumulate values to give the same type" in {
        MyList("foo","bar","baz").foldLeft("")((a,x) => a + x) shouldBe "foobarbaz"
      }
    }
  }
}