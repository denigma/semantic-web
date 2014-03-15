class Denigma.View extends Batman.View

  @option "name"

  viewDidAppear: ->
    if @get("appeared")? then return
    @activate()
    @set("appeared",true)

  viewWillDisappear: ->
    @set("appeared",false)

  viewDidDisappear: ->
    @set("appeared",false)

  activate: ->

  host: =>"http://#{window.location.host}/"
  withHost: (str)=> @host()+str


class Denigma.States extends Batman.DelegatingStateMachine

  canGo: (transition)->@canStartTransition(transition)

  tryGo: (transition)->
    if(@canStartTransition(transition))
      @[transition]()
      true
    else
      false

  goOr: (transition, fun)->
    if(@canStartTransition(transition))
      @[transition]()
      true
    else
      if fun? then fun()


class Denigma.MenuView extends Denigma.View

  class STM extends Batman.DelegatingStateMachine
    @transitions
      load:
        from: 'clean'
        to: 'loading'
      ready:
        from: 'loading'
        to: "loaded"
      cancel:
        from: 'loading'
        to: 'clean'


  constructor: ->
    super
    @set("lifecycle", new STM('clean',@))
    @lifecycle.onTransition("clean","loading",=>@onLoad())

  onLoad: =>
    Denigma.Menu.loadWithOptions({url: Denigma.models("menus/#{@get("name")}")},@onLoadCompleted)

  onLoadCompleted: (err, records, env)=>
    @set("items",records)
    @lifecycle.ready()


  activate: ->
    node = $(@get("node"))
    @lifecycle.load()








