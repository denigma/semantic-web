#= require spec_helper
#= require mercury/core/model

describe "Mercury.Model", ->

  Klass = ->
  subject = null

  beforeEach ->
    class Klass extends Mercury.Model
      @idCounter: 1
      @records:
        '5': {id: '5'}
        'c4': {id: 'c4'}
    subject = new Klass()

  describe "Modules", ->

    it "includes in the expected modules", ->
      expect( Klass.config ).to.be.a('Function')
      expect( Klass.on ).to.be.a('Function')
      expect( subject.on ).to.be.a('Function')
      expect( subject.config ).to.be.a('Function')
      expect( subject.t ).to.be.a('Function')
      expect( subject.log ).to.be.a('Function')


  describe ".define", ->

    it "assigns @className and @urlPrefix", ->
      Klass.define('TestModel', '_prefix_')
      expect( Klass.className ).to.eq('TestModel')
      expect( Klass.urlPrefix ).to.eq('_prefix_')

    it "sets the @logPrefix", ->
      Klass.define('TestModel')
      expect( Klass.logPrefix ).to.eq('TestModel:')
      expect( Klass::logPrefix ).to.eq('TestModel:')

    it "calls @off", ->
      spyOn(Klass, 'off')
      Klass.define('TestModel')
      expect( Klass.off ).called

    it "returns itself for chaining", ->
      expect( Klass.define('TestModel') ).to.eq(Klass)


  describe ".url", ->

    it "returns the expected url", ->
      Klass.urlPrefix = "/base/path"
      expect( Klass.url(isNew: -> true) ).to.eq('/base/path')

    it "returns the expected url", ->
      Klass.urlPrefix = "/base/path"
      expect( Klass.url(id: 42, isNew: -> false) ).to.eq('/base/path/42')


  describe ".find", ->

    beforeEach ->
      Klass.define('TestModel')
      Klass.records =
        '5': {id: '5'}
        'c4': {id: 'c4'}

    it "looks in the records for the id", ->
      expect( Klass.find(5) ).to.eql(id: '5')

    it "looks for the cid if the record wasn't found", ->
      expect( Klass.find('c4') ).to.eql(id: 'c4')

    it "throws an execption if the record wasn't found", ->
      expect(-> Klass.find('foo') ).to.throw(Error, "TestModel found no records with the ID 'foo'")


  describe ".all", ->

    it "returns the records as an array", ->
      expect( Klass.all() ).to.eql([{id: '5'}, {id: 'c4'}, subject])


  describe ".count", ->

    it "returns the count of the records", ->
      expect( Klass.count() ).to.eq(3)


  describe ".toJSON", ->

    it "returns the records (by calling @all)", ->
      expect( Klass.toJSON() ).to.eql(Klass.all())


  describe ".contains", ->

    it "returns the record if it finds one by it's id", ->
      expect( Klass.contains(5) ).to.eql(id: '5')
      expect( Klass.contains('c4') ).to.eql(id: 'c4')

    it "returns false if it doesn't find the record", ->
      expect( Klass.contains('foo') ).to.be.false


  describe ".uid", ->

    beforeEach ->
      Klass.idCounter = 1

    it "generates a unique id", ->
      expect( Klass.uid() ).to.eq('2')
      expect( Klass.uid() ).to.eq('3')
      expect( Klass.uid() ).to.eq('6')
      expect( Klass.uid() ).to.eq('7')

    it "uses a prefix if provided", ->
      expect( Klass.uid('c') ).to.eq('c2')
      expect( Klass.uid('c') ).to.eq('c3')
      expect( Klass.uid('c') ).to.eq('c5')
      expect( Klass.uid('c') ).to.eq('c6')


  describe "#constructor", ->

    it "calls #set", ->
      spyOn(Klass::, 'set')
      attrs = {foo: 'bar'}
      subject = new Klass(attrs)
      expect( subject.set ).calledWith(attrs)

    it "sets @errors to an empty object", ->
      expect( subject.errors ).to.eql({})

    it "generates a cid", ->
      expect( subject.cid ).to.eq('c1')

    it "adds itself to the global collection", ->
      Klass.records = null
      subject = new Klass()
      expect( Klass.records[subject.cid] ).to.eq(subject)


  describe "#url", ->

    it "calls the constructor url", ->
      spyOn(Klass, 'url', -> '_url_')
      expect( subject.url() ).to.eql('_url_')
      expect( Klass.url ).calledWith(subject)


  describe "#validate", ->

    it "returns nothing and is a placeholder", ->
      expect( subject.validate() ).to.be.undefined


  describe "#save", ->

    beforeEach ->
      spyOn($, 'ajax')

    it "returns false if not valid", ->
      spyOn(subject, 'isValid', -> false)
      expect( subject.save() ).to.be.false

    it "creates an ajax request", ->
      subject.save(url: '/some/path', foo: 'bar', error: null, success: null)
      expect( $.ajax ).calledWith
        foo: 'bar'
        method: 'POST'
        url: '/some/path'
        dataType: 'json'
        contentType: 'application/json; charset=utf-8'
        cache: false
        data: '{}'
        success: null
        error: null

    it "calls the #url method", ->
      spyOn(subject, 'url')
      subject.save()
      expect( subject.url ).called

    it "uses PUT when the record isn't new", ->
      spyOn(subject, 'isNew', -> false)
      subject.save()
      expect( $.ajax.args[0][0]['method'] ).to.eq('PUT')

    it "doesn't convert data to JSON if we don't have a dataType or it's already a string", ->
      subject.save(data: {foo: 'bar'})
      expect( $.ajax.args[0][0]['data'] ).to.eql('{"foo":"bar"}')
      subject.save(data: {foo: 'bar'}, dataType: null)
      expect( $.ajax.args[1][0]['data'] ).to.eql({foo: 'bar'})
      subject.save(data: '_string_')
      expect( $.ajax.args[2][0]['data'] ).to.eql('_string_')

    describe "on success", ->

      beforeEach ->
        $.ajax.restore()
        @server = sinon.fakeServer.create()
        @server.respondWith('POST', '/foo', [200, {'Content-Type': 'application/json'}, '{"url": "/some/url/file.ext", "id": 42}'])
        @server.respondWith('POST', '/bar', [200, {'Content-Type': 'application/json'}, '{"url": "/some/url/file.ext"}'])
        @server.respondWith('POST', '/raw', [200, {'Content-Type': 'application/html'}, '_html_'])

      it "calls set with the JSON", ->
        subject.save(url: '/foo')
        spyOn(subject, 'set')
        @server.respond()
        expect( subject.set ).calledWith(id: 42, url: '/some/url/file.ext')

      it "triggers a save event", ->
        subject.save(url: '/foo')
        spyOn(subject, 'trigger')
        @server.respond()
        expect( subject.trigger ).calledWith('save')

      it "calls @saveSuccess if it's defined", ->
        subject.save(url: '/foo')
        subject.saveSuccess = spy()
        @server.respond()
        expect( subject.saveSuccess ).called

      it "returns false if the json isn't an object", ->
        subject.save(url: '/raw', dataType: 'text', contentType: null)
        spyOn(subject, 'trigger')
        @server.respond()
        expect( subject.trigger ).not.called


    describe "on error", ->

      beforeEach ->
        $.ajax.restore()
        @server = sinon.fakeServer.create()
        @server.respondWith('POST', '/foo', [500, {'Content-Type': 'application/json'}, ''])
        spyOn(subject, 'notify')
        subject.save(url: '/foo', method: 'POST')

      it "triggers an error event", ->
        spyOn(subject, 'trigger')
        @server.respond()
        expect( subject.trigger ).calledWith('error')

      it "calls notify with the status", ->
        spyOn(subject, 't')
        @server.respond()
        expect( subject.t ).calledWith('Unable to process response: %s', 500)
        expect( subject.notify ).called

      it "calls #saveError if it's defined", ->
        subject.saveError = spy()
        @server.respond()
        expect( subject.saveError ).called


  describe "#toJSON", ->

    it "returns the attributes", ->
      obj = {foo: 'bar'}
      subject.attributes = obj
      expect( subject.toJSON() ).to.eql(foo: 'bar')
      expect( subject.toJSON() ).to.not.eq(obj)


  describe "#isNew", ->

    it "returns true when the instance doesn't have an id", ->
      expect( subject.isNew() ).to.be.true

    it "returns false when the instance has an id", ->
      spyOn(subject, 'exists', -> true)
      expect( subject.isNew() ).to.be.false


  describe "#isValid", ->

    it "returns true when there are no errors", ->
      expect( subject.isValid() ).to.be.true

    it "returns false when errors have been added to (typically from #validate)", ->
      spyOn(subject, 'validate', -> subject.errors['foo'] = [])
      expect( subject.isValid() ).to.be.false


  describe "#addError", ->

    beforeEach ->
      subject.errors = {}

    it "adds to @errors", ->
      subject.addError('foo', 'was bad')
      expect( subject.errors ).to.eql(foo: ['was bad'])


  describe "#errorMessages", ->

    it "returns false if it's valid", ->
      spyOn(subject, 'isValid', -> true)
      expect( subject.errorMessages() ).to.be.false

    it "generates a readable error message", ->
      spyOn(subject, 'isValid', -> false)
      subject.errors = {size: ['is too large', "can't be blank"], type: ["isn't good"]}
      expect( subject.errorMessages() ).to.eq("is too large, can't be blank\nisn't good")


  describe "#get", ->

    it "returns the value for the given attribute", ->
      subject.attributes = {foo: 'bar'}
      expect( subject.get('foo') ).to.eq('bar')


  describe "#set", ->

    beforeEach ->
      subject.attributes = {foo: 'bar'}

    it "sets the attributes", ->
      subject.set('foo', 'baz')
      expect( subject.attributes.foo ).to.eq('baz')

    it "allows passing an object instead of key/value", ->
      subject.set(foo: 'baz')
      expect( subject.attributes.foo ).to.eq('baz')

    it "pushes the current attributes onto the stack", ->
      subject.set(foo: 'baz')
      expect( subject.stack[0] ).to.eql({})
      expect( subject.stack[1] ).to.eql(foo: 'bar')

    it "calls #setId if an id was passed", ->
      spyOn(subject, 'setId')
      subject.set('id', 42)
      expect( subject.setId ).calledWith(42)

    it "doesn't call #setId if no id", ->
      spyOn(subject, 'setId')
      subject.set('foo', 'bar')
      expect( subject.setId ).not.called


  describe "#setId", ->

    it "assigns @id", ->
      subject.setId(42)
      expect( subject.id ).to.eql(42)

    it "keeps track of it in the collection", ->
      subject.setId(42)
      expect( Klass.records[42] ).to.eql(subject)


  describe "#exists", ->

    it "returns falsy value if there's no id", ->
      Klass.records[subject.cid] = subject
      expect( subject.exists() ).to.be.undefined

    it "returns true if there's an id (and the collection knows about it)", ->
      Klass.records[subject.cid] = subject
      subject.id = subject.cid
      expect( subject.exists() ).to.be.true
