jQuery =>
  host = "http://" + window.location.host

  scale = 1.5
  PDFJS.disableWorker = true
  #PDFJS.workerSrc = host+"assets/lib/pdf/pdf.worker.js"


  ###
  Returns scale factor for the canvas. It makes sense for the HiDPI displays.
  @return {Object} The object with horizontal (sx) and vertical (sy)
  scales. The scaled property is set to false if scaling is
  not required, true otherwise.
  ###
  getOutputScale = (ctx) ->
    devicePixelRatio = window.devicePixelRatio or 1
    backingStoreRatio = ctx.webkitBackingStorePixelRatio or ctx.mozBackingStorePixelRatio or ctx.msBackingStorePixelRatio or ctx.oBackingStorePixelRatio or ctx.backingStorePixelRatio or 1
    pixelRatio = devicePixelRatio / backingStoreRatio
    sx: pixelRatio
    sy: pixelRatio
    scaled: pixelRatio isnt 1

  renderPage = (page) ->
    viewport = page.getViewport(scale)
    $canvas = jQuery("<canvas></canvas>")

    # Set the canvas height and width to the height and width of the viewport
    canvas = $canvas.get(0)
    context = canvas.getContext("2d")
    canvas.height = viewport.height
    canvas.width = viewport.width

    # Append the canvas to the pdf container div
    $pdfContainer = jQuery("#pdfContainer")
    $pdfContainer.css("height", canvas.height + "px").css "width", canvas.width + "px"
    $pdfContainer.append $canvas
    canvasOffset = $canvas.offset()
    $textLayerDiv = jQuery("<div />").addClass("textLayer").css("height", viewport.height + "px").css("width", viewport.width + "px").offset(
      top: canvasOffset.top
      left: canvasOffset.left
    )

    # The following few lines of code set up scaling on the context if we are on a HiDPI display
    outputScale = getOutputScale(context)
    if outputScale.scaled
      cssScale = "scale(" + (1 / outputScale.sx) + ", " + (1 / outputScale.sy) + ")"
      CustomStyle.setProp "transform", canvas, cssScale
      CustomStyle.setProp "transformOrigin", canvas, "0% 0%"
      if $textLayerDiv.get(0)
        CustomStyle.setProp "transform", $textLayerDiv.get(0), cssScale
        CustomStyle.setProp "transformOrigin", $textLayerDiv.get(0), "0% 0%"
    context._scaleX = outputScale.sx
    context._scaleY = outputScale.sy
    context.scale outputScale.sx, outputScale.sy  if outputScale.scaled
    $pdfContainer.append $textLayerDiv
    page.getTextContent().then (textContent) ->
      textLayer = new TextLayerBuilder(
        textLayerDiv: $textLayerDiv.get(0)
        pageIndex: 0
      )
      textLayer.setTextContent textContent
      renderContext =
        canvasContext: context
        viewport: viewport
        textLayer: textLayer

      page.render renderContext

  renderPdf = (pdf) ->
    pdf.getPage(1).then renderPage

  loadPdf = (pdfPath) ->
    pdf = PDFJS.getDocument(pdfPath)
    pdf.then renderPdf

  loadPdf(host+ "/files/test.pdf")
