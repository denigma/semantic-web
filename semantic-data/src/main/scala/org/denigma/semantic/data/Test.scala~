package org.denigma.semantic.data


/** examples for doing IO
  * from sbt:
  *   project examples
  *   run-main org.w3.banana.examples.IOExample
  */
object IOExample {

  import java.io.File
  import org.w3.banana._
  import scala.util.Properties

  // just because we can :-)
  implicit class FileW(val file: File) extends AnyVal {
    def /(child: String): File = new File(file, child)
  }

  def main(args: Array[String]): Unit = {
    /* gets the important modules
     * you could use directly the ones declared in the package object
     */

    // RDFOps holds all the operations to manipulate RDF graphs, plus some syntax constructs
    implicit val ops = RDFOps[Rdf]
    import ops._

    // gets Turtle reader
    val reader = RDFReader[Rdf, Turtle]

    // gets RDF/XML writer
    val writer = RDFWriter[Rdf, RDFXML]

    /* reads TimBL's card in Turtle */

    val timblCard = "http://www.w3.org/People/Berners-Lee/card.ttl"
    val from = new java.net.URL(timblCard).openStream()
    // reading from a stream can fail so in real life, you would have to deal with the Try[Rdf#Graph]
    val graph: Rdf#Graph = reader.read(from, base = timblCard) getOrElse sys.error("couldn't read TimBL's card")

    /* prints TimBL's card to a file as RDF/XML */

    val tmpFile = new File(Properties.tmpDir) / "card.ttl"
    val to = new java.io.FileOutputStream(tmpFile)
    val ret = writer.write(graph, to, base = timblCard)
    if (ret.isSuccess)
      println(s"successfuly wrote TimBL's card to ${tmpFile.getAbsolutePath}")

    /* prints 10 triples to stdout */

    val graph10Triples = Graph(graph.toIterable.take(10).toSet)
    val graphAsString = writer.asString(graph10Triples, base = timblCard) getOrElse sys.error("coudn't serialize the graph")
    println(graphAsString)
  }

}

/** examples for doing SPARQL queries
  * from sbt:
  *   project examples
  *   run-main org.w3.banana.examples.SPARQLExample
  *
  * In this scenario, we search on dbpedia all the languages
  */
object SPARQLExample {

  import java.net.URL
  import org.w3.banana._
  import org.w3.banana.diesel._

  def main(args: Array[String]): Unit = {
    /* Scala is unable to infer the types correctly when using only type projection
     * a workaround is to parameterize over RDF
     */
    def aux[Rdf <: RDF]()(implicit ops: RDFOps[Rdf], sparqlOps: SparqlOps[Rdf], sparqlHttp: SparqlHttp[Rdf]): Unit = {
      import ops._
      import sparqlOps._

      /* gets a SparqlEngine out of a Sparql endpoint */

      val client = sparqlHttp(new URL("http://localhost:8080/bigdata/sparql"))

      /* creates a Sparql Select query */

      val query = SelectQuery("""
      PREFIX :<http://denigma.org/resource/>
      PREFIX dc:<http://purl.org/dc/elements/1.1/>
      PREFIX rdfs:<http://www.w3.org/2000/01/rdf-schema#>
      PREFIX onto:<http://www.ontotext.com/>
      PREFIX owl:<http://www.w3.org/2002/07/owl#>
      PREFIX xsd:<http://www.w3.org/2001/XMLSchema#>
      PREFIX rdf:<http://www.w3.org/1999/02/22-rdf-syntax-ns#>
      PREFIX human:<http://denigma.org/resource/Human/>

      SELECT ?stuff WHERE { ?stuff rdfs:subClassOf :Loss-of-Function }
                              """)

      /* executes the query */

      val answers: Rdf#Solutions = client.executeSelect(query).getOrFail()

      /* iterate through the solutions */

      val results: Iterable[Rdf#URI] = answers.toIterable map { row =>
      /* row is an Rdf#Solution, we can get an Rdf#Node from the variable name */
      /* both the #Rdf#Node projection and the transformation to Rdf#URI can fail in the Try type, hense the flatMap */
        row("stuff").flatMap(_.as[Rdf#URI]) getOrElse sys.error("die")
      }
      println(results.toList)
    }
    aux()
  }

}
