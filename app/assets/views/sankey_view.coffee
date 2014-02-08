class Denigma.SankeyView extends Batman.View

  sankey: undefined
  svg: undefined
  margin:
    top: 1
    right: 1
    bottom: 6
    left: 1



  width: 960
  height: 500

  viewDidAppear: ->
    unless @svg? then @init

  init:->
    debugger
    ###
      TODO: fix this bad unstable code
    ###

    options =
      exclusive: false
    node = @get("node")
    #element = $(@get("node"))

    #fun = (nd)=>
    file = "http://#{window.location.host}/public/data/energy.json"
    #"#chart"
    @svg = d3.select(node).append("svg")
    @svg.attr("width", @width + @margin.left + @margin.right).attr("height", @height + @margin.top + @margin.bottom)
    @svg.append("g").attr("transform", "translate(" + @margin.left + "," + @margin.top + ")")
    @sankey = d3.sankey().nodeWidth(15).nodePadding(10).size([ @width, @height ])

    d3.json file, (energy) =>@onLoad(energy)

  onLoad: (energy)=>
    color = d3.scale.category20()
    formatNumber = d3.format(",.0f")
    format = (d) ->
      formatNumber(d) + " TWh"
    path = @sankey.link()
    dragmove = (d) ->
      d3.select(this).attr "transform", "translate(" + d.x + "," + (d.y = Math.max(0, Math.min(@height - d.dy, d3.event.y))) + ")"
      @sankey.relayout()
      link.attr "d", path
    @sankey.nodes(energy.nodes).links(energy.links).layout 32
    link = @svg.append("g").selectAll(".link").data(energy.links).enter().append("path").attr("class", "link").attr("d", path).style("stroke-width", (d) ->
      Math.max 1, d.dy
    ).sort((a, b) ->
      b.dy - a.dy
    )
    link.append("title").text (d) ->
      d.source.name + " → " + d.target.name + "\n" + format(d.value)

    node = @svg.append("g").selectAll(".node").data(energy.nodes).enter().append("g").attr("class", "node").attr("transform", (d) ->
      "translate(" + d.x + "," + d.y + ")"
    ).call(d3.behavior.drag().origin((d) ->
      d
    ).on("dragstart", ->
      @parentNode.appendChild this
    ).on("drag", dragmove))
    node.append("rect").attr("height", (d) ->
      d.dy
    ).attr("width", @sankey.nodeWidth()).style("fill", (d) ->
      d.color = color(d.name.replace(RegExp(" .*"), ""))
    ).style("stroke", (d) ->
      d3.rgb(d.color).darker 2
    ).append("title").text (d) ->
      d.name + "\n" + format(d.value)

    node.append("text").attr("x", -6).attr("y", (d) ->
      d.dy / 2
    ).attr("dy", ".35em").attr("text-anchor", "end").attr("transform", null).text((d) ->
      d.name
    ).filter((d) ->
      d.x < @width / 2
    ).attr("x", 6 + @sankey.nodeWidth()).attr "text-anchor", "start"
