class Denigma.Edge extends Denigma.Item

  @serializeAsForm: false #do not remember if it is static or normal property
  @primaryKey: 'id'
  @hasMany "properties", {encoderKey: 'properties',saveInline:true,autoload:false}

  @encode 'id','name','label'

  @hasMany("vertices", {as:"incoming", encoderKey:"incoming", inverseOf:"vertex"})
  @hasMany("vertices", {as: "outgoing", encoderKey: "outgoing", inverseOf: "vertex"})

  #@hasMany "properties", {saveInline:true,autoload:false,inverseOf "property"}

  #@persist Batman.LocalStorage

  @storageKey: 'edges'
  @persist Batman.RestStorage
