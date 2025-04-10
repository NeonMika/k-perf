function assignNodeIds(root) {
  let idCount = 0;

  function traverse(node, depth) {
    node.nodeID = "node" + (idCount++);
    if (Array.isArray(node.Children)) {
      for (const child of node.Children) {
        traverse(child, depth+1);
      }
    }
  }

  traverse(root, 0);
  collapseByDepth(root, 3)
}

function createDotSource(root) {
  const dotBuilder = [];

  dotBuilder.push(
      `digraph KotlinIR {
    rankdir=TB;      // Top-to-bottom layout
    nodesep=1;       // Horizontal spacing
    ranksep=0.75;    // Vertical spacing
`
  );

  function traverse(node) {
    const typeName = node.NodeName || "";
    const caption = node.Caption || "";

    const clusterColor = getClusterColor(node.NodeType)

    if(clusterColor){
      dotBuilder.push(`subgraph cluster_${node.nodeID} {
        style="filled,rounded";
        color="${clusterColor}";
        fillcolor="${clusterColor}"`);
    }

    let label=typeName+"\n"+node.NodeType;
    if(caption){
      label+="\\n"+caption;
    }
    if(hasInvisibleChild(node)){
      label+="\\n➕";
    }else{
      label+="\\n➖";
    }

    const shape=getNodeShape(node.NodeType)
    if(node.visible){
      dotBuilder.push(`    ${node.nodeID} [id="${node.nodeID}", label="${label}", shape="${shape}"];`);


      if (Array.isArray(node.Children)) {
        for (const child of node.Children) {
          if(child.visible){
            dotBuilder.push(`    ${node.nodeID} -> ${child.nodeID};`);
            traverse(child);
          }
        }
      }
    }
    if(clusterColor){
      dotBuilder.push(`}`);
    }
  }

  traverse(root);
  dotBuilder.push("}\n");
  return dotBuilder.join("\n");
}

function createNodeDict(root) {
  const nodeDict = {};

  function traverse(node) {
    const nodeCopy = {};
    for (const [key, value] of Object.entries(node)) {
      if (key !== "Children") {
        nodeCopy[key] = value;
      }
    }

    nodeCopy.original = node;
    nodeDict[node.nodeID] = nodeCopy;


    if (Array.isArray(node.Children)) {
      for (const child of node.Children) {
        traverse(child);
      }
    }
  }

  traverse(root);
  return nodeDict;
}

function filterTree(root, filters) {
  function visit(node) {
    let currentNodeVisible = false;

    if (isFiltered(node, filters)) {
      currentNodeVisible = true;
    }

    if (Array.isArray(node.Children)) {
      for (const child of node.Children) {
        const childVisible = visit(child);
        if (childVisible) {
          currentNodeVisible = true;
        }
      }
    }

    node.visible = currentNodeVisible;
    return currentNodeVisible;
  }

  if(filters.length>0){
    visit(root);
  }else{
    collapseByDepth(root, 3)
  }
}

function isFiltered(node, filters) {
  const filtersByType = {};
  for (const filter of filters) {
    const { type, value } = filter;
    if (!filtersByType[type]) {
      filtersByType[type] = [];
    }
    filtersByType[type].push(value);
  }

  for (const type in filtersByType) {
    const values = filtersByType[type];
    let typeMatched = false;

    for (const value of values) {
      if (typeof node[type] === "string" && node[type].includes(value)) {
        typeMatched = true;
        break;
      }
    }

    if (!typeMatched) {
      return false;
    }
  }

  return true;
}

function collapseByDepth(root, maxDepth) {
  let idCount = 0;

  function traverse(node, depth) {
    if(depth<maxDepth){
      node.visible = true;
    }else{
      node.visible = false;
    }
    if (Array.isArray(node.Children)) {
      for (const child of node.Children) {
        traverse(child, depth+1);
      }
    }
  }

  traverse(root, 0);
}

function toggleChildren(node, visibility){
  if (Array.isArray(node.Children)) {
    for (const child of node.Children) {
      child.visible = visibility;
    }
  }
}

function expandAll(node){
  if (Array.isArray(node.Children)) {
    for (const child of node.Children) {
      child.visible = true;
      expandAll(child);
    }
  }
}

function hasInvisibleChild(node){
  if (Array.isArray(node.Children)) {
    for (const child of node.Children) {
      if(!child.visible){
        return true;
      }
    }
  }
  return false;
}

function getJSON() {
  const request = new XMLHttpRequest();
  request.open('GET', 'output.json', false);
  request.send(null);

  if (request.status === 200) {
    return request.responseText
  } else {
    throw new Error('Failed to load JSON');
  }
}
