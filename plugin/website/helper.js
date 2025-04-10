function markSelected(svg, selectedNodeId){
    nodeGroup = getNodeByNodeId(selectedNodeId);
    nodeGroup.classList.add("node-selected");
    svg.querySelectorAll(".node-selected").forEach(el => {
        const shape = el.querySelector("ellipse, polygon, path, rect");
        if (shape) {
            if (!shape.hasAttribute("data-original-stroke")) {
                shape.setAttribute("data-original-stroke", shape.getAttribute("stroke") || "#000");
            }
            shape.setAttribute("stroke", "#FF5722");
        }
    });
}

function unmarkSelected(svg){
    svg.querySelectorAll(".node-selected").forEach(el => {
        el.classList.remove("node-selected");
        const shape = el.querySelector("ellipse, polygon, path, rect");
        if (shape) {
            shape.setAttribute("stroke", shape.getAttribute("data-original-stroke") || "#000");
        }
    });
}

function setInfoDiv(nodeGroup){
    const nodeId = nodeGroup.querySelector("title").textContent.trim();
    const nodeData = nodeDict[nodeId];
    const infoDiv = document.getElementById("selected-node-info");

    infoDiv.innerHTML = "";

    const header = document.createElement("h3");
    header.textContent = `Selected Node: ${nodeData.NodeName}`;
    infoDiv.appendChild(header);

    for (const [key, value] of Object.entries(nodeData)) {
        if (["nodeID", "NodeName", "Caption", "Dump", "FunctionIdentity", "original", "visible"].includes(key)) continue;
        const p = document.createElement("p");
        const strong = document.createElement("strong");
        strong.textContent = insertSpaceBeforeCaps(key);
        p.append(strong);
        p.appendChild(document.createTextNode(`: ${value}`));
        infoDiv.appendChild(p);
    }

    const dumpParagraph = document.createElement("p");
    const dumpStrong = document.createElement("strong");
    dumpStrong.textContent = "Dump";
    dumpParagraph.append(dumpStrong);
    dumpParagraph.appendChild(document.createTextNode(`: ${nodeData.Dump}`));
    infoDiv.appendChild(dumpParagraph);

    function insertSpaceBeforeCaps(str) {
        return str.replace(/(?!^)([A-Z])/g, ' $1');
    }
}

function removeAllDottedLines(){
    const svg = document.getElementById("graph-container").querySelector("svg");
    svg.querySelectorAll(".dotted-line").forEach(line => {
        line.remove()
    })
}

function drawAllDottedLines(nodeGroup){
    const svg = document.getElementById("graph-container").querySelector("svg");
    const nodeId = nodeGroup.querySelector("title").textContent.trim();
    const nodeData = nodeDict[nodeId];
    if (nodeData.NodeName === "Call") {
        for (const [key, value] of Object.entries(nodeDict)) {
            if (value.NodeName === "Function") {
                if (value.FunctionIdentity === nodeData.FunctionIdentity) {
                    var functionNode = getNodeByNodeId(key)
                    drawDottedLine(nodeGroup, functionNode, svg)
                }
            }
        }
    }

    if (nodeData.NodeName === "Function") {
        for (const [key, value] of Object.entries(nodeDict)) {
            if (value.NodeName === "Call") {
                if (value.FunctionIdentity === nodeData.FunctionIdentity) {
                    var functionNode = getNodeByNodeId(key)
                    drawDottedLine(nodeGroup, functionNode, svg)
                }
            }
        }
    }
}

function getNodeByNodeId(nodeID){
    const svg = document.getElementById("graph-container").querySelector("svg");
    return svg.querySelector("#" + nodeID)
}

function drawDottedLine(node1, node2, svg) {
    const bbox1 = node1.getBBox();
    const bbox2 = node2.getBBox();

    const center1 = { x: bbox1.x + bbox1.width / 2, y: bbox1.y + bbox1.height / 2 };
    const center2 = { x: bbox2.x + bbox2.width / 2, y: bbox2.y + bbox2.height / 2 };

    function getEdgeMidpoints(bbox) {
        return [
            { x: bbox.x + bbox.width / 2, y: bbox.y },                    // Top
            { x: bbox.x + bbox.width / 2, y: bbox.y + bbox.height },        // Bottom
            { x: bbox.x,               y: bbox.y + bbox.height / 2 },       // Left
            { x: bbox.x + bbox.width,  y: bbox.y + bbox.height / 2 }        // Right
        ];
    }

    function getNearestMidpoint(midpoints, target) {
        let nearest = midpoints[0];
        let minDist = Math.hypot(midpoints[0].x - target.x, midpoints[0].y - target.y);
        midpoints.forEach(point => {
            const dist = Math.hypot(point.x - target.x, point.y - target.y);
            if (dist < minDist) {
                minDist = dist;
                nearest = point;
            }
        });
        return nearest;
    }

    const midpoints1 = getEdgeMidpoints(bbox1);
    const midpoints2 = getEdgeMidpoints(bbox2);

    const point1 = getNearestMidpoint(midpoints1, center2);
    const point2 = getNearestMidpoint(midpoints2, center1);


    const line = document.createElementNS("http://www.w3.org/2000/svg", "line");
    line.setAttribute("x1", point1.x);
    line.setAttribute("y1", point1.y);
    line.setAttribute("x2", point2.x);
    line.setAttribute("y2", point2.y);
    line.setAttribute("stroke", "#000");
    line.setAttribute("stroke-dasharray", "5,5");
    line.setAttribute("stroke-width", "2");
    line.classList.add("dotted-line");

    const graph = svg.querySelector(".graph");
    graph.appendChild(line);
}

function getClusterColor(type){
    if(document.getElementById('disable-clustering').checked){
        return "";
    }
    switch (type) {
        case "IrFileImpl": return "#e3f2fd";
        case "IrFunctionImpl": return "#e8f5e9";
        case "IrBlockBodyImpl": return "#fff8e1";
        case "IrClassImpl":  return "#ffebee";
    }
    return "";
}

function getNodeShape(type){
    if(document.getElementById('disable-clustering').checked){
        return "";
    }
    switch (type) {
        case "IrFileImpl": return "tra";
        case "IrFunctionImpl": return "diamond";
        case "IrBlockBodyImpl": return "hexagon";
        case "IrClassImpl":  return "trapezium";
    }
    return "ellipse";
}