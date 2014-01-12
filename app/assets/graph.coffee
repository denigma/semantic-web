###
This example is a copy of "basic.html", but with event bindings after.
Open your browser's console to see the "click", "overNode" and
"outNode" events logged.
###



l = 12
lb = 16

generateGraph = (n,e)->
  g =
    nodes: []
    edges: []
  N = n
  E = e
  for num in [0..N-1]
    g.nodes.push
      id: "n" + num
      label: "Node " + num
      x: Math.random()
      y: Math.random()
      size: l
      color: "#666"

  for num in [0..E-1]
    g.edges.push
      id: "e" + num
      source: "n" + (Math.random() * N | 0)
      target: "n" + (Math.random() * N | 0)
      size: Math.random()
      color: "#ccc"
  g

s = new sigma(
  graph: generateGraph(25,100)
  container: "graph-container"
  defaultEdgeType: "curve"
)

# Bind the events:
s.bind "overNode", (e) ->
  e.data.node.size = lb
  console.log e.type, e.data.node.label

s.bind "outNode", (e) ->
  e.data.node.size = l
  console.log e.type, e.data.node.label
s.startForceAtlas2()
