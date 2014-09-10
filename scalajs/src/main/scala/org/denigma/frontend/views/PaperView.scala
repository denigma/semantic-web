package org.denigma.frontend.views

import org.denigma.binding.views.BindableView
import org.scalajs.dom
import org.scalajs.dom._

import scala.collection.immutable._
import scala.scalajs.js

/**
 * View for paper viewer
 */
class PaperView(val elem:HTMLElement,val params:Map[String,Any] = Map.empty[String,Any]) extends BindableView
{


  override def bindView(element:HTMLElement) = {
    dom.alert("hello!")
    js.eval{
      """
        | var getOutputScale, host, loadPdf, renderPage, renderPdf, scale;
        |      host = "http://" + window.location.host;
        |      scale = 1.5;
        |      PDFJS.disableWorker = true;
        |
        |      /*
        |      Returns scale factor for the canvas. It makes sense for the HiDPI displays.
        |      @return {Object} The object with horizontal (sx) and vertical (sy)
        |      scales. The scaled property is set to false if scaling is
        |      not required, true otherwise.
        |       */
        |      getOutputScale = function(ctx) {
        |        var backingStoreRatio, devicePixelRatio, pixelRatio;
        |        devicePixelRatio = window.devicePixelRatio || 1;
        |        backingStoreRatio = ctx.webkitBackingStorePixelRatio || ctx.mozBackingStorePixelRatio || ctx.msBackingStorePixelRatio || ctx.oBackingStorePixelRatio || ctx.backingStorePixelRatio || 1;
        |        pixelRatio = devicePixelRatio / backingStoreRatio;
        |        return {
        |          sx: pixelRatio,
        |          sy: pixelRatio,
        |          scaled: pixelRatio !== 1
        |        };
        |      };
        |      renderPage = function(page) {
        |        var $canvas, $pdfContainer, $textLayerDiv, canvas, canvasOffset, context, cssScale, outputScale, viewport;
        |        viewport = page.getViewport(scale);
        |        $canvas = jQuery("<canvas></canvas>");
        |        canvas = $canvas.get(0);
        |        context = canvas.getContext("2d");
        |        canvas.height = viewport.height;
        |        canvas.width = viewport.width;
        |        $pdfContainer = jQuery("#pdfContainer");
        |        $pdfContainer.css("height", canvas.height + "px").css("width", canvas.width + "px");
        |        $pdfContainer.append($canvas);
        |        canvasOffset = $canvas.offset();
        |        $textLayerDiv = jQuery("<div />").addClass("textLayer").css("height", viewport.height + "px").css("width", viewport.width + "px").offset({
        |          top: canvasOffset.top,
        |          left: canvasOffset.left
        |        });
        |        outputScale = getOutputScale(context);
        |        if (outputScale.scaled) {
        |          cssScale = "scale(" + (1 / outputScale.sx) + ", " + (1 / outputScale.sy) + ")";
        |          CustomStyle.setProp("transform", canvas, cssScale);
        |          CustomStyle.setProp("transformOrigin", canvas, "0% 0%");
        |          if ($textLayerDiv.get(0)) {
        |            CustomStyle.setProp("transform", $textLayerDiv.get(0), cssScale);
        |            CustomStyle.setProp("transformOrigin", $textLayerDiv.get(0), "0% 0%");
        |          }
        |        }
        |        context._scaleX = outputScale.sx;
        |        context._scaleY = outputScale.sy;
        |        if (outputScale.scaled) {
        |          context.scale(outputScale.sx, outputScale.sy);
        |        }
        |        $pdfContainer.append($textLayerDiv);
        |        return page.getTextContent().then(function(textContent) {
        |          var renderContext, textLayer;
        |          textLayer = new TextLayerBuilder({
        |            textLayerDiv: $textLayerDiv.get(0),
        |            pageIndex: 0
        |          });
        |          textLayer.setTextContent(textContent);
        |          renderContext = {
        |            canvasContext: context,
        |            viewport: viewport,
        |            textLayer: textLayer
        |          };
        |          return page.render(renderContext);
        |        });
        |      };
        |      renderPdf = function(pdf) {
        |        return pdf.getPage(1).then(renderPage);
        |      };
        |      loadPdf = function(pdfPath) {
        |        var pdf;
        |        pdf = PDFJS.getDocument(pdfPath);
        |        return pdf.then(renderPdf);
        |      };
        |      loadPdf(host + "/assets/files/guest/Denigma - Deciphering Aging.pdf");
      """.stripMargin
    }
    super.bindView(element)


  }

  override def activateMacro(): Unit = { extractors.foreach(_.extractEverything(this))}
  override protected def attachBinders(): Unit = binders =  BindableView.defaultBinders(this)

}
