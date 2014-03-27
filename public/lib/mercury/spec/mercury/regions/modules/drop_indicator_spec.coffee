#= require spec_helper
#= require mercury/core/region
#= require mercury/regions/modules/drop_indicator

describe "Mercury.Region.Modules.DropIndicator", ->

  Klass = null
  Module = Mercury.Region.Modules.DropIndicator
  subject = null

  beforeEach ->
    class Klass extends Mercury.Region
      @include Module
    subject = new Klass('<div id="foo">')

  describe "#included", ->

    it "sets up the indicator element", ->
      expect( subject.$dropIndicator.is('.mercury-region-drop-indicator') ).to.be.true


  describe "#buildDropIndicator (via the build event)", ->

    it "appends the indicator after @el", ->
      spyOn(subject.$el, 'after')
      subject.trigger('build')
      expect( subject.$el.after ).calledWith(subject.$dropIndicator)

    it "calls #delegateEvents", ->
      spyOn(subject, 'delegateEvents')
      subject.trigger('build')
      expect( subject.delegateEvents ).calledWith
        dragenter: 'showDropIndicator'
        dragover: 'showDropIndicator'
        dragleave: 'hideDropIndicator'
        drop: 'hideDropIndicator'


  describe "#releaseDropIndicator (via the release event)", ->

    it "removes the indicator from the dom", ->
      spyOn(subject.$dropIndicator, 'remove')
      subject.trigger('release')
      expect( subject.$dropIndicator.remove ).called


  describe "#dropIndicatorPosition", ->

    it "returns css attributes to position the indicator", ->
      spyOn(subject.$el, 'position', -> top: 10, left: 20)
      spyOn(subject.$el, 'outerHeight', -> 100)
      spyOn(subject.$el, 'outerWidth', -> 100)
      expect( subject.dropIndicatorPosition() ).to.eql(top: 60, left: 70, display: 'block')


  describe "#showDropIndicator (via dragenter/dragover)", ->

    beforeEach ->
      subject.previewing = false
      Mercury.dragHack = false
      subject.onDropItem = true
      subject.onDropFile = true
      spyOn(subject, 'delay').yieldsOn(subject)

    it "positions the indicator", ->
      spyOn(subject, 'dropIndicatorPosition', -> '_css_')
      spyOn(subject.$dropIndicator, 'css')
      subject.$el.trigger('dragenter')
      expect( subject.$dropIndicator.css ).calledWith('_css_')

    it "removes the mercury-region-snippet-drop-indicator classname", ->
      spyOn(subject.$dropIndicator, 'removeClass')
      subject.$el.trigger('dragover')
      expect( subject.$dropIndicator.removeClass ).calledWith('mercury-region-snippet-drop-indicator')

    it "adds a classname if it looks like we're dragging a snippet", ->
      subject.onDropSnippet = true
      Mercury.dragHack = true
      spyOn(subject.$dropIndicator, 'addClass')
      subject.$el.trigger('dragover')
      expect( subject.$dropIndicator.addClass ).calledWith('mercury-region-snippet-drop-indicator')

    it "adds the mercury-shown classame", ->
      spyOn(subject.$dropIndicator, 'addClass')
      subject.$el.trigger('dragenter')
      expect( subject.$dropIndicator.addClass ).calledWith('mercury-shown')

    it "does nothing if we're previewing", ->
      subject.previewing = true
      subject.$el.trigger('dragenter')
      expect( subject.delay ).not.called

    it "does nothing if we're dragging a snippet and we don't have an onDropSnippet handler", ->
      Mercury.dragHack = true
      subject.onDropSnippet = false
      subject.$el.trigger('dragover')
      expect( subject.delay ).not.called

    it "does nothing if we aren't handling onDropItem and onDropFile", ->
      subject.onDropItem = false
      subject.onDropFile = false
      subject.$el.trigger('dragover')
      expect( subject.delay ).not.called


  describe "#hideDropIndicator (via dragleave/drop)", ->

    beforeEach ->
      spyOn(subject, 'delay').yieldsOn(subject)

    it "adds the mercury-shown classame", ->
      spyOn(subject.$dropIndicator, 'removeClass')
      subject.$el.trigger('drop')
      expect( subject.$dropIndicator.removeClass ).calledWith('mercury-shown')
