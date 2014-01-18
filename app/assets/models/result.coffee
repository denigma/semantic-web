class Denigma.Result extends Batman.Model

  @serializeAsForm: false #do not remember if it is static or normal property
  @hasMany "properties", {encoderKey: 'properties',saveInline:true,autoload:false}

  #@encode 'name','label'

  @storageKey: 'results'
  @persist Denigma.SparqlStorage
