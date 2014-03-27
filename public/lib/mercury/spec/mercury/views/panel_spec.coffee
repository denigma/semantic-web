#= require spec_helper
#= require mercury/views/panel

describe "Mercury.Panel", ->

  Klass = Mercury.Panel
  subject = null

  beforeEach ->
    subject = new Klass()

  afterEach ->
    subject.release()

  describe "#setWidth", ->

    it "sets the width of the element", ->
      spyOn(subject, 'css')
      subject.setWidth(42)
      expect( subject.css ).calledWith(width: 42)


  describe "#resize", ->

    beforeEach ->
      spyOn(subject.$titleContainer, 'outerHeight', -> 6)
      spyOn(subject.$el, 'height', -> 42)

    it "does nothing if not visible", ->
      subject.visible = false
      expect( subject.resize() ).to.be.undefined

    it "clears the @showContentTimeout", ->
      spyOn(window, 'clearTimeout')
      subject.showContentTimeout = '_timeout_'
      subject.resize()
      expect( window.clearTimeout ).calledWith('_timeout_')

    it "adds the mercury-no-animation class if we're not animating", ->
      spyOn(subject, 'addClass')
      subject.resize(false)
      expect( subject.addClass ).calledWith('mercury-no-animation')

    it "sets top and bottom if dimensions are available", ->
      spyOn(subject, 'css')
      subject.resize(true, top: 21, bottom: 22)
      expect( subject.css ).calledWith(top: 31, bottom: 32)

    it "removes left if the panel is all the way right", ->
      spyOn(subject, 'css')
      spyOn(subject.$el, 'outerWidth', -> 20)
      spyOn(subject.$el, 'offset', -> left: 70)
      subject.resize(true, top: 21, bottom: 22, width: 100)
      expect( subject.css ).calledWith(left: '')

    it "doesn't remove left if the panel has enough room", ->
      spyOn(subject, 'css')
      spyOn(subject.$el, 'outerWidth', -> 20)
      spyOn(subject.$el, 'offset', -> left: 20)
      subject.resize(true, top: 21, bottom: 22, width: 100)
      expect( subject.css ).not.calledWith(left: '')

    it "tries to get dimensions from the mercury interface", ->
      Mercury.interface.dimensions = -> top: 31, bottom: 32
      spyOn(subject, 'css')
      subject.resize()
      expect( subject.css ).calledWith(top: 41, bottom: 42)

    it "sets the content container height", ->
      spyOn(subject.$contentContainer, 'css')
      subject.resize()
      expect( subject.$contentContainer.css ).calledWith(height: 42 - 6)

    it "calls #showContent (in a delay if animating)", ->
      spyOn(subject, 'delay').yieldsOn(subject)
      spyOn(subject, 'showContent')
      subject.resize(false)
      expect( subject.showContent ).calledWith(false)
      subject.resize(true)
      expect( subject.delay ).calledWith(300, subject.showContent)
      expect( subject.showContent ).called

    it "removes the mercury-no-animation class", ->
      spyOn(subject, 'removeClass')
      subject.resize()
      expect( subject.removeClass ).calledWith('mercury-no-animation')

    it "sets animate to false if you pass an object for the first arg", ->
      spyOn(subject, 'delay')
      subject.resize({})
      expect( subject.delay ).not.called


  describe "#onShow", ->

    beforeEach ->
      spyOn(Mercury, 'trigger')

    it "delays a resize", ->
      spyOn(subject, 'delay').yieldsOn(subject)
      subject.onShow()
      expect( subject.delay ).calledWith(1, subject.resize)

    it "triggers a global panels:hide event", ->
      subject.onShow()
      expect( Mercury.trigger ).calledWith('panels:hide')


  describe "#onHide", ->

    beforeEach ->
      spyOn(Mercury, 'trigger')

    it "triggers a global focus event", ->
      subject.onHide()
      expect( Mercury.trigger ).calledWith('focus')


  describe "#focusFirstFocusable", ->

    it "intentionally does nothing", ->
      subject.focusFirstFocusable()


  describe "#keepFocusConstrained", ->

    it "intentionally does nothing", ->
      subject.keepFocusConstrained()


  describe "#setPositionOnDrag", ->

    beforeEach ->
      subject.viewportSize = width: 100
      spyOn(subject.$el, 'outerWidth', -> 20)
      spyOn(subject, 'css')

    it "keeps x > 10", ->
      subject.setPositionOnDrag(9)
      expect( subject.css ).calledWith(left: 10)
      subject.setPositionOnDrag(11)
      expect( subject.css ).calledWith(left: 11)

    it "pins back to the right when it's too far", ->
      subject.setPositionOnDrag(100)
      expect( subject.css ).calledWith(left: '')
