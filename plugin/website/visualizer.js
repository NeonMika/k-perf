function createDotSourceAndNodeDict(jsonObject) {
  const dotBuilder = [];
  const nodeDict = {};
  let idCount = 0;

  // Begin the DOT graph
  dotBuilder.push(
`digraph KotlinIR {
    rankdir=TB;      // Top-to-bottom layout
    nodesep=1;       // Horizontal spacing
    ranksep=0.75;    // Vertical spacing
`
  );

  // Recursive function that processes each node, appending to dotBuilder and nodeDict
  function processNode(node) {
    // Create a unique ID for this node
    const currentId = "node" + (idCount++);

    // --- DOT building ---
    const typeName = node.NodeName || "";
    const caption = node.Caption || "";
    dotBuilder.push(`    ${currentId} [label="${typeName}\\n${caption}"];`);

    // --- Node dictionary building ---
    // Copy all fields except "Children" into a dictionary entry
    const nodeCopy = {};
    for (const [key, value] of Object.entries(node)) {
      if (key !== "Children") {
        nodeCopy[key] = value;
      }
    }
    nodeDict[currentId] = nodeCopy;

    // --- Recurse through children ---
    if (Array.isArray(node.Children)) {
      for (const child of node.Children) {
        const childId = processNode(child);
        dotBuilder.push(`    ${currentId} -> ${childId};`);
      }
    }

    return currentId;
  }

  // Process the root node
  processNode(jsonObject);

  // Close the DOT graph
  dotBuilder.push("}\n");

  // Return both the DOT source and the node dictionary
  return {
    dotSource: dotBuilder.join("\n"),
    nodeDict
  };
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
