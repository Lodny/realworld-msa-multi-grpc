import {HomePage} from '../pages/home-page.js';
import {RegisterPage} from '../pages/register-page.js';
import {LoginPage} from '../pages/login-page.js';
import {EditorPage} from "../pages/editor-page.js";
import {SettingsPage} from "../pages/settings-page.js";
import {ProfilePage} from "../pages/profile-page.js";
import {ArticlePage} from "../pages/article-page.js";

const routeInfo = {
    routes: [
        {
            url: '/',
            name: 'home',
            element: HomePage
        },
        {
            url: '/login',
            name: 'login',
            element: LoginPage
        },
        {
            url: '/register',
            name: 'register',
            element: RegisterPage
        },
        {
            url: '/editor/:slug',
            name: 'editor',
            element: EditorPage
        },
        {
            url: '/editor',
            name: 'editor',
            element: EditorPage
        },
        {
            url: '/settings',
            name: 'settings',
            element: SettingsPage
        },
        {
            url: '/profile/:username',
            name: 'profile',
            element: ProfilePage
        },
        {
            url: '/article/:slug',
            name: 'article',
            element: ArticlePage
        },
    ]
};

function extractPath(url, basePath) {
    const regex = new RegExp(basePath.replace(/:[a-zA-Z0-9-]+/g, '([a-zA-Z0-9-]+)'));
    const match = url.match(regex);
    if (!match) return null;

    return [match[0], match[1]];
}

const getRouteByUrl = (url) => {
    const foundRoute = routeInfo.routes.toReversed().find(route => {
        return !!extractPath(url, route.url);
    });
    const [_, pathName] = extractPath(url, foundRoute.url);
    return {...foundRoute, pathName};
}

const getRouteByName = (name) => {
    console.log('routes::getRouteByName(): name:', name);
    return routeInfo.routes.find(route => route.name === name);
}

const getBaseUrlByUrl = (url) => {
    return routeInfo.routes.toReversed().find(route => {
        return !!extractPath(url, route.url);
    })?.url;
}

const getCustomTagHTML = (url) => {
    const route = getRouteByUrl(url);

    const routeTagName = route.element.name.replace(/([a-z])([A-Z])/g, '$1-$2').toLowerCase();
    const pathName = `pathName="${route.pathName ?? ''}"`
    console.log('routes::getCustomTagHTML(): routeTagName, pathName:', routeTagName, pathName);

    return `<${routeTagName} ${pathName}></${routeTagName}>`;
}

export {
    getRouteByUrl,
    getRouteByName,
    getBaseUrlByUrl,
    getCustomTagHTML
}
