class Denigma.DistortionView extends Batman.View

  ready: ->
    ###
      TODO: fix this bad unstable code
    ###

    options =
      exclusive: false
    #node = @get("node")
    element = $(@get("node"))

    fun = (nd)=>
      # Various accessors that specify the four dimensions of data to visualize.
      x = (d) ->
        d.income
      y = (d) ->
        d.lifeExpectancy
      radius = (d) ->
        d.population
      color = (d) ->
        d.region

      # Chart dimensions.
      margin =
        top: 5.5
        right: 19.5
        bottom: 12.5
        left: 39.5

      width = 960
      height = 500 - margin.top - margin.bottom

      # Various scales and distortions.
      xScale = d3.fisheye.scale(d3.scale.log).domain([
        300
        1e5
      ]).range([
        0
        width
      ])
      yScale = d3.fisheye.scale(d3.scale.linear).domain([
        20
        90
      ]).range([
        height
        0
      ])
      radiusScale = d3.scale.sqrt().domain([
        0
        5e8
      ]).range([
        0
        40
      ])
      colorScale = d3.scale.category10().domain([
        "Sub-Saharan Africa"
        "South Asia"
        "Middle East & North Africa"
        "America"
        "Europe & Central Asia"
        "East Asia & Pacific"
      ])

      # The x & y axes.
      xAxis = d3.svg.axis().orient("bottom").scale(xScale).tickFormat(d3.format(",d")).tickSize(-height)
      yAxis = d3.svg.axis().scale(yScale).orient("left").tickSize(-width)

      # Create the SVG container and set the origin.
      svg = d3.select("#chart").append("svg").attr("width", width + margin.left + margin.right).attr("height", height + margin.top + margin.bottom).append("g").attr("transform", "translate(" + margin.left + "," + margin.top + ")")

      # Add a background rect for mousemove.
      svg.append("rect").attr("class", "background").style("background-color","white").attr("width", width).attr "height", height

      # Add the x-axis.
      svg.append("g").attr("class", "x axis").attr("transform", "translate(0," + height + ")").call xAxis

      # Add the y-axis.
      svg.append("g").attr("class", "y axis").call yAxis

      # Add an x-axis label.
      svg.append("text").attr("class", "x label").attr("text-anchor", "end").attr("x", width - 6).attr("y", height - 6).text "income per capita, inflation-adjusted (dollars)"

      # Add a y-axis label.
      svg.append("text").attr("class", "y label").attr("text-anchor", "end").attr("x", -6).attr("y", 6).attr("dy", ".75em").attr("transform", "rotate(-90)").text "life expectancy (years)"

      # Load the data.
      d3.json "http://#{window.location.host}/public/data/nations.json", (nations) ->

        # Add a dot per nation. Initialize the data at 1800, and set the colors.

        # Add a title.

        # Positions the dots based on data.
        position = (dot) ->
          dot.attr("cx", (d) ->
            xScale x(d)
          ).attr("cy", (d) ->
            yScale y(d)
          ).attr "r", (d) ->
            radiusScale radius(d)

          return
        dot = svg.append("g").attr("class", "dots").selectAll(".dot").data(nations).enter().append("circle").attr("class", "dot").style("fill", (d) ->
          colorScale color(d)
        ).call(position).sort((a, b) ->
          radius(b) - radius(a)
        )
        dot.append("title").text (d) ->
          d.name

        svg.on "mousemove", ->
          mouse = d3.mouse(this)
          xScale.distortion(2.5).focus mouse[0]
          yScale.distortion(2.5).focus mouse[1]
          dot.call position
          svg.select(".x.axis").call xAxis
          svg.select(".y.axis").call yAxis




    setTimeout( (=>fun(element)), 500)

