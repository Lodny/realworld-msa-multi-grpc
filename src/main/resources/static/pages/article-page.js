import {iconCdn} from "../services/icon-cdn.js";
import {currentUser, store} from "../services/store.js";
import {RealCommentList} from "../components/real-comment-list.js";
import {RealArticleMeta} from "../components/real-article-meta.js";
import {actionQueue} from "../services/action-queue.js";

const style = `<style>
    .banner {
        color: #fff;
        background: #333;
        padding: 2rem;
        margin-bottom: 2rem
    }
    
    .banner h1 {
        text-shadow: 0 1px 3px rgba(0, 0, 0, .3);
        margin-bottom: 0
    }

    .article-page .banner {
        padding: 2rem 0
    }
    
    .article-page .banner h1 {
        font-size: 2.8rem;
        font-weight: 600
    }
    
    .article-page .banner .btn {
        opacity: .8
    }
    
    .article-page .banner .btn:hover {
        transition: .1s all;
        opacity: 1
    }
    
    .article-page .banner .article-meta {
        margin: 2rem 0 0
    }
    
    .article-page .banner .article-meta .author {
        color: #fff
    }
    
    .article-page .article-content p {
        font-family: 'source serif pro', serif;
        font-size: 1.2rem;
        line-height: 1.8rem;
        margin-bottom: 2rem
    }
    
    .article-page .article-content h1, .article-page .article-content h2, .article-page .article-content h3, .article-page .article-content h4, .article-page .article-content h5, .article-page .article-content h6 {
        font-weight: 500 !important;
        margin: 1.6rem 0 1rem
    }
    
    .article-page .article-actions {
        text-align: center;
        margin: 1.5rem 0 3rem
    }
    
    .col-xs-12 {
        position: relative;
        min-height: 1px;
        padding-right: 15px;
        padding-left: 15px;
        flex: 0 0 100%;
        max-width: 100%
    }
    
    hr {
        margin-top: 1rem;
        margin-bottom: 1rem;
        border: 0;
        border-top: 1px solid rgba(0, 0, 0, 0.1);
    }
    
    p {
        margin-bottom: 1rem;
    }
    
    .container.page a {
        color: #5CB85C;
    }
</style>`;

const getTemplate = (article, bLogin) => {
    const author = article.author;

    return `
        ${iconCdn}
        <link rel="stylesheet" href="../css/common.css">
        <link rel="stylesheet" href="../css/tag.css">
        ${style}

        <div class="article-page">
            <div class="banner">
                <div class="container">
                    <h1>${article.title}</h1>
                    <real-article-meta slug="${article.slug}" color="white"></real-article-meta>
                </div>
            </div>
            
            <div class="container page">
                <div class="row article-content">
                    <div class="col-xs-12">
                        <p>
                            ${article.body}
                        </p>
                        <ul class="tag-list">
                        ${
                            article.tagList.map(tag => `<li class="tag-default tag-pill tag-outline">${tag}</li>`).join('')
                        }
                        </ul>
                    </div>
                </div>
                
                <hr />
                
                <div class="article-actions">
                    <real-article-meta slug="${article.slug}" color="#5CB85C"></real-article-meta>
                </div>          
                
                <div class="row">
                    <real-comment-list class="col-md-8 offset-md-2" slug="${article.slug}">
                    </real-comment-list>
                </div>           
            </div>
        </div>
    `;
}

class ArticlePage extends HTMLElement {
    constructor() {
        super();
        this.attachShadow({mode: 'open'});

        const slug = this.getAttribute('pathName');
        console.log('article-page::constructor(): slug:', slug);
        const article = store.getArticle(slug);
        console.log('article-page::constructor(): article:', article);
        const user = currentUser();
        this.shadowRoot.innerHTML = getTemplate(article, !!user);
    }

    async connectedCallback() {
        console.log('article-page::connectedCallback(): 1:', 1);

        this.articleMetas = Array.from(this.shadowRoot.querySelectorAll('real-article-meta'));
        console.log('article-page::findElements(): this.articleMetas:', this.articleMetas);
    }

    disconnectedCallback() {
    }
}
customElements.define('article-page', ArticlePage);
export {ArticlePage}

