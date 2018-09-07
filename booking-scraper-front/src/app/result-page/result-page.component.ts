import { Component, OnInit } from '@angular/core';
import { ResultService } from '../service/result.service';
import { Router, ActivatedRoute } from '../../../node_modules/@angular/router';
import { Results } from '../model/results';

@Component({
  selector: 'app-result-page',
  templateUrl: './result-page.component.html',
  styleUrls: ['./result-page.component.css']
})
export class ResultPageComponent implements OnInit {

  results: Results;
  filters: any[];
  loading: boolean;

  constructor(
    private resultService: ResultService,
    private router: Router
  ) { }

  ngOnInit() {
    this.results = new Results([], []);
    this.filters = [];
    this.loading = true;

    this.resultService.getResults(this.router.url).subscribe(
      (data) => {
        this.results =  data;
        this.loading = false;
        this.resultService.getFilters().subscribe(
          (data) => {
            let filterParts = [];
            let params = window.location.href.split("&");
            for(let param of params){
              if(param.startsWith("filters=")){
                filterParts = param.substring(8).split(",");
                break;
              }
            }

            for(let filterArray in data){
              for(let filter of data[filterArray]){
                filter.checked = filterParts.indexOf(filter.name) > -1 ? true: false;
              }
            }
            
            this.filters = data;
          }
        )
      },
      (error) =>  {
        this.loading = false;
        alert(error.message);
      }
    )
  }

  loadMoreResults(){
    alert("Loading ...");
  }

}
