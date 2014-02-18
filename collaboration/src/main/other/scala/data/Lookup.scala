package org.denigma.data

import scala.collection.mutable.{ HashMap, MultiMap, Set }
import scala.collection.SortedMap
import scala.collection.mutable.MultiMap

/**
 * The basic idea of this class is that it search on a text inside a field
 *  and search field by text
 */
class Lookup
{

  val dictionary:MultiMap[String, String] = new HashMap[String, Set[String]] with MultiMap[String, String]
  val reverse:MultiMap[String, String] = new HashMap[String, Set[String]] with MultiMap[String, String]
  implicit val minor:Ordering[String] = new Ordering[String]{
    def compare(x: String, y: String): Int = if(x==y) 0 else if(x.size>y.size) 1 else /*if(x.size<y.size)*/ -1
  }
  /**
   * Adds key and value to dictionary and its reverse
   * @param key
   * @param value
   * @return
   */
  def add(key:String,value:String): Lookup =
  {
    this.dictionary.addBinding(key,value)
    this.reverse.addBinding(value,key)
    this
  }

  def suggestBy(str:String,where:MultiMap[String, String] ): SortedMap[String, Set[String]] =
  {
    var res: SortedMap[String, scala.collection.mutable.Set[String]] = SortedMap.empty[String,scala.collection.mutable.Set[String]]
    val iter = where.iterator
    while(iter.hasNext)
    {
      val (key,value)= iter.next()
      if(key.contains(str)) {
        res+=(key->value)
      }

    }
    res
  }

  def suggestValuesByKeys(str:String):SortedMap[String,Set[String]] = suggestBy(str,dictionary)

  /**
   * Searches keys by values
   * Used in gene aliases search, where you want to have suggessions
   * @param str
   * @return
   */
  def suggestKeysByValues(str:String):SortedMap[String,Set[String]]  = suggestBy(str,reverse)



  /**
   * Suggest keys by values as list
   * @param name
   * @return
   */
  def suggestKeyList(name:String)(implicit max:Int= 15): List[String] = {
    val sug: SortedMap[String, Set[String]] = this.suggestKeysByValues(name)
    sug.foldLeft(List.empty[String])
    {
      case (acc,(key:String,value:Set[String]))=>
        acc++value.map(v=>key+" ("+v+")")//.toList
    }
  }

  //def suggestKeyList(name:String): List[String] = this.suggestKeysByValues(name).foldLeft(List.empty[String]){ case (acc,(key,value))=>acc++value.toList }


  def suggestValueList(name:String): List[String] = this.suggestValuesByKeys(name).foldLeft(List.empty[String]){ case (acc,(key,value))=>acc++value.toList }



}
