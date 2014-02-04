###
  #Denigma chat App#
  Application coffee of the chat
###

#= extern Batman.App
#= extern Batman.Object

#disable routingkey warnings for controllers
Batman.config.minificationErrors = false

class Denigma extends Batman.App
  @route 'query/main', 'query#main'
  @route 'schema/sankey', 'schema#sankey'
  @route 'schema/distortion', 'schema#distortion'




  #stores to global container
container = Batman.container
container.Denigma = Denigma

Batman.config =
  pathToApp: '/'
  usePushState: true

  pathToHTML: ''
  fetchRemoteHTML: true
  cacheViews: false

  minificationErrors: false
  protectFromCSRF: false

fetchHTML2 =  (path) ->
  new Batman.Request
    url: Batman.Navigator.normalizePath(Batman.config.pathToHTML, "#{path}")
    type: 'html'
    success: (response) => @set(path, response)
    error: (response) -> throw new Error("Could not load html from #{path}")
Batman.HTMLStore::fetchHTML= fetchHTML2

$.ajaxSetup headers:
  "X-PJAX": "X-PJAX"

  #add listener to the window object to fire run when everything has been loaded
if(window?)
  window.addEventListener 'load', ->
    #disp = new Batman.EmptyDispatcher()
    #Denigma.set "navigator", disp
    #Denigma.set "dispatcher", disp
    Denigma.run()
    Denigma.fire "start"


