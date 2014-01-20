#class Denigma.Page
#
#  loading: false
#  constructor: (@count, @previous, @next)->
#
#class Denigma.PaginatedStorage extends Batman.RestStorage
#  paginate: (env)->
#  if env.json.results?
#    if env.json.count? then env.subject.page  = new Batman.Page(env.json.count,env.json.previous,env.json.next)
#    env.json = env.json.results
#  else
#    env.subject.page = null
#  env

class Denigma.SparqlStorage extends Batman.RestStorage
  @set("head",new Batman.Set())

  class @QueryError extends @StorageError
    name: "QueryError"
    constructor: (error,query) ->
      if query?
        super("There is an error in your SPARQL query:\n #{error}")
      else
        super("There is an error in your SPARQL query:\n #{error} \n IN \n"+query)

  getRecordFromData: (attributes, constructor = @model) -> constructor._makeOrFindRecordFromData(attributes)
  getRecordsFromData: (attributeSet, constructor = @model) -> constructor._makeOrFindRecordsFromData(attributeSet)

  extractBindings: (data)->
    if data.bindings?
      rows = data.bindings
      if Batman.typeOf(rows) is 'Array'
        rows.map (row,i,arr)->{
          id:  i
          bindings: row
          }
    else
      data



  @::after 'all', @skipIfError (env, next) ->
    if !env.data?
      return next()

    if typeof env.data is 'string'
      if env.data.length > 0
        try
          json = @_jsonToAttributes(env.data)
        catch error
          env.error = error
          return next()
    else if typeof env.data is 'object'
      json = env.data
    if (json.errors? and json.errors.length?)
      er = json.errors[0]
      env.error =  new @constructor.QueryError(er,json.query)
    env.json = json if json?
    next()

  @::after 'create', 'read', 'update', @skipIfError (env, next) ->
    if env.json?
      json = @extractFromNamespace(env.json, @recordJsonNamespace(env.subject))
      env.subject._withoutDirtyTracking -> @fromJSON(json)
    env.result = env.subject
    next()

  @::after 'readAll', @skipIfError (env, next) ->
    namespace = @collectionJsonNamespace(env.subject)
    env.recordsAttributes = @extractBindings(@extractFromNamespace(env.json, namespace))

    if env.data.head?
      if env.subject.setHeaders?
        env.subject.setHeaders(env.data.head)
    unless Batman.typeOf(env.recordsAttributes) is 'Array'
      namespace = @recordJsonNamespace(env.subject.prototype)
      env.recordsAttributes = [@extractFromNamespace(env.json, namespace)]
      #    v = env.recordsAttributes
      #    debugger

    env.result = env.records = @getRecordsFromData(env.recordsAttributes, env.subject)
    next()