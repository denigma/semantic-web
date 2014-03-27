Plugin = Mercury.registerPlugin 'snippets',
  description: 'Provides interface for adding snippets to various regions -- may require server implementation.'
  version: '1.0.0'

  config:
    toggleOnSnippetRegion: false

  actions:
    snippet: 'insert'

  registerButton: ->
    @button.set(type: 'snippets', global: true, toggle: true, subview: @bindTo(@panel = new Plugin.Panel()))


  bindTo: (view) ->
    view.on('insert:snippet', (value) => @triggerAction(value))


  insert: (name, snippetName) ->
    snippet = Mercury.getSnippet(snippetName, true).on('rendered', (view) -> Mercury.trigger('action', name, snippet, view))
    snippet.initialize(@region)


  onRegionFocus: (@region) ->
    return unless @config('toggleOnSnippetRegion')
    return if @region == @lastRegion
    @lastRegion = @region
    @togglePanelByRegion()


  togglePanelByRegion: ->
    return unless @panel
    if @region.type() == 'snippet'
      @shownByRegion = true unless @panel.visible
      @panel.show()
    else if @shownByRegion
      @panel.hide()
      @shownByRegion = false


class Plugin.Panel extends Mercury.Panel
  mixins:    [Mercury.View.Modules.FilterableList]
  template:  'snippets'
  className: 'mercury-snippets-panel'
  title:     'Snippets Panel'
  width:     300
  hidden:    true

  events:
    'click input.btn': (e) -> @trigger('insert:snippet', $(e.target).closest('[data-value]').data('value'))

  update: ->
    super
    items = @$('li')
    items.on('dragend', -> Mercury.dragHack = false).on 'dragstart', (e) ->
      Mercury.dragHack = true # webkit has problems with different drag/drop styles.
      e.originalEvent.dataTransfer.setData('snippet', $(e.target).data('value'))


JST['/mercury/templates/snippets'] = ->
  controls = """<div class="mercury-snippet-actions">Drag or <input type="button" value="Insert" class="btn"></div>"""
  ret = '<input type="search" class="mercury-filter"><ul>'
  for name, snippet of Mercury.Snippet.all()
    ret += """<li data-filter="#{name}" data-value="#{name}">#{snippet.title}<em>#{snippet.description}</em>#{controls}</li>"""
  ret + '</ul>'
