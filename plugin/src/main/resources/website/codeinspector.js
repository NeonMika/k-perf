const template = document.createElement('template');
template.innerHTML = `
  <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/prism/1.29.0/themes/prism.css"/>
  <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/prism/1.29.0/plugins/line-numbers/prism-line-numbers.css"/>
  <style>
    pre {
      margin: 0 !important; 
      padding: 10px;
      border: 1px solid #ccc;
      background: #f5f5f5;
      overflow: auto;
      font-family: Consolas, Menlo, Monaco, 'Courier New', monospace;
      font-size: 10.5pt;
      position: relative;
      width: 100%;
      height: 100%;
      box-sizing: border-box;
    }
    .token {
        background: transparent !important;
    }
    .unit {
      cursor: pointer;
    }
    .unit.hover-highlight {
      background-color: #FFA8A8;
    }
    .unit.selected {
      background-color: #FFE066;
    }
    #tooltip {
      position: absolute;
      padding: 4px 8px;
      background: #333;
      color: #fff;
      border-radius: 4px;
      pointer-events: none;
      font-size: 12px;
      display: none;
      z-index: 1000;
    }
  </style>
  <!-- Prism container sets language-kotlin and line-numbers -->
  <pre id="sourceContainer" class="line-numbers"><code class="language-kotlin"></code></pre>
  <div id="tooltip"></div>
`;

class CodeInspector extends HTMLElement {
    constructor() {
        super();
        this.attachShadow({mode: 'open'})
            .appendChild(template.content.cloneNode(true));

        this._fileNode = '';
        this._sourceCode = '';
        this._units = [];
        this._hover = null;
        this._selected = null;
    }

    connectedCallback() {
        this.container = this.shadowRoot.getElementById('sourceContainer');
        this.codeEl = this.container.querySelector('code');
        this.tooltip = this.shadowRoot.getElementById('tooltip');

        this.container.addEventListener('mousemove', e => this._onMouseMove(e));
        this.container.addEventListener('mouseleave', () => this._onMouseLeave());
        this.container.addEventListener('click', e => this._onClick(e));
    }

    set fileNode(fileNode) {
        if (this._fileNode !== fileNode) {
            if (fileNode == null) {
                this._fileNode = fileNode;
                this._sourceCode = "";
                this._units = [];
            } else {
                this._fileNode = fileNode;
                this._sourceCode = fileNode?.Content;
                this._units = getUnitsOfSourceCode(fileNode);
            }
            this._render();
        }
    }

    set sourceCode(code) {
        this._sourceCode = code;
        this._render();
    }

    set units(arr) {
        this._units = arr;
        this._render();
    }

    highlightUnit({start, end}) {
        const spans = this.codeEl.querySelectorAll('.unit');
        let target = null;
        spans.forEach(s => {
            if (+s.dataset.start === start && +s.dataset.end === end) target = s;
        });

        if (this._selected) this._selected.classList.remove('selected');
        this._selected = target;
        if (target) {
            target.classList.add('selected');
            target.scrollIntoView({block: 'center', behavior: "smooth"});
        }
    }

    _render() {
        this.codeEl.textContent = this._sourceCode;

        if (window.Prism) {
            Prism.highlightElement(this.codeEl);
        }

        const lineNumberRow = this.codeEl.querySelector('.line-numbers-rows');
        if (lineNumberRow) {
            this.codeEl.removeChild(lineNumberRow);
        }

        const prismSpans = this.getExistingSpanPositions(this.codeEl);

        const combinedUnits = this.combineSpans(this._units, prismSpans);

        this.codeEl.innerHTML = '';

        this.addSpans(combinedUnits);

        if (lineNumberRow) {
            this.codeEl.appendChild(lineNumberRow);
        }
    }

    addSpans(units) {
        const code = this._sourceCode;
        const events = [];
        units.forEach(u => {
            events.push({pos: u.start, type: 'start', unit: u, length: u.end - u.start});
            events.push({pos: u.end, type: 'end', unit: u, length: u.end - u.start});
        });

        events.sort((a, b) => {
            if (a.pos !== b.pos) return a.pos - b.pos;
            if (a.type !== b.type) return a.type === 'end' ? -1 : 1;
            if (a.type === 'end') return a.length - b.length;
            else return b.length - a.length;
        });

        let idx = 0;
        const stack = [document.createDocumentFragment()];

        events.forEach(ev => {
            const {pos, type, unit} = ev;
            if (pos > idx) {
                stack[stack.length - 1].appendChild(
                    document.createTextNode(code.slice(idx, pos))
                );
                idx = pos;
            }
            if (type === 'start') {
                const span = document.createElement('span');
                if ('class' in unit) {
                    span.className = unit.class;
                } else {
                    span.classList.add('unit');
                    span.dataset.name = unit.name;
                    span.dataset.start = unit.start;
                    span.dataset.end = unit.end;
                    span.dataset.nodeID = unit.nodeID;
                }

                try {
                    stack[stack.length - 1].appendChild(span);
                } catch (e) {
                    console.log(e)
                }
                stack.push(span);
            } else {
                stack.pop();
            }
        });

        if (idx < this._sourceCode.length) {
            stack[stack.length - 1].appendChild(
                document.createTextNode(code.slice(idx))
            );
        }

        this.codeEl.appendChild(stack[0]);
    }

    combineSpans(spans1, spans2) {
        const cuts = new Set();
        for (const {start, end} of spans1) {
            cuts.add(start);
            cuts.add(end);
        }

        const sortedCuts = Array.from(cuts).sort((a, b) => a - b);

        const split2 = [];
        for (const span of spans2) {
            let currStart = span.start;

            for (const cut of sortedCuts) {
                if (cut > currStart && cut < span.end) {
                    split2.push({
                        start: currStart,
                        end: cut,
                        class: span.class
                    });
                    currStart = cut;
                }
            }

            split2.push({
                start: currStart,
                end: span.end,
                class: span.class
            });
        }

        return [...spans1, ...split2];
    }

    getExistingSpanPositions(container) {
        const spans = [];
        let charIndex = 0;

        function walk(node) {
            if (node.nodeType === Node.TEXT_NODE) {
                charIndex += node.nodeValue.length;
            } else if (node.nodeType === Node.ELEMENT_NODE) {
                if (node.tagName === 'SPAN') {
                    const start = charIndex;
                    node.childNodes.forEach(walk);
                    const end = charIndex;
                    spans.push({
                        start,
                        end,
                        class: node.className
                    });
                } else {
                    node.childNodes.forEach(walk);
                }
            }
        }

        container.childNodes.forEach(walk);

        return spans;
    }


    _onMouseMove(e) {
        const span = e.target.closest('.unit');
        if (span && this.container.contains(span)) {
            if (this._hover && this._hover !== span) {
                this._hover.classList.remove('hover-highlight');
            }
            this._hover = span;
            span.classList.add('hover-highlight');
            this.tooltip.textContent = span.dataset.name;
            this.tooltip.style.display = 'block';
            const tooltipHeight = this.tooltip.offsetHeight;
            this.tooltip.style.left = (e.clientX + 5) + 'px';
            this.tooltip.style.top = (e.clientY - tooltipHeight - 5) + 'px';
        } else {
            this._onMouseLeave();
        }
    }

    _onMouseLeave() {
        if (this._hover) {
            this._hover.classList.remove('hover-highlight');
            this._hover = null;
        }
        this.tooltip.style.display = 'none';
    }

    _onClick(e) {
        const span = e.target.closest('.unit');
        if (!span) return;
        if (this._selected) this._selected.classList.remove('selected');
        this._selected = span;
        span.classList.add('selected');
        const detail = span.dataset;
        this.dispatchEvent(new CustomEvent('unitSelected', {detail}));
    }
}

customElements.define('code-inspector', CodeInspector);
