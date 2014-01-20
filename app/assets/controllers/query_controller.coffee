class Denigma.QueryController extends Batman.Controller
  ###
    This controller contains basic grid events
  ###
  constructor: ->
    super
    @set("query", @defQuery)
    @set("headers", new Batman.Set())
    @set("errors","")

  ###
    default query to be called
  ###
  defQuery:"SELECT ?subject ?pred WHERE { ?subject ?pred <http://denigma.org/resource/Aging>.}"


  @accessor 'results', -> Denigma.Result.get('loaded')
  @accessor 'hasErrors', -> @get("errors")?

  main: ->
    @submit()

  reset: ->
    @set("errors","")
    @set("query",@defQuery)

  submit: ->
    @set("errors","")
    Denigma.Result.clear()
    fun = (err, records, env)=>
      @set("errors",err)
    Denigma.Result.loadWithOptions {url:"models/sparql?query=#{@get("query")}"}, (err, records, env)->fun(err, records, env)


  all: ->
    console.log "all"

  @accessor 'results', ->
    Denigma.Result.get("loaded")
