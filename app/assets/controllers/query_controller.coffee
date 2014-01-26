class Denigma.QueryController extends Batman.Controller
  ###
    This controller contains basic grid events
  ###
  #  constructor: ->
  #    super
  #    @set("query", @defQuery)
  #    @set("headers", new Batman.Set())
  #    @set("errors","")

  ###
    default query to be called
  ###
  #defQuery:"SELECT ?subject ?pred WHERE { ?subject ?pred <http://denigma.org/resource/Aging>.}"
  #  defQuery: "PREFIX bds: <http://www.bigdata.com/rdf/search#> SELECT ?subject ?property ?object WHERE { ?object bds:search \"aging\" . ?subject ?property ?object . }"
  #
  #
  #  @accessor 'results', -> Denigma.Result.get('loaded')
  #  @accessor 'hasErrors', -> @get("errors")?

  addUploader: ->
    Array::last = -> @[@length - 1]
    $("#fileupload").fileupload
      dataType: "json"
      acceptFileTypes: /(\.|\/)(owl|rdf|tlt)$/i
      add: (e, data) ->
        $("#progress").remove()
        $(".uploaderror").remove()
        data.submit()
        $("<p id='progress'/>").text("Uploading...").appendTo("#files")
        #data.context.text data.files.last.name
        #      data.context = $("<button/>").text("Upload").appendTo("#files").click(->
        #        data.context = $("<p/>").text("Uploading...").replaceAll($(this))
        #        data.submit()
        #      )
      done: (e, data) ->

        fs = data.result.files
        for f in fs
          if(f.error) then $("<p class='uploaderror'/>").text("Upload error: " +f.error).insertAfter("#fileupload")
        $("#progress").text "Upload finished, next file?"
    console.log "uploader attached"

  main: ->
    fun = =>@addUploader
    setTimeout(fun(), 2000)
    setTimeout(fun(), 2000)

    #@submit()
  #
  #  reset: ->
  #    @set("errors",null)
  #    @set("query",@defQuery)
  #
  #  @accessor 'safeQuery', -> encodeURIComponent(@get("query"))
  #
  #  submit: ->
  #    @set("errors","")
  #    Denigma.Result.clear()
  #    fun = (err, records, env)=>
  #      @set("errors",err)
  #
  #      if env.data?.query? then @set("query",env.data.query)
  #    Denigma.Result.loadWithOptions {url:"models/sparql?query=#{@get("safeQuery")}"}, (err, records, env)->fun(err, records, env)
  #
  #
  #  all: ->
  #    console.log "all"
  #
  #  @accessor 'results', ->
  #    Denigma.Result.get("loaded")


  #  page_size: 15
  #  scroll_threshold: 80 #when to load new portion of records

  #  with_pagination: (str)=>
  #    switch str
  #      when str==null then "?page_size=#{@page}"
  #      when str.contains? !str.contains("page_size") && str.contains("?") then str+"&page_size=#{@page}"
  #      else str


  #  addScrolling: ->
  #    ###
  #      adds custom mscrolling to the grid
  #    ###
  #    callback = (e)=>
  #      if e.topPct>@scroll_threshold
  #        model = @model()
  #        if(model.page?)
  #          p = model.page
  #          unless p.loading==true
  #            if p.next?
  #              options = @with_pagination(p.next.substring(p.next.indexOf("?")+1)+"")
  #              fun = (err, records, env)->#console.log(records)
  #              model.load(options,fun)
  #              #model.load()
  #              p.loading = true
  #        else
  #          console.log("no scroll")
  #
  #
  #    params =
  #      theme:"dark-thick"
  #      advanced:
  #        updateOnContentResize: true
  #        autoScrollOnFocus: true
  #        updateOnBrowserResize:true
  #        autoHideScrollbar:true
  #        contentTouchScroll:true
  #      scrollButtons:
  #        enable: true
  #      callbacks:
  #        whileScrolling: callback
  #
  #    verParams = params
  #    $("section.tbody").mCustomScrollbar(verParams)
  #
  #    horParams = params
  #    horParams.horizontalScroll = true
