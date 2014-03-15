class Denigma.LoginView extends Denigma.View

  ###
    class that
  ###
  class LoginStates extends Denigma.States

    @transitions
      reset:
        from: ['application','registration','auth']
        to: 'unsigned'
      login:
        from: 'unsigned'
        to: 'auth'
      auth:
        from: 'unsigned'
        to: 'auth'
      confirm:
        from: 'auth'
        to: "signed"
      badLogin:
        from: "auth"
        to: "unsigned"
      signup:
        from: 'unsigned'
        to: "application"
      apply:
        from: "application"
        to: "registration"
      register:
        from: "registration"
        to: "signed"
      badRegistration:
        from: "registration"
        to: "application"
      logout:
        from: 'signed'
        to: 'unsigned'

  constructor: ->
    super
    if(@get("currentUser")?)
      @set("states", new LoginStates('signed',@))
    else
      @set("states", new LoginStates('unsigned',@))
    @set("password","")
    @set("repeat","")
    @set("username","")
    @states.onEnter("auth",@authHandler)

  authHandler: =>
    link = @withHost("users/login?username=#{@get("username")}&password=#{@get("password")}")
    fun = (data,status)=>
      alert(JSON.strinfigy(data))
      alert(status)
    $.ajax
      url: link,
      dataType : "json",
      success: fun

    #@on "unsigned->application",
  emailRegEx: new RegExp(/^((?!\.)[a-z0-9._%+-]+(?!\.)\w)@[a-z0-9-\.]+\.[a-z.]{2,5}(?!\.)\w$/i)
  loginRegEx: new RegExp(/[-_.a-zA-Z0-9]{3,}/)

  isEmail: (str)->str.length>4 && @emailRegEx.test(str)
  isRightLogin: (str)->str.length>4 && @loginRegEx.test(str)

  @accessor "is_email",
    get: -> @get("login").length>4 && @isEmail(@get("login"))

  @accessor "valid_password",
    get: -> @get("password") == @get("repeat") && @get("password").length>4

  @accessor "valid_login",
    get: ->@isRightLogin(@get("login"))

  @accessor "valid",
    get: -> @get("valid_password") && @get("valid_login")

  login: (node, event) ->
    console.log "login from state #{@get("states.state")}"
    unless @states.tryGo "login"
      unless @states.tryGo "reset"
        console.log "other state #{@get("states.state")}"
    console.log "login to state #{@get("states.state")}"

  register: (node,event) ->
    console.log "register from state #{@get("states.state")}"
    unless @states.tryGo "register"
      unless @states.tryGo "apply"
        unless @states.tryGo "signup"
          console.log "other state #{@get("states.state")}"
    console.log "register to state #{@get("states.state")}"


  @accessor "registerLabel", -> if(@get("state")=="application") then "Register" else "Sign up"

  @accessor 'state',
    get: -> @get("states.state")


  @accessor "currentUser",
    get: -> Denigma.get("currentUser")
    set: (value)->Denigma.set("currentUser",value)