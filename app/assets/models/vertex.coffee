class Denigma.Vertex extends Denigma.Item

  @serializeAsForm: false #do not remember if it is static or normal property
  @primaryKey: 'id'
  #@hasMany "members", {saveInline:false,autoload:true,inverseOf: 'organization', foreignKey:'organization'}

  @hasMany "properties", {encoderKey: 'properties',saveInline:true,autoload:false}

  @hasMany("edges", {as:"incoming", encoderKey:"incoming", inverseOf:"edge"})
  @hasMany("edges", {as: "outgoing", encoderKey: "outgoing", inverseOf: "edge"})

  @encode 'id','name','description'

  #@hasMany "edges", inverseOf: "edge"
  #@persist Batman.LocalStorage

  @storageKey: 'vertices'
  @persist Batman.RestStorage
