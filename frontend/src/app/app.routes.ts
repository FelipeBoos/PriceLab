import { Routes } from '@angular/router';
import { MainLayout } from './layouts/main-layout/main-layout';
import { Home } from './features/home/home';
import { Produtos } from './features/produtos/produtos';
import { Categorias } from './features/categorias/categorias';
import { Pedidos } from './features/pedidos/pedidos';
import { Usuarios } from './features/usuarios/usuarios';
import { Login } from './features/login/login';
import { EstrategiasPreco } from './features/estrategias-preco/estrategias-preco';

import { SimularEstrategiaPreco } from './features/estrategias-preco/simular-estrategia-preco/simular-estrategia-preco';

export const routes: Routes = [
    {
        path: '',
        redirectTo: 'app',
        pathMatch: 'full'
    },

    {
        path: 'login',
        component: Login
    },

    {
        path: 'app',
        component: MainLayout,
        children: [
            {
                path: '',
                component: Home,
                pathMatch: 'full'
            },
            {
                path: 'home',
                component: Home
            },
            {
                path: 'estrategias-preco',
                component: EstrategiasPreco
            },
            {
                path: 'estrategias-preco/simular',
                component: SimularEstrategiaPreco
            },

            {
                path: 'produtos',
                component: Produtos
            },
            {
                path: 'categorias',
                component: Categorias
            },
            {
                path: 'usuarios',
                component: Usuarios
            }
        ]
    },
];