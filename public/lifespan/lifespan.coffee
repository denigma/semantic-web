###
  Here is where the main code of the demo is
###
class Denigma.LifeSpan extends Batman.Object

  fixture: undefined
  barcharts: undefined
  @w: 640
  @h: 640

  @main: ->
    @fixture = new Denigma.Fixture()

    @poser = new Denigma.RowPoser(rowMargin =20, rowHeight = 56,marginX = 10, dur = 2000)

    @barcharts = new Denigma.BarCharts("#lifespanbars","row",@poser)
    @curves = new Denigma.Curves("#lifespancurves","curve",@poser)

    @positionBars()
    @positionCurves()

    @generate()

    #@generateCurves()

  @positionBars: ->
    @barcharts.svg.attr("class","chart")
    @barcharts.setSize(@w,@h)

  @generate: ->
    num = @fixture.rand(2,5)
    data = @fixture.generate(num)

    #groups = @fixture.generateCurves(num)
    #@curves.draw(groups)
    @curves.draw(data)
    @barcharts.draw(data)


  @positionCurves: ->
    @curves.svg.attr("class","chart")
    @curves.setSize(@w,@h)

  ###
  @generateCurves: =>
    num =  @fixture.rand(2,5)
    groups = @fixture.generateCurves(num)
    @curves.draw(groups)
  ###
Denigma.on "start", ->
  ###
    Event to initiate the main app
  ###
  Denigma.LifeSpan.main()
  #ls.experiment()

