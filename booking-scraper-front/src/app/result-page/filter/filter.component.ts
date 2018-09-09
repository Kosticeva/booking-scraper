import { Component, OnInit, Input } from '@angular/core';
import { SearchQuery } from '../../model/search-query';
import { Room } from '../../model/room';
import { Router, ActivatedRoute, Route } from '../../../../node_modules/@angular/router';
import { ResultService } from '../../service/result.service';
import { Results } from '../../model/results';

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
  @Input() results: Results;
  @Input() loading: boolean;
  searchResults: any;
  autocompleteOpen: boolean;

  ngOnInit() {
    this.autocompleteOpen = false;
    this.searchResults = {
      predictions: []
    }
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
    private activatedRoute: ActivatedRoute,
    private resultService: ResultService
  ) { 
    //this.router.routeReuseStrategy.shouldReuseRoute = () => false;
  }

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
    const msg = this.validateSearchParameters();
    if(msg != null){
      alert(msg);
      return;
    }

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

      if(this.searchQuery.rooms[i].childrenInRoom.length > 0){
        for(let j=0; j<this.searchQuery.rooms[i].childrenInRoom.length; j++){
          children += this.searchQuery.rooms[i].childrenInRoom[j];

          if(j < this.searchQuery.rooms[i].childrenInRoom.length -1){
            children += ",";
          }
        }
      }else{
        children += "-1";
      }

      if(i < this.searchQuery.rooms.length -1){
        href += ",";
        children += ";";
      }
    }

    href += children;
    href += this.addFilters();
    this.router.navigateByUrl(href);
  }

  addFilters(): string{
    let filterParam = "&filters=";
    for(let filterArray in this.filters){
      for(let filter of this.filters[filterArray]){
        if(filter.checked){
          filterParam += encodeURI(filter.name)+",";
        }
      }
    }

    return filterParam.substring(0, filterParam.length-1);
  }

  searchLocations(){
    if(this.searchQuery.location.length > 2){
      this.resultService.getPlaces(this.searchQuery.location).subscribe(
        (data) => {
          this.autocompleteOpen = true;
          this.searchResults = data;
        }
      )
    }
  }

  setLocation(location: string) {
    this.autocompleteOpen = false;
    this.searchQuery.location = location;
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

      this.searchQuery.location = loc;
      this.searchQuery.dates.checkInDate = new Date(ciDate);
      this.searchQuery.dates.checkOutDate = new Date(coDate);
      this.searchQuery.rooms = [];

      if(adultPerRoom.length == rooms && childrenPerRoom.length == rooms) {

        for(let i=0; i<rooms; i++){
          let childrenAges = [];
          if(childrenPerRoom[i] !== ""){
            let ages = childrenPerRoom[i].split(',');
            for(let j=0; j<ages.length; j++){
              if(Number.parseInt(ages[j]) > -1){
                childrenAges.push(Number.parseInt(ages[j]));
              }
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

  validateSearchParameters(): string{
    if(this.searchQuery.location == null || this.searchQuery.location == ""){
      document.getElementsByName("destination")[0].classList.add("border");
      document.getElementsByName("destination")[0].classList.add("border-danger");
      return "Please specify a location";
    }
    document.getElementsByName("destination")[0].classList.remove("border-danger")
    document.getElementsByName("destination")[0].classList.remove("border");


    if(this.searchQuery.dates.checkInDate == null || this.searchQuery.dates.checkInDate.toString() === "Invalid Date"){
      document.getElementsByName("check-in")[0].classList.add("border");
      document.getElementsByName("check-in")[0].classList.add("border-danger");
      return "Please specify a check in date";
    }
    document.getElementsByName("check-in")[0].classList.remove("border");
    document.getElementsByName("check-in")[0].classList.remove("border-danger");

    if(this.searchQuery.dates.checkOutDate == null || this.searchQuery.dates.checkOutDate.toString() === "Invalid Date"){
      document.getElementsByName("check-out")[0].classList.add("border");
      document.getElementsByName("check-out")[0].classList.add("border-danger");
      return "Please specify a check out date";
    }
    document.getElementsByName("check-out")[0].classList.remove("border");
    document.getElementsByName("check-out")[0].classList.remove("border-danger");

    if(this.searchQuery.dates.checkInDate > this.searchQuery.dates.checkOutDate){
      document.getElementsByName("check-in")[0].classList.add("border");
      document.getElementsByName("check-in")[0].classList.add("border-danger");
      document.getElementsByName("check-out")[0].classList.add("border");
      document.getElementsByName("check-out")[0].classList.add("border-danger");
      return "Make sure that the check in date is before the specified check out date";
    }
    document.getElementsByName("check-in")[0].classList.remove("border");
    document.getElementsByName("check-in")[0].classList.remove("border-danger");
    document.getElementsByName("check-out")[0].classList.remove("border");
    document.getElementsByName("check-out")[0].classList.remove("border-danger");

    const now = new Date();
    now.setDate(now.getDate()-1);
    if(this.searchQuery.dates.checkInDate < now) {
      document.getElementsByName("check-in")[0].classList.add("border");
      document.getElementsByName("check-in")[0].classList.add("border-danger");
      return "You cannot book accommodation in the past";
    }
    document.getElementsByName("check-in")[0].classList.remove("border");
    document.getElementsByName("check-in")[0].classList.remove("border-danger");

    if(this.searchQuery.rooms.length == 0){
      return "Please specify number of persons staying in each room";
    }

    return null;
  }
}
