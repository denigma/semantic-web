$ ->
  Array::last = -> @[@length - 1]
  $("#fileupload").fileupload
    dataType: "json"
    acceptFileTypes: /(\.|\/)(owl|rdf|tlt)$/i
    add: (e, data) ->
      $("#progress").remove()
      $(".uploaderror").remove()
      data.submit()
      $("<p id='progress'/>").text("Uploading...").appendTo("#files")
      #data.context.text data.files.last.name
      #      data.context = $("<button/>").text("Upload").appendTo("#files").click(->
      #        data.context = $("<p/>").text("Uploading...").replaceAll($(this))
      #        data.submit()
      #      )
    done: (e, data) ->

      fs = data.result.files
      for f in fs
        if(f.error) then $("<p class='uploaderror'/>").text("Upload error: " +f.error).insertAfter("#fileupload")
      $("#progress").text "Upload finished, next file?"
