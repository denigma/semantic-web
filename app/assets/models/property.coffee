class Denigma.Property extends Batman.Model

  serializeAsForm: false
  @serializeAsForm: false #do not remember if it is static or normal property
  @primaryKey: '__id'

  #@hasMany "members", {saveInline:false,autoload:true,inverseOf: 'organization', foreignKey:'organization'}

  #@encode 'id','type', 'value'
  @encode "__id",'name', 'kind', "value"
  #@belongsTo 'parentItem', inverseOf: 'prop'

  @storageKey: 'properties'
  @persist Batman.RestStorage
