#= require spec_helper
#= require mercury/views/toolbar_item

describe "Mercury.ToolbarItem", ->

  Klass = Mercury.ToolbarItem
  subject = null

  beforeEach ->
    subject = new Klass()

  afterEach ->
    subject.release()

  describe "#constructor", ->

    beforeEach ->
      subject.release()

    it "calls super", ->
      spyOn(Klass.__super__, 'constructor', false)
      subject = new Klass('_name_', '_type_', '_value_')
      expect( Klass.__super__.constructor ).calledWith()


  describe "#build", ->

    beforeEach ->
      spyOn(subject, 'buildSubview')

    it "calls #addClasses", ->
      spyOn(subject, 'addClasses')
      subject.build()
      expect( subject.addClasses ).called

    it "calls #buildSubview for all the items in @value", ->
      subject.value = foo: 'bar', bar: 'baz'
      subject.build()
      expect( subject.buildSubview ).calledWith('foo', 'bar')
      expect( subject.buildSubview ).calledWith('bar', 'baz')

    it "adds an additional separator if it's a group", ->
      subject.value = {}
      subject.type = 'group'
      subject.build()
      expect( subject.buildSubview ).calledWith('sep-final', '-')

    it "calls #buildExpander if we're a container and want an expander", ->
      spyOn(subject, 'buildExpander')
      subject.expander = true
      subject.type = 'container'
      subject.build()
      expect( subject.buildExpander ).called

    it "does nothing if @value isn't an object", ->
      subject.value = ''
      subject.type = 'group'
      subject.build()
      expect( subject.buildSubview ).not.called


  describe "#buildSubview", ->

    beforeEach ->
      spyOn(subject, 'appendView')

    it "appends a new group ToolbarItem if the value is an object", ->
      spyOn(Mercury, 'ToolbarItem', -> result: '_group_')
      subject.buildSubview('foo', foo: 'bar')
      expect( Mercury.ToolbarItem ).calledWith('foo', 'group', foo: 'bar')
      expect( subject.appendView ).calledWith(result: '_group_')

    it "appends a new separator ToolbarItem if the value is a string", ->
      spyOn(Mercury, 'ToolbarItem', -> result: '_separator_')
      subject.buildSubview('foo', '_value_')
      expect( Mercury.ToolbarItem ).calledWith('foo', 'separator', '_value_')
      expect( subject.appendView ).calledWith(result: '_separator_')

    it "creates a new ToolbarButton if the value is an array", ->
      spyOn(Mercury.ToolbarButton, 'create', -> '_button_')
      subject.buildSubview('foo', [1, 2, 3])
      expect( Mercury.ToolbarButton.create ).calledWith('foo', 1, 2, 3)
      expect( subject.appendView ).calledWith('_button_')

    it "does nothing if the value is something like a number", ->
      subject.buildSubview('foo', 123)
      expect( subject.appendView ).not.called


  describe "#buildExpander", ->

    beforeEach ->
      spyOn(Mercury, 'ToolbarExpander', -> inst: '_toolbar_expander_')
      spyOn(subject, 'appendView')


    it "appends a toolbar expander", ->
      subject.buildExpander()
      expect( subject.appendView ).calledWith(inst: '_toolbar_expander_')
      expect( Mercury.ToolbarExpander ).calledWith(parent: subject)


  describe "#hiddenButtons", ->

    beforeEach ->
      spyOn(subject.$el, 'height', -> 2)
      spyOn(subject, '$', -> ['<em>', '<div><em>_title_</em></div>', '<em class="mercury-toolbar-button foo">'])
      @callCount = 0
      spyOn($.fn, 'position', => top: 1 + @callCount++) # increase for each one

    it "returns buttons that are out of view (detecting that top is more than height)", ->
      res = subject.hiddenButtons()
      expect( res.length ).to.eq(2)
      expect( res[0] ).to.eql
        title: '_title_'
        class: undefined
        el: res[0].el
      expect( res[1] ).to.eql
        title: undefined
        class: 'foo'
        el: res[1].el


  describe "#addClasses", ->

    it "adds the expected classes", ->
      spyOn(subject, 'addClass')
      subject.addClasses()
      expect( subject.addClass ).calledWith("mercury-toolbar-unknown-unknown mercury-toolbar-unknown")
      subject.name = 'customName'
      subject.type = 'customType'
      subject.value = '-'
      subject.addClasses()
      expect( subject.addClass ).calledWith("mercury-toolbar-custom-name-custom-type mercury-toolbar-line-custom-type")
