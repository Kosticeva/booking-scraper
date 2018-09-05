import { Injectable } from "../../../node_modules/@angular/core";
import { Observable } from "../../../node_modules/rxjs";
import { Result } from "../model/result";
import { HttpClient, HttpHeaders } from "../../../node_modules/@angular/common/http";
import { Results } from "../model/results";

@Injectable()
export class ResultService{

    constructor(
        private http: HttpClient
    ) {}

    getResults(url: string): Observable<Results>{
        return this.http.post<Results>("http://localhost:8080/api"+url,[]);
    }

    getFilters(): Observable<any>{
        return this.http.get("http://localhost:8080/api/filters");
    }

    getPlaces(location: string): Observable<any>{
        let headers = new HttpHeaders();
        return this.http.get("http://localhost:8080/api/locations?query="+ location);
    }

}