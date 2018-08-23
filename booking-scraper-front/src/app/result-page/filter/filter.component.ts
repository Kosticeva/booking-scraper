import { Component, OnInit } from '@angular/core';
import { SearchQuery } from '../../model/search-query';
import { Room } from '../../model/room';
import { Router, ActivatedRoute } from '../../../../node_modules/@angular/router';

@Component({
  selector: 'app-filter',
  templateUrl: './filter.component.html',
  styleUrls: ['./filter.component.css']
})
export class FilterComponent implements OnInit {

  searchQuery: SearchQuery;
  now: Date;
  years: Number[];
  adults: Number[];
  filters: any[];

  ngOnInit() {
    this.now = new Date();
    this.searchQuery = {
      location: "",
      filters: [],
      dates: {
        checkInDate: new Date(),
        checkOutDate: new Date(this.now.getTime() + 24*60*60*1000)
      },
      rooms: []
    }
    this.years = [];
    this.adults = [];
    for(let i=0; i<18; i++){
      this.years.push(i);
    }
    for(let i=0; i<21; i++){
      this.adults.push(i);
    }
    this.extractParamsFromUri();
    this.initFilters();
  }

  constructor(
    private router: Router,
    private activatedRoute: ActivatedRoute
  ) { }

  newRoom(){
    this.searchQuery.rooms.push(
      {
         adultsInRoom: 1,
         childrenInRoom: []
      }
    )
  }

  deleteRoom(room: Room){
    this.searchQuery.rooms.splice(this.searchQuery.rooms.indexOf(room), 1);
  }

  addChild(room: Room){
    room.childrenInRoom.push(0);
  }

  getName(i:number, j: number): string{
    return "name-"+i+"-"+j;
  }

  doSearch(){
    let href="search?destination="+encodeURI(this.searchQuery.location)+"&checkIn=";
    const ciDate = this.searchQuery.dates.checkInDate;
    href += ciDate.getDate() + "-" + (ciDate.getMonth()+1) + "-" + ciDate.getFullYear();
    href += "&checkOut=";
    const coDate = this.searchQuery.dates.checkOutDate;
    href += coDate.getDate() + "-" + (coDate.getMonth()+1) + "-" + coDate.getFullYear();
    href += "&rooms="+this.searchQuery.rooms.length;
    href += "&adults=";

    let children = "&children=";
    for(let i=0; i<this.searchQuery.rooms.length; i++){
      href += this.searchQuery.rooms[i].adultsInRoom;

      for(let j=0; j<this.searchQuery.rooms[i].childrenInRoom.length; j++){
        children += this.searchQuery.rooms[i].childrenInRoom[j];

        if(j < this.searchQuery.rooms[i].childrenInRoom.length -1){
          children += ",";
        }
      }

      if(i < this.searchQuery.rooms.length -1){
        href += ",";
        children += ";";
      }
    }

    href += children;
    this.router.navigateByUrl(href);
  }

  extractParamsFromUri(){
    this.activatedRoute.queryParams.subscribe(params => {
      let loc = params['destination'];
      let ciDate = params['checkIn'];
      let coDate = params['checkOut'];

      let rooms = params['rooms'];
      let adults = params['adults'];
      let children = params['children'];

      const adultPerRoom = adults.split(',');
      const childrenPerRoom = children.split(';');
      if(adultPerRoom.length == rooms && childrenPerRoom.length == rooms) {
        this.searchQuery.location = loc;
        this.searchQuery.dates.checkInDate = new Date(ciDate);
        this.searchQuery.dates.checkOutDate = new Date(coDate);
        this.searchQuery.rooms = [];

        for(let i=0; i<rooms; i++){
          let childrenAges = [];
          if(childrenPerRoom[i] !== ""){
            let ages = childrenPerRoom[i].split(',');
            for(let j=0; j<ages.length; j++){
              childrenAges.push(Number.parseInt(ages[j]));
            }
          }

          this.searchQuery.rooms.push(
            {
              adultsInRoom: Number.parseInt(adultPerRoom[i]),
              childrenInRoom: childrenAges
            }
          );
        }
      }
      
    });
  }

  initFilters(){
    this.filters = [];
    let amenities = [];
    amenities.push({
      name: 'kitchen',
      value: 'Kitchen',
      checked: false
    });
    amenities.push({
      name: 'pool',
      value: 'Pool',
      checked: false
    });
    amenities.push({
      name: 'wifi',
      value: 'Wi-Fi',
      checked: false
    });
    amenities.push({
      name: 'parking',
      value: 'Parking',
      checked: false
    });
    this.filters.push(amenities);

    let payments =  [];
    payments.push({
      name: 'freeCanc',
      value: 'Free cancelation',
      checked: false
    });
    this.filters.push(payments);

    let prices = [];
    prices.push({
      name: 'price1',
      value: '0$-57$',
      checked: false
    });
    prices.push({
      name: 'price2',
      value: '110$-199$',
      checked: false
    });
    this.filters.push(prices);

    let cats = [];
    cats.push({
      name: 'cat1',
      value: '1*',
      checked: false
    });
    cats.push({
      name: 'cat2',
      value: '**',
      checked: false
    });
    this.filters.push(cats);

    this.filters.push([]);
    this.filters.push([]);
  }
}
