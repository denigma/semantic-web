###!
The Plain region is a simplified single line HTML5 Content Editable region. It restricts drag/drop, can restrict paste
and line feeds and only provides the ability to do some common actions like bold, italics, and underline. This is a
useful region for headings and other single line areas.

Dependencies:
  rangy-core - https://code.google.com/p/rangy/
  rangy-serializer
  rangy-cssclassapplier

Configuration:
  regions:plain:
    actions  : true                                      # allow the common actions (bold/italic/underline)
    pasting  : true                                      # allow pasting -- always sanitized to text
    newlines : false                                     # allow line feeds (on enter and paste)
###
class Mercury.Region.Plain extends Mercury.Region
  @define 'Mercury.Region.Plain', 'plain'
  @include Mercury.Region.Modules.HtmlSelection
  @include Mercury.Region.Modules.SelectionValue
  @include Mercury.Region.Modules.ContentEditable

  @supported: Mercury.support.wysiwyg

  @events:
    'keydown': 'onKeyEvent'
    'paste': 'onPaste'

  constructor: ->
    try window.rangy.init()
    catch e
      @notify(@t('requires Rangy'))
      return false

    super
    @actions = false if @options.allowActs == false


  onDropItem: (e) ->
    @prevent(e)


  onPaste: (e) ->
    @prevent(e)
    @insertContent(e.originalEvent.clipboardData.getData('text/plain'))


  insertContent: (content) ->
    content = if @options.newlines then content.replace('\n', '<br>') else content.replace('\n', ' ')
    document.execCommand('insertHTML', null, content || ' ') # todo: this is just a stub for now


  onKeyEvent: (e) ->
    return if e.keyCode >= 37 && e.keyCode <= 40 # arrows
    return if e.metaKey && e.keyCode == 90 # undo / redo
    return @prevent(e) if e.keyCode == 13 && !@options.newlines # return

    # common actions
    if e.metaKey then switch e.keyCode
      when 66 # b
        @prevent(e)
        return @handleAction('bold')
      when 73 # i
        @prevent(e)
        return @handleAction('italic')
      when 85 # u
        @prevent(e)
        return @handleAction('underline')

    @pushHistory(e.keyCode)


Mercury.Region.Plain.addToolbar

  decoration:
    bold:          ['Bold']
    italic:        ['Italicize']
    underline:     ['Underline']


Mercury.Region.Plain.addAction

  bold:      -> @toggleWrapSelectedWordsInClass('red')
  italic:    -> @toggleWrapSelectedWordsInClass('highlight')
  underline: -> @toggleWrapSelectedWordsInClass('blue')
