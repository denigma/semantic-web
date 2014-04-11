Denigma SemanticWeb project
###########################

This is Denigma SemanticWeb app where for all data bigdata triple store is used.
At the moment the project includes:
* aktor-based non-blocking BigData driver
* web-interface to query bigdata
* sparql helpers that analyze AST and add binding/limits
* scalajs based micro-binding framework
* scalajs views
* small part of front-end (logging/signing up)

Setting Up
==========

To set up the project you need to (most of instructions are for Deiban/Ubuntu based Linux, but for Windows it will be somehow similar):
* Have scala 2.10.x, play 2.2.x and sbt 0.13 installed:
    - Make sure you use JDK 1.7+ and have JAVA_HOME variable setup
    - Install Scala ( http://scala-lang.org/ ) and sbt ( http://www.scala-sbt.org/release/docs/Getting-Started/Setup.html )
    - Download TypeSafe Activator and add it to your Path (  http://www.playframework.com/download )
* Install Coffeescript:
    - install nodejs ( https://github.com/joyent/node/wiki/Installing-Node.js-via-package-manager )
    - install coffeescript with
        $  sudo npm install -g coffee-script
* run tests:
    $ activator test
* run the app:
    $ activator run
* Check if app runs without errors in styles. I added index.png pic to show how the styles may look like
* generate project files of your favourite IDE
    $ activator gen-idea #for Intellij IDEA, OR
    $ activator eclipse #for Eclipse

Notes
-----

As the code is young there are many rough edges. Only very small set of features are working, some features (like login/logout)
are not implemented (or partially implemented)

Architecture
============
At the moment it consists of a bunch of projects:
 * semantic-web (main project) that is web-application
 * semantic-data ( subproject ) that contains the code that deales with BigData rdf QuadStore and quries to it
 * macro ( subproject ) that contains macroses implementations
 * collaboration ( not included in build yet) that contains code of websocket chats and live updates that will be partially ported in
 order to provide nice collaboration experience

There is also a bunch of ScalaJS projects:
* jsmacro project for scalajs macroses
* binding project with scalajs binding microframework that allows binding of different reactive variables to HTML properties
* scala shared source folder - for classes that are shared between backend and frontend

 Build definitions are situated in build.sbt and inside project folder.

 WARNING: there is also a lot of code that is either experimental or deprecated (will be removed soon), mostly - coffeescript code
 as well as some packages like org.denigma.semantic.classes

Semantic-Web project
====================

The is a root project of the app, where the web App and UI resides.
Most of the code is inside app subfolder.
By default Login page is shown plus some test input for bindings.

Note: there used to be interactive SPARQL endpoint for readonly sparql queries, not yet working login, and some half-working charts
but they were removed to be rewritten with scalajs


Front-end
=========

A bunch of fronted javascript/coffeescript libs are used. Some of them are loaded with Webjars, some of them reside in semantic-web/public
folder, due to a high number of javascript libs github even mistakenly classified this project as javascript-one.

At the moment the project in transition from coffeescript (where batman.js was used as framework, most of coffeescript code still resides in
app/assets folder ) to ScalaJS


Overall fronted part consists of play2 twirl templates (in semantic-web/views), macrojs project, scala shared source folder and scalajs project.


ScalaJS subproject
------------------

Main frontend code is situated in scalajs project that depends on shared code "scala" folder and binding subproject.


Binding subproject
------------------


The main idea behind frontend is binding of scalajs views to html.
For instance taken following html::
   <div class="right menu" data-view="login">
       <div class="item"  data-showif="isSigned">
            <div class="ui teal button" data-event-click="logoutClick">Log out</div>
        </div>
   <!some other code>
   </div>

Here at the beginning the html is binding to LoginView class, then each data-<something> property is binding to corresponding
reactive variable (Rx-s and Var-s in ScalaRX https://github.com/scala-js/scala-js ), so when this variable changes so does html.
There is a view hierarchy, that starts from a view that is automaticly binded to "body" tag

There is also a shared (scala) folder for classes that are shared between frontend and backend, as well as picklers.

JSMacro subproject
------------------

Under the hood bindings are done with use of macroses. All rx variables are extracted by macroses into Map-s to make them accessible
for binding views. There is a problem with macro evaluation that I do not know yet how to solve: all macroses are evaluated in classes
where they are declared,that means that if you declared extractMap(this) and inherit from this class somewhere in ChildClass the maps
will be done only from the class where the macro was declared. That is the reason why there are a lot of abstract methods (with macroses) that must be
implemented when you inherit form one of the views.

Scala shared code folder
------------------------

In this folder a shared code is accumulated, that is used both by backend and frontend.There some case classes as well as
picklers (to serialize them) are accumulated.

Configuration
-------------

The configuration is inside conf folder. Part of configuration is written in turtle ( config.ttl), part of configuration (dev/test/prod)
is loaded only in development/test/production modes respectively.

Semantic-Data subproject
========================
This project is concentrated on dealing with the database. Embedded BigData ( http://bigdata.com ) database in QuadMode is used there.
I used it in embedded mode as we will not have a lot of data in the very beginning and as it does not seem to be hard to move to clustered bigdata.

Semantic-Data project is structured as Play2 plugin. In fact it is like DB driver for play + DSL to work with SPARQL.
In fact there is no need to dive into it deeply as in most of the cases it is enough to know SPARQL DSL and extend Query and/or Update controller.
That means that it is included by ( 10000:org.denigma.semantic.SemanticPlugin ) inside play.plugins inside SemanticWeb configuration.
So there is SemanticPlugin class, which onstarts method rung when the app has started.
Than prg.denigma.semantic.platform.SP object acts
    sp.extractConfig(app) //gets PlayConfig file and extracts info from it
    sp.cleanIfInTest() //cleans some files if run in test mode
    sp.start(app) //starts everything (incl. database)
Main configuration is application.conf, it imports dev/prod/test conf files depending on a mode in which the app is run ( Test/Development/Proeduation).
At the moment Semantic-Data uses configuration of the main play app ( in our case - SemanticWeb ), I have not created standalone conf yet.

The database is located in org.denigma.semantic.storage package. But access to it is highly abstracted so most of the classes that deal with it
 mix trait org.semantic.reading.CanRead and/or trait org.semantic.reading.CanWrite that have logger and only one method - provide connection.
 The project has reading and writing packages where respective features of the database are explored.

 An access to the database are done in one of two ways: blocking or nonblocking.

Nonblocking (actor-based) database access API
---------------------------------------------

 For nonblocking access some actors where created ( org.denigma.semantic.actors ):

    * one writer actor (as the app operate in one writer -> many readers mode). Writer actor is threadsafe and is run within a separate thread

    * reader actor that is routed with SmallesMailBoxPool ( http://doc.akka.io/docs/akka/snapshot/scala/routing.html#SmallestMailboxPool ).
    That means that whenever a message comes to a reader actor reference it is redirected to one of the reader actors that do queries.
    The main advantage of such way of dealing with reads is that you get Future's in response, so all database access is non blocking.
    Reader actors are configured with PinnedDispatcher (that implies one thread per actor) and are also controlled by the Resizer that is
    configured in application.conf (it may have different configuration for tests and production, so you should look into dev/test/prod.conf
    inside semantic-web/conf folder) and that adjust a number of readers (and thus a number of threads) depending on app's load and configuration.

 To deal with database in a nonblocking way you should inherit either from:

  * WithSemanticReader or WithSemanticWriter (depending on operations you want to do)
  Those traits provide access to reader/writer actors (they grab them from the app) so you do not need to initialize anything inside of them.
  * OR from Controllers inside org.denigma.semantic.controllers
  Those controllers inherit from WithSemanticReader/Writer
  and also provide methods for querying the database doing ask quries to reader/writer under the hood.

Classes of messages that are sent to reader/writer actors can be found
inside org.denigma.semantic.actors.readers.Read and org.denigma.semantic.actors.readers.Write

Blocking (syncronious) database access API
------------------------------------------

It is used mostly for testing purposes and it is not thread-safe for writing (as it does not control that only one write connection is opened).
In order to use it it is enought from one of the traits inside org.denigma.semantic.controllers.sync.
WithSyncReader/Writer are traits that provide read/write connection respectively, those methods do not need to be overridden as they provide
connections from static object SyncWriter/Reader that on its turn get it from the app (that on its turn get if from database). But of course,
 usually sync. controllers are used that are inside org.semantic.data.syc.QueryControllers.

Querying classes
----------------

There are two packages that provide methods for querying the database. org.denigma.semantic.reading (for Select/Construct/Ask/Describe) and
org.denigma.semantic.reading for Updates. They are used both inside database actors and sync API.

Inside org.denigma.semantic.reading packages there are package objects with type aliases for the most useful classes.
Depending on type of quries there are constructs/selections/etc packages. There are also queries package that deals with
queries that we have to define yet (i.e. quries to sparql endpoint that can be either select/ask/construct/describe) and also provides
some nice methods for limiting/offseting quries (there is also modifiers package that does AST stuff).

Inmemory cache
--------------

Writer actors received a change watcher that sends update to cache actor that on its turn updates all cache consumers

Macro subproject
================

Is used for compile-time source code generation and other useful macros things. At the moment only one macro is there.

Collaboration subproject
========================

This subproject provides collaborative features like websocket webchats/tasks. It was moved from another app and has not been integrated yet.
This means that it is not part of the application build.

WARNING: in the moment collaboration subproject is NOT a part of the build and probably will be completely rewritten

