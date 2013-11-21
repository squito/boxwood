Boxwood
=======

Memory-efficient & fast storage of data (like an array); named-fields with type-checking
(like a case class). Particularly useful for numeric calculations over a well-defined
space of features.

Usage
-----

Define a trait for each "group" of features that extends `FeatureSet`.  Be sure to use
`abstract override` when you define `setOffsetIndex`

    import com.quantifind.boxwood._

    trait SodaSize extends FeatureSet {
      var sodaSizeStartIdx: Int = _
      abstract override def setOffsetIndex(idx: Int) = {
        sodaSizeStartIdx = idx
        super.setOffsetIndex(idx + 3)
      }
      def small = sodaSizeStartIdx
      def medium = sodaSizeStartIdx + 1
      def large = sodaSizeStartIdx + 2
    }

    trait Entree extends FeatureSet {
      var entreeStartIdx: Int = _
      abstract override def setOffsetIndex(idx: Int) = {
        entreeStartIdx = idx
        super.setOffsetIndex(idx + 2)
      }
      def hotdog = entreeStartIdx
      def hamburger = entreeStartIdx + 1
    }


Combine all the feature sets you want by mixing them into `BaseFeatureSet`.  You then get a unique
index for each feature, which you can use in an array.  The magic is that you can both work with
the data as an array if you like, and also access specific elements in a type-safe manner.


    object LunchOrder extends BaseFeatureSet with SodaSize with Entree

    val engLunchOrder = new FeatureSetArray[Int, LunchOrder.type](LunchOrder)
    //... get lunch order from engineers
    engLunchOrder(LunchOrder.hotdog) = 3
    engLunchOrder(LunchOrder.hamburger) = 1
    engLunchOrder(LunchOrder.medium) = 4

    val salesLunchOrder = new FeatureSetArray[Int, LunchOrder.type](LunchOrder)
    //... get lunch order from salespeople
    salesLunchOrder(LunchOrder.hotdog) = 3
    salesLunchOrder(LunchOrder.small) = 1
    salesLunchOrder(LunchOrder.large) = 2

    //combine the order together.  note how we can loop over the fields easily
    val companyOrder = new FeatureSetArray[Int, LunchOrder.type](
      engLunchOrder.arr.zip(salesLunchOrder.arr).map{case(eng, sales) => eng+sales},
      LunchOrder
    )

    def giveOrderToSamTheHotDogVendor(order: FeatureSetArray[Int,Entree]) {
      //access the hot dog total in a type-safe way.  Note that we don't really care
      // what the index of hotdogs is, or what else is in the array
      println("Sam, please give me " + order(order.featureSet.hotdog) + " hotdogs")
    }

    giveOrderToSamTheHotDogVendor(companyOrder)


We could add more "groups" of features to `LunchOrder`, and the code above would still work.  We'd still
sum the sub-order together in the same way, and we'd still access specific elements from the order the same
way.

See the unit tests for more examples.

What's the Point?
------

At this point, you're probably thinking the example is neat, but why bother?  You might think of 3 alternatives --
a case class, a map, or an array.  All of them have some faults which Boxwood corrects.

First, lets consider a case class.  Why didn't I just define:

    case class EntreeOrder(hamburgers:Int, hotdogs: Int)

and just directly store the number of hamburgers and hotdogs order in that class?  Now I don't have a simple way to add orders
together.  Sure, I can define `+=`, but I've got to do that for every group of features I add.  (Maybe the executives
also get order an Appetizer and a Dessert.)

So how about a simple `Map[String,Int]`?  That way, I can just throw whatever I want in there, and its easy to define a `+=` which
adds the values for each key together.  However, I've now thrown type-safety out the window.  The compiler wouldn't prevent me
from using "hotdog" in one place, and "HotDog" in another.  Sure, I could define constants, but when I pass the map around, the consumers
wouldn't have any way of knowing what the valid set of keys were.

Furthermore, both the case class and Map solution have a problem that is very important for numerical computing.  They are horribly
inefficient in memory.  In the silly examples above, it doesn't matter, but imagine that you're iterating thousands of times over
100s of millions of data points.  (Eg., I'm trying to train a regressor from an individual's lunch order to their productivity, using data
from the entire US population.)  You want that data to be very compact, so that its fit in memory, and you want to be occupy contiguous blocks of
memory, to maximize cache-locality.

So, why don't we just use a plain old array?  It would solve the performance problems, but now we've completely thrown type-safety out
the window.  How do you know what's in each slot of the array?  Again, you could define constants, but you're left with two problems.  (1) How
could you mix-and-match various feature sets together, without having to redefine the constants each time?  (2) How do you "annotate" each
array with its set of features, so that downstream code can still access specific elements in a type-safe way?

Boxwood is the solution to those problems.

Why Not Use EnumMaps?
------ 

Java's EnumMaps might seem like a pretty good solution.  You get type-safe access since you can only request values associated with an enum, and
the map is backed by an array, so its efficient.  However, they re insufficient for two reasons.  (1) You can't make an EnumMap which combines multiple
enums together.  For example, if I made a separate enum for Entree and Soda, one EnumMap couldn't store both.  (2) Though they are backed by an array,
you can't work with that array directly.  This may seem minor, but again its a big deal for numeric computing.  If I have a method to take the dot
product of two arrays, I need to give it an array (I don't even want an implicit that copies data into a new array).


Enums and EnumMaps are still great at what they do, though, and they led us to create the EnumUnion type and associated FeatureSet.


Gotchas
-------

Be sure to set the "initial" value of each trait's offset holder to `_`, not to `0`.  For example, this won't work:

    trait Blah extends FeatureSet {
      //the initializer here will run *after* the concrete class's initializer, so the startIdx will 
      // get set back to 0 after a call to setOffsetIndex has set it to the right value
      var blahStartIdx = 0
      abstract override def setOffsetIndex(myIndex: Int) = {
        blahStartIdx = myIndex
        myIndex + 2
      }
      ...
    }


