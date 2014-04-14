package org.denigma.frontend.views

import org.denigma.views.OrdinaryView
import org.scalajs.dom.{MouseEvent, HTMLElement}
import rx.{Rx, Var}
import scalatags.HtmlTag

/**
 * View for article with some text
 */
class ArticleView(element:HTMLElement,params:Map[String,Any] = Map.empty[String,Any]) extends OrdinaryView("article",element){
  override def tags: Map[String, Rx[HtmlTag]] = this.extractTagRx(this)

  override def strings: Map[String, Rx[String]] = this.extractStringRx(this)

  override def bools: Map[String, Rx[Boolean]] = this.extractBooleanRx(this)

  override def mouseEvents: Map[String, Var[MouseEvent]] = this.extractMouseEvens(this)


  val authors = Var("Ilia Stambler, Daria Khalturina, Edouard Deboneul")
  val title = Var("ILA Manifesto")

  val text = Var("""
    <p>We advocate the advancement of healthy longevity for the entire population through scientific research, public health, advocacy and social activism. We emphasize and promote the struggle against the chief enemy of healthy longevity – the aging process.</p>
    <p>The aging process is the root of most chronic diseases afflicting the world population. This process causes the largest proportion of disability and mortality, and needs to be treated accordingly. Society needs to dedicate efforts toward its treatment and correction, as for any other material disease.</p>
    <p>The problem of aging is grave and threatening. Yet, we often witness an almost complete oblivion to its reality and severity. There is a soothing tendency to ignore the future, to distract the mind from aging and death from aging, and even to present aging and death in a misleading, apologetic and utopian light. At the same time, there is an unfounded belief that aging is a completely unmanageable, inexorable process. This disregard of the problem and this unfounded sense of impotence do not contribute to the improvement of the well-being of the aged and their healthy longevity. There is a need to present the problem in its full severity and importance and to act for its solution or mitigation to the best of our ability.</p>
    <p>We call to raise the public awareness of the problem of aging in its full scope. We call the public to recognize this severe problem and dedicate efforts and resources – including economic, social-political, scientific, technological and media resources – to its maximal possible alleviation for the benefit of the aging population, for their healthy longevity. We promote the idea that mental and spiritual maturation and the increase in healthy longevity are not synonymous with aging and deterioration.</p>
    <p>We advocate the reinforcement and acceleration of basic and applied biomedical research, as well as the development of technological, industrial, environmental, public health and educational measures, specifically directed for healthy longevity. If given sufficient support, such measures can increase the healthy life expectancy of the aged population, the period of their productivity, their contribution to the development of society and economy, as well as their sense of enjoyment, purpose and valuation of life.</p>
    <p>We advocate that the development of scientific measures for healthy life extension be given the maximal possible public and political support that it deserves, not only by the professional community but also by the broad public.</p>
    """
    )

  val published = Var("01/01/2013")
  val lastEdited = Var("01/01/2014")



}
