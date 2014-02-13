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

  @host: => "http://#{window.location.host}/"
  @url: (str)=>@host() + str

  @models: (str)=>Denigma.url("models/#{str}")
  @pages: (str)=>Denigma.url("pages/#{str}")

  @set("currentUser", null)

  @classAccessor 'signed',
    get: -> @get("currentUser") && @get("token")



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
  viewExtension: ""

fetchHTML2 =  (path) ->
  new Batman.Request
    url: Batman.Navigator.normalizePath(Batman.config.pathToHTML, "#{path}")
    type: 'html'
    success: (response) => @set(path, response)
    error: (response) -> throw new Error("Could not load html from #{path}")
Batman.HTMLStore::fetchHTML= fetchHTML2

$.ajaxSetup headers:
  "X-PJAX": "X-PJAX"

jQuery ->
    Denigma.run()
    Denigma.fire "start"
    #    req = new XMLHttpRequest()
    #    req.open "GET", document.location, false
    #    req.send null
    #    headers = req.getAllResponseHeaders().toLowerCase()
    #    alert headers

