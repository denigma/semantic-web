class Denigma.DecorView extends Denigma.BasicView

  constructor: (poser,@width)->
    super(poser)

  append: (rows)->
    ###
      adds rowumn decorations
    ###
    h = @poser.contentHeight()
    border = rows.append("rect")
    border.attr("class","decor")
      .attr("width",@width)
      .attr("height",h)
      .attr("rx",10)
      .attr("ry",10)

  update: (rows)->
    dec = rows.select(".decor")
    dec.each (d,i)->
      el = $(@)
      funOver = (dat)=>
        el.attr("stroke-width",8).style("stroke",(d)->d.color) if el.style?
      funOut = (dat)=>
        el.attr("stroke-width",3).style("stroke",(d)->d.color) if el.style?
      d.on "over", funOver
      d.on "out", funOut

    dec.style("stroke",(d)->d.color).attr("stroke-width",3)