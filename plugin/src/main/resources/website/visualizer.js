function restructureTree(root) {
    const ignoreList = ["NodeName", "Children", "Caption", "Dump", "FunctionIdentity", "Content", "StartOffset", "EndOffset", "ObjectIdentity"];
    let idCount = 0;

    function traverse(node, parent) {

        node.displayedData = Object.fromEntries(
            Object.entries(node).filter(([key]) => !ignoreList.includes(key) && key !== "intermediate")
        );

        node.nodeID = "node" + (idCount++);
        node.parent = parent;
        node.highlight = false;
        if (!("intermediate" in node)) {
            node.intermediate = false;
        }
        if (Array.isArray(node.Children)) {
            for (const child of node.Children) {
                traverse(child, node);
            }
        }
    }

    traverse(root, null);
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
        function escapeDotSymbols(s) {
            return s
                ?.replace(/&/g, "&amp;")
                ?.replace(/</g, "&lt;")
                ?.replace(/>/g, "&gt;");
        }
        function truncate(str, maxLength) {
            return str.length > maxLength ? str.slice(0, maxLength) + "..." : str;
        }

        const typeName = node.NodeName || "";
        const caption = node.Caption || "";

        const clusterColor = getClusterColor(node.NodeType)

        if (clusterColor) {
            dotBuilder.push(`subgraph cluster_${node.nodeID} {
                            style="filled,rounded";
                            color="grey";
                            margin=25;
                            fillcolor="${clusterColor}"`);
        }

        // noinspection XmlDeprecatedElement,HtmlDeprecatedTag,HtmlUnknownAttribute
        let label = `<FONT FACE="Calibri" POINT-SIZE="16">${typeName}</FONT><BR/>` +
            `<FONT FACE="Courier New" >${node.NodeType}</FONT>`;


        if (caption) {
            // noinspection XmlDeprecatedElement,HtmlDeprecatedTag
            label += `<BR/><FONT FACE="Courier New" >${escapeDotSymbols(caption)}</FONT>`;
        }

        if (node.NodeType === "IrConstImpl" && !node.intermediate) {
            // noinspection XmlDeprecatedElement,HtmlDeprecatedTag
            label += `<BR/><FONT FACE="Courier New" >${truncate(escapeDotSymbols(node.Value), 30)}</FONT>`;
        }

        if (hasInvisibleChild(node)) {
            label += `<BR/><BR/>➕  `;
        } else {
            label += `<BR/><BR/>➖  `;
        }


        if (node.visible) {
            dotBuilder.push(`    ${node.nodeID} [${nodeAttributes(node, label)}];`);


            if (Array.isArray(node.Children)) {
                for (const child of node.Children) {
                    if (child.visible) {
                        dotBuilder.push(`    ${node.nodeID} -> ${child.nodeID} [label="${child.Relationship}"];`);
                        traverse(child);
                    }
                }
            }
        }
        if (clusterColor) {
            dotBuilder.push(`}`);
        }
    }

    traverse(root);
    dotBuilder.push("}\n");
    return dotBuilder.join("\n");
}

function nodeAttributes(node, label) {
    const attrs = [
        `id="${node.nodeID}"`,
        `label=<${label}>`,
        `shape="${getNodeShape(node.NodeType)}"`
    ];

    if (node.highlight) attrs.push(`fillcolor="#FFFF00"`);

    const styles = [];
    if (node.highlight) styles.push('filled');
    if (node.intermediate) styles.push('dotted');
    if (styles.length) attrs.push(`style="${styles.join(',')}"`);

    return attrs.join(', ');
}

function createNodeDict(root) {
    const nodeDict = {};

    function traverse(node) {
        nodeDict[node.nodeID] = node;

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

        node.highlight = false;
        if (isFiltered(node, filters)) {
            currentNodeVisible = true;
            node.highlight = true;
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

    if (filters.length > 0) {
    } else {
        collapseByDepth(root, 3)
    }
}

function isFiltered(node, filters) {
    if (filters.length === 0) {
        return false;
    }
    const filtersByType = {};
    for (const filter of filters) {
        const {type, value} = filter;
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

    function traverse(node, depth) {
        node.visible = depth < maxDepth;
        if (Array.isArray(node.Children)) {
            for (const child of node.Children) {
                traverse(child, depth + 1);
            }
        }
    }

    traverse(root, 0);
}

function toggleChildren(node, visibility) {
    if (Array.isArray(node.Children)) {
        for (const child of node.Children) {
            child.visible = visibility;
        }
    }
}

function expandAll(node) {
    if (Array.isArray(node.Children)) {
        for (const child of node.Children) {
            child.visible = true;
            expandAll(child);
        }
    }
}

function expandAllParents(node) {
    node.visible = true;
    if (node.parent) {
        expandAllParents(node.parent);
    }
}

function hasInvisibleChild(node) {
    if (Array.isArray(node.Children)) {
        for (const child of node.Children) {
            if (!child.visible) {
                return true;
            }
        }
    }
    return false;
}

function getFileNodeOfNode(node) {
    let fileNode = node;
    while (fileNode != null && fileNode.NodeType !== "IrFileImpl") {
        fileNode = fileNode.parent;
    }
    if (fileNode == null) {
        return null;
    }
    return fileNode;
}

function getSourceCodeOfNode(node) {
    if (!("StartOffset" in node && "EndOffset" in node)) {
        return null;
    }
    let fileNode = getFileNodeOfNode(node)
    if (fileNode == null) {
        return null;
    }
    return fileNode.Content.substring(node.StartOffset, node.EndOffset);
}

function getUnitsOfSourceCode(fileNode) {
    let units = []

    function traverse(node) {
        if ("StartOffset" in node && "EndOffset" in node && node.StartOffset !== node.EndOffset) {
            units.push({name: node.NodeType, start: node.StartOffset, end: node.EndOffset, nodeID: node.nodeID})
        }
        if (Array.isArray(node.Children)) {
            for (const child of node.Children) {
                traverse(child);
            }
        }
    }

    traverse(fileNode);
    return units;
}


function groupArrays(tree) {
    if (!tree.Children || tree.Children.length === 0) {
        return tree;
    }

    const processedChildren = tree.Children.map(child => groupArrays(child));

    const newChildren = [];
    let i = 0;

    while (i < processedChildren.length) {
        const first = processedChildren[i];
        const prop = extractBeforeBracket(first.Relationship);
        const type = first.NodeName;

        const run = [first];
        let j = i + 1;
        while (
            j < processedChildren.length &&
            extractBeforeBracket(processedChildren[j].Relationship) === prop &&
            processedChildren[j].NodeName === type
            ) {
            run.push(processedChildren[j]);
            j++;
        }

        if (run.length < 3) {
            run.forEach(node => newChildren.push(node));
        } else {
            newChildren.push({
                NodeType: run[0].NodeType,
                NodeName: `${type} Group`,
                Caption: "",
                Dump: `Group of ${prop} (type=${type})`,
                intermediate: true,
                Relationship: prop + `[${extractArrayIndex(run[0].Relationship)}..${extractArrayIndex(run[run.length - 1].Relationship)}]`,
                Type: type,
                Children: run
            });
        }

        i = j;
    }

    return {
        ...tree,
        Children: newChildren
    };

    function extractBeforeBracket(str) {
        const index = str.indexOf('[');
        return index === -1 ? str : str.substring(0, index);
    }

    function extractArrayIndex(str) {
        const start = str.indexOf('[');
        const end = str.indexOf(']');
        return parseInt(str.substring(start + 1, end), 10);
    }
}