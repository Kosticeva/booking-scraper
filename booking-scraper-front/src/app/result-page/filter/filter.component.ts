import { Component, OnInit, Input } from '@angular/core';
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
  @Input() filters: any[];

  ngOnInit() {
    this.now = new Date();
    this.searchQuery = {
      location: "",
      filters: [],
      dates: {
        checkInDate: new Date(),
        checkOutDate: new Date(this.now.getTime() + 24*60*60*1000)
      },
      rooms: [],
      markers: []
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
    href += ciDate.toLocaleString('sv-SE', { year: 'numeric', month: 'numeric', day: 'numeric' });
    href += "&checkOut=";
    const coDate = this.searchQuery.dates.checkOutDate;
    href += coDate.toLocaleString('sv-SE', { year: 'numeric', month: 'numeric', day: 'numeric' });
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
    href += this.addFilters();
    window.location.href = href;
  }

  addFilters(): string{
    let filterParam = "&filters=";
    for(let filterArray in this.filters){
      for(let filter of this.filters[filterArray]){
        if(filter.checked){
          filterParam += encodeURI(filter.filterName)+",";
        }
      }
    }

    return filterParam.substring(0, filterParam.length-1);
  }

  extractParamsFromUri(){
    this.activatedRoute.queryParams.subscribe(params => {
      let loc = params['destination'];
      let ciDate = params['checkIn'];
      let coDate = params['checkOut'];

      let rooms = params['rooms'];
      let adults = params['adults'];
      let children = params['children'];

      let filters = params['filters'];

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
}
