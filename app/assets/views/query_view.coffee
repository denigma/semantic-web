class Denigma.QueryView extends Batman.View
  ###
    This controller contains basic grid events
  ###
  constructor: ->
    super
    @set("query", @defQuery)
    @set("headers", new Batman.Set())
    @set("errors","")


  defQuery: "PREFIX bds: <http://www.bigdata.com/rdf/search#> SELECT ?subject ?property ?object WHERE { ?object bds:search \"aging\" . ?subject ?property ?object . }"

  reset: ->
    @set("errors",null)
    @set("query",@defQuery)

  @accessor 'safeQuery', -> encodeURIComponent(@get("query"))

  submit: ->
    @set("errors","")
    Denigma.Result.clear()
    fun = (err, records, env)=>
      @set("errors",err)

      if env.data?.query? then @set("query",env.data.query)
    Denigma.Result.loadWithOptions {url:"models/sparql?query=#{@get("safeQuery")}"}, (err, records, env)->fun(err, records, env)

  @accessor 'results', ->
    Denigma.Result.get("loaded")

  ready: ->
    @submit()

class Denigma.EditorView extends Batman.View
  editor: undefined

  ready: ->
    ###
      TODO: fix this bad unstable code
    ###

    options =
      exclusive: false
    node = @get("node")
    element = $(@get("node"))
    cont = @superview
    fun = (nd)=>
      #cont = @get("controller")

      editor = CodeMirror(node,
        value: cont.get("query")
        mode: "sparql"
      )
      editor.on "change", (ins,ch)=>
        cont.set("query",editor.getValue())
      #console.log editor.getValue()
      upd = (q)->if(editor.getValue()!=q) then editor.setValue(q)
      cont.observe('query', upd)
      @editor


    setTimeout (=>fun(node)), 500






