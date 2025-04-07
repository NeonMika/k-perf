function assignNodeIds(root) {
  let idCount = 0;

  function traverse(node) {
    node.nodeID = "node" + (idCount++);
    node.visible = true;
    if (Array.isArray(node.Children)) {
      for (const child of node.Children) {
        traverse(child);
      }
    }
  }

  traverse(root);
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

    if(node.visible){
      dotBuilder.push(`    ${node.nodeID} [label="${typeName}\\n${caption}"];`);

      if (Array.isArray(node.Children)) {
        for (const child of node.Children) {
          if(child.visible){
            dotBuilder.push(`    ${node.nodeID} -> ${child.nodeID};`);
            traverse(child);
          }
        }
      }
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

function filterTree(root, key, filterValue) {
  function visit(node) {
    let currentNodeVisible = false;

    if (typeof node[key] === "string" && node[key].includes(filterValue)) {
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

  visit(root);
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
