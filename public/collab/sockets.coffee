###
# SocketEvent class #
Socket Event class is a class that does all conversions and packing of events send by sockets and channels
###

#= extern Batman.Object

class Batman.SocketEvent
  ###
  Socket Event class is a class that does all conversions and packing of events send by sockets and channels
  it contains a lot of useful static helpers to generate events that needed
  ###

  constructor: (@content, @channel, @request = "push", @room = "all")->
    ###
      creates websocket event where
      content is inside content variable, channel is for source (model) or type of content
      request is for what you want to do with content
      room is for what users to you want to spread this info
    ###
    unless @content.id? or @content.query then @content.id = SocketEvent.genId()
  #@id = if id=="" then SocketEvent.genId() else id



  @makeEvent: (content,channel, req, room = "all")->
    ###
      creates a socketevent, where:
      content is content of event
      channel is name of the channel that is used for this event
      req is a request with what this event is send
      room is an info to which users should the event be sent to
    ###
    new Batman.SocketEvent(content, channel, req, room)

  @makeQueryEvent: (query,channel, room = "all")->Batman.SocketEvent.makeEvent(query:query, channel, "read", room)

  @makeLookupEvent: (lField,lQuery,channel, room = "me")->
    content =
      field: lField
      query: lQuery
    Batman.SocketEvent.makeEvent(content, channel, "lookup", room)



  @makePushEvent: (content,channel, room = "all")->Batman.SocketEvent.makeEvent(content, channel, "push", room)

  @makeReadEvent: (id,channel, room = "all")->Batman.SocketEvent.makeEvent(id:id, channel, "read", room)

  @makeReadAllEvent: (channel,  room = "all")->Batman.SocketEvent.makeEvent(query:"all", channel, "read", room)

  @makeSaveEvent: (obj, channel)->
    data = Batman.SocketEvent.fromData(obj)
    data.channel = channel
    data.request = "save"
    data.room = "all"
    data


  @makeRemoveEvent: (id,channel, room="all")->Batman.SocketEvent.makeEvent(id:id, channel, "delete", room)



  @fromEvent: (event)->
    ###
    factory that generate SocketEvent from websocket event
    ###
    if event instanceof Batman.SocketEvent then return event
    if not event.data? then throw new Error("No data inside of websocket event")
    @fromData(event.data)

  @genId : ->
    ###
    ##Generates GUI as id for a record
    ###
    "xxxxxxxx-xxxx-4xxx-yxxx-xxxxxxxxxxxx".replace /[xy]/g, (c) ->
      r = Math.random() * 16 | 0
      v = (if c is "x" then r else (r & 0x3 | 0x8))
      v.toString 16


  @fromData: (data)->
    ###
    factory that generate SocketEvent from the data
    ###
    if data instanceof Batman.SocketEvent then return data
    if typeof(data) =="string" then return @fromString(data)
    #to avoid typical bug of nested data
    data = data.data if data.data?
    channel = if data.channel? then data.channel else "default"
    content =
      if data.content?
        if typeof data.content =="string" then @toJSON(data.content) else data.content
      else
        data
    request = if data.request? then data.request else "push"
    room = if data.room? then data.room else "all"
    new Batman.SocketEvent(content,channel,request,room)


  @fromString: (str)->
    ###
    factory that generate SocketEvent from some string
    ###
    #if typeof str !="string" then throw new Error("not string received by fromString but "+JSON.stringify(str))
    data = @toJSON(str)
    return  if data is undefined or typeof(data)=="string" then new Batman.SocketEvent(str,"default","save") else @fromData(data)


  @toJSON = (str) ->
    ###
    tries to convert string to json, returns initial string if failed
    ###
    if typeof str !="string" then return str
    try
      obj = JSON.parse str
    catch e
    return str
    if obj==undefined then str else obj

###
# Channel class #
Every sockets info to channels.
Channels are needed to communicate directly with the model
###
#= extern Batman.Object

#= require <socket.coffee>


class Batman.Channel extends Batman.Object
  ###
  #Channel class
  Every sockets info to channels.
  Channels are needed to communicate directly with the model
  ###
  constructor: (name) ->
    @name = name
    @on "onmessage", (event)=>@onmessage(event)

  save: (obj, id) =>
    obj.id = id
    @save obj

  save: (obj)=> @fire "send", Batman.SocketEvent.makeSaveEvent(obj,@name)

  read: (id)=> @fire "send", Batman.SocketEvent.makeReadEvent(id, @name)

  readAll: => @fire "send", Batman.SocketEvent.makeReadAllEvent(@name)

  remove: (id)=> @fire "send", Batman.SocketEvent.makeRemoveEvent(id, @name)

  lookup: (field,query)=> @fire "send", Batman.SocketEvent.makeLookupEvent(field,query, @name)

  query: (query)=> @fire "send", Batman.SocketEvent.makeQueryEvent(query, @name)

  stream2src: (stream)=>
    ###
      gets URL from the stream
    ###
    if window.URL?
      window.URL.createObjectURL(stream)
    else
      if window.webkitURL?
        window.webkitURL.createObjectURL(stream)
      else
        if window.mozURL?
          window.mozURL.createObjectURL(stream)
        else
          stream

  askWebcam: =>
    @ask "webcam"



  send: (obj) =>
    data = Batman.SocketEvent.fromData(obj)
    data.channel = @name
    @fire "send", data


  receive: (event) =>
    #should receive event with data
    @fire "onmessage", event
    @fire(event.request, event)

  onNextMessage:(fun)=>@once "onmessage", (event)=>fun(event)

  onmessage: (event) =>
    ###
      call back the receives info from socket send to this channel
    ###

  ask: (question)=>
    ###
      asks router for some additional info
    ###
    @fire "ask", question


  attach: (obj)=>
    ###
      Attaches the channel to the socket wrapper and subscribes to its events
    ###
    receive = @receive #trick to overcome "this" javascript change
    obj.on @name, receive
    obj.on "all", receive

    send = obj.send
    @on "send", send

    @on "ask", obj.ask
    cl = @constructor.name
    obj.fire(cl,@name)
    @

###
# MockSocket class #
Mock socket is needed for tests to simulate websocket behaviour
###

#= extern Batman.Object
#_require Batman.Channel
#_require Batman.SocketEvent



class Batman.CacheSocket extends Batman.Object

  isMock: true
  isCache: true
  input: []

  onopen: ->
    ###
    Open event
    ###
    console.log "open"

  constructor: (@url)->
    super
    @input = []

  send: (data)=> @input.push(data)

  onmessage: (event)->
    ###
    On message
    ###
    data = event.data
    console.log(data)

  onerror: =>
    console.log "error"

  onclose: =>
    console.log "close"

  randomInt: (min, max)=>
    ###
      random int generating function
    ###
    Math.floor(Math.random() * (max - min + 1)) + min

  unapply: (successor)=>
    if(@input? and successor.send?)
      for el in @input then successor.send(el)


###
  #Socket class#
  it not only uses either real or mock socket but broadcasts messages to various channels through events
###

#= extern Batman.Object
#_require socket_event
#_require cache_socket
#_require channel

###
  Routerclass
###

#= extern Batman.Object


class Batman.SimpleRouter extends Batman.Object
  ###
    Simple router does only simple broadcasting relying on channel info from the socket
  ###

  broadcast: (info, socket)->
    ###
      transforms info into SocketEvents and routes them further, to the channels
      some routers split info into several parts and send to difference channels
    ###
    event = Batman.SocketEvent.fromEvent(info)
    ### broadcasts the message further ###
    unless event instanceof Batman.SocketEvent
      throw Error 'should be socket event'
    ### broadcast event to appropriate channels ###
    socket.fire(event.channel, event)
    if(event.room? and event.room!="all")
      socket.fire(event.channel+"2"+event.room, event)  #TODO: fix this indus code


  send: (obj, websocket)->
    ###
      sends event to the websocket
    ###
    if typeof obj == 'string'
      websocket.send(obj)
    else
      str = JSON.stringify Batman.SocketEvent.fromData(obj)
      websocket.send str
  #websocket.send obj

  myStream: null
  webCamPending: false


  respond: (question,socket)->
    ###
      respond to questions from channels
    ###
    switch question
      when "webcam"
        if @myStream?
          socket.fire "localStream", @myStream
        else
          if @webCamPending==false
            navigator.getUserMedia or (navigator.getUserMedia = navigator.mozGetUserMedia or navigator.webkitGetUserMedia or navigator.msGetUserMedia)
            if navigator.getUserMedia
              onsuccess = (stream)=>
                socket.fire "localStream", stream
                @webCamPending = false
              onerror = @onError
              @webCamPending = true
              navigator.getUserMedia
                video: true
                audio: true
                onsuccess
                onerror
            else
              alert "getUserMedia is not supported in this browser."
          @webCamPending = true



class Batman.Socket extends Batman.Object
  ###
    websocket wrapper that broadcast info to its channels
    it not only uses either real or mock socket but broadcasts messages to various channels through events
  ###
  constructor: (url)->
    ###
    creates a socket object
    ###
    #checks if websocket is in batman container
    @router = new Batman.SimpleRouter()
    @websocket = @getWebSocket(url)
    Batman.Socket.instance = @



  url: "none"


  isMock: => not @websocket? or @websocket.isMock?

  createWebSocket:  (url)=>
    ###
      creates websocket or mocksocket
    ###
    if url=="none"
      websocket = new Batman.CacheSocket(url)
    else
      websocket = new WebSocket(url)
    @url = url
    @setWebsocket(websocket)

  setWebsocket: (websocket)=>
    defOpen = (event)=>
      @websocket = websocket
      @addSocketHandlers(@websocket)
    if @websocket?
      if websocket==@websocket then return @websocket
      if @websocket.isCache?
        old = @websocket
        websocket.onopen = (event)=>
          defOpen(event)
          old.unapply(websocket)
      else
        websocket.onopen = defOpen
    else
      websocket.onopen = defOpen
    if websocket.isMock? then websocket.onopen()

  addSocketHandlers: (websocket)=>
    websocket.onmessage = (event)=> @broadcast(event)
    websocket.onerror = (err)=>alert "some ERROR occured"
    websocket.onclose = ()=>alert "socket is CLOSED"
    websocket

  broadcast: (info)->
    @router.broadcast(info,@)


  getWebSocket: (url)=>
    ###
      TODO: rewrite, all this searches, the global scope only confuses
    ###
    if  Batman.container.websocket?
      if (Batman.container.websocket.isMock and url=='none') or url==@url
        return Batman.container.websocket
    @createWebSocket(url)




  withUrl: (url)=>
    ###
      returns self but changes the websocket if needed
    ###
    if(url!=@url) then @websocket = @getWebSocket(url)
    @


  @getInstance: (url="none")=>
    ###
      works as singletone
      TODO: rewrite
    ###
    if Batman.Socket.instance? then Batman.Socket.instance else return new Batman.Socket(url)
  ###
  if Batman.container.socket?
    return Batman.container.socket.withUrl(url)
  else
    return new Batman.Socket(url)

  ###

  getChannel: (name)=>
    ###
      gets or creates channel
    ###
    @getOrSet(name,=>new Batman.Channel(name).attach(@))

  getSpecialChannel: (name,factory)=>
    ###
      gets or creates channel with factory that is provided
    ###
    @getOrSet(name,=>factory().attach(@))

  ###
  getVideoChannel: (name,room)=>
    #TODO: think how to fix in Future
    @getOrSet(name+"2"+room,=>new Batman.VideoChannel(name,room).attach(@))
  ###

  send: (obj)=> @router.send(obj, @websocket)

  ask: (question)=>
    ###
      executes if there is a request to the router (but without info to be send to server)
    ###
    @router.respond(question,@)

