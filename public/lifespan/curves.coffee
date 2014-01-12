class Denigma.Curves extends Denigma.Charts
  ###
    class to display lifespan curves
  ###

  marginX : 15 #left margin
  marginY : 15 #top margin
  ticksY: 20

  pointRad: 5

  constructor: (selector,subclass, @poser = null)->
    super(selector,subclass)
    @xAxis = d3.svg.axis().orient("bottom")
    funY = (t)->if(t==0) then "" else (t+"%")
    @yAxis = d3.svg.axis().orient("right").ticks(@ticksY).tickFormat(funY)
    @svg.append("svg").attr("class","xAxis")
    @svg.append("svg").attr("class","yAxis")
    @svg.append("text").attr("id","titleY").text("% alive")
    @svg.append("text").attr("id","titleX").text("time")



  draw: (data)->
    #controls =  [d.control for d in data]
    #tests = (d.test for d in data)
    max = @max(data)
    yrMax = @height-@marginY*2
    @xScale = d3.scale.linear().domain([0,max]).range([@marginX, @width-@marginX])
    @yScale = d3.scale.linear().domain([0,100]).range([yrMax, @marginY])
    @updateAxises(data)
    sel =  @select(data)
    @hide(sel.exit())
    novel = @append(sel.enter())
    @update(sel)

  max: (data)->d3.max(data,(d)->d.get("max"))


  append: (novel)->
    sv = novel.append("svg")
    sv.attr("class","#{@subclass}")
    p = sv.append("path")
    novel



  makePoint: (x,y,color)=> {x:x,y:y,c:color}

  makeCoords: (d)=>
    ###
      creates coord array for curves
    ###
    col = @randColor()
    d.color = col
    orgs = d.test.get("organisms").sort((a,b)->a-b)
    res = [@makePoint(0,100,col)]
    len = orgs.length
    alive = len
    for o in orgs
      alive = alive-1
      res.push(@makePoint(o,Math.round(alive/len*100),col))
    res

  randColor: =>'#'+(0x1000000+(Math.random())*0xffffff).toString(16).substr(1,6)

  update: (sel)->
    xFun = (d)=> @xScale(d.x)
    yFun = (d)=> @yScale(d.y)

    poly = d3.svg.line()
      .x( xFun)
      .y( yFun )
    #  .interpolate("line")
      .interpolate("monotone")

    coords = @makeCoords
    data = sel.data().map((d)->coords(d))
    path = sel.select("path")
    getData = (d,i)->data[i]
    getColor = (d,i)->data[i][0].c
    makePoly = (d,i)->poly(data[i])

    path
      .attr("stroke",getColor)
      .attr("fill","none")
      .attr("d",makePoly)

    #TODO: fix event binding possible bugs
    sel.each (d,i)->
      el = $(@)
      funOver = (dat)=>
        el.attr("stroke-width",3)
      funOut = (dat)=>
        el.attr("stroke-width",1)
      d.on "over", funOver
      d.on "out", funOut
    sel.on("mouseover",(d)->d.fire("over",d))
    sel.on("mouseout",(d)->d.fire("out",d))


    points = sel.selectAll("circle.point").data(getData)
    points.exit().remove()
    points.enter().append("circle").attr("class","point").attr("r",@pointRad)
    points.attr("cx",xFun).attr("cy",yFun).attr("fill",(d)->d.c)



  updateAxises: (data)->
    @xAxis.scale(@xScale)
    gx = @svg.select("svg.xAxis")
    gx.attr("y",@height-@marginY*2).call(@xAxis)

    @yAxis.scale(@yScale)
    gy = @svg.select("svg.yAxis")
    gy.attr("x",@marginX).call(@yAxis)

    #TODO: fix this ugly hardcoded positioning and styles
    @svg.select("#"+"titleX")
      .attr("x", @width  - @marginX-35)
      .attr("y", @height)
      .text("time")
      .style("font-size",22)


    @svg.select("#titleY")
      .attr("transform", "rotate(-90)")
      .attr("x", 0-@height/6+@marginY )
      .attr("y", 0-@marginX )
      .attr("dy", "1em")
      .style("text-anchor", "middle")
      .text("survival rate")
      .style("font-size",24)


