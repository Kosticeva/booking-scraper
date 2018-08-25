import { Component, OnInit } from '@angular/core';
import { Result } from '../model/result';

@Component({
  selector: 'app-result-page',
  templateUrl: './result-page.component.html',
  styleUrls: ['./result-page.component.css']
})
export class ResultPageComponent implements OnInit {

  results: Result[];

  constructor() { }

  ngOnInit() {
    this.results = [];
  }

}
