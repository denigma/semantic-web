class Batman.Page

  loading: false
  constructor: (@count, @previous, @next)->

class Batman.DjangoStorage extends Batman.RestStorage
  ###
    Storage with Django specific features
  ###
  @::after 'create', 'read', 'update', @skipIfError (env, next) ->
    @paginate(env)
    if env.json?
      json = @extractFromNamespace(env.json, @recordJsonNamespace(env.subject))
      env.subject._withoutDirtyTracking -> @fromJSON(json)
    env.result = env.subject
    next()

  @::after 'readAll', @skipIfError (env, next) ->
    namespace = @collectionJsonNamespace(env.subject)
    @paginate(env)
    env.recordsAttributes = @extractFromNamespace(env.json, namespace)

    unless Batman.typeOf(env.recordsAttributes) is 'Array'
      namespace = @recordJsonNamespace(env.subject.prototype)
      env.recordsAttributes = [@extractFromNamespace(env.json, namespace)]

    env.result = env.records = @getRecordsFromData(env.recordsAttributes, env.subject)
    next()

  paginate: (env)->
    if env.json.results?
      if env.json.count? then env.subject.page  = new Batman.Page(env.json.count,env.json.previous,env.json.next)
      env.json = env.json.results
    else
      env.subject.page = null
    env

  request: (env, next) ->
    ###
      override to change put format
    ###
    options = Batman.extend env.options,
      autosend: false
      success: (data) -> env.data = data
      error: (error) -> env.error = error
      loaded: ->
        env.response = env.request.get('response')
        next()
    if options.data?
      if options.method == "PUT"
        #Dirty fix but, saving now works!
        for key,value of options.data
          options.data = value
          break
    #      if options.method == "GET" && options.data?
    #        #Another dirty fix but, for associations
    #        for key,value of options.data
    #          if key.indexOf("_id")>-1
    #            options.data[key] = undefined
    #            options.data[key.replace("_id","")] = value

    console.log(options)
    env.request = new Batman.Request(options)
    #console.log JSON.stringify(options)
    env.request.send()
