class Denigma.SectionView extends Batman.View

  ready: ->
    options =
      exclusive: false
    @element = $(@get("node"))
    @element.find('.accordion').accordion(options)

class Denigma.ActiveView extends Denigma.SectionView

  ready: ->
    super()
    fun = ->Mercury.trigger('reinitialize')
    setTimeout(fun, 1000)
    #    fun = =>
    #      #alert "delay"
    #      @element.find('b').hallo
    #        plugins:
    #          halloformat: {}
    #          halloblock: {}
    #          hallojustify: {}
    #          hallolists: {}
    #
    #        #'hallolink': {},
    #          halloreundo: {}
    #
    #        editable: true
    #    setTimeout(fun, 1000)




class Denigma.EdgeView extends Denigma.ActiveView

class Denigma.VertexView extends Denigma.ActiveView

class Denigma.Accordion extends Batman.View
  ready: ->
    options =
      exclusive: false
    element = $(@get("node"))

    setTimeout (->element.find('.ui.accordion').accordion(options)), 1000


class Denigma.EdgesView extends Denigma.Accordion
class Denigma.VerticesView extends Denigma.Accordion

class Denigma.QueryView extends Batman.View

  editor: undefined
  ready: ->
    ###
      TODO: fix this bad unstable code
    ###
    options =
      exclusive: false
    node = @get("node")
    element = $(@get("node"))
    fun = (nd)=>
      cont = @get("controller")
      editor = CodeMirror(node,
        value: cont.get("query")
        mode: "sparql"
      )
      editor.on "change", (ins,ch)=>
        cont.set("query",editor.getValue())
        #console.log editor.getValue()
      upd = (q)->if(editor.getValue()!=q) then editor.setValue(q)
      cont.observe('query', upd)
      @editor


    setTimeout (=>fun(node)), 500

