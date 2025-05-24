function markSelected(selectedNodeId) {
    const svg = document.getElementById("graph-container").querySelector("svg");
    nodeGroup = getNodeByNodeId(selectedNodeId);
    nodeGroup.classList.add("node-selected");
    svg.querySelectorAll(".node-selected").forEach(el => {
        const shape = el.querySelector("ellipse, polygon, path, rect");
        if (shape) {
            shape.setAttribute("stroke", "#FFE066");
            shape.setAttribute("stroke-width", 5);
        }
    });
}

function unmarkSelected() {
    const svg = document.getElementById("graph-container").querySelector("svg");
    svg.querySelectorAll(".node-selected").forEach(el => {
        el.classList.remove("node-selected");
        const shape = el.querySelector("ellipse, polygon, path, rect");
        if (shape) {
            shape.setAttribute("stroke", "#000");
            shape.setAttribute("stroke-width", 1);
        }
    });
}

function setInfoDiv(nodeId) {
    const infoDiv = document.getElementById("selected-node-info");

    if (!nodeId) {
        infoDiv.innerHTML = "<h3>Click on any node to see details here</h3>";
        const expandBtn = document.getElementById('expandAllChildren');
        expandBtn.disabled = true;
        inspector.fileNode = null;
        return;
    }

    const nodeData = nodeDict[nodeId];

    infoDiv.innerHTML = "";

    const header = document.createElement("h3");
    header.textContent = `Selected Node: ${nodeData.NodeName}`;
    infoDiv.appendChild(header);

    for (const [key, value] of Object.entries(nodeData.displayedData)) {
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

    const sourcecodeButton = document.createElement("button");
    sourcecodeButton.textContent = "Show Sourcecode";
    sourcecodeButton.onclick = () => {
        const popup = document.getElementById('fullscreen-popup');
        popup.classList.add('active');
        const codeHeading = popup.querySelector("#codeHeading");
        codeHeading.textContent = "Original Source Code of " + nodeData.NodeType + " " + nodeData.Caption;
        const codeParagraph = popup.querySelector("#codeParagraph");
        codeParagraph.textContent = getSourceCodeOfNode(nodeData) ?? 'Source Code not found';
    };
    infoDiv.appendChild(sourcecodeButton);

    if (nodeData.NodeName === "Function" || nodeData.NodeName === "Call") {
        const functionCallButton = document.createElement("button");
        if (nodeData.NodeName === "Function") {
            functionCallButton.textContent = "Expand all calls";
        } else if (nodeData.NodeName === "Call") {
            functionCallButton.textContent = "Expand function";
        }
        functionCallButton.onclick = () => {
            const nodes = getAllNodesWithFunctionIdentity(nodeData.FunctionIdentity);
            for (let node of nodes) {
                if(nodeData.NodeName !== node.NodeName){
                    expandAllParents(node);
                }
            }
            autoZoomToNode = false;
            updateGraph();
        };
        infoDiv.appendChild(functionCallButton);
    }


    function insertSpaceBeforeCaps(str) {
        return str.replace(/(?!^)([A-Z])/g, ' $1');
    }
}

function setCodeInspector(nodeId) {
    const inspector = document.getElementById('inspector');

    if (!nodeId) {
        inspector.fileNode = null;
        return;
    }

    const nodeData = nodeDict[nodeId];

    const fileNode = getFileNodeOfNode(nodeData);
    if (fileNode) {
        inspector.fileNode = fileNode;
        if (!nodeData.intermediate) {
            inspector.highlightUnit({start: nodeData.StartOffset, end: nodeData.EndOffset});
        } else {
            inspector.highlightUnit({start: -1, end: -1})
        }
    } else {
        inspector.fileNode = null;
    }
}

function removeAllDottedLines() {
    const svg = document.getElementById("graph-container").querySelector("svg");
    svg.querySelectorAll(".dotted-line").forEach(line => {
        line.remove()
    })
}

function getAllNodesWithFunctionIdentity(functionIdentity) {
    const nodes = [];
    for (const [key, value] of Object.entries(nodeDict)) {
        if (value.NodeName === "Function" || value.NodeName === "Call") {
            if (value.FunctionIdentity === functionIdentity) {
                nodes.push(value);
            }
        }
    }
    return nodes;
}

function drawAllDottedLines(nodeGroup) {
    const svg = document.getElementById("graph-container").querySelector("svg");
    const nodeId = nodeGroup.querySelector("title").textContent.trim();
    const nodeData = nodeDict[nodeId];

    if (nodeData.NodeName === "Call") {
        const nodes = getAllNodesWithFunctionIdentity(nodeData.FunctionIdentity);
        for (node of nodes) {
            if (node.NodeName === "Function") {
                const functionNode = getNodeByNodeId(node.nodeID)
                if (functionNode) {
                    drawDottedLine(nodeGroup, functionNode, svg)
                }
            }
        }
    }

    if (nodeData.NodeName === "Function") {
        const nodes = getAllNodesWithFunctionIdentity(nodeData.FunctionIdentity);
        for (node of nodes) {
            if (node.NodeName === "Call") {
                const functionNode = getNodeByNodeId(node.nodeID)
                if (functionNode) {
                    drawDottedLine(nodeGroup, functionNode, svg)
                }
            }
        }
    }
}

function getNodeByNodeId(nodeID) {
    const svg = document.getElementById("graph-container").querySelector("svg");
    return svg.querySelector("#" + nodeID)
}

function drawDottedLine(node1, node2, svg) {
    const bbox1 = node1.getBBox();
    const bbox2 = node2.getBBox();

    const center1 = {x: bbox1.x + bbox1.width / 2, y: bbox1.y + bbox1.height / 2};
    const center2 = {x: bbox2.x + bbox2.width / 2, y: bbox2.y + bbox2.height / 2};

    function getEdgeMidpoints(bbox) {
        return [
            {x: bbox.x + bbox.width / 2, y: bbox.y},                    // Top
            {x: bbox.x + bbox.width / 2, y: bbox.y + bbox.height},        // Bottom
            {x: bbox.x, y: bbox.y + bbox.height / 2},       // Left
            {x: bbox.x + bbox.width, y: bbox.y + bbox.height / 2}        // Right
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

function getClusterColor(type) {
    switch (type) {
        case "IrFileImpl":
            return "#e3f2fd";
        case "IrFunctionImpl":
            return "#e8f5e9";
        case "IrBlockBodyImpl":
            return "#fff8e1";
        case "IrBlockImpl":
            return "#fff8e1";
        case "IrClassImpl":
            return "#ffebee";
    }
    return "";
}

function getNodeShape(type) {
    switch (type) {
        case "IrFileImpl":
            return "tra";
        case "IrFunctionImpl":
            return "diamond";
        case "IrBlockBodyImpl":
            return "hexagon";
        case "IrBlockImpl":
            return "hexagon";
        case "IrClassImpl":
            return "trapezium";
    }
    return "ellipse";
}

function setupWidthDragging() {
    const container = document.querySelector('.container');
    const cols = Array.from(container.querySelectorAll('.column'));
    const seps = Array.from(container.querySelectorAll('.separator'));

    let ratios = [4, 7, 2];

    function applyRatios() {
        cols.forEach((col, i) => {
            col.style.flex = `${ratios[i]} ${ratios[i]} 0px`;
        });
    }

    applyRatios();

    let drag = {
        idx: null,
        startX: 0,
        startW1: 0,
        startW2: 0
    };

    function onMouseMove(e) {
        if (drag.idx === null) return;
        const dx = e.clientX - drag.startX;

        let newW1 = drag.startW1 + dx;
        let newW2 = drag.startW2 - dx;
        const min = 50;
        if (newW1 < min) {
            newW1 = min;
            newW2 = drag.startW1 + drag.startW2 - min;
        }
        if (newW2 < min) {
            newW2 = min;
            newW1 = drag.startW1 + drag.startW2 - min;
        }

        const containerW = container.clientWidth;
        const gutterW = 100;
        const availableW = containerW - gutterW;
        const sumRatios = ratios.reduce((a, b) => a + b, 0);
        const unit = availableW / sumRatios;

        // update only the two columns being dragged
        ratios[drag.idx] = newW1 / unit;
        ratios[drag.idx + 1] = newW2 / unit;
        applyRatios();
    }

    function onMouseUp() {
        drag.idx = null;
        window.removeEventListener('mousemove', onMouseMove);
        window.removeEventListener('mouseup', onMouseUp);
    }

    seps.forEach((sep, i) => {
        sep.addEventListener('mousedown', e => {
            drag.idx = i;
            drag.startX = e.clientX;
            const r1 = cols[i].getBoundingClientRect();
            const r2 = cols[i + 1].getBoundingClientRect();
            drag.startW1 = r1.width;
            drag.startW2 = r2.width;

            window.addEventListener('mousemove', onMouseMove);
            window.addEventListener('mouseup', onMouseUp);
        });
    });
}

function zoomToNode(nodeId) {
    if (!nodeId) {
        return;
    }
    const svgSel = graphvizInstance.zoomSelection();
    const zoomBeh = graphvizInstance.zoomBehavior();
    const nodeG = getNodeByNodeId(nodeId);

    const bb = nodeG.getBBox();
    const cx = bb.x + bb.width / 2;
    const cy = bb.y + bb.height / 2;

    const container = document.getElementById("graph-container");
    const {width: Cw, height: Ch} = container.getBoundingClientRect();

    const padding = 50;

    const k = Math.min(Cw / (bb.width + 2 * padding), Ch / (bb.height + 2 * padding));

    const duration = 750;

    svgSel
        .transition()
        .duration(duration)
        .call(zoomBeh.translateTo, cx, cy)
        .on("end", function () {
            svgSel
                .transition()
                .duration(duration)
                .call(zoomBeh.scaleTo, k);
        });
}

function setUpDownloadButton(svg) {
    document.getElementById("download-svg").addEventListener("click", function () {
        const svgData = new XMLSerializer().serializeToString(svg);
        const blob = new Blob([svgData], {type: "image/svg+xml"});
        const url = URL.createObjectURL(blob);
        const a = document.createElement("a");
        a.href = url;
        a.download = "dot_graph.svg";
        document.body.appendChild(a);
        a.click();
        document.body.removeChild(a);
        URL.revokeObjectURL(url);
    });
}