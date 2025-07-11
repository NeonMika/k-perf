function markSelected(selectedNodeId) {
    const svg = document.getElementById("graph-container").querySelector("svg");
    let nodeGroup = getNodeByNodeId(selectedNodeId);
    nodeGroup.classList.add("node-selected");
    svg.querySelectorAll(".node-selected").forEach(el => {
        const shape = el.querySelector("ellipse, polygon, path, rect");
        if (shape) {
            shape.setAttribute("stroke", "#FFE066");
            shape.setAttribute("stroke-width", String(5));
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
            shape.setAttribute("stroke-width", String(1));
        }
    });
}

function setInfoDiv(nodeId) {
    const infoDiv = document.getElementById("selected-node-info");
    const leftCol = document.getElementById("leftCol");

    leftCol.querySelectorAll('.optional').forEach(el => el.remove());

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

    if (!nodeData.intermediate) {
        const sourcecodeButton = document.createElement("button");
        sourcecodeButton.textContent = "Show Sourcecode";
        sourcecodeButton.className = "optional";
        sourcecodeButton.onclick = () => {
            const popup = document.getElementById('fullscreen-popup');
            popup.classList.add('active');
            const codeHeading = popup.querySelector("#codeHeading");
            codeHeading.textContent = "Original Source Code of " + nodeData.NodeType + " " + nodeData.Caption;
            const codeParagraph = popup.querySelector("#codeParagraph");
            codeParagraph.textContent = getSourceCodeOfNode(nodeData) ?? 'Source Code not found';
        };
        leftCol.appendChild(sourcecodeButton);

        const inspectButton = document.createElement("button");
        inspectButton.textContent = "Inspect Object";
        inspectButton.className = "optional";
        inspectButton.onclick = () => {
            const insp = document.getElementById('object-inspector');
            insp.open(nodeData.ObjectIdentity, nodeData.NodeType)
        };
        leftCol.appendChild(inspectButton);
    }


    if ("FunctionIdentity" in nodeData) {
        const functionCallButton = document.createElement("button");
        functionCallButton.className = "optional";
        if (!nodeData.NodeType.toLowerCase().includes("call")) {
            functionCallButton.textContent = "Expand all calls";
        } else {
            functionCallButton.textContent = "Expand function";
        }
        functionCallButton.onclick = () => {
            const nodes = getAllNodesWithFunctionIdentity(nodeData.FunctionIdentity);
            for (let node of nodes) {
                if (nodeData.NodeName !== node.NodeName) {
                    expandAllParents(node);
                }
            }
            updateGraph();
        };
        leftCol.appendChild(functionCallButton);
    }


    function insertSpaceBeforeCaps(str) {
        return str.replace(/(?!^)([A-Z])/g, ' $1');
    }
}

function setCodeInspector(nodeId) {
    const inspector = document.getElementById('inspector');

    if (!nodeId) {
        inspector.setFileNode(null);
        return;
    }

    const nodeData = nodeDict[nodeId];

    const fileNode = getFileNodeOfNode(nodeData);
    if (fileNode) {
        inspector.setFileNode(fileNode);
        if (!nodeData.intermediate) {
            inspector.highlightUnit({start: nodeData.StartOffset, end: nodeData.EndOffset});
        } else {
            inspector.highlightUnit({start: -1, end: -1})
        }
    } else {
        inspector.setFileNode(null);
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
    for (const [, value] of Object.entries(nodeDict)) {
        if ("FunctionIdentity" in value) {
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

    if ("FunctionIdentity" in nodeData) {
        const nodes = getAllNodesWithFunctionIdentity(nodeData.FunctionIdentity);
        for (node of nodes) {
            if (nodeData.NodeType.toLowerCase().includes("call") !== node.NodeType.toLowerCase().includes("call")) {
                const otherNode = getNodeByNodeId(node.nodeID)
                if (otherNode) {
                    drawDottedLine(nodeGroup, otherNode, svg, nodeData.nodeID, node.nodeID);
                }
            }
        }
    }
}

function getNodeByNodeId(nodeID) {
    const svg = document.getElementById("graph-container").querySelector("svg");
    return svg.querySelector("#" + nodeID)
}

function drawDottedLine(node1, node2, svg, nodeID1, nodeID2) {
    const bbox1 = node1.getBBox();
    const bbox2 = node2.getBBox();

    const center1 = {x: bbox1.x + bbox1.width / 2, y: bbox1.y + bbox1.height / 2};
    const center2 = {x: bbox2.x + bbox2.width / 2, y: bbox2.y + bbox2.height / 2};

    function getEdgeMidpoints(bbox) {
        return [
            {x: bbox.x + bbox.width / 2, y: bbox.y},
            {x: bbox.x + bbox.width / 2, y: bbox.y + bbox.height},
            {x: bbox.x, y: bbox.y + bbox.height / 2},
            {x: bbox.x + bbox.width, y: bbox.y + bbox.height / 2}
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

    const hit = document.createElementNS("http://www.w3.org/2000/svg", "line");
    hit.setAttribute("x1", point1.x);
    hit.setAttribute("y1", point1.y);
    hit.setAttribute("x2", point2.x);
    hit.setAttribute("y2", point2.y);
    hit.setAttribute("stroke", "transparent");
    hit.setAttribute("stroke-width", "10");
    hit.setAttribute("pointer-events", "stroke");
    hit.classList.add("dotted-line");
    hit.style.cursor = "pointer";                    // show pointer

    hit.addEventListener("click", e => {
        const clickX = e.clientX;
        const clickY = e.clientY;
        const rect1 = node1.getBoundingClientRect();
        const rect2 = node2.getBoundingClientRect();
        const center1Client = {
            x: rect1.left + rect1.width / 2,
            y: rect1.top + rect1.height / 2
        };
        const center2Client = {
            x: rect2.left + rect2.width / 2,
            y: rect2.top + rect2.height / 2
        };
        const d1 = Math.hypot(clickX - center1Client.x, clickY - center1Client.y);
        const d2 = Math.hypot(clickX - center2Client.x, clickY - center2Client.y);
        if (d1 < d2) zoomToNode(nodeID2);
        else zoomToNode(nodeID1);
    });

    const graph = svg.querySelector(".graph");
    graph.appendChild(line);
    graph.appendChild(hit);
    return line;
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

    const graphG = svgSel.select('g');
    const graphBB = graphG.node().getBBox();
    const ratioX = graphBB.width / bb.width;
    const ratioY = graphBB.height / bb.height;
    const k = Math.min(ratioX, ratioY);

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