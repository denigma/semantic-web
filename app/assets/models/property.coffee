class Denigma.Property extends Batman.Model

  serializeAsForm: false
  @serializeAsForm: false #do not remember if it is static or normal property
  @primaryKey: 'name'
  @encode 'name', "value"
  #@belongsTo 'parentItem', inverseOf: 'prop'

  @storageKey: 'properties'
  @persist Batman.RestStorage
