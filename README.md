# HeadsPlugin usage example
An example of how to use the HeadsPlugin project. (https://github.com/CC007/maven-repo/tree/master/lib-releases/com/github/cc007/HeadsPlugin/1.8.7-1.0.4)

It demonstrates how to extend from the HeadsPlugin class and how to use the HeadsUtils, HeadsCreator and HeadsPlacer classes

##It demonstrates:
 - placing heads around location 0 64 0 and filling inventory with heads...
   - from specified category: /heads &lt;categoryname>
   - from all categories: /heads
   - from search for keyword: /heads search &lt;keyword>
 - placing a single head at location 0 64 0 and adding that head to inventory from search: /heads (searchfirst|searchatindex &lt;index>) &lt;keyword>
