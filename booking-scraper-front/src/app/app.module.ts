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
import { LoadingModule, ANIMATION_TYPES } from 'ngx-loading';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { MatAutocompleteModule, MatInputModule } from '@angular/material';

import { DatepickerModule, BsDatepickerModule } from 'ngx-bootstrap/datepicker';
import { FormsModule, ReactiveFormsModule } from '../../node_modules/@angular/forms';
import { ResultService } from './service/result.service';
import { HttpClientModule } from '../../node_modules/@angular/common/http';

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
    CollapseModule.forRoot(),
    BrowserAnimationsModule,
    MatInputModule,
    ReactiveFormsModule,
    MatAutocompleteModule,
    LoadingModule.forRoot(
      {
        animationType: ANIMATION_TYPES.circleSwish
      }
    ),
    HttpClientModule
  ],
  providers: [ResultService],
  bootstrap: [AppComponent]
})
export class AppModule { }
