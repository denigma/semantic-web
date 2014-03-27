#= require mercury/core/plugin
#= require_self
#= require mercury/views/modal
#= require mercury/views/toolbar_palette
#= require mercury/plugins/character_plugin

Mercury.registerPlugin ||= Mercury.Plugin.register if Mercury.Plugin

describe "Mercury.Plugin.Character", ->

  Klass = null
  subject = null

  beforeEach ->
    Klass = Mercury.Plugin.get('character')
    subject = Mercury.Plugin.get('character', true)

  it "needs to be tested"
