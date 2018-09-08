import { Component, OnInit, Input } from '@angular/core';
import { Result } from '../../model/result';
import { Results } from '../../model/results';
import { ActivatedRoute } from '../../../../node_modules/@angular/router';

@Component({
  selector: 'app-result-list',
  templateUrl: './result-list.component.html',
  styleUrls: ['./result-list.component.css']
})
export class ResultListComponent implements OnInit {

  @Input() results: Results;
  filtersAdded: string[];

  constructor( private activatedRoute: ActivatedRoute ) { }

  ngOnInit() {
    this.filtersAdded = [];
    this.activatedRoute.queryParams.subscribe(
      params => {
        if(params['filters'] !== ""){
          this.filtersAdded = params['filters'].split(',');
        }
      }
    )
  }



}
