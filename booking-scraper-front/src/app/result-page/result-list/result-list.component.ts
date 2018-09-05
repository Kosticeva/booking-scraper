import { Component, OnInit, Input } from '@angular/core';
import { Result } from '../../model/result';
import { Results } from '../../model/results';

@Component({
  selector: 'app-result-list',
  templateUrl: './result-list.component.html',
  styleUrls: ['./result-list.component.css']
})
export class ResultListComponent implements OnInit {

  @Input() results: Results;

  constructor() { }

  ngOnInit() {
  }

}
