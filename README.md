boxwood
=======

Numeric vectors with type-safe access




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


