import { Component, OnInit } from '@angular/core';
import { SearchQuery } from '../../model/search-query';
import { Room } from '../../model/room';
import { Router } from '../../../../node_modules/@angular/router';
import { ResultService } from '../../service/result.service';

@Component({
  selector: 'app-main-search',
  templateUrl: './main-search.component.html',
  styleUrls: ['./main-search.component.css']
})
export class MainSearchComponent implements OnInit {
  searchQuery: SearchQuery;
  now: Date;
  years: Number[];
  adults: Number[];
  searchResults: {
    predictions: any[]
  }

  ngOnInit() {
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
  }

  constructor(
    private router: Router,
    private resultService: ResultService
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
      }

      if(i < this.searchQuery.rooms.length -1){
        href += ",";
        children += ";";
      }
    }

    href += children;
    href += "&filters=";
    this.router.navigateByUrl(href);
  }

  searchLocations(){
    if(this.searchQuery.location.length > 2){
      this.resultService.getPlaces(this.searchQuery.location).subscribe(
        (data) => {
          this.searchResults = data;
        }
      )
    }
  }

  setLocation(location: string) {
    this.searchQuery.location = location;
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
