class Denigma.Table extends Batman.Model

  @_makeOrFindRecordFromData: (attributes) ->
    record = @_loadRecord(attributes)
    @_mapIdentity(record)

  @_makeOrFindRecordsFromData: (attributeSet) ->
    newRecords = for attributes in attributeSet
      @_loadRecord(attributes)

    @_mapIdentities(newRecords)
    newRecords


class Denigma.Result extends Denigma.Table

  @primaryKey: 'id'

  @serializeAsForm: false #do not remember if it is static or normal property

  @bindingEncoder:encode: (value, key, builtJSON, record) ->
    @get(key)
    decode: (value, key, incomingJSON, outgoingObject, record) ->
      res = new Batman.Hash()
      for k,v of value
        res.set(k,v)
      res

  @encode 'bindings',@bindingEncoder


  # `fromJSON` uses the various decoders for each key to generate a record instance from the JSON
  # stored in whichever storage mechanism.
  fromJSON: (data) ->
    #    b = @getOrSet("properties", ->new Batman.Set())
    #    b.clear()
    #debugger
    super(data)


  @storageKey: 'results'
  @persist Denigma.SparqlStorage

  @setHeaders: (head)=>
    titles = @getOrSet("head",->new Batman.Set())
    titles.clear()
    if head.vars?
      for h in head.vars
        titles.add(h)
    titles


