import { Component, OnInit } from '@angular/core';
import { Result } from '../../model/result';

@Component({
  selector: 'app-result-list',
  templateUrl: './result-list.component.html',
  styleUrls: ['./result-list.component.css']
})
export class ResultListComponent implements OnInit {

  results: Result[];

  constructor() { }

  ngOnInit() {
    this.results = [];

    this.results.push(
      {
        resultLink: "www.google.com",
        resultTitle: "Google",
        resultPrice: 1412.41,
        resultCategory: 5,
        resultRating: 4,
        offers: [
          {
            price: 312,
            site: "Expedia",
            link: "www.expedia.com"
          },
          {
            price: 315,
            site: "Hotels",
            link: "www.hotels.com"
          },
          {
            price: 3212,
            site: "Orbitz",
            link: "www.orbitz.com"
          },
          {
            price: 3415,
            site: "Travelocity",
            link: "www.travelocity.com"
          }
        ]
      }
    );
    this.results.push(
      {
        resultLink: "www.facebook.com",
        resultTitle: "Facebook",
        resultPrice: 43242.41,
        resultCategory: 5,
        resultRating: 4,
        offers: []
      }
    );
    this.results.push(
      {
        resultLink: "www.booking.com",
        resultTitle: "Booking",
        resultPrice: 12.41,
        resultCategory: 5,
        resultRating: 4,
        offers: []
      }
    );
    this.results.push(
      {
        resultLink: "www.expedia.com",
        resultTitle: "Expedia",
        resultPrice: 1.41,
        resultCategory: 5,
        resultRating: 4,
        offers: []
      }
    );
  }

}
