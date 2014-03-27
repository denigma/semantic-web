#= require spec_helper
#= require mercury/core/region

describe "Mercury.Region", ->

  Klass = ->
  subject = null

  beforeEach ->
    Mercury.configure 'regions', identifier: 'id', attribute: 'data-mercury', options: 'data-mercury-options'
    class Klass extends Mercury.Region
      supported: true
    subject = new Klass('<div data-mercury="foo" id="test">')

  describe "Modules", ->

    it "defines the right namespace", ->
      expect( Klass.Modules ).to.be.a('Object')

    it "includes in the expected modules", ->
      expect( Klass.config ).to.be.a('Function')
      expect( Klass.on ).to.be.a('Function')
      expect( Klass.t ).to.be.a('Function')
      expect( subject.on ).to.be.a('Function')
      expect( subject.config ).to.be.a('Function')
      expect( subject.t ).to.be.a('Function')
      expect( subject.log ).to.be.a('Function')
      expect( subject.pushStack ).to.be.a('Function')


  describe ".define", ->

    it "assigns @klass and @type", ->
      Klass.define('TestRegion', '_test_')
      expect( Klass.klass ).to.eq('TestRegion')
      expect( Klass.type ).to.eq('_test_')

    it "sets the @logPrefix", ->
      Klass.define('TestRegion')
      expect( Klass.logPrefix ).to.eq('TestRegion:')
      expect( Klass::logPrefix ).to.eq('TestRegion:')

    it "merges actions with prototype.actions", ->
      Klass::actions = foo: 'bar'
      Klass.define('TestRegion', '_test_', bar: 'baz')
      expect( Klass::actions ).to.eql(foo: 'bar', bar: 'baz')

    it "defaults the toolbars", ->
      Klass.define('TestRegion', '_test_', bar: 'baz')
      expect( Klass::toolbars ).to.eql(['_test_'])

    it "calls .off", ->
      spyOn(Klass, 'off')
      Klass.define('TestRegion')
      expect( Klass.off ).called

    it "returns itself for chaining" ,->
      expect( Klass.define('TestRegion') ).to.eq(Klass)


  describe ".create", ->

    beforeEach ->
      Mercury.Region.Foo = spy()
      spyOn(Klass, 'notify')
      spyOn(Klass::, 'notify')

    it "instantiates the expected region class with the element", ->
      Klass.create('<div data-mercury="foo">')
      expect( Mercury.Region.Foo ).called

    it "detects the type using the configurable attribute", ->
      Mercury.configure 'regions:attribute', 'data-type'
      Klass.create('<div data-type="fOo">')
      expect( Mercury.Region.Foo ).called

    it "notifies if there was no type detected", ->
      Klass.create('<div id="test">')
      expect( Klass.notify ).calledWith('Region type not provided')

    it "notifies if we're falling back to the base region", ->
      Klass.create('<div data-mercury="bar" id="test">')
      expect( Klass.notify ).calledWith('Unknown Bar region type, falling back to base region')


  describe ".addBehavior", ->

    beforeEach ->
      spyOn(Klass, 'addAction')
      spyOn(Klass, 'addContext')
      spyOn(Klass, 'addData')
      spyOn(Klass, 'addToolbar')

    it "calls addAction if there's an action", ->
      Klass.addBehavior('name')
      expect( Klass.addAction ).not.called
      Klass.addBehavior('name', action: '_action_')
      expect( Klass.addAction ).calledWith('name', '_action_')

    it "calls addContext if there's a context", ->
      Klass.addBehavior('name', context: '_context_')
      expect( Klass.addContext ).calledWith('name', '_context_')

    it "calls addData if there's data", ->
      Klass.addBehavior('name', data: '_data_')
      expect( Klass.addData ).calledWith('name', '_data_')

    it "calls addToolbar if there's a toolbar", ->
      Klass.addBehavior('name', toolbar: '_toolbar_')
      expect( Klass.addToolbar ).calledWith('name', name: '_toolbar_')



  describe ".addAction", ->

    it "adds the action with a handler", ->
      Klass.addAction('foo', '_handler_')
      expect( Klass.actions.foo ).to.eq('_handler_')

    it "allows passing an object of actions/handlers", ->
      Klass.addAction(foo: '_handler_')
      expect( Klass.actions.foo ).to.eq('_handler_')

    it "merges the actions with the existing actions", ->
      Klass.addAction(foo: '_foo_handler_')
      Klass.addAction(bar: '_bar_handler_')
      expect( Klass.actions ).to.eql(foo: '_foo_handler_', bar: '_bar_handler_')


  describe ".addContext", ->

    it "adds the context with a handler", ->
      Klass.addContext('foo', '_handler_')
      expect( Klass.context.foo ).to.eq('_handler_')

    it "allows passing an object of actions/handlers", ->
      Klass.addContext(foo: '_handler_')
      expect( Klass.context.foo ).to.eq('_handler_')

    it "merges the contexts with existing contexts", =>
      Klass.addContext(foo: '_foo_handler_')
      Klass.addContext(bar: '_bar_handler_')
      expect( Klass.context ).to.eql(foo: '_foo_handler_', bar: '_bar_handler_')


  describe ".addData", ->

    it "adds the attribute with a handler", ->
      Klass.addData('foo', '_handler_')
      expect( Klass.dataAttrs.foo ).to.eq('_handler_')

    it "allows passing an object of actions/handlers", ->
      Klass.addData(foo: '_handler_')
      expect( Klass.dataAttrs.foo ).to.eq('_handler_')

    it "merges the contexts with existing contexts", =>
      Klass.addData(foo: '_foo_handler_')
      Klass.addData(bar: '_bar_handler_')
      expect( Klass.dataAttrs ).to.eql(foo: '_foo_handler_', bar: '_bar_handler_')


  describe ".addToolbar", ->

    beforeEach ->
      spyOn(Mercury, 'configure')

    it "adds the toolbar", ->
      Klass.addToolbar(foo: 'bar')
      expect( Mercury.configure ).calledWith('toolbars:unknown', foo: 'bar')
      Klass.addToolbar('custom', foo: 'bar')
      expect( Mercury.configure ).calledWith('toolbars:unknown:custom', foo: 'bar')


  describe "#constructor", ->

    beforeEach ->
      spyOn(Klass::, 'notify')

    it "checks if the element already has a region", ->
      spyOn($.fn, 'data', -> true)
      subject = new Klass('<div>')
      expect( $.fn.data ).calledWith('region')

    it "notifies if not supported", ->
      Klass.supported = false
      subject = new Klass()
      expect( subject.notify ).calledWith('Is unsupported in this browser')

    it "merges options with the region configuration", ->
      Mercury.configure 'regions:unknown', foo: 'bar'
      subject = new Klass('<div>', bar: 'baz')
      expect( subject.options ).to.eql(foo: 'bar', bar: 'baz')

    it "merges options with the options data from the element", ->
      Mercury.configure 'regions:unknown', foo: 'bar', bar: 'baz'
      subject = new Klass($("""<div data-mercury-options='{"previewing": true, "bar": "foo"}'">"""), foo: 'baz')
      expect( subject.options.previewing ).to.be.true
      expect( subject.options.foo ).to.eq('bar')
      expect( subject.foo ).to.eq('bar')
      expect( subject.options.bar ).to.eq('foo')
      expect( subject.bar ).to.eq('foo')

    it "merges context with constructor.context", ->
      Klass.context = foo: 'bar'
      Klass::context = bar: 'baz'
      subject = new Klass('<div>')
      expect( subject.context ).to.eql(foo: 'bar', bar: 'baz')

    it "merges dataAttrs with constructor.dataAttrs", ->
      Klass.dataAttrs = foo: 'bar'
      Klass::dataAttrs = bar: 'baz'
      subject = new Klass('<div>')
      expect( subject.dataAttrs ).to.eql(foo: 'bar', bar: 'baz')

    it "merges defaultData with constructor.defaultData", ->
      Klass.defaultData = foo: 'bar'
      Klass::defaultData = bar: 'baz'
      subject = new Klass('<div>')
      expect( subject.defaultData ).to.eql(foo: 'bar', bar: 'baz')

    it "sets @placeholder", ->
      subject = new Klass($("""<div data-mercury-options='{"placeholder":"Test placeholder"}'">"""), foo: 'bar')
      expect( subject.placeholder ).to.eq('Test placeholder')

    it "calls #beforeBuild if it's defined", ->
      Klass::beforeBuild = spy()
      subject = new Klass('<div>')
      expect( subject.beforeBuild ).called

    it "sets a tabindex (so it's focusable) unless we've provided our own focusable element", ->
      subject = new Klass('<div id="_name_">')
      expect( subject.attr('tabindex') ).to.eq('0')
      subject = new Klass('<div id="_name_">', $focusable: $('<div>'))
      expect( subject.attr('tabindex') ).to.be.undefined

    it "sets the name from the configurable attribute", ->
      subject = new Klass('<div id="_name_">')
      expect( subject.name ).to.eq('_name_')
      Mercury.configure 'regions:identifier', 'data-name'
      subject = new Klass('<div data-name="_name_">')
      expect( subject.name ).to.eq('_name_')

    it "allows passing the name", ->
      subject = new Klass('<div>', name: '_name_')
      expect( subject.name ).to.eq('_name_')

    it "sets default instance vars (and allows them to be overridden)", ->
      subject = new Klass('<div id="name">')
      expect( subject.previewing ).to.be.false
      expect( subject.focused ).to.be.false
      expect( subject.$focusable ).to.eq(subject.$el)
      expect( subject.skipHistoryOn ).to.eql(['redo'])

      focusable = $('<textarea>')
      subject = new Klass('<div id="name">', previewing: true, focused: true, $focusable: focusable, skipHistoryOn: ['foo'])
      expect( subject.previewing ).to.be.true
      expect( subject.focused ).to.be.true
      expect( subject.$focusable ).to.eq(focusable)
      expect( subject.skipHistoryOn ).to.eql(['foo'])

    it "calls #setInitialData", ->
      spyOn(Klass::, 'setInitialData')
      subject = new Klass('<div>')
      expect( subject.setInitialData ).called

    it "calls #afterBuild if it's defined", ->
      Klass::afterBuild = spy()
      subject = new Klass('<div>')
      expect( subject.afterBuild ).called

    it "sets data-region to the instance", ->
      subject = new Klass('<div>')
      expect( subject.$el.data('region') ).to.eq(subject)

    it "sets data-placehoder", ->
      subject = new Klass('<div>', placeholder: 'Test placeholder')
      expect( subject.$focusable.data('placeholder') ).to.eq('Test placeholder')

    it "notifies if we have no name", ->
      subject = new Klass('<div>')
      expect( subject.notify ).calledWith('No name provided for the unknown region, falling back to random')

    it "falls back to a random name if we have no name", ->
      spyOn(Math, 'random', -> 42)
      subject = new Klass('<div>')
      expect( subject.name ).to.eq('unknown420000')
      expect( subject.$el.attr('id') ).to.eq('unknown420000')

    it "calls #addRegionAttrs", ->
      spyOn(Klass::, 'addRegionAttrs')
      subject = new Klass('<div>')
      expect( subject.addRegionAttrs ).called

    it "calls #pushHistory", ->
      spyOn(Klass::, 'pushHistory')
      subject = new Klass('<div>')
      expect( subject.pushHistory ).called

    it "doesn't call #pushHistory if @skipHistoryOnInitialize", ->
      Klass::skipHistoryOnInitialize = true
      spyOn(Klass::, 'pushHistory')
      subject = new Klass('<div>')
      expect( subject.pushHistory ).not.called

    it "calls #bindDefaultEvents", ->
      spyOn(Klass::, 'bindDefaultEvents')
      subject = new Klass('<div id="name">')
      expect( subject.bindDefaultEvents ).called

    it "calls #handleAction", ->
      spyOn(Klass::, 'handleAction')
      subject = new Klass('<div id="name">')
      expect( subject.handleAction ).called



  describe "#setInitialData", ->

    it "assigns data based on the element data, or defaults", ->
      spyOn(subject, 'data')
      subject.defaultData = foo: 'bar'
      subject.$el.data('bar', 'baz')
      subject.dataAttrs = {foo: spy(), bar: spy(), baz: spy()}
      subject.setInitialData()
      expect( subject.data ).calledWith(foo: 'bar')
      expect( subject.data ).calledWith(bar: 'baz')
      expect( subject.data ).calledWith(baz: null)


    describe "#addRegionAttrs", ->

    it "adds a class to the element", ->
      subject.$el.removeAttr('class')
      subject.addRegionAttrs()
      expect( subject.$el.attr('class') ).to.eq('mercury-unknown-region')
      expect( subject.$focusable.attr('data-mercury-region') ).to.eq('true')


  describe "#type", ->

    it "returns the constructor.type", ->
      expect( subject.type() ).to.eq('unknown')


  describe "#trigger", ->

    it "calls super", ->
      spyOn(Klass.__super__, 'trigger')
      subject.trigger('foo')
      expect( Klass.__super__.trigger ).called

    it "triggers a global event", ->
      spyOn(Mercury, 'trigger')
      subject.trigger('foo')
      expect( Mercury.trigger ).calledWith('region:foo', subject)


  describe "#hasAction", ->

    it "returns true if an action is defined", ->
      subject.actions.foo = '_action_'
      expect( subject.hasAction('foo') ).to.be.true

    it "returns false if an action isn't defined", ->
      expect( subject.hasAction('foo') ).to.be.false


  describe "#hasContext", ->

    it "returns false if a context isn't defined", ->
      expect( subject.hasContext('foo') ).to.be.false

    it "returns the result of the context method converted to a boolean", ->
      subject.context.foo = -> 'foo'
      expect( subject.hasContext('foo') ).to.be.true
      expect( subject.hasContext('foo', true) ).to.eql('foo')
      subject.context.foo = -> false
      expect( subject.hasContext('foo') ).to.be.false


  describe "#handleAction", ->

    beforeEach ->
      subject.focused = true
      subject.actions = {foo: spy()}
      spyOn(Mercury.Action, 'create', -> '_action_')
      spyOn(subject, 'pushHistory')

    it "calls #pushHistory if the action isn't set in @skipHistoryOn", ->
      subject.handleAction('redo')
      expect( subject.pushHistory ).not.called
      subject.handleAction('undo')
      expect( subject.pushHistory ).called

    it "creates an action instance from the options", ->
      subject.handleAction('foo', bar: 'baz')
      expect( Mercury.Action.create ).calledWith('foo', bar: 'baz')

    it "calls the action we've delegated", ->
      subject.handleAction('foo')
      expect( subject.actions.foo ).calledWith('_action_')

    it "triggers an action event", ->
      spyOn(subject, 'trigger')
      subject.handleAction('foo')
      expect( subject.trigger ).calledWith('action', '_action_')

    it "triggers an update event", ->
      spyOn(subject, 'trigger')
      subject.handleAction('foo')
      expect( subject.trigger ).calledWith('update')

    it "doesn't call the action we've delegated if not focused", ->
      subject.focused = false
      subject.previewing = false
      subject.handleAction('foo')
      expect( subject.actions.foo ).not.called

    it "doesn't call the action we've delegated if previewing", ->
      subject.previewing = true
      subject.handleAction('foo')
      expect( subject.actions.foo ).not.called


  describe "#handleMode", ->

    it "calls a #toggleMode method with arguments passed", ->
      subject.toggleFoo = spy()
      subject.togglePreview = spy()
      subject.handleMode('preview', 1, 2, '3')
      expect( subject.togglePreview ).calledWith(1, 2, '3')
      subject.handleMode('foo')
      expect( subject.toggleFoo ).called


  describe "#togglePreview", ->

    beforeEach ->
      subject.previewing = false

    it "toggles @previewing", ->
      subject.togglePreview()
      expect( subject.previewing ).to.be.true
      subject.togglePreview()
      expect( subject.previewing ).to.be.false

    it "triggers a preview event", ->
      spyOn(subject, 'trigger')
      subject.togglePreview()
      expect( subject.trigger ).calledWith('preview', true)

    it "calls #onTogglePreview if it's defined", ->
      subject.onTogglePreview = spy()
      subject.togglePreview()
      expect( subject.onTogglePreview ).called

    describe "when previewing", ->

      it "calls #blur", ->
        spyOn(subject, 'blur')
        subject.togglePreview()
        expect( subject.blur ).called

      it "removes the tabindex attribute (so it's no longer focusable)", ->
        subject.$focusable.attr(tabindex: 42)
        subject.togglePreview()
        expect( subject.$focusable.attr('tabindex') ).to.be.undefined

      it "removes the data attribute used for styling", ->
        subject.$focusable.attr('data-mercury-region', true)
        subject.togglePreview()
        expect( subject.$focusable.attr('data-mercury-region') ).to.be.undefined

    describe "when not previewing", ->

      beforeEach ->
        subject.previewing = true

      it "adds a tabindex attribute", ->
        subject.$focusable.removeAttr('tabindex')
        subject.togglePreview()
        expect( subject.$focusable.attr('tabindex') ).to.eq('0')

      it "adds the data attribute used for styling", ->
        subject.$focusable.removeAttr('data-mercury-region')
        subject.togglePreview()
        expect( subject.$focusable.attr('data-mercury-region') ).to.eq('true')


  describe "#pushHistory", ->

    beforeEach ->
      spyOn(subject, 'pushStack')
      spyOn(subject, 'toStack', -> '_stack_')

    it "pushes to the stack", ->
      subject.pushHistory()
      expect( subject.toStack ).called
      expect( subject.pushStack ).calledWith('_stack_')

    describe "with a keycode", ->

      it "pushes to the stack on return, delete, or backspace", ->
        subject.pushHistory(13)
        expect( subject.toStack ).calledOnce
        expect( subject.pushStack ).calledOnce
        subject.pushHistory(46)
        expect( subject.toStack ).calledTwice
        expect( subject.pushStack ).calledTwice
        subject.pushHistory(8)
        expect( subject.toStack ).calledThrice
        expect( subject.pushStack ).calledThrice

      it "delays pushing to the stack", ->
        spyOn(subject, 'delay').yields()
        subject.pushHistory(42)
        expect( subject.delay ).calledWith(2500, sinon.match.func)
        expect( subject.toStack ).called
        expect( subject.pushStack ).calledWith('_stack_')


  describe "#focus", ->

    it "sets @focused to true", ->
      subject.focus()
      expect( subject.focused ).to.be.true

    it "calls focus on the element", ->
      spyOn(subject.$el, 'focus')
      subject.focus()
      expect( subject.$el.focus ).called

    it "sets the scrollTop back to what it was before focus", ->
      subject.focus(true)
      subject.focus()

    it "calls @onFocus if it's defined", ->
      subject.onFocus = spy()
      subject.focus()
      expect( subject.onFocus ).called


  describe "#blur", ->

    it "sets @focused to false", ->
      subject.blur()
      expect( subject.focused ).to.be.false

    it "calls blur on the element", ->
      spyOn(subject.$el, 'blur')
      subject.blur()
      expect( subject.$el.blur ).called

    it "calls @onBlur if it's defined", ->
      subject.onBlur = spy()
      subject.blur()
      expect( subject.onBlur ).called


  describe "#value", ->

    it "calls #html with the value", ->
      spyOn(subject, 'html')
      subject.value('foo')
      expect( subject.html ).calledWith('foo')

    it "calls #html without arguments if the value is null or undefined", ->
      spyOn(subject, 'html')
      subject.value()
      expect( subject.html ).calledWith()


  describe "#data", ->

    it "allows getting data", ->
      subject.$el.data(foo: 'bar')
      expect( subject.data() ).to.eql(foo: 'bar')
      expect( subject.data('foo') ).to.eq('bar')

    it "sets the element data", ->
      expect( subject.data(foo: 'bar') ).to.eq(subject.$el)
      expect( subject.$el.data() ).to.eql(mercury: "foo", region: subject, mercuryRegion: true, foo: 'bar', placeholder: '')
      subject.data('bar', 'baz')
      expect( subject.$el.data() ).to.eql(mercury: 'foo', region: subject, mercuryRegion: true, foo: 'bar', bar: 'baz', placeholder: '')


  describe "#cleanData", ->

    beforeEach ->
      @data =
        region: '_region_',
        mercury: '_mercury_',
        mercuryRegion: '_mercuryRegion_',
        placeholder: '_placeholder_',
        regionOptions: '_regionOptions'


    it "removes unwanted data from an object", ->
      @data.foo = 'bar'
      subject.cleanData(@data)
      expect( @data ).to.eql(foo: 'bar')


  describe "#setData", ->

    it "sets the data to the element", ->
      subject.setData(foo: 'bar')
      expect( subject.$el.data() ).to.eql(mercury: 'foo', region: subject, mercuryRegion: true, foo: 'bar', placeholder: '')

    it "calls any data attr handlers that are defined", ->
      subject.dataAttrs.foo = spy()
      subject.setData(foo: 'bar')
      expect( subject.dataAttrs.foo ).calledWith('bar')


  describe "#snippets", ->

    it "returns an empty object", ->
      expect( subject.snippets() ).to.eql({})


  describe "#hasChanges", ->

    it "returns false if there's no changes", ->
      expect( subject.hasChanges() ).to.be.false

    it "returns true if @changed", ->
      subject.changed = true
      expect( subject.hasChanges() ).to.be.true

    it "returns true if @initialValue has changed", ->
      spyOn(subject, 'toJSON', -> '_foo_')
      expect( subject.hasChanges() ).to.be.true


  describe "#onSave", ->

    it "resets @initialValue and @changed", ->
      spyOn(subject, 'toJSON', -> '_bar_')
      subject.changed = true
      subject.onSave()
      expect( subject.changed ).to.be.false
      expect( subject.initialValue ).to.eq('"_bar_"')


  describe "#onUndo", ->

    it "calls #value with the value returned from #undoStack", ->
      spyOn(subject, 'fromStack')
      spyOn(subject, 'undoStack', -> '_undo_stack_')
      subject.onUndo()
      expect( subject.fromStack ).calledWith('_undo_stack_')


  describe "#onItemDropped", ->

    beforeEach ->
      spyOn(subject, 'focus')
      @e = originalEvent: {dataTransfer: {files: [], getData: ->}}, preventDefault: spy()

    it "calls #focus", ->
      subject.onItemDropped(@e)
      expect( subject.focus ).called

    it "prevents the event and calls #onDropFile with the expected array when files are dropped", ->
      subject.onDropFile = spy()
      @e.originalEvent.dataTransfer.files = ['_file1_', '_file2_']
      subject.onItemDropped(@e)
      expect( @e.preventDefault ).called
      expect( subject.onDropFile ).calledWith(['_file1_', '_file2_'])

    it "doesn't prevent the event if there's no #onDropFile handler", ->
      @e.originalEvent.dataTransfer.files = ['_file1_', '_file2_']
      subject.onItemDropped(@e)
      expect( @e.preventDefault ).not.called

    it "calls #onDropSnippet with the snippet if it's defined and it looks like a snippet was dropped", ->
      subject.onDropSnippet = spy()
      spyOn(@e.originalEvent.dataTransfer, 'getData', -> '_snippet_name_')
      mock = initialize: spy()
      spyOn(Mercury.Snippet, 'get', -> mock)
      subject.onItemDropped(@e)
      expect( Mercury.Snippet.get ).calledWith('_snippet_name_', true)
      expect( subject.onDropSnippet ).calledWith(mock)
      expect( mock.initialize ).calledWith(subject)

    it "doesn't call #onDragSnippet if it doesn't look like a snippet was dropped", ->
      subject.onDropSnippet = spy()
      subject.onItemDropped(@e)
      expect( subject.onDropSnippet ).not.called

    it "calls #onDropItem if it's defined", ->
      subject.onItemDropped(@e)
      subject.onDropItem = spy()
      subject.onItemDropped(@e)
      expect( subject.onDropItem ).calledWith(@e, @e.originalEvent.dataTransfer)

    it "does nothing if we're previewing", ->
      subject.previewing = true
      subject.onItemDropped(@e)
      expect( subject.focus ).not.called

    it "calls #handleAction", ->
      spyOn(subject, 'handleAction')
      subject.onItemDropped(@e)
      expect( subject.handleAction ).calledWith('dropped')


  describe "#onRedo", ->

    it "calls #value with the value returned from #redoStack", ->
      spyOn(subject, 'fromStack')
      spyOn(subject, 'redoStack', -> '_redo_stack_')
      subject.onRedo()
      expect( subject.fromStack ).calledWith('_redo_stack_')


  describe "#toStack", ->

    it "returns the results of #toJSON", ->
      spyOn(subject, 'toJSON', -> '_json_')
      expect( subject.toStack() ).to.eq('_json_')
      expect( subject.toJSON ).called


  describe "#fromStack", ->

    it "calls #fromJSON", ->
      spyOn(subject, 'fromJSON')
      subject.fromStack(foo: 'bar')
      expect( subject.fromJSON ).calledWith(foo: 'bar')


  describe "#toJSON", ->

    beforeEach ->
      subject.name = '_name_'
      subject.constructor.type = '_type_'
      spyOn(subject, 'value', -> '_value_')
      spyOn(subject, 'data', -> {foo: 'bar'})
      spyOn(subject, 'snippets', -> '_snippets_')

    it "returns the expected object", ->
      expect( subject.toJSON() ).to.eql
        name: '_name_'
        type: '_type_'
        value: '_value_'
        data: {foo: 'bar'}
        snippets: '_snippets_'


  describe "#fromJSON", ->

    beforeEach ->
      spyOn(subject, 'value', -> '_value_')
      spyOn(subject, 'data', -> {region: 'foo', foo: 'bar'})

    it "parses the json if it's a string", ->
      spyOn(JSON, 'parse', -> {})
      subject.fromJSON('_json_')
      expect( JSON.parse ).calledWith('_json_')

    it "sets the value if present", ->
      subject.fromJSON({})
      expect( subject.value ).not.called
      subject.fromJSON(value: null)
      expect( subject.value ).not.called
      subject.fromJSON(value: undefined)
      expect( subject.value ).not.called
      subject.fromJSON(value: '')
      expect( subject.value ).calledWith('')
      subject.fromJSON(value: '_value_')
      expect( subject.value ).calledWith('_value_')

    it "sets the data if present", ->
      subject.fromJSON({})
      expect( subject.data ).not.called
      subject.fromJSON(data: '_data_')
      expect( subject.data ).calledWith('_data_')


  describe "#load", ->

    beforeEach ->
      spyOn(subject, 'fromJSON')

    it "calls #fromJSON", ->
      subject.load(foo: 'bar')
      expect( subject.fromJSON ).calledWith(foo: 'bar')

    it "calls #loadSnippets if it's available", ->
      subject.loadSnippets = spy()
      subject.load(foo: 'bar', snippets: {bar: 'baz'})
      expect( subject.loadSnippets ).calledWith(bar: 'baz')
      subject.load({})
      expect( subject.loadSnippets ).calledWith({})

    it "calls #handleAction", ->
      spyOn(subject, 'handleAction')
      subject.load({})
      expect( subject.handleAction ).calledWith('loaded')


  describe "#release", ->

    it "removes the reference for data-region", ->
      expect( subject.$el.data('region') ).to.be.defined
      subject.release()
      expect( subject.$el.data('region') ).to.be.null

    it "removes the region class from @$el", ->
      expect( subject.$el.hasClass('mercury-unknown-region') ).to.be.true
      subject.release()
      expect( subject.$el.hasClass('mercury-unknown-region') ).to.be.false

    it "removes the tabindex attribute from @$focusable", ->
      expect( subject.$focusable.attr('tabindex') ).to.eq('0')
      subject.release()
      expect( subject.$focusable.attr('tabindex') ).to.be.undefined

    it "removes the data-mercury-region attribute from @$focusable", ->
      expect( subject.$focusable.attr('data-mercury-region') ).to.eq('true')
      subject.release()
      expect( subject.$focusable.attr('data-mercury-region') ).to.be.undefined

    it "triggers a release event", ->
      spyOn(subject, 'trigger')
      subject.release()
      expect( subject.trigger ).calledWith('release')

    it "calls @$el.off and @$focusable.off", ->
      subject.$focusable = off: spy(), removeAttr: spy(-> subject.$focusable), blur: spy()
      spyOn(subject.$el, 'off')
      subject.release()
      expect( subject.$el.off ).called
      expect( subject.$focusable.off ).called

    it "calls #off", ->
      spyOn(subject, 'off')
      subject.release()
      expect( subject.off ).called

    it "calls #blur", ->
      spyOn(subject, 'blur')
      subject.release()
      expect( subject.blur ).called


  describe "#bindDefaultEvents", ->

    beforeEach ->
      spyOn(subject, 'bindFocusEvents')
      spyOn(subject, 'bindKeyEvents')
      spyOn(subject, 'bindMouseEvents')
      spyOn(subject, 'bindDropEvents')

    it "calls #delegateActions with what we've defined", ->
      subject.actions = {foo: 'bar'}
      subject.constructor.actions = {bit: 'bot'}
      spyOn(subject, 'delegateActions')
      subject.bindDefaultEvents()
      expect( subject.delegateActions ).calledWith(foo: 'bar', bit: 'bot', undo: 'onUndo', redo: 'onRedo')

    it "calls #delagateEvents with the expected events", ->
      spyOn(subject, 'delegateEvents')
      subject.bindDefaultEvents()
      expect( subject.delegateEvents ).calledWith
        'mercury:action': sinon.match.func
        'mercury:mode': sinon.match.func
        'mercury:save': sinon.match.func

    it "binds to the global 'action' event", ->
      spyOn(subject, 'handleAction')
      spyOn(Mercury, 'on').callsArgOnWith(1, subject, 1, 2, '3')
      subject.bindDefaultEvents()
      expect( Mercury.on ).calledWith('action', sinon.match.func)
      expect( subject.handleAction ).calledWith(1, 2, '3')

    it "binds to the global 'mode' event", ->
      spyOn(subject, 'handleMode')
      spyOn(Mercury, 'on').callsArgOnWith(1, subject, 1, 2, '3')
      subject.bindDefaultEvents()
      expect( Mercury.on ).calledWith('mode', sinon.match.func)
      expect( subject.handleMode ).calledWith(1, 2, '3')

    it "binds to the global 'save' event", ->
      spyOn(subject, 'onSave')
      spyOn(Mercury, 'on').callsArgOnWith(1, subject)
      subject.bindDefaultEvents()
      expect( Mercury.on ).calledWith('save', sinon.match.func)
      expect( subject.onSave ).called

    it "calls #bindFocusEvents", ->
      subject.bindDefaultEvents()
      expect( subject.bindFocusEvents ).called

    it "calls #bindKeyEvents", ->
      subject.bindDefaultEvents()
      expect( subject.bindKeyEvents ).called

    it "calls #bindMouseEvents", ->
      subject.bindDefaultEvents()
      expect( subject.bindMouseEvents ).called

    it "calls #bindDropEvents", ->
      subject.bindDefaultEvents()
      expect( subject.bindDropEvents ).called


  describe "#bindFocusEvents", ->

    it "calls #delegateEvents with the expected events on @$focusable", ->
      spyOn(subject, 'delegateEvents')
      subject.bindFocusEvents()
      expect( subject.delegateEvents ).calledWith subject.$focusable,
        focus: sinon.match.func
        blur: sinon.match.func

    describe "focus", ->

      beforeEach ->
        @events = {}
        spyOn(subject, 'delegateEvents', => @events = arguments[1])
        spyOn(subject, 'trigger')
        subject.bindFocusEvents()

      it "triggers an event", ->
        @events.focus()
        expect( subject.trigger ).calledWith('focus')

      it "sets @focused to true", ->
        @events.focus()
        expect( subject.focused ).to.be.true

      it "calls #onFocus if it exists", ->
        subject.onFocus = spy()
        @events.focus()
        expect( subject.onFocus ).calledOnce

    describe "blur", ->

      beforeEach ->
        @events = {}
        spyOn(subject, 'delegateEvents', => @events = arguments[1])
        spyOn(subject, 'trigger')
        subject.bindFocusEvents()

      it "triggers a blur event", ->
        @events.blur()
        expect( subject.trigger ).calledWith('blur')

      it "sets @focused to false", ->
        @events.blur()
        expect( subject.focused ).to.be.false

      it "calls #onBlur if it exists", ->
        subject.onBlur = spy()
        @events.blur()
        expect( subject.onBlur ).calledOnce


  describe "#bindKeyEvents", ->

    beforeEach ->
      @events = {}
      spyOn(subject, 'delegateEvents', => @events = arguments[1])

    it "calls #delegateEvents with the expected events on @$focusable", ->
      subject.bindKeyEvents()
      expect( subject.delegateEvents ).calledWith subject.$focusable,
        keyup: sinon.match.func
        keydown: sinon.match.func

    describe "keyup", ->

      beforeEach ->
        subject.bindKeyEvents()
        @e = {}

      it "triggers an update event", ->
        spyOn(subject, 'trigger')
        @events.keyup(@e)
        expect( subject.trigger ).calledWith('update')

    describe "keydown", ->

      beforeEach ->
        spyOn(subject, 'handleAction')
        subject.bindKeyEvents()
        @e = metaKey: true, keyCode: 90, shiftKey: false, preventDefault: spy()

      it "handles undo on metaKey+z", ->
        @events.keydown(@e)
        expect( @e.preventDefault ).called
        expect( subject.handleAction ).calledWith('undo')

      it "handles redo on metaKey+Z (shiftKey)", ->
        @e.shiftKey = true
        @events.keydown(@e)
        expect( @e.preventDefault ).called
        expect( subject.handleAction ).calledWith('redo')

      it "does nothing if not the right key combination", ->
        @e.metaKey = false
        @e.keyCode = 13 # enter
        @events.keydown(@e)
        expect( @e.preventDefault ).not.called
        expect( subject.handleAction ).not.called


  describe "#bindMouseEvents", ->

    beforeEach ->
      @events = {}
      spyOn(subject, 'delegateEvents', => @events = arguments[1])

    it "calls #delegateEvents with the expected events on @$focusable", ->
      subject.bindMouseEvents()
      expect( subject.delegateEvents ).calledWith subject.$focusable,
        mouseup: sinon.match.func

    describe "mouseup", ->

      beforeEach ->
        subject.bindMouseEvents()
        @e = {}

      it "triggers an update event", ->
        spyOn(subject, 'trigger')
        @events.mouseup(@e)
        expect( subject.trigger ).calledWith('update')


  describe "#bindDropEvents", ->

    beforeEach ->
      @events = {}
      spyOn(subject, 'delegateEvents', => @events = arguments[1])
      subject.onDropFile = true
      subject.onDropItem = true
      subject.onDropSnippet = true

    it "calls #delegateEvents with the expected events on @$focusable", ->
      subject.bindDropEvents()
      expect( subject.delegateEvents ).calledWith subject.$focusable,
        dragenter: sinon.match.func
        dragover: sinon.match.func
        drop: 'onItemDropped'

    it "does nothing if there's no #onDropFile, #onDropItem or #onDropSnippet methods", ->
      subject.onDropFile = false
      subject.onDropItem = false
      subject.onDropSnippet = false
      subject.bindDropEvents()
      expect( subject.delegateEvents ).not.called

    describe "dragenter", ->

      beforeEach ->
        subject.bindDropEvents()
        @e = preventDefault: spy()

      it "calls preventDefault for all preemptive events", ->
        @events.dragenter(@e)
        expect( @e.preventDefault ).called

      it "doesn't prevent the event if @previewing", ->
        subject.previewing = true
        @events.dragenter(@e)
        expect( @e.preventDefault ).not.called

    describe "dragover", ->

      beforeEach ->
        subject.bindDropEvents()
        @e = preventDefault: spy()

      it "doesn't prevent the event if @previewing", ->
        subject.previewing = true
        @events.dragover(@e)
        expect( @e.preventDefault ).not.called

      it "doesn't call preventDefault for all preemptive events if @editableDropBehavior", ->
        subject.editableDropBehavior = true
        @events.dragover(@e)
        expect( @e.preventDefault ).not.called

      it "calls preventDefault if Mercury.dragHack is set (regardless of @editableDropBehavior)", ->
        subject.editableDropBehavior = true
        Mercury.dragHack = true
        @events.dragover(@e)
        expect( @e.preventDefault ).called


  describe "#delegateActions", ->

    describe "binding to a callback directly", ->

      it "resolves the action", ->
        callback = spy()
        subject.delegateActions(action: callback)
        subject.actions['action'](1, 2, '3')
        expect( callback ).calledWith(1, 2, '3')

    describe "binding to a method (by string)", ->

      it "locates the method and resolves the action", ->
        spyOn(subject, 'release')
        subject.delegateActions('action': 'release')
        subject.actions['action']()
        expect( subject.release ).called

      it "throws an exception if the method doesn't exist", ->
        expect(-> subject.delegateActions(event: 'foo') ).to.throw(Error, "foo doesn't exist")
