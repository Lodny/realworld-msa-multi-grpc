const style = `<style>
    a {
        color: #5CB85C;
    }

    footer {
        position: fixed;
        bottom: 0;
        z-index: 999;
        width: 100%;
        
        font-family: "Titillium Web", sans-serif;
        background-color: #F3F3F3;
        
        margin-top: 2rem;
    }
    
    .container {
        line-height: 1.5rem;
        padding: 1rem 5rem;
        
        display: flex;
    }
    
    .logo-font {
        font-size: 1rem;
    }
    
    .attribution {
        color: #aaa;
        font-size: 0.7rem;
        font-weight: lighter;
        margin-left: 1rem;
    }           
</style>`;


function getTemplate() {
    return `
        <link rel="stylesheet" href="../css/common.css">
        ${style}
        
        <footer>
            <div class="container">
                <a href="/" class="logo-font">conduit</a>
                <span class="attribution">
                      An interactive learning project from <a href="https://thinkster.io">Thinkster</a>. Code &amp;
                      design licensed under MIT.
                </span>
            </div>
        </footer>
    `;
}

class RealFooter extends HTMLElement {
    constructor() {
        super();
        this.attachShadow({mode: 'open'});
        this.shadowRoot.innerHTML = getTemplate()
    }

    connectedCallback() {
        console.log('real-footer::connectedCallback(): 1:', 1);
    }
}

customElements.define('real-footer', RealFooter);
