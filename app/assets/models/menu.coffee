class Denigma.Menu extends Batman.Model

  constructor: (uri)->
    super

  @primaryKey: 'uri'

  @serializeAsForm: false #do not remember if it is static or normal property

  #@hasMany("menus", {as:"children", encoderKey:"children"})

  @encode "uri", "label", "page"

  @storageKey: 'menus'

  @persist Batman.RestStorage
