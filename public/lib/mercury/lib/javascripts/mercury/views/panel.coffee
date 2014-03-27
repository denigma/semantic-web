#= require mercury/views/modal
#= require mercury/views/modules/draggable
#= require mercury/templates/panel

class Mercury.Panel extends Mercury.Modal
  @include Mercury.View.Modules.Draggable

  @logPrefix: 'Mercury.Panel:'
  @className: 'mercury-dialog mercury-panel'

  @elements:
    content: '.mercury-panel-content'
    contentContainer: '.mercury-panel-content-container'
    titleContainer: '.mercury-panel-title'
    title: '.mercury-panel-title span'

  @events:
    'mercury:interface:resize': 'resize'
    'mercury:panels:hide': 'hide'
    'mousedown .mercury-panel-title em': 'prevent'
    'mousedown .mercury-panel-title': 'startDrag'
    'click .mercury-panel-title em': 'hide'

  primaryTemplate: 'panel'
  releaseOnHide: false

  setWidth: (width) ->
    @css(width: width)


  resize: (animate = true, dimensions = null) =>
    return unless @visible
    clearTimeout(@showContentTimeout)
    if typeof(animate) == 'object'
      dimensions = animate
      animate = false
    @addClass('mercury-no-animation') unless animate
    if dimensions ||= Mercury.interface?.dimensions?()
      @css(top: dimensions.top + 10, bottom: dimensions.bottom + 10)
      @css(left: '') if @$el.outerWidth() + @$el.offset().left >= dimensions.width - 10
    titleHeight = @$titleContainer.outerHeight()
    height = @$el.height()
    @$contentContainer.css(height: height - titleHeight)
    if animate
      @showContentTimeout = @delay(300, @showContent)
    else
      @showContent(false)
    @removeClass('mercury-no-animation')


  onShow: ->
    @delay(1, @resize)
    Mercury.trigger('panels:hide')


  onHide: ->
    Mercury.trigger('focus')


  focusFirstFocusable: ->


  keepFocusConstrained: ->


  setPositionOnDrag: (x) ->
    x = 10 if x < 10
    return @css(left: '') if x >= @viewportSize.width - @$el.outerWidth() - 10
    @css(left: x)
