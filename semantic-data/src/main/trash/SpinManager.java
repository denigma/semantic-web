package org.denigma.semantic.classes;

/**
 * Created by antonkulaga on 1/25/14.
 */
trait SpinManager {
  self:SemanticStore=>
    val PREF = WI
    val WI = "http://webintelligence.eu/resources"



    /*
    returns spin representation of the query
     */
    def asSpin(query:String):String = {
      val model: Model = ModelFactory.createDefaultModel
      model.setNsPrefix("rdf", RDF.getURI)
      model.setNsPrefix("wi",WI)
      val arqQuery: Query = ARQFactory.get.createQuery(model, query)
      val arq2SPIN: ARQ2SPIN = new ARQ2SPIN(model)
      val spinQuery: Select = arq2SPIN.createQuery(arqQuery, null).asInstanceOf[Select]
      val st = new StringWriter()
      model.write(st,FileUtils.langTurtle)
      st.toString

    }



      /*
      creates SPIN template from the query
     */
    def asTemplate(str:String,name:String) = {
      val model: Model = ModelFactory.createDefaultModel
      val template: Template = model.createResource(WI+"/"+name, SPIN.Template).as(classOf[Template])
      val arq2SPIN: ARQ2SPIN = new ARQ2SPIN(model)
      val arqQuery: Query = ARQFactory.get.createQuery(model, str)
      val spinQuery: Select = arq2SPIN.createQuery(arqQuery, null).asInstanceOf[Select]
      template.addProperty(SPIN.body, spinQuery)
      val argument: Resource = model.createResource(SPL.Argument)
      argument.addProperty(SPL.predicate, model.getProperty(ARG.NS + "predicate"))
      argument.addProperty(SPL.valueType, RDF.Property)
      argument.addProperty(RDFS.comment, "The predicate to get the value of.")
      template.addProperty(SPIN.constraint, argument)
      val st = new StringWriter()
      template.getModel.write(st,FileUtils.langTurtle)
       st.toString
    }


}
