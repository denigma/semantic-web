package org.denigma.semantic.classes

import org.openrdf.model.Resource
import com.bigdata.rdf.sail.BigdataSailRepositoryConnection
import org.denigma.semantic.data.{SemanticStore, QueryResult}
import org.denigma.semantic.SG
import com.bigdata.rdf.sail.BigdataSailRepositoryConnection
import org.denigma.semantic.classes.SemanticModel
import org.denigma.semantic.data.{SemanticStore, QueryResult}
import org.denigma.semantic.SG
import org.openrdf.model.impl._
import org.openrdf.model._
import org.openrdf.model

import com.bigdata.rdf.sail.BigdataSailRepositoryConnection
import org.denigma.semantic.classes.{SemanticResource, SemanticModel}
import org.denigma.semantic.data.{SemanticStore, QueryResult}
import org.denigma.semantic.SG
import org.openrdf.model.impl._
import org.openrdf.model._
import org.openrdf.model
import com.bigdata.rdf.sail.BigdataSailRepositoryConnection
import org.openrdf.model.{vocabulary, Literal, Resource, Statement}
import org.openrdf.model.vocabulary._


/*
restricts properties
 */
class SemanticRestriction(url:Resource,con:BigdataSailRepositoryConnection) extends SemanticModel(url){

  val onProperty = null

}
