import { NgModule } from "../../node_modules/@angular/core";
import { Routes, RouterModule } from "../../node_modules/@angular/router";
import { FrontPageComponent } from "./front-page/front-page.component";
import { ResultPageComponent } from "./result-page/result-page.component";

export const routes: Routes = [
    {
        path: 'home',
        component: FrontPageComponent
    },
    {
        path: 'search',
        component: ResultPageComponent
    },
    {
        path: '',
        pathMatch: 'full',
        redirectTo: '/home'
    }
];

@NgModule({
    imports: [ RouterModule.forRoot(routes)],
    exports: [RouterModule]
  })
  export class AppRoutingModule { }