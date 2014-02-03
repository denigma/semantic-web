Denigma SemanticWeb project
============================

This is Denigma SemanticWeb app where for all data bigdata triple store is used

Most of the code that deals with bigdata is inside subproject

Setting Up
----------

To set up the project you need to (most of instructions are for Deiban/Ubuntu based Linux, but for Windows it will be somehow similar):
* Have scala 2.10.x, play 2.2.x and sbt 0.13 installed:
    - Make sure you use JDK 1.7+ and have JAVA_HOME variable setup
    - Install Scala ( http://scala-lang.org/ ) and sbt ( http://www.scala-sbt.org/release/docs/Getting-Started/Setup.html )
    - Download play and add PLAY_HOME to your Path
* Install Coffeescript:
    - install nodejs ( https://github.com/joyent/node/wiki/Installing-Node.js-via-package-manager )
    - install coffeescript with
        $  sudo npm install -g coffee-script
* Install Sass and Compass:
    - you may need to install ruby2 and gems first ( see http://gorails.com/setup/ubuntu/13.10 ), and then:
    $ sudo gem install compass --pre
* Install following Compass libs:
    $ sudo gem install semantic-ui-sass
    $ sudo gem install singularitygs
* run tests:
    $ play test
* run the app:
    $ play run
* Check if app runs without errors and with semantic-ui styles. I added index.png pic to show how the styles may look like


Architecture
------------
At the moment it consists of tree projects:
 * macro project that contains macroses implementations
 * semantic-data that contains the code that deales with BigData rdf QuadStore and quries to it
 * semantic-web (main project) that is web-application



Notes
-----

As the code is young there are many rough edges. For instance because of weird PlaySass configuration
I had to put some semantic ui folders (images and fonts) to the root, otherwise they did not compile