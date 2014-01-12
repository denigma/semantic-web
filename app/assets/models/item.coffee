class Denigma.Item extends Batman.Model
  constructor: ->
    super
    @set "selected", false

  @accessor 'unselected',
    get: -> not @get("selected")

  @select: (id)->@_loadIdentity(id)?.set("selected",true)
  @deselect: (id)->@_loadIdentity(id)?.set("selected",false)

  serializeAsForm: false