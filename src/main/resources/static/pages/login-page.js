import {actionQueue, addGoAction} from "../services/action-queue.js";
import {iconCdn} from "../services/icon-cdn.js";

const style = `<style>
        
</style>`;

const getTemplate = () => {
    return `
        ${iconCdn}
        <link rel="stylesheet" href="../css/common.css">
        <link rel="stylesheet" href="../css/form.css">
        <link rel="stylesheet" href="../css/register-login.css">
        
        ${style}

        <div class="auth-page">
            <div class="container page">
                <div class="row">
                    <div class="col-md-6 offset-md-3 col-xs-12">
                        <h1 class="text-xs-center">Sign in</h1>
                        <p class="text-xs-center">
                            <a href="/register">Need an account?</a>
                        </p>

                        <ul class="error-messages">
                            <li>That email is already taken</li>
                        </ul>

                        <form>
                            <fieldset class="form-group">
                                <input class="form-control form-control-lg" type="text" placeholder="Email" />
                            </fieldset>
                            <fieldset class="form-group">
                                <input class="form-control form-control-lg" type="password" placeholder="Password" />
                            </fieldset>
                            <button class="btn btn-lg btn-primary pull-xs-right" tabindex="-1">Sign in</button>
                        </form>
                    </div>
                </div>
            </div>
        </div>        
    `;
}

class LoginPage extends HTMLElement {
    constructor() {
        super();
        this.attachShadow({mode: 'open'});

        this.shadowRoot.innerHTML = getTemplate();
    }

    connectedCallback() {
        console.log('login-page::connectedCallback(): 1:', 1);

        this.emailTag = this.shadowRoot.querySelector('input[type="text"]');
        this.passwordTag = this.shadowRoot.querySelector('input[type="password"]');

        this.shadowRoot.querySelector('button')
            .addEventListener('click', this.login);

        this.shadowRoot.querySelector('a')
            .addEventListener('click', (evt) => {
                evt.preventDefault();
                addGoAction(evt.target.href);
            });
    }

    login = (evt) => {
        evt.preventDefault();
        this.shadowRoot.activeElement?.blur();
        console.log('login-page::login(): 1:', 1);

        actionQueue.addAction({
            type: 'login',
            data: {
                // email: this.emailTag.value,
                // password: this.passwordTag.value,
                email: 'cider@drink.com',
                password: '1234'
            },
            set: 'user',
            nextRoute: '/',
        });
    }

    callback = ({type, result}) => {
        console.log('login-page::callback(): type, message:', type, result);

        const errorCallback = (result) => {
            if (result.startsWith('email'))
                this.emailTag.focus();
            else if (result.startsWith('password'))
                this.passwordTag.focus();
        }

        const runCallback = {
            'error': errorCallback,
        }
        runCallback[type] && runCallback[type](result);
    }
}
customElements.define('login-page', LoginPage);
export {LoginPage}

