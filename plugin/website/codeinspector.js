const template = document.createElement('template');
template.innerHTML = `
  <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/prism/1.29.0/themes/prism.css"/>
  <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/prism/1.29.0/plugins/line-numbers/prism-line-numbers.css"/>
  <style>
    pre {
      margin: 0;
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
        this.attachShadow({ mode: 'open' })
            .appendChild(template.content.cloneNode(true));

        this._fileNode = '';
        this._sourceCode = '';
        this._units = [];
        this._hover = null;
        this._selected = null;
    }

    connectedCallback() {
        this.container = this.shadowRoot.getElementById('sourceContainer');
        this.codeEl    = this.container.querySelector('code');
        this.tooltip   = this.shadowRoot.getElementById('tooltip');

        this.container.addEventListener('mousemove', e => this._onMouseMove(e));
        this.container.addEventListener('mouseleave', () => this._onMouseLeave());
        this.container.addEventListener('click', e => this._onClick(e));
    }

    set fileNode(fileNode) {
        if(this._fileNode !== fileNode) {
            this._fileNode = fileNode;
            this._sourceCode = fileNode?.Content;
            this._units=getUnitsOfSourceCode(fileNode);
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

    highlightUnit({ start, end }) {
        const spans = this.codeEl.querySelectorAll('.unit');
        let target = null;
        spans.forEach(s => {
            if (+s.dataset.start === start && +s.dataset.end === end) target = s;
        });
        if (!target) return;

        if (this._selected) this._selected.classList.remove('selected');
        this._selected = target;
        target.classList.add('selected');
        target.scrollIntoView({ block: 'center' });
    }

    _render() {
        if (!this.container) return;
        const code = this._sourceCode;
        const units = this._units;

        this.codeEl.textContent = code;

        if (window.Prism) {
            Prism.highlightElement(this.codeEl);
        }

        this.wrapRanges(this.codeEl, units);

        if (window.Prism) {
            if (Prism.plugins?.lineNumbers) {
                Prism.plugins.lineNumbers.resize(this.codeEl);
            }
        }
    }

    wrapRanges(container, units) {

        units.sort((a, b) => {
            if (a.start !== b.start) return b.start - a.start;
            return a.end - b.end;
        });

        for (let unit of units) {
            const walker = document.createTreeWalker(
                container,
                NodeFilter.SHOW_TEXT,
                null,
                false
            );

            let node, charCount = 0;
            let startNode, startOffset, endNode, endOffset;

            while ((node = walker.nextNode())) {
                const nextCount = charCount + node.nodeValue.length;

                if (!startNode && nextCount >= unit.start) {
                    startNode   = node;
                    startOffset = unit.start - charCount;
                }
                if (startNode && nextCount >= unit.end) {
                    endNode   = node;
                    endOffset = unit.end   - charCount;
                    break;
                }
                charCount = nextCount;
            }

            if (!startNode || !endNode) continue; // skip invalid ranges

            const range = document.createRange();
            range.setStart(startNode,  startOffset);
            range.setEnd(  endNode,    endOffset);

            const fragment = range.cloneContents();
            range.deleteContents();

            const span = document.createElement('span');
            span.classList.add('unit');
            span.dataset.name  = unit.name;
            span.dataset.start = unit.start;
            span.dataset.end   = unit.end;
            span.dataset.nodeID = unit.nodeID;
            span.appendChild(fragment);
            range.insertNode(span);
        }
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
            this.tooltip.style.top  = (e.clientY - tooltipHeight - 5) + 'px';
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
        this.dispatchEvent(new CustomEvent('unitSelected', { detail }));
    }
}

customElements.define('code-inspector', CodeInspector);
