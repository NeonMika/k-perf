const templateObjInsp = document.createElement('template');
templateObjInsp.innerHTML = `
  <style>
    :host {
      position: fixed;
      inset: 0;
      display: flex;
      align-items: center;
      justify-content: center;
      pointer-events: none;
      opacity: 0;
      transition: opacity 0.4s ease;
      z-index: 1000;
      width: 100vw;
      height: 100vh;
      background: rgba(0, 0, 0, 0.8);
    }
    :host(.open) {
      pointer-events: auto;
      opacity: 1;
    }
    #modalContent {
      width: 70%;
      height: 90vh;
      background: #ffffff;
      border-radius: 1rem;
      padding: 1.5em;
      box-shadow: 0 4px 20px rgba(0, 0, 0, 0.2);
      display: flex;
      flex-direction: column;
      color: #333333
      font-family: Arial, sans-serif;
    }
    #header {
      display: flex;
      justify-content: space-between;
      align-items: center;
      margin-bottom: 1em;
      height: 60px;
    }
    #closeBtn {
      background: transparent;
      border: none;
      font-size: 1.5rem;
      cursor: pointer;
      color: #666;
      transition: color 0.2s ease;
    }
    #closeBtn:hover { color: #f44336; }
    #tree {
      flex: 1;
      overflow-y: auto;
      padding-right: 0.5em;
    }
    #tree::-webkit-scrollbar { width: 8px; }
    #tree::-webkit-scrollbar-track {
      background: #eaeaea; border-radius: 4px;
    }
    #tree::-webkit-scrollbar-thumb {
      background: #c1c1c1; border-radius: 4px;
    }
    .property-list {
      list-style: none;
      margin: 0;
      padding-left: 1em;
      border-left: 2px solid #ddd;
    }
    .row{
      display: flex;
      flex-direction: row;
      font-family: inherit;
    }
    .row h1 {
        font-size: 2rem;
        margin-bottom: 1rem;
    }
    .property-item {
      position: relative;
      padding: 0.5em;
      margin: 0.2em 0;
      border-radius: 4px;
      cursor: pointer;
      transition: background 0.2s ease;
    }
    .property-item:hover { background: none; }
    .property-item:hover:not(:has(.property-item:hover)) {
      background: #f0f0f0;
    }
    
    .property-item::before {
      content: '\\25BA';
      position: absolute;
      left: -1.4em;
      top: 0.9em;
      transition: transform 0.2s ease;
      font-size: 0.9em;
    }
    .property-item.expanded::before {
      transform: rotate(90deg);
    }
    .property-info { font-size: 0.95rem; }
    
    .property-value {
      background: #f5f5f5;
      font-family: monospace;
      padding: 0.3em;
      border-radius: 4px;
      display: inline-block;
      max-width: 200px;
      white-space: nowrap;
      overflow: hidden;
      text-overflow: ellipsis;
      vertical-align: middle;
      cursor: pointer;
    }
    .property-value.expanded {
      white-space: normal;
      max-width: none;
    }
  </style>

  <div id="modalContent">
    <div id="header">
      <pre class="row"><h1>Object Inspector - </h1><h1 id="title"></h1></pre>

      <button id="closeBtn">×</button>
    </div>
    <div id="tree"></div>
  </div>
`;

class ObjectInspector extends HTMLElement {
    constructor() {
        super();
        this.attachShadow({ mode: 'open' })
            .appendChild(templateObjInsp.content.cloneNode(true));

        this._tree  = this.shadowRoot.getElementById('tree');
        this._title  = this.shadowRoot.getElementById('title');
        this.shadowRoot.getElementById('closeBtn')
            .addEventListener('click', () => this.hide());
    }

    open(id, title) {
        this._tree.innerHTML = '';
        this._title.textContent=title;
        this._renderObject(id, this._tree);
        this.classList.add('open');
    }

    hide() {
        this.classList.remove('open');
    }

    _renderObject(id, container) {
        const query = '?id=' + id;
        fetch('/inspect' + query)
            .then(res => res.ok ? res.json() : Promise.reject(res.statusText))
            .then(data => {
                const ul = document.createElement('ul');
                ul.className = 'property-list';

                data.forEach(prop => {
                    const li = document.createElement('li');
                    li.className = 'property-item';

                    const info = document.createElement('span');
                    info.className = 'property-info visibility-' + prop.visibility;

                    const prefix = document.createTextNode(`${prop.name} (${prop.visibility}) : ${prop.returnType}\t`);
                    const valueSpan = document.createElement('span');
                    valueSpan.className = 'property-value';
                    valueSpan.textContent = prop.value;
                    valueSpan.title = prop.value;
                    valueSpan.addEventListener('click', e => {
                        e.stopPropagation();
                        valueSpan.classList.toggle('expanded');
                    });

                    info.append(prefix, valueSpan);
                    li.appendChild(info);

                    li.addEventListener('click', e => {
                        e.stopPropagation();
                        const expanded = li.classList.toggle('expanded');
                        const childList = li.querySelector('ul');
                        if (childList) {
                            childList.style.display = expanded ? 'block' : 'none';
                        } else if (expanded) {
                            this._renderObject(prop.id, li);
                        }
                    });

                    ul.appendChild(li);
                });

                container.appendChild(ul);
        });
    }
}

customElements.define('object-inspector', ObjectInspector);
