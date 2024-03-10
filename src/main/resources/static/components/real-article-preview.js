import {currentUser, store} from "../services/store.js";
import {iconCdn} from "../services/icon-cdn.js";
import {actionQueue, addGoAction} from "../services/action-queue.js";

const style = `<style>
    img {
        overflow-clip-margin: content-box;
        overflow: clip;
    }
    
    a {
        color: #5CB85C;
        h1 {
            color: black;
        }
    }
    
    
    .article-preview {
        border-top: 1px solid rgba(0, 0, 0, .1);
        padding: 1.5rem 0;
        
        .article-meta {
            margin: 0 0 1rem;
        }
        
        .preview-link {
             h1 {
                font-weight: 600 !important;
                font-size: 1.5rem !important;
                margin-bottom: 3px;
            }
        
            p {
                font-weight: 300;
                font-size: 24px;
                color: #999;
                margin-bottom: 15px;
                font-size: 1rem;
                line-height: 1.3rem;
            }
            
            span {
                max-width: 30%;
                font-size: .8rem;
                font-weight: 300;
                color: #bbb;
                vertical-align: middle;
            }
            
            ul {
                float: right;
                max-width: 50%;
                vertical-align: top;
                li {
                    font-weight: 300;
                    font-size: .8rem !important;
                    padding-top: 0 !important;
                    padding-bottom: 0 !important;
                }
            }
            
        }
    }
</style>`;

const getTemplate = (article) => {
    const author = article.author;

    return `
        ${iconCdn}
        <link rel="stylesheet" href="../css/common.css">
        <link rel="stylesheet" href="../css/article-meta.css">
        <link rel="stylesheet" href="../css/tag.css">
        ${style}
    
        <div class="article-preview">
            <div class="article-meta">
                <a href="/profile/${author.username}"><img src="${author.image}" /></a>
                <div class="info">
                    <a href="/profile/${author.username}" class="author">${author.username}</a>
                    <span class="date">${article.createdAt}</span>
                </div>
                <button class="btn btn-outline-primary btn-sm pull-xs-right ${article.favorited ? 'focus' : ''}">
                    <i class="ion-heart"></i> 10
                </button>
            </div>
            <a href="/article/${article.slug}" class="preview-link">
                <h1>${article.title}</h1>
                <p>${article.description}</p>
                <span>Read more...</span>
                <ul class="tag-list">
                ${
                    article.tagList.map(tag => `<li class="tag-default tag-pill tag-outline">${tag}</li>`).join('')
                }
                </ul>
            </a>
        </div>
    `;
}


class RealArticlePreview extends HTMLElement {
    constructor() {
        super();
        this.attachShadow({mode: 'open'});
        this.listenTypes = ['favorite', 'unfavorite'];

        const slug = this.getAttribute('slug');
        console.log('real-article-preview::constructor(): slug:', slug);
        if (!slug)
            throw Error('slug is required');

        this.article = store.getArticle(slug);
        console.log('real-article-preview::constructor(): this.article:', this.article);

        this.shadowRoot.innerHTML = getTemplate(this.article);
    }

    connectedCallback() {
        console.log('real-article-preview::connectedCallback(): 1:', 1);
        actionQueue.addListener(this.listenTypes, this);

        this.shadowRoot.querySelector('a.preview-link')
            .addEventListener('click', this.goLink);
        this.favoriteButton = this.shadowRoot.querySelector('button');
        this.favoriteButton.addEventListener('click', this.favorite);

        this.shadowRoot.querySelectorAll('.article-meta a')
            .forEach(link => link.addEventListener('click', this.goLink));

        this.updateFavorite(this.article);
    }

    unconnectedCallback() {
        actionQueue.removeListener(this.listenTypes, this);
    }

    favorite = (evt) => {
        evt.preventDefault();
        console.log('real-article-preview::favorite(): this.article:', this.article);

        actionQueue.addAction({
            type: this.article.favorited ? 'unfavorite' : 'favorite',
            data: {
                value: this.article.slug,
            },
        });
    }

    goLink = (evt) => {
        evt.preventDefault();
        console.log('real-article-preview::goLink(): evt.target.closest(a):', evt.target.closest('a'));

        const url = evt.target.closest('a')?.href;
        console.log('real-article-preview::goLink(): url:', url);
        addGoAction(url);
    }

    updateFavorite(article) {
        console.log('real-article-preview::updateFavorite(): 1:', 1);
        this.favoriteButton.innerHTML = `<i class="ion-heart"></i> ${article.favoritesCount}`;
        article.favorited
            ? this.favoriteButton.classList.add('focus')
            : this.favoriteButton.classList.remove('focus');
    }

    callback = ({type, result, data}) => {
        console.log('real-article-preview::callback(): type, result:', type, result);
        console.log('real-article-preview::callback(): data:', data);

        const favoriteCallback = (_, data) => {
            if (!data) return;

            const {article} = data;
            if (this.article.slug !== article.slug) return;

            this.article = article;
            store.setArticle(this.article);
            this.updateFavorite(this.article);
        }

        const runCallback = {
            'favorite':   favoriteCallback,
            'unfavorite':   favoriteCallback,
        }
        runCallback[type] && runCallback[type](result, data);
    }
}

customElements.define('real-article-preview', RealArticlePreview);
export {RealArticlePreview}


