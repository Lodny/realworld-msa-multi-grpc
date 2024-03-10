import {RealBanner} from '../components/real-banner.js'
import {RealTab} from '../components/real-tab.js'
import {RealSidebar} from '../components/real-sidebar.js'
import {iconCdn} from "../services/icon-cdn.js";
import {currentUser, store} from "../services/store.js";
import {actionQueue} from "../services/action-queue.js";

const style = `<style>

</style>`;

const getTemplate = (tabTitles, activeTab) => {
    return `
        ${iconCdn}
        <link rel="stylesheet" href="../css/common.css">
        ${style}
        
        <div class="home-page">
            <real-banner></real-banner>            
        
            <div class="container page">
                <div class="row">
                    <real-tab class="col-md-9" tab-titles="${tabTitles}" active-tab="${activeTab}">
                        <div class="articles"></div>
                        <real-paging></real-paging>
                    </real-tab>
                    <real-sidebar class="col-md-3"></real-sidebar>
                </div>
            </div>
        </div>       
    `;
}

class HomePage extends HTMLElement {
    constructor() {
        super();
        this.attachShadow({mode: 'open'});
        this.listenTypes = ['getArticles', 'changePage', 'selectTag', 'activeTab'];

        const [tabTitles, activeTab] = this.getTabTitles();
        this.activeTab = activeTab;
        this.shadowRoot.innerHTML = getTemplate(tabTitles, activeTab);
    }

    getTabTitles(tagName) {
        let activeTab = 'Global Feed';
        let tabTitles = [activeTab];

        const user = currentUser();
        if (user)
            tabTitles = ['Your Feed', ...tabTitles];

        if (tagName) {
            activeTab = `#${tagName}`;
            tabTitles = [...tabTitles, activeTab];
        }

        return [tabTitles, activeTab];
    }

    async connectedCallback() {
        console.log('home-page::connectedCallback(): 1:', 1);
        actionQueue.addListener(this.listenTypes, this);

        this.tabTag = this.shadowRoot.querySelector('real-tab');

        this.articlesTag = this.shadowRoot.querySelector('.articles');

        this.getArticles(this.activeTab);
    }

    disconnectedCallback() {
        actionQueue.removeListener(this.listenTypes, this);
    }

    updateArticles(articles) {
        const activeTab = this.tabTag.getAttribute('active-tab');
        console.log('home-page::updateArticles(): activeTab:', activeTab);

        this.articlesTag.innerHTML = articles
            ?.map(article => `<real-article-preview slug="${article.slug}"></real-article-preview>`)
            .join('')
    }

    getArticles(activeTab, page = 1) {
        let param = {};
        if (activeTab === 'Global Feed') {

        } else if (activeTab === 'Your Feed') {
            const user = currentUser();
            param = {feed: user.username}
        } else {
            param = {tag: activeTab.slice(1)}
        }

        actionQueue.addAction({
            type: 'getArticles',
            data: {
                value: param,
                page
            },
            set: 'articles',
        })
    }

    callback = ({type, result, data}) => {
        console.log('home-page::callback(): type, result', type, result);
        console.log('home-page::callback(): data:', data);

        const getArticlesCallback = (articles, {totalPages, number}) => {
            this.updateArticles(articles);
            actionQueue.addAction({
                type: 'pageInfo',
                data: {
                    totalPages,
                    number
                }
            });
        }

        const changePageCallback = (page) => {
            this.getArticles(this.activeTab, Number(page));
        }

        const selectTagCallback = (tagName) => {
            const [tabTitles, activeTab] = this.getTabTitles(tagName);

            actionQueue.addAction({
                type: 'tabTitles',
                data: {
                    tabTitles,
                    activeTab
                }
            })
        }

        const activeTabCallback = (activeTab) => {
            this.activeTab = activeTab
            this.getArticles(this.activeTab);
        }

        const runCallback = {
            'getArticles': getArticlesCallback,
            'changePage': changePageCallback,
            'selectTag': selectTagCallback,
            'activeTab': activeTabCallback,
        }
        runCallback[type] && runCallback[type](result, data);
    }
}
customElements.define('home-page', HomePage);
export {HomePage}
