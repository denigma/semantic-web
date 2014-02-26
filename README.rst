Denigma SemanticWeb project
============================

This is Denigma SemanticWeb app where for all data bigdata triple store is used.
At the moment the project includes:
* aktor-based non-blocking BigData driver
* web-interface to query bigdata
* sparql helpers that analyze AST and add binding/limits
* some demo pages with d3js visualizations
* some code from other projects ( wesin, collaboration) that will be integrated in future

Most of the code that deals with bigdata is inside SemanticData subproject.

Setting Up
----------

To set up the project you need to (most of instructions are for Deiban/Ubuntu based Linux, but for Windows it will be somehow similar):
* Have scala 2.10.x, play 2.2.x and sbt 0.13 installed:
    - Make sure you use JDK 1.7+ and have JAVA_HOME variable setup
    - Install Scala ( http://scala-lang.org/ ) and sbt ( http://www.scala-sbt.org/release/docs/Getting-Started/Setup.html )
    - Download play2 Framework and add PLAY_HOME to your Path (  http://www.playframework.com/download )
* Install Coffeescript:
    - install nodejs ( https://github.com/joyent/node/wiki/Installing-Node.js-via-package-manager )
    - install coffeescript with
        $  sudo npm install -g coffee-script
* Init submodules:
    $   git submodule init
    $   git submodule update
* run tests:
    $ play test
* run the app:
    $ play run
* Check if app runs without errors in styles. I added index.png pic to show how the styles may look like


Architecture
------------
At the moment it consists of tree projects:
 * semantic-web (main project) that is web-application
 * semantic-data ( subproject ) that contains the code that deales with BigData rdf QuadStore and quries to it
 * wesin ( subproject ) RDF graph project that will be used for inmemory caching of Ontologies + config + userdata + rules and for some traversals/inferencing
 * macro ( subproject ) that contains macroses implementations
 * collaboration ( not included in build yet) that contains code of websocket chats and live updates that will be partially ported in
 order to provide nice collaboration experience


Notes
-----

As the code is young there are many rough edges. Only very small set of features are working, some features (like login/logout)
are not implemented (or partially imlemented)