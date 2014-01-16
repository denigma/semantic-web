class Denigma.Control extends Denigma.BasicView
  constructor: (@selector,@element,@subclass)->
    ###
      jquery-like selector string is passed,
      something like '#lifespan'
    ###
    @node = d3.select(@selector)

  enter:(data)->@select(data).enter()

  exit:(data)->@select(data).exit().remove()

  select: (data)->
    @node.selectAll("#{@element}.#{@subclass}").data(data)

  append: (novel)->
    error("append is not implemented")
    novel


  draw: (data)->
    sel =  @select(data)
    @hide(sel.exit())
    novel = @append(sel.enter())
    @update(sel)

  hide: (sel)->
    sel.remove()


  update: (sel)->
    error("update is not implemented")