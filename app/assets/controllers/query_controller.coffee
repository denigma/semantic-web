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
  #defQuery:"SELECT ?subject ?pred WHERE { ?subject ?pred <http://denigma.org/resource/Aging>.}"
  defQuery: "PREFIX bds: <http://www.bigdata.com/rdf/search#> SELECT ?subject ?property ?object WHERE { ?object bds:search \"aging\" . ?subject ?property ?object . }"


  @accessor 'results', -> Denigma.Result.get('loaded')
  @accessor 'hasErrors', -> @get("errors")?

  main: ->
    @submit()

  reset: ->
    @set("errors",null)
    @set("query",@defQuery)

  @accessor 'safeQuery', -> encodeURIComponent(@get("query"))

  submit: ->
    @set("errors","")
    Denigma.Result.clear()
    fun = (err, records, env)=>
      @set("errors",err)
    Denigma.Result.loadWithOptions {url:"models/sparql?query=#{@get("safeQuery")}"}, (err, records, env)->fun(err, records, env)


  all: ->
    console.log "all"

  @accessor 'results', ->
    Denigma.Result.get("loaded")
