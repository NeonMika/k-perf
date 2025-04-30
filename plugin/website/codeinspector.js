const template = document.createElement('template');
template.innerHTML = `
  <style>
    #sourceContainer {
      width: 100%;
      white-space: pre;  
      overflow: auto;      
      background: #f5f5f5;
      font-family: Consolas, Menlo, Monaco, 'Courier New', monospace;
      font-size: 10.5pt;
      position: relative;
      padding: 10px;
      box-sizing: border-box;
      border: 1px solid #ccc;
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
  <div id="sourceContainer"></div>
  <div id="tooltip"></div>
`;

class CodeInspector extends HTMLElement {
    constructor() {
        super();
        this.attachShadow({ mode: 'open' })
            .appendChild(template.content.cloneNode(true));

        this._sourceCode = '';
        this._units = [];
        this._hover = null;
        this._selected = null;
    }

    connectedCallback() {
        this.container = this.shadowRoot.getElementById('sourceContainer');
        this.tooltip   = this.shadowRoot.getElementById('tooltip');

        this.container.addEventListener('mousemove', e => this._onMouseMove(e));
        this.container.addEventListener('mouseleave', () => this._onMouseLeave());
        this.container.addEventListener('click', e => this._onClick(e));
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
        const spans = this.container.querySelectorAll('.unit');
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
        this.container.innerHTML = '';

        const events = [];
        units.forEach(u => {
            events.push({ pos: u.start, type: 'start', unit: u });
            events.push({ pos: u.end,   type: 'end',   unit: u });
        });
        events.sort((a,b) => a.pos - b.pos || (a.type==='end'? -1:1));

        let idx = 0;
        const stack = [document.createDocumentFragment()];

        events.forEach(ev => {
            const { pos, type, unit } = ev;
            if (pos > idx) {
                stack[stack.length-1].appendChild(
                    document.createTextNode(code.slice(idx, pos))
                );
                idx = pos;
            }
            if (type === 'start') {
                const span = document.createElement('span');
                span.classList.add('unit');
                span.dataset.name  = unit.name;
                span.dataset.start = unit.start;
                span.dataset.end   = unit.end;
                span.dataset.nodeID   = unit.nodeID;
                stack[stack.length-1].appendChild(span);
                stack.push(span);
            } else {
                stack.pop();
            }
        });

        if (idx < code.length) {
            stack[stack.length-1].appendChild(
                document.createTextNode(code.slice(idx))
            );
        }

        this.container.appendChild(stack[0]);
    }

    _onMouseMove(e) {
        const span = e.target.closest('.unit');
        if (span && this.container.contains(span)) {
            if (this._hover && this._hover!==span) {
                this._hover.classList.remove('hover-highlight');
            }
            this._hover = span;
            span.classList.add('hover-highlight');
            this.tooltip.textContent = span.dataset.name;
            this.tooltip.style.display = 'block';
            const containerRect = this.container.getBoundingClientRect();
            const tooltipHeight = this.tooltip.offsetHeight;

            this.tooltip.style.left = (e.clientX  + 5) + 'px';
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
