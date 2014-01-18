class Denigma.QueryController extends Batman.Controller
  ###
    This controller contains basic grid events
  ###
  constructor: ->
    super
    @set("query", @defQuery)

  ###
    default query to be called
  ###
  defQuery:"SELECT ?subject ?pred WHERE { ?subject ?pred <http://denigma.org/resource/Aging>.}"


  @accessor 'results', -> Denigma.Result.get('loaded')

  main: ->
    @submit()

  reset: ->
    @set("query",@defQuery)

  submit: ->
    Denigma.Result.clear()
    Denigma.Result.loadWithOptions({url:"models/sparql?query=#{@get("query")}"})


  all: ->
    console.log "all"

  @accessor 'results', ->
    Denigma.Result.get("loaded")
