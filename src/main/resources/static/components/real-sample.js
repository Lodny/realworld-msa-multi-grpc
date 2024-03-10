class RealSample extends HTMLElement {
    constructor() {
        super();
        // Shadow DOM 생성
        this.attachShadow({mode: 'open'});

        // Shadow DOM에 템플릿 삽입
        this.shadowRoot.innerHTML = `
            <style>
                .sample-component {
                    background-color: lightblue;
                    padding: 10px;
                }
            </style>
            <div class="sample-component">
                <h2>Hello, <span id="name"></span>!</h2>
                <button id="change-name">Change Name</button>
            </div>
        `;

        this.nameSpan = this.shadowRoot.getElementById('name');
        this.changeNameButton = this.shadowRoot.getElementById('change-name');

        this.changeNameButton.addEventListener('click', () => this.changeName());
    }

    // 컴포넌트 속성 정의
    static get observedAttributes() {
        return ['name'];
    }

    connectedCallback() {
        this.updateName();
    }

    // 컴포넌트 속성 변경 시 호출되는 메서드
    attributeChangedCallback(name, oldValue, newValue) {
        if (name === 'name' && oldValue !== newValue) {
            this.updateName();
        }
    }

    // 이름 변경 메서드
    changeName() {
        const newName = prompt('Enter a new name:');
        if (newName) {
            this.setAttribute('name', newName);
        }
    }

    // 이름 업데이트 메서드
    updateName() {
        this.nameSpan.textContent = this.getAttribute('name') || 'World';
    }
}
customElements.define('real-sample', RealSample);
export {RealSample}
