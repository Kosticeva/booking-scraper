import { BrowserModule } from '@angular/platform-browser';
import { NgModule } from '@angular/core';

import { AppComponent } from './app.component';
import { FrontPageComponent } from './front-page/front-page.component';
import { ResultPageComponent } from './result-page/result-page.component';
import { FilterComponent } from './result-page/filter/filter.component';
import { ResultListComponent } from './result-page/result-list/result-list.component';
import { MainSearchComponent } from './front-page/main-search/main-search.component';
import { AppRoutingModule } from './app-routing.module';
import { TooltipModule } from 'ngx-bootstrap/tooltip';
import { CollapseModule } from 'ngx-bootstrap/collapse';

import { DatepickerModule, BsDatepickerModule } from 'ngx-bootstrap/datepicker';
import { FormsModule } from '../../node_modules/@angular/forms';

@NgModule({
  declarations: [
    AppComponent,
    FrontPageComponent,
    ResultPageComponent,
    FilterComponent,
    ResultListComponent,
    MainSearchComponent
  ],
  imports: [
    BrowserModule,
    AppRoutingModule,
    FormsModule,
    TooltipModule.forRoot(),
    BsDatepickerModule.forRoot(),
    DatepickerModule.forRoot(),
    CollapseModule.forRoot()
  ],
  providers: [],
  bootstrap: [AppComponent]
})
export class AppModule { }
