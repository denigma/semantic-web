class Denigma.GraphController extends Batman.Controller
  ###
    This controller contains basic grid events
  ###
  constructor: ->
    super
    upd = (root)->@rootChanged(root)
    @observe('root', upd)
    @set("root", "7e18454f-1141-4e5d-a3f3-d8ce5ef0a211")

  genId: ->
    ###
    ##Generates GUI as id for a record
    ###
    rep = (c) ->
      r = Math.random() * 16 | 0
      v = (if c is "x" then r else (r & 0x3 | 0x8))
      v.toString(16)
    res = ("xxxxxxxx-xxxx-4xxx-yxxx-xxxxxxxxxxxx".replace /[xy]/g, rep)
    return res

  s: null #sigma variable

  rootChanged: (id)->
    console.log "incoming/#{id}"
    Denigma.Edge.loadWithOptions({url:"incoming/#{id}"})
    Denigma.Edge.loadWithOptions({url:"outgoing/#{id}"})

  content: ->

  showEditor:->   #Mercury.show()

  hideEditor:->  #Mercury.hide()



  @accessor 'inE', ->
    id = @get("root")
    Denigma.Edge.get('loaded').filter (e)->e.get("outgoing").some((v)->v.get("id")==id)



  @accessor 'outE', ->
    id = @get("root")
    Denigma.Edge.get('loaded').filter (e)->e.get("incoming").some((v)->v.get("id")==id)

  add: ->
    props =
      key1:"value1"
      key2:"value2"
      key3:"value3"

    n = new Denigma.Vertex(id:@genId(),name:"test_name2", description:"test_description2",properties:props)
    n.save()

  labelSize:  16
  labelSizeOver : 24

  g: {nodes:[],edges:[]}

  @afterAction only: "content", ->@initGraph()

  makeEdge: (from,to)=>
    id: @genId()
    source: from
    target: to
    #size: Math.random()
    size: @labelSize
    color: "#ccc"

  forN: (n, key)=>
    node = n
    node.id = node.get("id")
    node.color = "rgb(246,178,107)"
    node.x = Math.random()
    node.y = Math.random()
    node.label = n.get(key)
    node.size = @labelSize
    node.labelSize = @labelSize
    node

  forE: (e)=>
    ###
    transforms models of hyperedges into graph elements, is used in ForEach loop and update handlers
    ###
    g = @s.graph
    node = @forN(e,"label")
    node.color = "rgb(166, 77, 121)"


    #    $denigma_red: rgb(204,0,0);
    #    $denigma_orange: rgb(246,178,107);
    #    $denigma_violet: rgb(166, 77, 121);
    #    $denigma_rose: #e06666;
    #    $denigma_blue: #4a86e8;

    g.putNode(node)

    forV = (v,inc=true)=>

      n = @forN(v,"name")
      g.putNode(n)
      if(inc)
        from = n.id
        to = node.id
      else
        from = node.id
        to = n.id
      edge = @makeEdge(from,to)
      #debugger
      g.addEdge(edge)
      n
    e.get("incoming").forEach (v)=>forV(v,true)
    e.get("outgoing").forEach (v)=>forV(v,false)
    @s.startForceAtlas2()

  extendSigma: ->
    sigma.classes.graph.addMethod 'nodeMap', -> @nodesIndex
    sigma.classes.graph.addMethod 'nodeList', -> @nodesArray
    sigma.classes.graph.addMethod 'putNode', (node)->
      # Check that the node is an object and has an id:
      throw "addNode: Wrong arguments."  if Object(node) isnt node or arguments.length isnt 1
      throw "The node must have a string id."  if typeof node.id isnt "string"
      console.log "The node \"" + node.id + "\" already exists."  if @nodesIndex[node.id]
      id = node.get("id")

      # Add the id such that it is not possible to modify it:
      Object.defineProperty node, "id",
        value: id
        enumerable: true

      # Add empty containers for edges indexes:
      @inNeighborsIndex[id] = {}
      @outNeighborsIndex[id] = {}
      @allNeighborsIndex[id] = {}
      @inNeighborsCount[id] = 0
      @outNeighborsCount[id] = 0
      @allNeighborsCount[id] = 0

      # Add the node to indexes:
      @nodesArray.push node
      @nodesIndex[node.id] = node

      # Return the current instance:
      this


  initGraph: ->
    ###
    initiates sigma
    ###
    @extendSigma()



    @s = new sigma(
      graph: @g
      container: "graph-container"
      defaultEdgeType: "curve"
    )
    forE = @forE
    edges = Denigma.Edge.get("loaded")
    edges.forEach (e)->forE(e)
    edges.on 'itemsWereAdded', (items) =>
      for item in items
        forE(item)
      #@s.startForceAtlas2()


    # Bind the events:
    @s.bind "overNode", (e) ->
      v = e.data.node
      v.labelSize = @labelSizeOver
      v.set("selected",true)
      #      if(v.constructor.name=="Edge")
      #        Denigma.Edge.select(v.id)
      #      else
      #        Denigma.Vertex.select(v.id)

    @s.bind "outNode", (e) ->
      v = e.data.node
      v.labelSize = @labelSize
      v.set("selected",false)
      #      if(v.constructor.name=="Edge")
      #        Denigma.Edge.deselect(v.id)
      #      else
      #        Denigma.Vertex.deselect(v.id)

    options =
      strongGravityMode: true
      outboundAttractionDistribution: true



  vertexMouseOver: (node,event,context)->
    ###
    when mouse is over node section
    ###
    v = context.get("vertex")
    v.set("selected",true)
    v.labelSize = @labelSizeOver
    console.log "mouseOver"

  vertexMouseOut: (node,event, context)->
    ###
    when mouse is Out node section
    ###
    v = context.get("vertex")
    v.set("selected",false)
    v.labelSize = @labelSize
    console.log "mouseOUT"

  edgeMouseOver: (node,event,context)->
    v = context.get("edge")
    v.set("selected",true)
    console.log "mouseOver"

  edgeMouseOut: (node,event, context)->
    v = context.get("edge")
    v.set("selected",false)
    console.log "mouseOUT"

  initMercury: ->
    ###
      for setting up Mercure editor
    ###
    Mercury.configuration.interface =
      enabled    : true                                     # initial visible state - trigger 'interface:show' to show
      class      : 'FrameInterface'                          # interface class - used on Mercury.init()
      toolbar    : 'Toolbar'                                 # toolbar class to use within the interface
      statusbar  : 'Statusbar'                               # statusbar class to use within the interface
      uploader   : 'Uploader'                                # uploader class to use within the interface
      silent     : false                                     # set to true to disable asking about changes when leaving
      shadowed   : true                                     # puts the interface into a shadow dom when it's available
      maskable   : false                                     # uses a mask over the document for toolbar dialogs
      style      : false                                     # interface style - 'small', 'flat' or 'small flat'
      floating   : true                                     # floats to the focused region
      floatWidth : false                                     # fixed width for floating interface (pixel value - eg. 520)
      floatDrag  : true
    Mercury.init()
    #Mercury.hide()
    #@render(false)
    #@init()
    #@test()

