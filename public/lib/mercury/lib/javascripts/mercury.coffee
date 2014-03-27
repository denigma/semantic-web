# Configuration.
#------------------------------------------------------------------------------
#= require mercury/config
#
# Mercury dependencies and core libary.
#------------------------------------------------------------------------------
#= require mercury/dependencies
#= require mercury/mercury
#
# All region types.
#------------------------------------------------------------------------------
#= require mercury/regions
#
# All locales.
#------------------------------------------------------------------------------
#= require mercury/locales
#
# Additional functionality.
#------------------------------------------------------------------------------
# You can require any files that add additional functionality to Mercury here.
#
# Initialize Mercury.
jQuery ->
  # First do anything necessary for Ajax requests to be successful -- in our case we need the csrf token for Rails.
  #
  $.ajaxSetup headers: {'X-CSRF-Token': $('meta[name="csrf-token"]').attr('content')}

  # To initialize Mercury call Mercury.init. This will use the interface configuration and instantiate the configured
  # interface, which in turn instantiates the interface elements and regions. In our demo we allow using an iframe
  # (which should be provided by you and in the DOM), and if the iframe isn't available the standard non-iframe
  # interface will be used.
  #
  # For more information about the Mercury API, you might want to check the developer_interface in examples. It contains
  # examples for creating / releasing / deleting dynamic regions, saving, toggling the interface, keeping Mercury
  # focused, how you can use the Mercury Core library as a framework to build a custom interface, and more. There are
  # other useful examples in there as well -- for all development levels.
  #
  Mercury.init()

  # If you wanted to do some custom behaviors (this is limited, and configuration is recommended), you can provide many
  # of the options to the interface. Here we're making it a fixed floating interface that has a set width and location.
  #
  #Mercury.init(frame: '#mercury_frame', floating: true, placed: true, attributes: {style: 'position:fixed;top:10px;left:10px;width:520px'})

Mercury.Region.Markdown.addToolbar 'history', history: ['History', plugin: 'history']
Mercury.Region.Markdown.addToolbar 'color', color: ['Color', plugin: 'color']
Mercury.Region.Markdown.addToolbar 'block', block: ['Block', plugin: 'blocks']

# todo:
#   html region doesn't init?
#   !!! when I add a snippet to the second snippet region, the first snippet region loses it's snippets.
#   remove snippet contents per snippet or let them be serialized as direct content / make sure that works.
#   do snippet regions work better if they serialize their entire element structure in the history (or does this cause massive memory leaks/lingering events that might be bad?)
#   better strategy for snippet management/removal -- focusable with toolbar buttons would be nice, combined with a lesser toolbar in line? (drag handle on hover, etc?)
#   ask/invite translators to revisit their translations.
# todo regions:
#   html: when using the iframe, rangy isn't behaving the same (doesn't work because it doesn't understand the context).
#   html: selections are a little wonky on undo/redo still, and sometimes in firefox they're not applying because the checksum doesn't validate.
#   html: finish it.
#   html: provide way to override tags that are used for different actions -- like markdown wrappers.
#   list: add list region -- sort of like workflowy.
#   table: add table region -- sort of like a spreadsheet, but with no math -- maybe math?
#   canvas: add canvas region -- save/load as base64 image.
#   markdown: polish it a bit more -- mostly selections when wrapping is off.
#   caman: toolbar doesn't switch back to caman region (the canvas doesn't properly trigger the focus event? -- even after some work it's like caman changes the canvas element out from under us -- the easy fix is to just wrap it in a div instead of assigning it directly to an img tag, but this is lame.)
# todo ideas:
#   port jQuery.extend into module so we can use that within instances (which is where it's mostly used).
#   add better touch support to various aspects (draggable module specifically).
#   noteworthy: markdown doesn't wrap the same as other elements.
