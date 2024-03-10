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
                        <h1 class="text-xs-center">Sign up</h1>
                        <p class="text-xs-center">
                            <a href="/login">Have an account?</a>
                        </p>
        
                        <ul class="error-messages">
                            <li>That email is already taken</li>
                        </ul>
        
                        <form>
                            <fieldset class="form-group">
                                <input class="form-control form-control-lg" type="text" placeholder="Username" />
                            </fieldset>
                            <fieldset class="form-group">
                                <input class="form-control form-control-lg" type="text" placeholder="Email" />
                            </fieldset>
                            <fieldset class="form-group">
                                <input class="form-control form-control-lg" type="password" placeholder="Password" />
                            </fieldset>
                            <button class="btn btn-lg btn-primary pull-xs-right" tabindex="-1">Sign up</button>
                        </form>
                    </div>
                </div>
            </div>
        </div>        
    `;
}

class RegisterPage extends HTMLElement {
    constructor() {
        super();
        this.attachShadow({mode: 'open'});
        this.shadowRoot.innerHTML = getTemplate();
    }

    connectedCallback() {
        console.log('register-page::connectedCallback(): 1:', 1);

        this.usernameTag = this.shadowRoot.querySelector('input[placeholder="Username"]');
        this.emailTag = this.shadowRoot.querySelector('input[placeholder="Email"]');
        this.passwordTag = this.shadowRoot.querySelector('input[placeholder="Password"]');

        this.shadowRoot.querySelector('button').addEventListener('click', this.register);
        this.shadowRoot.querySelector('a')
            .addEventListener('click', (evt) => {
                evt.preventDefault();
                addGoAction(evt.target.href);
            });
    }

    register = (evt) => {
        evt.preventDefault();
        this.shadowRoot.activeElement?.blur();
        console.log('register-page::register(): 1:', 1);

        actionQueue.addAction({
            type: 'registerUser',
            data: {
                username: this.usernameTag.value,
                email: this.emailTag.value,
                password: this.passwordTag.value,
                // username: 'cider',
                // email: 'cider@drink.com',
                // password: '1234'
            },
            set: 'user',
            nextRoute: '/',
        });
    }

    callback = ({type, result}) => {
        console.log('register-page::callback(): type, result:', type, result);

        const errorCallback = (result) => {
            if (result.startsWith('username'))
                this.usernameTag.focus();
            else if (result.startsWith('email'))
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
customElements.define('register-page', RegisterPage);
export {RegisterPage}
